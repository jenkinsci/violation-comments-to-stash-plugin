package org.jenkinsci.plugins.jvctb.config;

import java.util.List;

import se.bjurr.violations.lib.reports.Parser;

import static com.google.common.collect.Lists.newArrayList;

public class ViolationsToBitbucketServerConfigHelper {
  public static final String FIELD_BITBUCKETSERVERURL = "bitbucketServerUrl";
  public static final String FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS =
      "createCommentWithAllSingleFileComments";
  public static final String FIELD_COMMENTONLYCHANGEDCONTENT = "commentOnlyChangedContent";
  public static final String FIELD_COMMENTONLYCHANGEDCONTENTCONTEXT =
      "commentOnlyChangedContentContext";
  public static final String FIELD_CREATESINGLEFILECOMMENTS = "createSingleFileComments";
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_PATTERN = "pattern";
  public static final String FIELD_PROJECTKEY = "projectKey";
  public static final String FIELD_PULLREQUESTID = "pullRequestId";
  public static final String FIELD_REPORTER = "reporter";
  public static final String FIELD_REPOSLUG = "repoSlug";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_USERNAMEPASSWORDCREDENTIALSID = "usernamePasswordCredentialsId";
  public static final String FIELD_MINSEVERITY = "minSeverity";
  public static final String FIELD_KEEP_OLD_COMMENTS = "keepOldComments";

  public static ViolationsToBitbucketServerConfig createNewConfig() {
    return new ViolationsToBitbucketServerConfig();
  }

  public static List<ViolationConfig> getAllViolationConfigs() {
    final List<ViolationConfig> violationConfigs = newArrayList();
    for (final Parser parser : Parser.values()) {
      final ViolationConfig violationConfig = new ViolationConfig();
      violationConfig.setParser(parser);
      violationConfigs.add(violationConfig);
    }
    return violationConfigs;
  }
}
