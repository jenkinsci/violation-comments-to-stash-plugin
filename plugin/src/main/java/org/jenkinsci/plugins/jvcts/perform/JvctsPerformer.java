package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_BASE_URL;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PROJECT;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PULL_REQUEST_ID;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_REPO;
import static org.jenkinsci.plugins.jvcts.stash.JvctsStashClient.commentPullRequest;
import static org.jenkinsci.plugins.jvcts.stash.JvctsStashClient.getChangedFileInPullRequest;
import static org.jenkinsci.plugins.jvcts.stash.JvctsStashClient.removeCommentsFromPullRequest;
import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.violations.model.Violation;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

import com.google.common.annotations.VisibleForTesting;

public class JvctsPerformer {
 public static final String WORKSPACE = "WORKSPACE";
 private static final Logger logger = Logger.getLogger(JvctsPerformer.class.getName());

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

   File workspace = new File(env.expand("$" + WORKSPACE));
   doPerform(config, workspace);
  } catch (Exception e) {
   logger.log(SEVERE, "", e);
   return;
  }
 }

 @VisibleForTesting
 static void doPerform(ViolationsToStashConfig config, File workspace) throws MalformedURLException {
  commentStash(new FullBuildModelWrapper(config, workspace).getViolationsPerFile(), config);
 }

 private static void commentStash(Map<String, List<Violation>> violationsPerFile, ViolationsToStashConfig config)
   throws MalformedURLException {
  for (String changedFileInStash : getChangedFileInPullRequest(config)) {
   /**
    * Should always use filename reported by Stash, then we know Stash will
    * recognize it.
    */
   removeCommentsFromPullRequest(config, changedFileInStash);
   for (Violation violation : getViolationsForFile(violationsPerFile, changedFileInStash)) {
    commentPullRequest(config, changedFileInStash, violation.getLine(), constructCommentMessage(violation));
   }
  }
 }

 /**
  * Get list of violations that has files ending with changed file in Stash.
  * Violation instances may have absolute or relative paths, we can not trust
  * that.
  */
 private static List<Violation> getViolationsForFile(Map<String, List<Violation>> violationsPerFile,
   String changedFileInStash) {
  for (String s : violationsPerFile.keySet()) {
   if (s.endsWith(changedFileInStash)) {
    return violationsPerFile.get(s);
   }
  }
  return newArrayList();
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
  expanded.setStashRepo(environment.expand(config.getStashRepo()));
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   ParserConfig p = new ParserConfig();
   p.setParserTypeDescriptorName(environment.expand(parserConfig.getParserTypeDescriptorName()));
   p.setPattern(environment.expand(parserConfig.getPattern()));
   expanded.getParserConfigs().add(p);
  }
  return expanded;
 }

 /**
  * Enables testing of configuration GUI.
  */
 private static void logConfiguration(ViolationsToStashConfig config, AbstractBuild<?, ?> build, BuildListener listener) {
  listener.getLogger().println(FIELD_STASH_BASE_URL + ": " + config.getStashBaseUrl());
  listener.getLogger().println(FIELD_STASH_PROJECT + ": " + config.getStashProject());
  listener.getLogger().println(FIELD_STASH_PULL_REQUEST_ID + ": " + config.getStashPullRequestId());
  listener.getLogger().println(FIELD_STASH_REPO + ": " + config.getStashRepo());
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   listener.getLogger().println(parserConfig.getParserTypeDescriptorName() + ": " + parserConfig.getPattern());
  }
 }
}
