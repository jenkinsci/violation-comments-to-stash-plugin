package org.jenkinsci.plugins.jvctb.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctb.config.CredentialsHelper.findCredentials;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKETSERVERURL;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENT;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENTCONTEXT;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_CREDENTIALSID;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_KEEP_OLD_COMMENTS;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_MINSEVERITY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PROJECTKEY;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_REPOSLUG;
import static se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi.violationCommentsToBitbucketServerApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;
import static se.bjurr.violations.lib.util.Filtering.withAtLEastSeverity;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.jvctb.ProxyConfigDetails;
import org.jenkinsci.plugins.jvctb.config.ViolationConfig;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.remoting.RoleChecker;
import se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.reports.Parser;

public class JvctbPerformer {
  private static Logger LOG = Logger.getLogger(JvctbPerformer.class.getSimpleName());

  @VisibleForTesting
  public static void doPerform(
      final ProxyConfigDetails proxyConfigDetails,
      final ViolationsToBitbucketServerConfig config,
      final File workspace,
      final StandardCredentials credentials,
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

    final ViolationsLogger violationsLogger =
        new ViolationsLogger() {
          @Override
          public void log(final Level level, final String string, final Throwable e) {
            Logger.getLogger(JvctbPerformer.class.getName()).log(level, string, e);
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            listener.getLogger().println(string + "\n" + sw.toString());
          }

          @Override
          public void log(final Level level, final String string) {
            Logger.getLogger(JvctbPerformer.class.getName()).log(level, string);
            if (level != Level.FINE) {
              listener.getLogger().println(string);
            }
          }
        };

    final Set<Violation> allParsedViolations = new TreeSet<>();
    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      if (!isNullOrEmpty(violationConfig.getPattern())) {
        Set<Violation> parsedViolations =
            violationsApi() //
                .withViolationsLogger(violationsLogger) //
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
    final ViolationCommentsToBitbucketServerApi violationCommentsToBitbucketServerApi =
        violationCommentsToBitbucketServerApi();
    if (proxyConfigDetails != null) {
      violationCommentsToBitbucketServerApi //
          .withProxyHostNameOrIp(proxyConfigDetails.getHost()) //
          .withProxyHostPort(proxyConfigDetails.getPort()) //
          .withProxyUser(proxyConfigDetails.getUser()) //
          .withProxyPassword(proxyConfigDetails.getPass());
    }
    try {
      if (credentials instanceof StandardUsernamePasswordCredentials) {
        final StandardUsernamePasswordCredentials usernamePassword =
            (StandardUsernamePasswordCredentials) credentials;
        violationCommentsToBitbucketServerApi //
            .withUsername(usernamePassword.getUsername()) //
            .withPassword(Secret.toString(usernamePassword.getPassword()));
      } else if (credentials instanceof StringCredentials) {
        final StringCredentials personalAccessToken = (StringCredentials) credentials;
        violationCommentsToBitbucketServerApi //
            .withPersonalAccessToken(Secret.toString(personalAccessToken.getSecret()));
      }

      final String commentTemplate = config.getCommentTemplate();
      violationCommentsToBitbucketServerApi //
          .withBitbucketServerUrl(config.getBitbucketServerUrl()) //
          .withPullRequestId(pullRequestIdInt) //
          .withProjectKey(config.getProjectKey()) //
          .withRepoSlug(config.getRepoSlug()) //
          .withViolations(allParsedViolations) //
          .withCreateCommentWithAllSingleFileComments(
              config.getCreateCommentWithAllSingleFileComments()) //
          .withCreateSingleFileComments(config.getCreateSingleFileComments()) //
          .withCreateSingleFileCommentsTasks(config.getCreateSingleFileCommentsTasks()) //
          .withCommentOnlyChangedContent(config.getCommentOnlyChangedContent()) //
          .withCommentOnlyChangedContentContext(config.getCommentOnlyChangedContentContext()) //
          .withShouldCommentOnlyChangedFiles(config.getCommentOnlyChangedFiles()) //
          .withShouldKeepOldComments(config.isKeepOldComments()) //
          .withCommentTemplate(commentTemplate) //
          .withMaxNumberOfViolations(config.getMaxNumberOfViolations()) //
          .withViolationsLogger(violationsLogger) //
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
    expanded.setCreateSingleFileCommentsTasks(config.getCreateSingleFileCommentsTasks());
    expanded.setCredentialsId(config.getCredentialsId());
    expanded.setCommentOnlyChangedContent(config.getCommentOnlyChangedContent());
    expanded.setCommentOnlyChangedContentContext(config.getCommentOnlyChangedContentContext());
    expanded.setCommentOnlyChangedFiles(config.getCommentOnlyChangedFiles());
    expanded.setMinSeverity(config.getMinSeverity());
    expanded.setKeepOldComments(config.isKeepOldComments());
    expanded.setCommentTemplate(config.getCommentTemplate());
    expanded.setMaxNumberOfViolations(config.getMaxNumberOfViolations());

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
      final ProxyConfigDetails proxyConfigDetails,
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

      final Optional<StandardCredentials> credentials =
          findCredentials(
              build.getParent(),
              configExpanded.getCredentialsId(),
              configExpanded.getBitbucketServerUrl());

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
              doPerform(
                  proxyConfigDetails, configExpanded, workspace, credentials.orNull(), listener);
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
    logger.println(FIELD_CREDENTIALSID + ": " + !isNullOrEmpty(config.getCredentialsId()));
    logger.println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
    logger.println("createSingleFileCommentsTasks: " + config.getCreateSingleFileCommentsTasks());
    logger.println(
        FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS
            + ": "
            + config.getCreateCommentWithAllSingleFileComments());
    logger.println(FIELD_COMMENTONLYCHANGEDCONTENT + ": " + config.getCommentOnlyChangedContent());
    logger.println(
        FIELD_COMMENTONLYCHANGEDCONTENTCONTEXT
            + ": "
            + config.getCommentOnlyChangedContentContext());
    logger.println("commentOnlyChangedFiles: " + config.getCommentOnlyChangedFiles());
    logger.println(FIELD_MINSEVERITY + ": " + config.getMinSeverity());
    logger.println(FIELD_KEEP_OLD_COMMENTS + ": " + config.isKeepOldComments());
    logger.println("commentTemplate: " + config.getCommentTemplate());
    logger.println("maxNumberOfViolations: " + config.getMaxNumberOfViolations());

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
