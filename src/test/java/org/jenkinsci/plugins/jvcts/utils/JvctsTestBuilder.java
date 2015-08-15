package org.jenkinsci.plugins.jvcts.utils;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static hudson.plugins.violations.TypeDescriptor.TYPES;
import static java.nio.charset.Charset.defaultCharset;
import static org.jenkinsci.plugins.jvcts.perform.JvctsPerformer.doPerform;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.assertRequested;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.getWorkspace;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.preConfigure;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.CHANGES_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.fake;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.readFile;
import hudson.model.StreamBuildListener;

import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

public class JvctsTestBuilder {

 private final Map<String, String> patternsPerType;
 private final List<String> files = newArrayList();
 private final List<String> assertRequested = newArrayList();

 private JvctsTestBuilder(Map<String, String> patternsPerType) throws Exception {
  this.patternsPerType = patternsPerType;
 }

 public static JvctsTestBuilder assertThat(String type, String pattern) throws Exception {
  Map<String, String> patternsPerType = newHashMap();
  patternsPerType.put(type, pattern);
  return new JvctsTestBuilder(patternsPerType);
 }

 public JvctsTestBuilder and(String type, String pattern) {
  patternsPerType.put(type, pattern);
  return this;
 }

 public void test() throws Exception {
  List<ParserConfig> configs = newArrayList();
  for (String pattern : patternsPerType.keySet()) {
   configs.add(new ParserConfig(TYPES.get(pattern), patternsPerType.get(pattern)));
  }
  ViolationsToStashConfig config = preConfigure(configs);
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  for (String asserted : assertRequested) {
   assertRequested(asserted);
  }
 }

 public JvctsTestBuilder findsComments(String file) throws Exception {
  fake(CHANGES_GET, "{\"values\": [{\"path\":{\"toString\": \"" + file + "\"}}]}");
  fake("http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=" + file
    + "&limit=999999 GET", readFile("pullrequestcomments_GET_none.json"));
  this.files.add(file);
  return this;
 }

 public JvctsTestBuilder postsComment(String file, int line, String comment) {
  fake(
    "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments POST { \"text\": \""
      + comment + "\", \"anchor\": { \"line\": \"" + line
      + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + file + "\" }}", "");
  assertRequested
    .add("http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments POST { \"text\": \""
      + comment
      + "\", \"anchor\": { \"line\": \""
      + line
      + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + file + "\" }}");
  return this;
 }
}
