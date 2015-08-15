package org.jenkinsci.plugins.jvcts.utils;

import static com.google.common.base.Joiner.on;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.fakeStashClient;
import static org.jenkinsci.plugins.jvcts.utils.StashClientFaker.getRequestsSentToStash;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

import com.google.common.io.Resources;

public class JvctsTestUtils {
 private JvctsTestUtils() {
 }

 private static void disableLogging() {
  Logger logger = Logger.getLogger("");
  for (Handler h : logger.getHandlers()) {
   logger.removeHandler(h);
  }
 }

 public static ViolationsToStashConfig preConfigure(List<ParserConfig> pconfigs) throws IOException {
  ViolationsToStashConfig config = new ViolationsToStashConfig();
  for (ParserConfig pconfig : pconfigs) {
   config.getParserConfigs().add(pconfig);
  }
  config.setStashBaseUrl("http://stash.server/");
  config.setStashUser("stashUser");
  config.setStashPassword("stashPassword");
  config.setStashProject("stashProject");
  config.setStashRepo("stashRepo");

  disableLogging();

  fakeStashClient();
  return config;
 }

 public static File getWorkspace() {
  return new File(Resources.getResource("test-resources-placeholder.txt").getFile()).getParentFile();
 }

 public static void assertRequested(String request) {
  for (String requested : getRequestsSentToStash()) {
   if (requested.equals(request)) {
    return;
   }
  }
  fail("Did not capture:\n" + request + "\nCaptured:\n" + on("\n").join(getRequestsSentToStash()));
 }
}
