package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Joiner.on;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_1_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_2_DELETE;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_CHECKSTYLEFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_FINDBUGS_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDANDCHECKSTYLE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.COMMENTS_PMDFILE_GET;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.fakeStashClient;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.getRequestsSentToStash;
import static org.jenkinsci.plugins.jvcts.stash.StashClientFaker.readFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
  for (Parser p : Parser.values()) {
   config.getParserConfigs().add(
     new ParserConfig(p.getTypeDescriptorName(), "**/" + p.getTypeDescriptorName() + ".xml"));
  }
  config.setStashBaseUrl("http://stash.server/");
  config.setStashUser("stashUser");
  config.setStashPassword("stashPassword");
  config.setStashProject("stashProject");
  config.setStashPullRequestId("1");
  config.setStashRepo("stashRepo");

  disableLogging();

  fakeStashClient();

  JvctsPerformer.doPerform(config, getWorkspace());

  assertEquals(14, getRequestsSentToStash().size());
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
