package org.jenkinsci.plugins.jvctb;

import hudson.Extension;

import java.io.Serializable;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Optional;

/**
 * Created by magnayn on 07/04/2016.
 */
@Extension
public class ViolationsToBitbucketServerGlobalConfiguration extends GlobalConfiguration implements Serializable {

 private static final long serialVersionUID = -5458709657500220354L;

 /**
  * Returns this singleton instance.
  *
  * @return the singleton.
  */
 public static Optional<ViolationsToBitbucketServerGlobalConfiguration> get() {
  return Optional.fromNullable(GlobalConfiguration.all().get(ViolationsToBitbucketServerGlobalConfiguration.class));
 }

 public String bitbucketServerUrl;
 public String password;
 public String projectKey;
 public String repoSlug;
 public String username;

 public ViolationsToBitbucketServerGlobalConfiguration() {
  load();
 }

 @Override
 public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
  req.bindJSON(this, json);
  save();
  return true;
 }

 public String getBitbucketServerUrl() {
  return this.bitbucketServerUrl;
 }

 public String getPassword() {
  return this.password;
 }

 public String getProjectKey() {
  return this.projectKey;
 }

 public String getRepoSlug() {
  return this.repoSlug;
 }

 public String getUsername() {
  return this.username;
 }

 @DataBoundSetter
 public void setBitbucketServerUrl(String bitbucketServerUrl) {
  this.bitbucketServerUrl = bitbucketServerUrl;
 }

 @DataBoundSetter
 public void setPassword(String password) {
  this.password = password;
 }

 @DataBoundSetter
 public void setProjectKey(String projectKey) {
  this.projectKey = projectKey;
 }

 @DataBoundSetter
 public void setRepoSlug(String repoSlug) {
  this.repoSlug = repoSlug;
 }

 @DataBoundSetter
 public void setUsername(String username) {
  this.username = username;
 }
}
