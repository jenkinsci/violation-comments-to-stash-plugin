package org.jenkinsci.plugins.jvctb.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctb.config.CredentialsHelper.findCredentials;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKETSERVERURL;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENT;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENTCONTEXT;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_KEEP_OLD_COMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_MINSEVERITY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PROJECTKEY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_REPOSLUG;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_USERNAMEPASSWORDCREDENTIALSID;
import static se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi.violationCommentsToBitbucketServerApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;
import static se.bjurr.violations.lib.util.Filtering.withAtLEastSeverity;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvctb.config.ViolationConfig;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.jenkinsci.remoting.RoleChecker;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.util.Secret;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.reports.Parser;

public class JvctbPerformer {
  private static Logger LOG = Logger.getLogger(JvctbPerformer.class.getSimpleName());

  @VisibleForTesting
  public static void doPerform(
      final ViolationsToBitbucketServerConfig config,
      final File workspace,
      final StandardUsernamePasswordCredentials standardUsernamePasswordCredentials,
      final TaskListener listener)
      throws MalformedURLException {
    if (isNullOrEmpty(config.getPullRequestId())) {
      listener
          .getLogger()
          .println(
              "No pull request id defined, will not send violation comments to Bitbucket Server.");
      return;
    }
    final Integer pullRequestIdInt = Integer.valueOf(config.getPullRequestId());

    final List<Violation> allParsedViolations = newArrayList();
    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      if (!isNullOrEmpty(violationConfig.getPattern())) {
        List<Violation> parsedViolations =
            violationsApi() //
                .findAll(violationConfig.getParser()) //
                .withReporter(violationConfig.getReporter()) //
                .inFolder(workspace.getAbsolutePath()) //
                .withPattern(violationConfig.getPattern()) //
                .violations();
        final SEVERITY minSeverity = config.getMinSeverity();
        if (minSeverity != null) {
          parsedViolations = withAtLEastSeverity(parsedViolations, minSeverity);
        }

        allParsedViolations.addAll(parsedViolations);
        listener
            .getLogger()
            .println(
                "Found " + parsedViolations.size() + " violations from " + violationConfig + ".");
      }
    }

    listener
        .getLogger()
        .println(
            "PR: "
                + config.getProjectKey()
                + "/"
                + config.getRepoSlug()
                + "/"
                + config.getPullRequestId()
                + " on "
                + config.getBitbucketServerUrl());

    try {
      violationCommentsToBitbucketServerApi() //
          .withUsername(standardUsernamePasswordCredentials.getUsername()) //
          .withPassword(Secret.toString(standardUsernamePasswordCredentials.getPassword())) //
          .withBitbucketServerUrl(config.getBitbucketServerUrl()) //
          .withPullRequestId(pullRequestIdInt) //
          .withProjectKey(config.getProjectKey()) //
          .withRepoSlug(config.getRepoSlug()) //
          .withViolations(allParsedViolations) //
          .withCreateCommentWithAllSingleFileComments(
              config.getCreateCommentWithAllSingleFileComments()) //
          .withCreateSingleFileComments(config.getCreateSingleFileComments()) //
          .withCommentOnlyChangedContent(config.getCommentOnlyChangedContent()) //
          .withCommentOnlyChangedContentContext(config.getCommentOnlyChangedContentContext()) //
          .withShouldKeepOldComments(config.isKeepOldComments()) //
          .toPullRequest();
    } catch (final Exception e) {
      Logger.getLogger(JvctbPerformer.class.getName()).log(SEVERE, "", e);
      final StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      listener.getLogger().println(sw.toString());
    }
  }

  /** Makes sure any Jenkins variable, used in the configuration fields, are evaluated. */
  @VisibleForTesting
  static ViolationsToBitbucketServerConfig expand(
      final ViolationsToBitbucketServerConfig config, final EnvVars environment) {
    final ViolationsToBitbucketServerConfig expanded = new ViolationsToBitbucketServerConfig();
    expanded.setBitbucketServerUrl(environment.expand(config.getBitbucketServerUrl()));
    expanded.setPullRequestId(environment.expand(config.getPullRequestId()));
    expanded.setProjectKey(environment.expand(config.getProjectKey()));
    expanded.setRepoSlug(environment.expand(config.getRepoSlug()));
    expanded.setCreateCommentWithAllSingleFileComments(
        config.getCreateCommentWithAllSingleFileComments());
    expanded.setCreateSingleFileComments(config.getCreateSingleFileComments());
    expanded.setUsernamePasswordCredentialsId(config.getUsernamePasswordCredentialsId());
    expanded.setCommentOnlyChangedContent(config.getCommentOnlyChangedContent());
    expanded.setCommentOnlyChangedContentContext(config.getCommentOnlyChangedContentContext());
    expanded.setMinSeverity(config.getMinSeverity());
    expanded.setKeepOldComments(config.isKeepOldComments());

    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      final String pattern = environment.expand(violationConfig.getPattern());
      final String reporter = violationConfig.getReporter();
      final Parser parser = violationConfig.getParser();
      if (isNullOrEmpty(pattern) || parser == null) {
        LOG.fine("Ignoring violationConfig because of null/empty -values: " + violationConfig);
        continue;
      }
      final ViolationConfig p = new ViolationConfig();
      p.setPattern(pattern);
      p.setReporter(reporter);
      p.setParser(parser);
      expanded.getViolationConfigs().add(p);
    }
    return expanded;
  }

  public static void jvctsPerform(
      final ViolationsToBitbucketServerConfig configUnexpanded,
      final FilePath fp,
      final Run<?, ?> build,
      final TaskListener listener) {
    try {
      final EnvVars env = build.getEnvironment(listener);
      final ViolationsToBitbucketServerConfig configExpanded = expand(configUnexpanded, env);
      listener.getLogger().println("---");
      listener.getLogger().println("--- Jenkins Violation Comments to Bitbucket Server ---");
      listener.getLogger().println("---");
      logConfiguration(configExpanded, build, listener);

      final Optional<StandardUsernamePasswordCredentials> credentials =
          findCredentials(configExpanded.getUsernamePasswordCredentialsId());
      if (!credentials.isPresent()) {
        listener.getLogger().println("Credentials not found!");
        return;
      }

      listener.getLogger().println("Pull request: " + configExpanded.getPullRequestId());

      fp.act(
          new FileCallable<Void>() {

            private static final long serialVersionUID = 6166111757469534436L;

            @Override
            public void checkRoles(final RoleChecker checker) throws SecurityException {}

            @Override
            public Void invoke(final File workspace, final VirtualChannel channel)
                throws IOException, InterruptedException {
              setupFindBugsMessages();
              listener.getLogger().println("Workspace: " + workspace.getAbsolutePath());
              doPerform(configExpanded, workspace, credentials.get(), listener);
              return null;
            }
          });
    } catch (final Exception e) {
      Logger.getLogger(JvctbPerformer.class.getName()).log(SEVERE, "", e);
      final StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      listener.getLogger().println(sw.toString());
      return;
    }
  }

  private static void logConfiguration(
      final ViolationsToBitbucketServerConfig config,
      final Run<?, ?> build,
      final TaskListener listener) {
    final PrintStream logger = listener.getLogger();
    logger.println(FIELD_BITBUCKETSERVERURL + ": " + config.getBitbucketServerUrl());
    logger.println(FIELD_PROJECTKEY + ": " + config.getProjectKey());
    logger.println(FIELD_REPOSLUG + ": " + config.getRepoSlug());
    logger.println(FIELD_PULLREQUESTID + ": " + config.getPullRequestId());

    logger.println(
        FIELD_USERNAMEPASSWORDCREDENTIALSID
            + ": "
            + !isNullOrEmpty(config.getUsernamePasswordCredentialsId()));

    logger.println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
    logger.println(
        FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS
            + ": "
            + config.getCreateCommentWithAllSingleFileComments());
    logger.println(FIELD_COMMENTONLYCHANGEDCONTENT + ": " + config.getCommentOnlyChangedContent());
    logger.println(
        FIELD_COMMENTONLYCHANGEDCONTENTCONTEXT
            + ": "
            + config.getCommentOnlyChangedContentContext());
    logger.println(FIELD_MINSEVERITY + ": " + config.getMinSeverity());
    logger.println(FIELD_KEEP_OLD_COMMENTS + ": " + config.isKeepOldComments());

    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      logger.println(
          violationConfig.getReporter() + " with pattern " + violationConfig.getPattern());
    }
  }

  private static void setupFindBugsMessages() {
    try {
      final String findbugsMessagesXml =
          CharStreams.toString(
              new InputStreamReader(
                  JvctbPerformer.class.getResourceAsStream("findbugs-messages.xml"), UTF_8));
      setFindbugsMessagesXml(findbugsMessagesXml);
    } catch (final IOException e) {
      propagate(e);
    }
  }
}
