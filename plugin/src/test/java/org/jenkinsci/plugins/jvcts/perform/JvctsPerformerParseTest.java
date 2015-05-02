package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Joiner.on;
import static java.nio.charset.Charset.defaultCharset;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_1_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_2_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_CHECKSTYLEFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_FINDBUGS_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDANDCHECKSTYLE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.fakeStashClient;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.getRequestsSentToStash;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.readFile;
import static org.junit.Assert.fail;
import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvcts.config.Parser;
import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class JvctsPerformerParseTest {
 private ViolationsToStashConfig config;

 @Before
 public void before() throws IOException {
  config = new ViolationsToStashConfig();
  ParserConfig checkStyleParserConfig = new ParserConfig(Parser.CHECKSTYLE.getTypeDescriptorName(), "**/"
    + Parser.CHECKSTYLE.getTypeDescriptorName() + ".xml, **/" + Parser.CHECKSTYLE.getTypeDescriptorName()
    + "_relativePath.xml");
  config.getParserConfigs().add(checkStyleParserConfig);
  config.getParserConfigs().add(
    new ParserConfig(Parser.PMD.getTypeDescriptorName(), "**/" + Parser.PMD.getTypeDescriptorName() + ".xml"));
  config.getParserConfigs()
    .add(
      new ParserConfig(Parser.FINDBUGS.getTypeDescriptorName(), "**/" + Parser.FINDBUGS.getTypeDescriptorName()
        + ".xml"));
  config.setStashBaseUrl("http://stash.server/");
  config.setStashUser("stashUser");
  config.setStashPassword("stashPassword");
  config.setStashProject("stashProject");
  config.setStashPullRequestId("1");
  config.setStashRepo("stashRepo");

  disableLogging();

  fakeStashClient();

  BuildListener listener = new StreamBuildListener(System.out, defaultCharset());
  JvctsPerformer.doPerform(config, getWorkspace(), listener);
 }

 private void disableLogging() {
  Logger logger = Logger.getLogger("");
  for (Handler h : logger.getHandlers()) {
   logger.removeHandler(h);
  }
 }

 @Test
 public void testThatCheckstyleIsCommented() throws IOException {
  assertRequested(readFile("checkstyle_checkstylefile_1.json"));
  assertRequested(readFile("checkstyle_checkstylefile_2.json"));
  assertRequested(readFile("checkstyle_checkstylefile_3_relativePath.json"));
 }

 @Test
 public void testThatOldCommentsAreDeleted() throws IOException {
  assertRequested(COMMENTS_CHECKSTYLEFILE_GET);
  assertRequested(COMMENTS_PMDANDCHECKSTYLE_GET);
  assertRequested(COMMENTS_PMDFILE_GET);
  assertRequested(COMMENTS_FINDBUGS_GET);
  assertRequested(COMMENTS_1_DELETE);
  assertRequested(COMMENTS_2_DELETE);
 }

 @Test
 public void testThatCheckstyleAndPmdCanCommentsOnSameFileIsCommented() throws IOException {
  assertRequested(readFile("checkstyle_pmdandcheckstyle.json"));
 }

 @Test
 public void testThatPmdIsCommented() throws IOException {
  assertRequested(readFile("pmd_pmdfile.json"));
 }

 @Test
 public void testThatFindbugsIsCommented() throws IOException {
  assertRequested(readFile("findbugs_code.json"));
 }

 private File getWorkspace() {
  return new File(Resources.getResource("test-resources-placeholder.txt").getFile()).getParentFile();
 }

 private void assertRequested(String request) {
  for (String requested : getRequestsSentToStash()) {
   if (requested.equals(request)) {
    return;
   }
  }
  fail("Did not capture:\n" + request + "\nCaptured:\n" + on("\n").join(getRequestsSentToStash()));
 }
}
