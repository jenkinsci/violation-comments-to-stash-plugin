package org.jenkinsci.plugins.jvctb.perform;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctb.JvctbLogger.doLog;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKETSERVERURL;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PASSWORD;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PROJECTKEY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_REPOSLUG;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_USERNAME;
import static se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi.violationCommentsToBitbucketServerApi;
import static se.bjurr.violations.lib.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import org.jenkinsci.plugins.jvctb.config.ViolationConfig;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.jenkinsci.remoting.RoleChecker;

import se.bjurr.violations.lib.model.Violation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.CharStreams;

public class JvctbPerformer {

 @VisibleForTesting
 public static void doPerform(ViolationsToBitbucketServerConfig config, File workspace, TaskListener listener)
   throws MalformedURLException {
  if (isNullOrEmpty(config.getPullRequestId())) {
   doLog(INFO, "No pull request id defined, will not send violation comments.");
   return;
  }
  Integer pullRequestIdInt = Integer.valueOf(config.getPullRequestId());
  if (!isNullOrEmpty(config.getUsername()) && !isNullOrEmpty(config.getPassword())) {
   doLog(INFO, "Using username/password: " + config.getUsername().substring(0, 1) + ".../*********");
  } else {
   doLog(INFO, "No username/email specified. Will not comment any pull request.");
   return;
  }

  doLog(INFO,
    "Will comment PR " + config.getProjectKey() + "/" + config.getRepoSlug() + "/" + config.getPullRequestId() + " on "
      + config.getBitbucketServerUrl());

  List<Violation> allParsedViolations = newArrayList();
  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   List<Violation> parsedViolations = violationsReporterApi()//
     .findAll(violationConfig.getReporter())//
     .inFolder(workspace.getAbsolutePath())//
     .withPattern(violationConfig.getPattern())//
     .violations();
   allParsedViolations.addAll(parsedViolations);
  }

  try {
   violationCommentsToBitbucketServerApi()//
     .withUsername(emptyToNull(config.getUsername()))//
     .withPassword(emptyToNull(config.getPassword()))//
     .withBitbucketServerUrl(config.getBitbucketServerUrl())//
     .withPullRequestId(pullRequestIdInt)//
     .withProjectKey(config.getProjectKey())//
     .withRepoSlug(config.getRepoSlug())//
     .withViolations(allParsedViolations)//
     .withCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments())//
     .withCreateSingleFileComments(config.getCreateSingleFileComments())//
     .toPullRequest();
  } catch (Exception e) {
   doLog(SEVERE, "", e);
  }
 }

 public static void jvctsPerform(final ViolationsToBitbucketServerConfig configUnexpanded, FilePath fp,
   Run<?, ?> build, final TaskListener listener) {
  try {
   EnvVars env = build.getEnvironment(listener);
   final ViolationsToBitbucketServerConfig configExpanded = expand(configUnexpanded, env);
   listener.getLogger().println("---");
   listener.getLogger().println("--- Jenkins Violation Comments to Bitbucket Server ---");
   listener.getLogger().println("---");
   logConfiguration(configExpanded, build, listener);

   listener.getLogger().println("Running Jenkins Violation Comments To Bitbucket Server");
   listener.getLogger().println("Will comment " + configExpanded.getPullRequestId());

   fp.act(new FileCallable<Void>() {

    private static final long serialVersionUID = 6166111757469534436L;

    @Override
    public void checkRoles(RoleChecker checker) throws SecurityException {

    }

    @Override
    public Void invoke(File workspace, VirtualChannel channel) throws IOException, InterruptedException {
     setupFindBugsMessages();
     doLog(INFO, "Workspace: " + workspace.getAbsolutePath());
     doPerform(configExpanded, workspace, listener);
     return null;
    }
   });
  } catch (Exception e) {
   doLog(SEVERE, "", e);
   return;
  }
 }

 private static void logConfiguration(ViolationsToBitbucketServerConfig config, Run<?, ?> build, TaskListener listener) {
  listener.getLogger().println(FIELD_BITBUCKETSERVERURL + ": " + config.getBitbucketServerUrl());
  listener.getLogger().println(FIELD_REPOSLUG + ": " + config.getRepoSlug());
  listener.getLogger().println(FIELD_PROJECTKEY + ": " + config.getProjectKey());
  listener.getLogger().println(FIELD_PULLREQUESTID + ": " + config.getPullRequestId());

  listener.getLogger().println(FIELD_USERNAME + ": " + !isNullOrEmpty(config.getUsername()));
  listener.getLogger().println(FIELD_PASSWORD + ": " + !isNullOrEmpty(config.getPassword()));

  listener.getLogger().println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
  listener.getLogger().println(
    FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS + ": " + config.getCreateCommentWithAllSingleFileComments());

  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   doLog(INFO, violationConfig.getReporter() + " with pattern " + violationConfig.getPattern());
  }
 }

 private static void setupFindBugsMessages() {
  try {
   String findbugsMessagesXml = CharStreams.toString(new InputStreamReader(JvctbPerformer.class
     .getResourceAsStream("findbugs-messages.xml")));
   setFindbugsMessagesXml(findbugsMessagesXml);
  } catch (IOException e) {
   propagate(e);
  }
 }

 /**
  * Makes sure any Jenkins variable, used in the configuration fields, are
  * evaluated.
  */
 static ViolationsToBitbucketServerConfig expand(ViolationsToBitbucketServerConfig config, EnvVars environment) {
  ViolationsToBitbucketServerConfig expanded = new ViolationsToBitbucketServerConfig();
  expanded.setBitbucketServerUrl(environment.expand(config.getBitbucketServerUrl()));
  expanded.setUsername(environment.expand(config.getUsername()));
  expanded.setPassword(environment.expand(config.getPassword()));
  expanded.setPullRequestId(environment.expand(config.getPullRequestId()));
  expanded.setRepoSlug(environment.expand(config.getRepoSlug()));
  expanded.setProjectKey(environment.expand(config.getProjectKey()));
  expanded.setCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments());
  expanded.setCreateSingleFileComments(config.getCreateSingleFileComments());
  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   ViolationConfig p = new ViolationConfig();
   p.setPattern(environment.expand(violationConfig.getPattern()));
   p.setReporter(violationConfig.getReporter());
   expanded.getViolationConfigs().add(p);
  }
  return expanded;
 }
}
