package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Joiner.on;
import static java.nio.charset.Charset.defaultCharset;
import static org.jenkinsci.plugins.jvcts.perform.JvctsPerformer.doPerform;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_1_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_2_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_CHECKSTYLEFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_FINDBUGS_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDANDCHECKSTYLE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.fakeStashClient;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.getRequestsSentToStash;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.prToCommit;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.readFile;
import static org.junit.Assert.fail;
import hudson.model.StreamBuildListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvcts.config.Parser;
import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;
import org.junit.Test;

import com.google.common.io.Resources;

public class JvctsPerformerParseTest {
 private ViolationsToStashConfig config;

 public void preConfigure() throws IOException {
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
  config.setStashRepo("stashRepo");

  disableLogging();

  fakeStashClient();

 }

 private void disableLogging() {
  Logger logger = Logger.getLogger("");
  for (Handler h : logger.getHandlers()) {
   logger.removeHandler(h);
  }
 }

 @Test
 public void testThatPullRequestCheckstyleIsCommented() throws IOException {
  preConfigure();
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("checkstyle_checkstylefile_1.json"));
  assertRequested(readFile("checkstyle_checkstylefile_2.json"));
  assertRequested(readFile("checkstyle_checkstylefile_3_relativePath.json"));
 }

 @Test
 public void testThatPullRequestOldCommentsAreDeleted() throws IOException {
  preConfigure();
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
  preConfigure();
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("checkstyle_pmdandcheckstyle.json"));
 }

 @Test
 public void testThatPullRequestPmdIsCommented() throws IOException {
  preConfigure();
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("pmd_pmdfile.json"));
 }

 @Test
 public void testThatPullRequestFindbugsIsCommented() throws IOException {
  preConfigure();
  config.setStashPullRequestId("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(readFile("findbugs_code.json"));
 }

 @Test
 public void testThatCommitCheckstyleIsCommented() throws IOException {
  preConfigure();
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_1.json")));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_2.json")));
  assertRequested(prToCommit(readFile("checkstyle_checkstylefile_3_relativePath.json")));
 }

 @Test
 public void testThatCommitOldCommentsAreDeleted() throws IOException {
  preConfigure();
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(COMMENTS_CHECKSTYLEFILE_GET));
  assertRequested(prToCommit(COMMENTS_PMDANDCHECKSTYLE_GET));
  assertRequested(prToCommit(COMMENTS_PMDFILE_GET));
  assertRequested(prToCommit(COMMENTS_FINDBUGS_GET));
  assertRequested(prToCommit(COMMENTS_1_DELETE));
  assertRequested(prToCommit(COMMENTS_2_DELETE));
 }

 @Test
 public void testThatCommitCheckstyleAndPmdCanCommentsOnSameFileIsCommented() throws IOException {
  preConfigure();
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(readFile("checkstyle_pmdandcheckstyle.json")));
 }

 @Test
 public void testThatCommitPmdIsCommented() throws IOException {
  preConfigure();
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(readFile("pmd_pmdfile.json")));
 }

 @Test
 public void testThatCommitFindbugsIsCommented() throws IOException {
  preConfigure();
  config.setCommitHash("1");
  doPerform(config, getWorkspace(), new StreamBuildListener(System.out, defaultCharset()));
  assertRequested(prToCommit(readFile("findbugs_code.json")));
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
