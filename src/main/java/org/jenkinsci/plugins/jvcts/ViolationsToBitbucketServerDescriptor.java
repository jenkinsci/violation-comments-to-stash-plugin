package org.jenkinsci.plugins.jvcts;

import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_BASE_URL;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_PASSWORD;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_PROJECT;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_PULL_REQUEST_ID;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_REPO;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_BITBUCKET_SERVER_USER;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_COMMIT_HASH;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_PATTERN;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.FIELD_PREFIX;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfigHelper.createNewConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.util.List;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.StaplerRequest;

public final class ViolationsToBitbucketServerDescriptor extends BuildStepDescriptor<Publisher> {
 private ViolationsToBitbucketServerConfig config;

 public ViolationsToBitbucketServerDescriptor() {
  super(ViolationsToBitbucketServerRecorder.class);
  load();
  if (config == null || config.getParserConfigs().size() != createNewConfig().getParserConfigs().size()) {
   this.config = createNewConfig();
  }
 }

 @Override
 public String getDisplayName() {
  return "Report Violations to Bitbucket Server (or Stash)";
 }

 @Override
 public String getHelpFile() {
  return super.getHelpFile();
 }

 @SuppressWarnings("unchecked")
 @Override
 public Publisher newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
  ViolationsToBitbucketServerConfig config = createNewConfig();
  config.setBitbucketServerBaseUrl(formData.getString(FIELD_BITBUCKET_SERVER_BASE_URL));
  config.setBitbucketServerProject(formData.getString(FIELD_BITBUCKET_SERVER_PROJECT));
  config.setBitbucketServerUser(formData.getString(FIELD_BITBUCKET_SERVER_USER));
  config.setBitbucketServerPassword(formData.getString(FIELD_BITBUCKET_SERVER_PASSWORD));
  config.setBitbucketServerRepo(formData.getString(FIELD_BITBUCKET_SERVER_REPO));
  config.setBitbucketServerPullRequestId(formData.getString(FIELD_BITBUCKET_SERVER_PULL_REQUEST_ID));
  config.setCommitHash(formData.getString(FIELD_COMMIT_HASH));
  int i = 0;
  for (String pattern : (List<String>) formData.get(FIELD_PATTERN)) {
   config.getParserConfigs().get(i++).setPattern(pattern);
  }
  i = 0;
  for (String pathPrefix : (List<String>) formData.get(FIELD_PREFIX)) {
   config.getParserConfigs().get(i++).setPathPrefix(pathPrefix);
  }
  ViolationsToBitbucketServerRecorder publisher = new ViolationsToBitbucketServerRecorder();
  publisher.setConfig(config);
  return publisher;
 }

 @Override
 public boolean isApplicable(@SuppressWarnings("rawtypes") final Class<? extends AbstractProject> jobType) {
  return true;
 }

 /**
  * Create new blank configuration. Used when job is created.
  */
 public ViolationsToBitbucketServerConfig getNewConfig() {
  return createNewConfig();
 }
}
