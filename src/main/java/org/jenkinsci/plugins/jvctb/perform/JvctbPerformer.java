package org.jenkinsci.plugins.jvctb.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctb.config.CredentialsHelper.findCredentials;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKETSERVERURL;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PASSWORD;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PROJECTKEY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_REPOSLUG;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_USERNAME;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_USERNAMEPASSWORDCREDENTIALSID;
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
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvctb.config.ViolationConfig;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.jenkinsci.remoting.RoleChecker;

import se.bjurr.violations.lib.model.Violation;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;

public class JvctbPerformer {

 @VisibleForTesting
 public static void doPerform(ViolationsToBitbucketServerConfig config, File workspace, TaskListener listener)
   throws MalformedURLException {
  if (isNullOrEmpty(config.getPullRequestId())) {
   listener.getLogger().println("No pull request id defined, will not send violation comments to Bitbucket Server.");
   return;
  }
  Integer pullRequestIdInt = Integer.valueOf(config.getPullRequestId());

  List<Violation> allParsedViolations = newArrayList();
  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   if (!isNullOrEmpty(violationConfig.getPattern())) {
    List<Violation> parsedViolations = violationsReporterApi()//
      .findAll(violationConfig.getReporter())//
      .inFolder(workspace.getAbsolutePath())//
      .withPattern(violationConfig.getPattern())//
      .violations();
    allParsedViolations.addAll(parsedViolations);
    listener.getLogger().println("Found " + parsedViolations.size() + " violations from " + violationConfig + ".");
   }
  }

  String username = null;
  String password = null;
  if (config.isUseUsernamePassword()) {
   username = checkNotNull(emptyToNull(config.getUsername()), "username selected but not set!");
   password = checkNotNull(emptyToNull(config.getPassword()), "password selected but not set!");
   listener.getLogger().println("Using username / password");
  } else if (config.isUseUsernamePasswordCredentials()) {
   if (!isNullOrEmpty(config.getUsernamePasswordCredentialsId())) {
    Optional<StandardUsernamePasswordCredentials> credentials = findCredentials(config
      .getUsernamePasswordCredentialsId());
    if (credentials.isPresent()) {
     username = checkNotNull(emptyToNull(credentials.get().getUsername()), "Credentials username selected but not set!");
     password = checkNotNull(emptyToNull(credentials.get().getPassword().getPlainText()),
       "Credentials password selected but not set!");
     listener.getLogger().println("Using credentials");
    } else {
     listener.getLogger().println("Credentials not found!");
     return;
    }
   } else {
    listener.getLogger().println("No credentials selected!");
    return;
   }
  } else {
   listener.getLogger().println(
     "No OAuth2 token and no username/password specified. Will not comment any pull request.");
   return;
  }

  listener.getLogger().println(
    "Will comment PR " + config.getProjectKey() + "/" + config.getRepoSlug() + "/" + config.getPullRequestId() + " on "
      + config.getBitbucketServerUrl());

  try {
   violationCommentsToBitbucketServerApi()//
     .withUsername(username)//
     .withPassword(password)//
     .withBitbucketServerUrl(config.getBitbucketServerUrl())//
     .withPullRequestId(pullRequestIdInt)//
     .withProjectKey(config.getProjectKey())//
     .withRepoSlug(config.getRepoSlug())//
     .withViolations(allParsedViolations)//
     .withCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments())//
     .withCreateSingleFileComments(config.getCreateSingleFileComments())//
     .toPullRequest();
  } catch (Exception e) {
   listener.getLogger().println(e.getMessage());
   Logger.getLogger(JvctbPerformer.class.getName()).log(SEVERE, "", e);
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
     listener.getLogger().println("Workspace: " + workspace.getAbsolutePath());
     doPerform(configExpanded, workspace, listener);
     return null;
    }
   });
  } catch (Exception e) {
   listener.getLogger().println(e.getMessage());
   Logger.getLogger(JvctbPerformer.class.getName()).log(SEVERE, "", e);
   return;
  }
 }

 private static void logConfiguration(ViolationsToBitbucketServerConfig config, Run<?, ?> build, TaskListener listener) {
  listener.getLogger().println(FIELD_BITBUCKETSERVERURL + ": " + config.getBitbucketServerUrl());
  listener.getLogger().println(FIELD_PROJECTKEY + ": " + config.getProjectKey());
  listener.getLogger().println(FIELD_REPOSLUG + ": " + config.getRepoSlug());
  listener.getLogger().println(FIELD_PULLREQUESTID + ": " + config.getPullRequestId());

  listener.getLogger().println(
    FIELD_USERNAMEPASSWORDCREDENTIALSID + ": " + !isNullOrEmpty(config.getUsernamePasswordCredentialsId()));
  listener.getLogger().println(FIELD_USERNAME + ": " + !isNullOrEmpty(config.getUsername()));
  listener.getLogger().println(FIELD_PASSWORD + ": " + !isNullOrEmpty(config.getPassword()));

  listener.getLogger().println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
  listener.getLogger().println(
    FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS + ": " + config.getCreateCommentWithAllSingleFileComments());

  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   listener.getLogger().println(violationConfig.getReporter() + " with pattern " + violationConfig.getPattern());
  }
 }

 private static void setupFindBugsMessages() {
  try {
   String findbugsMessagesXml = CharStreams.toString(new InputStreamReader(JvctbPerformer.class
     .getResourceAsStream("findbugs-messages.xml"), UTF_8));
   setFindbugsMessagesXml(findbugsMessagesXml);
  } catch (IOException e) {
   propagate(e);
  }
 }

 /**
  * Makes sure any Jenkins variable, used in the configuration fields, are
  * evaluated.
  */
 @VisibleForTesting
 static ViolationsToBitbucketServerConfig expand(ViolationsToBitbucketServerConfig config, EnvVars environment) {
  ViolationsToBitbucketServerConfig expanded = new ViolationsToBitbucketServerConfig();
  expanded.setBitbucketServerUrl(environment.expand(config.getBitbucketServerUrl()));
  expanded.setUsername(environment.expand(config.getUsername()));
  expanded.setPassword(environment.expand(config.getPassword()));
  expanded.setPullRequestId(environment.expand(config.getPullRequestId()));
  expanded.setProjectKey(environment.expand(config.getProjectKey()));
  expanded.setRepoSlug(environment.expand(config.getRepoSlug()));
  expanded.setCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments());
  expanded.setCreateSingleFileComments(config.getCreateSingleFileComments());
  expanded.setUsernamePasswordCredentialsId(config.getUsernamePasswordCredentialsId());
  expanded.setUseUsernamePassword(config.isUseUsernamePassword());
  expanded.setUseUsernamePasswordCredentials(config.isUseUsernamePasswordCredentials());
  for (ViolationConfig violationConfig : config.getViolationConfigs()) {
   ViolationConfig p = new ViolationConfig();
   p.setPattern(environment.expand(violationConfig.getPattern()));
   p.setReporter(violationConfig.getReporter());
   expanded.getViolationConfigs().add(p);
  }
  return expanded;
 }
}
