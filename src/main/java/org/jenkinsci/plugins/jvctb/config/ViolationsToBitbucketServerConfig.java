package org.jenkinsci.plugins.jvctb.config;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jvctb.ViolationsToBitbucketServerGlobalConfiguration;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import se.bjurr.violations.lib.model.SEVERITY;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.jenkinsci.plugins.jvctb.config.CredentialsHelper.migrateCredentials;

public class ViolationsToBitbucketServerConfig
    extends AbstractDescribableImpl<ViolationsToBitbucketServerConfig> implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;
  private boolean commentOnlyChangedContent;
  private String bitbucketServerUrl;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  @Deprecated private transient String password;
  private String projectKey;
  private String pullRequestId;
  private String repoSlug;
  @Deprecated private transient String username;
  private String usernamePasswordCredentialsId;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private int commentOnlyChangedContentContext;
  private SEVERITY minSeverity;
  private boolean keepOldComments;

  public ViolationsToBitbucketServerConfig() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerConfig(
      String projectKey, String repoSlug, String pullRequestId) {
    this.projectKey = Util.fixEmptyAndTrim(projectKey);
    this.repoSlug = Util.fixEmptyAndTrim(repoSlug);
    this.pullRequestId = Util.fixEmptyAndTrim(pullRequestId);
  }

  public ViolationsToBitbucketServerConfig(ViolationsToBitbucketServerConfig rhs) {
    violationConfigs = rhs.violationConfigs;
    createSingleFileComments = rhs.createSingleFileComments;
    createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    projectKey = rhs.projectKey;
    repoSlug = rhs.repoSlug;
    pullRequestId = rhs.pullRequestId;
    bitbucketServerUrl = rhs.bitbucketServerUrl;
    usernamePasswordCredentialsId = rhs.usernamePasswordCredentialsId;
    commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    commentOnlyChangedContentContext = rhs.commentOnlyChangedContentContext;
    this.minSeverity = rhs.minSeverity;
    this.keepOldComments = rhs.keepOldComments;
  }

  public void applyDefaults(ViolationsToBitbucketServerGlobalConfiguration defaults) {
    if (isNullOrEmpty(bitbucketServerUrl)) {
      bitbucketServerUrl = defaults.getBitbucketServerUrl();
    }
    if (isNullOrEmpty(usernamePasswordCredentialsId)) {
      usernamePasswordCredentialsId = defaults.getUsernamePasswordCredentialsId();
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

    private Object readResolve() {
      if (StringUtils.isBlank(usernamePasswordCredentialsId) && username != null && password != null) {
        usernamePasswordCredentialsId = migrateCredentials(username, password);
      }
      return this;
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
    final ViolationsToBitbucketServerConfig other = (ViolationsToBitbucketServerConfig) obj;
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
    if (keepOldComments != other.keepOldComments) {
      return false;
    }
    if (minSeverity != other.minSeverity) {
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

  public String getProjectKey() {
    return projectKey;
  }

  public String getPullRequestId() {
    return pullRequestId;
  }

  public String getRepoSlug() {
    return repoSlug;
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
    result = prime * result + (keepOldComments ? 1231 : 1237);
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (projectKey == null ? 0 : projectKey.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repoSlug == null ? 0 : repoSlug.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
    result = prime * result + (violationConfigs == null ? 0 : violationConfigs.hashCode());
    return result;
  }

  @DataBoundSetter
  public void setBitbucketServerUrl(String bitbucketServerUrl) {
    this.bitbucketServerUrl = Util.fixEmptyAndTrim(bitbucketServerUrl);
  }

  @DataBoundSetter
  public void setCommentOnlyChangedContent(boolean commentOnlyChangedContent) {
    this.commentOnlyChangedContent = commentOnlyChangedContent;
  }

  @DataBoundSetter
  public void setCommentOnlyChangedContentContext(int commentOnlyChangedContentContext) {
    this.commentOnlyChangedContentContext = commentOnlyChangedContentContext;
  }

  @DataBoundSetter
  public void setCreateCommentWithAllSingleFileComments(
      boolean createCommentWithAllSingleFileComments) {
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  }

  @DataBoundSetter
  public void setCreateSingleFileComments(boolean createSingleFileComments) {
    this.createSingleFileComments = createSingleFileComments;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  @DataBoundSetter
  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
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

  @DataBoundSetter
  public void setUsernamePasswordCredentialsId(String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = Util.fixEmptyAndTrim(usernamePasswordCredentialsId);
  }

  @DataBoundSetter
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
        + ", projectKey="
        + projectKey
        + ", pullRequestId="
        + pullRequestId
        + ", repoSlug="
        + repoSlug
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
        + ", violationConfigs="
        + violationConfigs
        + ", commentOnlyChangedContentContext="
        + commentOnlyChangedContentContext
        + ", minSeverity="
        + minSeverity
        + ", keepOldComments="
        + keepOldComments
        + "]";
  }

  public boolean isKeepOldComments() {
    return keepOldComments;
  }

  @DataBoundSetter
  public void setKeepOldComments(boolean keepOldComments) {
    this.keepOldComments = keepOldComments;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<ViolationsToBitbucketServerConfig> {
    @Nonnull
    @Override
    public String getDisplayName() {
      return "Violations To Bitbucket Server Config";
    }

    @Restricted(NoExternalUse.class)
    public ListBoxModel doFillMinSeverityItems() {
      ListBoxModel items = new ListBoxModel();
      items.add("Default, Global Config or Info", "");
      for (SEVERITY severity : SEVERITY.values()) {
        items.add(severity.name());
      }
      return items;
    }

    public ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
      return CredentialsHelper.doFillUsernamePasswordCredentialsIdItems();
    }
  }
}
