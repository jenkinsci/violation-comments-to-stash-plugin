package org.jenkinsci.plugins.jvcts.perform;

import static hudson.plugins.violations.TypeDescriptor.TYPES;
import static hudson.plugins.violations.types.checkstyle.CheckstyleDescriptor.CHECKSTYLE;
import static hudson.plugins.violations.types.findbugs.FindBugsDescriptor.FINDBUGS;
import static hudson.plugins.violations.types.pmd.PMDDescriptor.PMD;
import static java.nio.charset.Charset.defaultCharset;
import static org.jenkinsci.plugins.jvcts.perform.JvctsPerformer.doPerform;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestBuilder.assertThat;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.assertRequested;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.getWorkspace;
import static org.jenkinsci.plugins.jvcts.utils.JvctsTestUtils.preConfigure;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.CHANGES_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_1_DELETE;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_2_DELETE;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_CHECKSTYLEFILE_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_FINDBUGS_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_PMDANDCHECKSTYLE_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.COMMENTS_PMDFILE_GET;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.fake;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.prToCommit;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.readFile;
import hudson.model.StreamBuildListener;

import java.io.IOException;

import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class JvctsPerformerParseTest {

 @Test
 public void testThatPullRequestCheckstyleIsCommented() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(CHECKSTYLE), "**/" + CHECKSTYLE + ".xml, **/" + CHECKSTYLE + "_relativePath.xml"))
    .build());
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("checkstyle_checkstylefile_1.json"));
  assertRequested(readFile("checkstyle_checkstylefile_2.json"));
  assertRequested(readFile("checkstyle_checkstylefile_3_relativePath.json"));
 }

 @Test
 public void testThatPullRequestCheckstyleHtmlLintIsCommented() throws Exception {
  assertThat(CHECKSTYLE, "**/htmllint-report.xml")
    .findsComments("project/index.html")
    .postsComment(
      "project/index.html",
      8,
      "checkstyle L8 High (0) â€œ&â€ did not start a character reference. (â€œ&â€ probably should have been escaped as â€œ&amp;â€ .)")
    .test();
 }

 @Test
 public void testThatPullRequestOldCommentsAreDeleted() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(PMD), "**/" + PMD + ".xml"),
    new ParserConfig(TYPES.get(CHECKSTYLE), "**/" + CHECKSTYLE + ".xml, **/" + CHECKSTYLE + "_relativePath.xml"),
    new ParserConfig(TYPES.get(FINDBUGS), "**/" + FINDBUGS + ".xml")).build());
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(COMMENTS_CHECKSTYLEFILE_GET);
  assertRequested(COMMENTS_PMDANDCHECKSTYLE_GET);
  assertRequested(COMMENTS_PMDFILE_GET);
  assertRequested(COMMENTS_FINDBUGS_GET);
  assertRequested(COMMENTS_1_DELETE);
  assertRequested(COMMENTS_2_DELETE);
 }

 @Test
 public void testThatPullRequestCheckstyleAndPmdCanCommentsOnSameFileIsCommented() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  fake(
    "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=project/index.html&limit=999999 GET",
    readFile("pullrequestcomments_GET_none.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(PMD), "**/" + PMD + ".xml"),
    new ParserConfig(TYPES.get(CHECKSTYLE), "**/" + CHECKSTYLE + ".xml, **/" + CHECKSTYLE + "_relativePath.xml"),
    new ParserConfig(TYPES.get(FINDBUGS), "**/" + FINDBUGS + ".xml")).build());
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("checkstyle_pmdandcheckstyle.json"));
 }

 @Test
 public void testThatPullRequestPmdIsCommented() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(PMD), "**/" + PMD + ".xml")).build());
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("pmd_pmdfile.json"));
 }

 @Test
 public void testThatPullRequestFindbugsIsCommented() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(FINDBUGS), "**/" + FINDBUGS + ".xml")).build());
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("findbugs_code.json"));
 }

 @Test
 public void testThatCommitCheckstyleIsCommented() throws IOException {
  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  fake(
    "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=project/index.html&limit=999999 GET",
    readFile("pullrequestcomments_GET_none.json"));
  ViolationsToStashConfig config = preConfigure(new ImmutableList.Builder<ParserConfig>().add(
    new ParserConfig(TYPES.get(CHECKSTYLE), "**/" + CHECKSTYLE + ".xml, **/" + CHECKSTYLE + "_relativePath.xml"))
    .build());
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_1.json")));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_2.json")));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_3_relativePath.json")));
 }
}
