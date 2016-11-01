package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.violations.lib.reports.Reporter;

public class ViolationsToBitbucketServerConfigHelper {
 public static final String FIELD_BITBUCKETSERVERURL = "bitbucketServerUrl";
 public static final String FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS = "createCommentWithAllSingleFileComments";
 public static final String FIELD_COMMENTONLYCHANGEDCONTENT = "commentOnlyChangedContent";
 public static final String FIELD_CREATESINGLEFILECOMMENTS = "createSingleFileComments";
 public static final String FIELD_PASSWORD = "password";
 public static final String FIELD_PATTERN = "pattern";
 public static final String FIELD_PROJECTKEY = "projectKey";
 public static final String FIELD_PULLREQUESTID = "pullRequestId";
 public static final String FIELD_REPORTER = "reporter";
 public static final String FIELD_REPOSLUG = "repoSlug";
 public static final String FIELD_USERNAME = "username";
 public static final String FIELD_USERNAMEPASSWORDCREDENTIALSID = "usernamePasswordCredentialsId";
 public static final String FIELD_USEUSERNAMEPASSWORD = "useUsernamePassword";
 public static final String FIELD_USEUSERNAMEPASSWORDCREDENTIALS = "useUsernamePasswordCredentials";

 public static ViolationsToBitbucketServerConfig createNewConfig() {
  ViolationsToBitbucketServerConfig config = new ViolationsToBitbucketServerConfig();
  List<ViolationConfig> violationConfigs = getAllViolationConfigs();
  config.setViolationConfigs(violationConfigs);
  return config;
 }

 public static List<ViolationConfig> getAllViolationConfigs() {
  List<ViolationConfig> violationConfigs = newArrayList();
  for (Reporter reporter : Reporter.values()) {
   ViolationConfig violationConfig = new ViolationConfig();
   violationConfig.setReporter(reporter);
   violationConfigs.add(violationConfig);
  }
  return violationConfigs;
 }
}
