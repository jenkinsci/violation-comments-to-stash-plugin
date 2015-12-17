package org.jenkinsci.plugins.jvcts;

import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_COMMIT_HASH;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_PATTERN;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_PREFIX;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_BASE_URL;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PASSWORD;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PROJECT;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_PULL_REQUEST_ID;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_REPO;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FIELD_STASH_USER;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.createNewConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.util.List;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;
import org.kohsuke.stapler.StaplerRequest;

public final class ViolationsToStashDescriptor extends BuildStepDescriptor<Publisher> {
 private ViolationsToStashConfig config;

 public ViolationsToStashDescriptor() {
  super(ViolationsToStashRecorder.class);
  load();
  if (config == null || config.getParserConfigs().size() != createNewConfig().getParserConfigs().size()) {
   this.config = createNewConfig();
  }
 }

 @Override
 public String getDisplayName() {
  return "Report Violations to Stash";
 }

 @Override
 public String getHelpFile() {
  return super.getHelpFile();
 }

 @SuppressWarnings("unchecked")
 @Override
 public Publisher newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
  ViolationsToStashConfig config = createNewConfig();
  config.setStashBaseUrl(formData.getString(FIELD_STASH_BASE_URL));
  config.setStashProject(formData.getString(FIELD_STASH_PROJECT));
  config.setStashUser(formData.getString(FIELD_STASH_USER));
  config.setStashPassword(formData.getString(FIELD_STASH_PASSWORD));
  config.setStashRepo(formData.getString(FIELD_STASH_REPO));
  config.setStashPullRequestId(formData.getString(FIELD_STASH_PULL_REQUEST_ID));
  config.setCommitHash(formData.getString(FIELD_COMMIT_HASH));
  int i = 0;
  for (String pattern : (List<String>) formData.get(FIELD_PATTERN)) {
   config.getParserConfigs().get(i++).setPattern(pattern);
  }
  i = 0;
  for (String pathPrefix : (List<String>) formData.get(FIELD_PREFIX)) {
   config.getParserConfigs().get(i++).setPathPrefix(pathPrefix);
  }
  ViolationsToStashRecorder publisher = new ViolationsToStashRecorder();
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
 public ViolationsToStashConfig getNewConfig() {
  return createNewConfig();
 }
}
