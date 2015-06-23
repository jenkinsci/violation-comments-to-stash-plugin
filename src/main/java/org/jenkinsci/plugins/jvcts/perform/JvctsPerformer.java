package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvcts.JvctsLogger.doLog;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_COMMIT_HASH;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_BASE_URL;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PROJECT;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PULL_REQUEST_ID;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_REPO;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_USER;
import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.violations.model.Violation;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.jvcts.JvctsLogger;
import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;
import org.jenkinsci.plugins.jvcts.stash.JvctsStashClient;

import com.google.common.annotations.VisibleForTesting;

public class JvctsPerformer {

 public static void jvctsPerform(ViolationsToStashConfig config, AbstractBuild<?, ?> build, BuildListener listener) {
  try {
   EnvVars env = build.getEnvironment(listener);
   config = expand(config, env);
   listener.getLogger().println("---");
   listener.getLogger().println("--- Jenkins Violation Comments to Stash ---");
   listener.getLogger().println("---");
   logConfiguration(config, build, listener);

   listener.getLogger().println("Running Jenkins Violation Comments To Stash");
   listener.getLogger().println("Will comment " + config.getStashPullRequestId());

   File workspace = new File(build.getExecutor().getCurrentWorkspace().toURI());
   doLog(FINE, "Workspace: " + workspace.getAbsolutePath());
   doPerform(config, workspace, listener);
  } catch (Exception e) {
   doLog(SEVERE, "", e);
   return;
  }
 }

 @VisibleForTesting
 static void doPerform(ViolationsToStashConfig config, File workspace, BuildListener listener)
   throws MalformedURLException {
  commentStash(new FullBuildModelWrapper(config, workspace, listener).getViolationsPerFile(), config, listener);
 }

 private static void commentStash(Map<String, List<Violation>> violationsPerFile, ViolationsToStashConfig config,
   BuildListener listener) throws MalformedURLException {
  JvctsStashClient jvctsStashClient = new JvctsStashClient(config, listener);
  if (!isNullOrEmpty(config.getStashPullRequestId())) {
   doLog(FINE, "Commenting pull request \"" + config.getStashPullRequestId() + "\"");
   for (String changedFileInStash : jvctsStashClient.getChangedFileInPullRequest()) {
    doLog(FINE, "Changed file in pull request: \"" + changedFileInStash + "\"");
    jvctsStashClient.removeCommentsFromPullRequest(changedFileInStash);
    for (Violation violation : getViolationsForFile(violationsPerFile, changedFileInStash, listener)) {
     jvctsStashClient.commentPullRequest(changedFileInStash, violation.getLine(), constructCommentMessage(violation));
    }
   }
  }
  if (!isNullOrEmpty(config.getCommitHash())) {
   doLog(FINE, "Commenting commit \"" + config.getCommitHash() + "\"");
   for (String changedFileInStash : jvctsStashClient.getChangedFileInCommit()) {
    doLog(FINE, "Changed file in commit: \"" + changedFileInStash + "\"");
    jvctsStashClient.removeCommentsCommit(changedFileInStash);
    for (Violation violation : getViolationsForFile(violationsPerFile, changedFileInStash, listener)) {
     jvctsStashClient.commentCommit(changedFileInStash, violation.getLine(), constructCommentMessage(violation));
    }
   }
  }
 }

 /**
  * Get list of violations that has files ending with changed file in Stash.
  * Violation instances may have absolute or relative paths, we can not trust
  * that.
  */
 private static List<Violation> getViolationsForFile(Map<String, List<Violation>> violationsPerFile,
   String changedFileInStash, BuildListener listener) {
  List<Violation> found = newArrayList();
  for (String reportedFile : violationsPerFile.keySet()) {
   if (reportedFile.endsWith(changedFileInStash) || changedFileInStash.endsWith(reportedFile)) {
    JvctsLogger.doLog(listener, FINE, "Found comments for \"" + changedFileInStash + "\" (reported as \""
      + reportedFile + "\")");
    found.addAll(violationsPerFile.get(reportedFile));
   } else {
    doLog(FINE, "Changed file and reported file not matching. Stash: \"" + changedFileInStash + "\" Reported: \""
      + reportedFile + "\"");
   }
  }
  return found;
 }

 private static String constructCommentMessage(Violation v) {
  String message = firstNonNull(emptyToNull(v.getMessage()), v.getPopupMessage());
  String severity = v.getSeverity();
  if (!isNullOrEmpty(v.getSeverityLevel() + "")) {
   severity += " (" + v.getSeverityLevel() + ") ";
  }
  return (v.getType() + " L" + v.getLine() + " " + severity + message).trim().replaceAll("\n", " ")
    .replaceAll("  ", " ");
 }

 /**
  * Makes sure any Jenkins variable, used in the configuration fields, are
  * evaluated.
  */
 private static ViolationsToStashConfig expand(ViolationsToStashConfig config, EnvVars environment) {
  ViolationsToStashConfig expanded = new ViolationsToStashConfig();
  expanded.setStashBaseUrl(environment.expand(config.getStashBaseUrl()));
  expanded.setStashUser(environment.expand(config.getStashUser()));
  expanded.setStashPassword(environment.expand(config.getStashPassword()));
  expanded.setStashProject(environment.expand(config.getStashProject()));
  expanded.setStashPullRequestId(environment.expand(config.getStashPullRequestId()));
  expanded.setCommitHash(environment.expand(config.getCommitHash()));
  expanded.setStashRepo(environment.expand(config.getStashRepo()));
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   ParserConfig p = new ParserConfig();
   p.setParserTypeDescriptor(parserConfig.getParserTypeDescriptor());
   p.setPattern(environment.expand(parserConfig.getPattern()));
   p.setPathPrefix(environment.expand(parserConfig.getPathPrefixOpt().or("")));
   expanded.getParserConfigs().add(p);
  }
  return expanded;
 }

 /**
  * Enables testing of configuration GUI.
  */
 private static void logConfiguration(ViolationsToStashConfig config, AbstractBuild<?, ?> build, BuildListener listener) {
  listener.getLogger().println(FIELD_STASH_USER + ": " + config.getStashUser());
  listener.getLogger().println(FIELD_STASH_BASE_URL + ": " + config.getStashBaseUrl());
  listener.getLogger().println(FIELD_STASH_PROJECT + ": " + config.getStashProject());
  listener.getLogger().println(FIELD_STASH_PULL_REQUEST_ID + ": " + config.getStashPullRequestId());
  listener.getLogger().println(FIELD_COMMIT_HASH + ": " + config.getCommitHash());
  listener.getLogger().println(FIELD_STASH_REPO + ": " + config.getStashRepo());
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   listener.getLogger().println(parserConfig.getParserTypeDescriptor().getName() + ": " + parserConfig.getPattern());
   if (parserConfig.getPathPrefixOpt().isPresent()) {
    listener.getLogger().println(
      parserConfig.getParserTypeDescriptor().getName() + " pathPrefix: " + parserConfig.getPathPrefix());
   }
  }
 }
}
