package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import org.jenkinsci.plugins.jvctb.ViolationsToBitbucketServerGlobalConfiguration;

public class ViolationsToBitbucketServerConfig implements Serializable {
 private static final long serialVersionUID = 4851568645021422528L;

 private String bitbucketServerUrl;
 private boolean createCommentWithAllSingleFileComments;
 private boolean createSingleFileComments;
 private String password;
 private String projectKey;
 private String pullRequestId;
 private String repoSlug;
 private String username;
 private List<ViolationConfig> violationConfigs = newArrayList();

 public ViolationsToBitbucketServerConfig() {

 }

 public ViolationsToBitbucketServerConfig(boolean createSingleFileComments,
   boolean createCommentWithAllSingleFileComments, String projectKey, String repoSlug, String password,
   String username, String pullRequestId, String bitbucketServerUrl, List<ViolationConfig> violationConfigs) {
  this.violationConfigs = violationConfigs;
  this.createSingleFileComments = createSingleFileComments;
  this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  this.projectKey = projectKey;
  this.repoSlug = repoSlug;
  this.password = password;
  this.username = username;
  this.pullRequestId = pullRequestId;
  this.bitbucketServerUrl = bitbucketServerUrl;
 }

 public ViolationsToBitbucketServerConfig(ViolationsToBitbucketServerConfig rhs) {
  this.violationConfigs = rhs.violationConfigs;
  this.createSingleFileComments = rhs.createSingleFileComments;
  this.createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
  this.projectKey = rhs.projectKey;
  this.repoSlug = rhs.repoSlug;
  this.password = rhs.password;
  this.username = rhs.username;
  this.pullRequestId = rhs.pullRequestId;
  this.bitbucketServerUrl = rhs.bitbucketServerUrl;
 }

 public void applyDefaults(ViolationsToBitbucketServerGlobalConfiguration defaults) {
  if (isNullOrEmpty(this.bitbucketServerUrl)) {
   this.bitbucketServerUrl = defaults.getBitbucketServerUrl();
  }
  if (isNullOrEmpty(this.username)) {
   this.username = defaults.getUsername();
  }
  if (isNullOrEmpty(this.password)) {
   this.password = defaults.getPassword();
  }
  if (isNullOrEmpty(this.repoSlug)) {
   this.repoSlug = defaults.getRepoSlug();
  }
  if (isNullOrEmpty(this.projectKey)) {
   this.projectKey = defaults.getProjectKey();
  }
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  ViolationsToBitbucketServerConfig other = (ViolationsToBitbucketServerConfig) obj;
  if (this.bitbucketServerUrl == null) {
   if (other.bitbucketServerUrl != null) {
    return false;
   }
  } else if (!this.bitbucketServerUrl.equals(other.bitbucketServerUrl)) {
   return false;
  }
  if (this.createCommentWithAllSingleFileComments != other.createCommentWithAllSingleFileComments) {
   return false;
  }
  if (this.createSingleFileComments != other.createSingleFileComments) {
   return false;
  }
  if (this.password == null) {
   if (other.password != null) {
    return false;
   }
  } else if (!this.password.equals(other.password)) {
   return false;
  }
  if (this.projectKey == null) {
   if (other.projectKey != null) {
    return false;
   }
  } else if (!this.projectKey.equals(other.projectKey)) {
   return false;
  }
  if (this.pullRequestId == null) {
   if (other.pullRequestId != null) {
    return false;
   }
  } else if (!this.pullRequestId.equals(other.pullRequestId)) {
   return false;
  }
  if (this.repoSlug == null) {
   if (other.repoSlug != null) {
    return false;
   }
  } else if (!this.repoSlug.equals(other.repoSlug)) {
   return false;
  }
  if (this.username == null) {
   if (other.username != null) {
    return false;
   }
  } else if (!this.username.equals(other.username)) {
   return false;
  }
  if (this.violationConfigs == null) {
   if (other.violationConfigs != null) {
    return false;
   }
  } else if (!this.violationConfigs.equals(other.violationConfigs)) {
   return false;
  }
  return true;
 }

 public String getBitbucketServerUrl() {
  return this.bitbucketServerUrl;
 }

 public boolean getCreateCommentWithAllSingleFileComments() {
  return this.createCommentWithAllSingleFileComments;
 }

 public boolean getCreateSingleFileComments() {
  return this.createSingleFileComments;
 }

 public String getPassword() {
  return this.password;
 }

 public String getProjectKey() {
  return this.projectKey;
 }

 public String getPullRequestId() {
  return this.pullRequestId;
 }

 public String getRepoSlug() {
  return this.repoSlug;
 }

 public String getUsername() {
  return this.username;
 }

 public List<ViolationConfig> getViolationConfigs() {
  return this.violationConfigs;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((this.bitbucketServerUrl == null) ? 0 : this.bitbucketServerUrl.hashCode());
  result = prime * result + (this.createCommentWithAllSingleFileComments ? 1231 : 1237);
  result = prime * result + (this.createSingleFileComments ? 1231 : 1237);
  result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
  result = prime * result + ((this.projectKey == null) ? 0 : this.projectKey.hashCode());
  result = prime * result + ((this.pullRequestId == null) ? 0 : this.pullRequestId.hashCode());
  result = prime * result + ((this.repoSlug == null) ? 0 : this.repoSlug.hashCode());
  result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
  result = prime * result + ((this.violationConfigs == null) ? 0 : this.violationConfigs.hashCode());
  return result;
 }

 public void setBitbucketServerUrl(String bitbucketServerUrl) {
  this.bitbucketServerUrl = bitbucketServerUrl;
 }

 public void setCreateCommentWithAllSingleFileComments(boolean createCommentWithAllSingleFileComments) {
  this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
 }

 public void setCreateSingleFileComments(boolean createSingleFileComments) {
  this.createSingleFileComments = createSingleFileComments;
 }

 public void setPassword(String password) {
  this.password = password;
 }

 public void setProjectKey(String projectKey) {
  this.projectKey = projectKey;
 }

 public void setPullRequestId(String string) {
  this.pullRequestId = string;
 }

 public void setRepoSlug(String repoSlug) {
  this.repoSlug = repoSlug;
 }

 public void setUsername(String username) {
  this.username = username;
 }

 public void setViolationConfigs(List<ViolationConfig> parsers) {
  this.violationConfigs = parsers;
 }

 @Override
 public String toString() {
  return "ViolationsToBitbucketServerConfig [bitbucketServerUrl=" + this.bitbucketServerUrl
    + ", createCommentWithAllSingleFileComments=" + this.createCommentWithAllSingleFileComments
    + ", createSingleFileComments=" + this.createSingleFileComments + ", password=" + this.password + ", projectKey="
    + this.projectKey + ", pullRequestId=" + this.pullRequestId + ", repoSlug=" + this.repoSlug + ", username="
    + this.username + ", violationConfigs=" + this.violationConfigs + "]";
 }
}
