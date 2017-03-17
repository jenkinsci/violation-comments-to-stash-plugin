package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import org.jenkinsci.plugins.jvctb.ViolationsToBitbucketServerGlobalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsToBitbucketServerConfig implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;
  private boolean commentOnlyChangedContent;
  private String bitbucketServerUrl;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  private String password;
  private String projectKey;
  private String pullRequestId;
  private String repoSlug;
  private String username;
  private String usernamePasswordCredentialsId;
  private boolean useUsernamePassword;
  private boolean useUsernamePasswordCredentials;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private int commentOnlyChangedContentContext;
  private SEVERITY minSeverity;

  public ViolationsToBitbucketServerConfig() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerConfig(
      boolean createSingleFileComments,
      boolean createCommentWithAllSingleFileComments,
      String projectKey,
      String repoSlug,
      String password,
      String username,
      String pullRequestId,
      String bitbucketServerUrl,
      List<ViolationConfig> violationConfigs,
      String usernamePasswordCredentialsId,
      boolean useUsernamePasswordCredentials,
      boolean useUsernamePassword,
      boolean commentOnlyChangedContent,
      int commentOnlyChangedContentContext,
      SEVERITY minSeverity) {

    List<ViolationConfig> allViolationConfigs = includeAllReporters(violationConfigs);

    this.violationConfigs = allViolationConfigs;
    this.createSingleFileComments = createSingleFileComments;
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
    this.projectKey = projectKey;
    this.repoSlug = repoSlug;
    this.password = password;
    this.username = username;
    this.pullRequestId = pullRequestId;
    this.bitbucketServerUrl = bitbucketServerUrl;
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
    this.useUsernamePasswordCredentials = useUsernamePasswordCredentials;
    this.useUsernamePassword = useUsernamePassword;
    this.commentOnlyChangedContent = commentOnlyChangedContent;
    this.commentOnlyChangedContentContext = commentOnlyChangedContentContext;
    this.minSeverity = minSeverity;
  }

  public ViolationsToBitbucketServerConfig(ViolationsToBitbucketServerConfig rhs) {
    violationConfigs = rhs.violationConfigs;
    createSingleFileComments = rhs.createSingleFileComments;
    createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    projectKey = rhs.projectKey;
    repoSlug = rhs.repoSlug;
    password = rhs.password;
    username = rhs.username;
    pullRequestId = rhs.pullRequestId;
    bitbucketServerUrl = rhs.bitbucketServerUrl;
    usernamePasswordCredentialsId = rhs.usernamePasswordCredentialsId;
    useUsernamePasswordCredentials = rhs.useUsernamePasswordCredentials;
    useUsernamePassword = rhs.useUsernamePassword;
    commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    commentOnlyChangedContentContext = rhs.commentOnlyChangedContentContext;
    this.minSeverity = rhs.minSeverity;
  }

  public void applyDefaults(ViolationsToBitbucketServerGlobalConfiguration defaults) {
    if (isNullOrEmpty(bitbucketServerUrl)) {
      bitbucketServerUrl = defaults.getBitbucketServerUrl();
    }
    if (isNullOrEmpty(usernamePasswordCredentialsId)) {
      usernamePasswordCredentialsId = defaults.getUsernamePasswordCredentialsId();
    }
    if (isNullOrEmpty(username)) {
      username = defaults.getUsername();
    }
    if (isNullOrEmpty(password)) {
      password = defaults.getPassword();
    }
    if (isNullOrEmpty(repoSlug)) {
      repoSlug = defaults.getRepoSlug();
    }
    if (isNullOrEmpty(projectKey)) {
      projectKey = defaults.getProjectKey();
    }
    if (this.minSeverity == null) {
      this.minSeverity = defaults.getMinSeverity();
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
    if (bitbucketServerUrl == null) {
      if (other.bitbucketServerUrl != null) {
        return false;
      }
    } else if (!bitbucketServerUrl.equals(other.bitbucketServerUrl)) {
      return false;
    }
    if (commentOnlyChangedContent != other.commentOnlyChangedContent) {
      return false;
    }
    if (commentOnlyChangedContentContext != other.commentOnlyChangedContentContext) {
      return false;
    }
    if (createCommentWithAllSingleFileComments != other.createCommentWithAllSingleFileComments) {
      return false;
    }
    if (createSingleFileComments != other.createSingleFileComments) {
      return false;
    }
    if (minSeverity != other.minSeverity) {
      return false;
    }
    if (password == null) {
      if (other.password != null) {
        return false;
      }
    } else if (!password.equals(other.password)) {
      return false;
    }
    if (projectKey == null) {
      if (other.projectKey != null) {
        return false;
      }
    } else if (!projectKey.equals(other.projectKey)) {
      return false;
    }
    if (pullRequestId == null) {
      if (other.pullRequestId != null) {
        return false;
      }
    } else if (!pullRequestId.equals(other.pullRequestId)) {
      return false;
    }
    if (repoSlug == null) {
      if (other.repoSlug != null) {
        return false;
      }
    } else if (!repoSlug.equals(other.repoSlug)) {
      return false;
    }
    if (useUsernamePassword != other.useUsernamePassword) {
      return false;
    }
    if (useUsernamePasswordCredentials != other.useUsernamePasswordCredentials) {
      return false;
    }
    if (username == null) {
      if (other.username != null) {
        return false;
      }
    } else if (!username.equals(other.username)) {
      return false;
    }
    if (usernamePasswordCredentialsId == null) {
      if (other.usernamePasswordCredentialsId != null) {
        return false;
      }
    } else if (!usernamePasswordCredentialsId.equals(other.usernamePasswordCredentialsId)) {
      return false;
    }
    if (violationConfigs == null) {
      if (other.violationConfigs != null) {
        return false;
      }
    } else if (!violationConfigs.equals(other.violationConfigs)) {
      return false;
    }
    return true;
  }

  public String getBitbucketServerUrl() {
    return bitbucketServerUrl;
  }

  public boolean getCommentOnlyChangedContent() {
    return commentOnlyChangedContent;
  }

  public int getCommentOnlyChangedContentContext() {
    return commentOnlyChangedContentContext;
  }

  public boolean getCreateCommentWithAllSingleFileComments() {
    return createCommentWithAllSingleFileComments;
  }

  public boolean getCreateSingleFileComments() {
    return createSingleFileComments;
  }

  public String getPassword() {
    return password;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public String getPullRequestId() {
    return pullRequestId;
  }

  public String getRepoSlug() {
    return repoSlug;
  }

  public String getUsername() {
    return username;
  }

  public String getUsernamePasswordCredentialsId() {
    return usernamePasswordCredentialsId;
  }

  public List<ViolationConfig> getViolationConfigs() {
    return violationConfigs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (bitbucketServerUrl == null ? 0 : bitbucketServerUrl.hashCode());
    result = prime * result + (commentOnlyChangedContent ? 1231 : 1237);
    result = prime * result + commentOnlyChangedContentContext;
    result = prime * result + (createCommentWithAllSingleFileComments ? 1231 : 1237);
    result = prime * result + (createSingleFileComments ? 1231 : 1237);
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (password == null ? 0 : password.hashCode());
    result = prime * result + (projectKey == null ? 0 : projectKey.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repoSlug == null ? 0 : repoSlug.hashCode());
    result = prime * result + (useUsernamePassword ? 1231 : 1237);
    result = prime * result + (useUsernamePasswordCredentials ? 1231 : 1237);
    result = prime * result + (username == null ? 0 : username.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
    result = prime * result + (violationConfigs == null ? 0 : violationConfigs.hashCode());
    return result;
  }

  private List<ViolationConfig> includeAllReporters(List<ViolationConfig> violationConfigs) {
    List<ViolationConfig> allViolationConfigs =
        ViolationsToBitbucketServerConfigHelper.getAllViolationConfigs();
    for (ViolationConfig candidate : allViolationConfigs) {
      for (ViolationConfig input : violationConfigs) {
        if (candidate.getReporter() == input.getReporter()) {
          candidate.setPattern(input.getPattern());
        }
      }
    }
    return allViolationConfigs;
  }

  public boolean isUseUsernamePassword() {
    return useUsernamePassword;
  }

  public boolean isUseUsernamePasswordCredentials() {
    return useUsernamePasswordCredentials;
  }

  public void setBitbucketServerUrl(String bitbucketServerUrl) {
    this.bitbucketServerUrl = bitbucketServerUrl;
  }

  public void setCommentOnlyChangedContent(boolean commentOnlyChangedContent) {
    this.commentOnlyChangedContent = commentOnlyChangedContent;
  }

  public void setCommentOnlyChangedContentContext(int commentOnlyChangedContentContext) {
    this.commentOnlyChangedContentContext = commentOnlyChangedContentContext;
  }

  public void setCreateCommentWithAllSingleFileComments(
      boolean createCommentWithAllSingleFileComments) {
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  }

  public void setCreateSingleFileComments(boolean createSingleFileComments) {
    this.createSingleFileComments = createSingleFileComments;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
  }

  public void setPullRequestId(String string) {
    pullRequestId = string;
  }

  public void setRepoSlug(String repoSlug) {
    this.repoSlug = repoSlug;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setUsernamePasswordCredentialsId(String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
  }

  public void setUseUsernamePassword(boolean useUsernamePassword) {
    this.useUsernamePassword = useUsernamePassword;
  }

  public void setUseUsernamePasswordCredentials(boolean useUsernamePasswordCredentials) {
    this.useUsernamePasswordCredentials = useUsernamePasswordCredentials;
  }

  public void setViolationConfigs(List<ViolationConfig> parsers) {
    violationConfigs = parsers;
  }

  @Override
  public String toString() {
    return "ViolationsToBitbucketServerConfig [commentOnlyChangedContent="
        + commentOnlyChangedContent
        + ", bitbucketServerUrl="
        + bitbucketServerUrl
        + ", createCommentWithAllSingleFileComments="
        + createCommentWithAllSingleFileComments
        + ", createSingleFileComments="
        + createSingleFileComments
        + ", password="
        + password
        + ", projectKey="
        + projectKey
        + ", pullRequestId="
        + pullRequestId
        + ", repoSlug="
        + repoSlug
        + ", username="
        + username
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
        + ", useUsernamePassword="
        + useUsernamePassword
        + ", useUsernamePasswordCredentials="
        + useUsernamePasswordCredentials
        + ", violationConfigs="
        + violationConfigs
        + ", commentOnlyChangedContentContext="
        + commentOnlyChangedContentContext
        + ", minSeverity="
        + minSeverity
        + "]";
  }
}
