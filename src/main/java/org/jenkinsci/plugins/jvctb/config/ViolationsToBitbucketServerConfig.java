package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.jenkinsci.plugins.jvctb.config.CredentialsHelper.migrateCredentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jvctb.ViolationsToBitbucketServerGlobalConfiguration;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsToBitbucketServerConfig
    extends AbstractDescribableImpl<ViolationsToBitbucketServerConfig> implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;
  private boolean commentOnlyChangedContent;
  private boolean commentOnlyChangedFiles = true;
  private String bitbucketServerUrl;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  private boolean createSingleFileCommentsTasks;
  private String projectKey;
  private String pullRequestId;
  private String repoSlug;
  private String credentialsId;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private int commentOnlyChangedContentContext;
  private SEVERITY minSeverity;
  private boolean keepOldComments;
  private String commentTemplate;
  @Deprecated private transient String username;
  @Deprecated private transient String password;
  @Deprecated private transient String usernamePasswordCredentialsId;
  @Deprecated private transient String personalAccessTokenId;
  private Integer maxNumberOfViolations;

  public ViolationsToBitbucketServerConfig() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerConfig(
      final String projectKey, final String repoSlug, final String pullRequestId) {
    this.projectKey = Util.fixEmptyAndTrim(projectKey);
    this.repoSlug = Util.fixEmptyAndTrim(repoSlug);
    this.pullRequestId = Util.fixEmptyAndTrim(pullRequestId);
  }

  public ViolationsToBitbucketServerConfig(final ViolationsToBitbucketServerConfig rhs) {
    violationConfigs = rhs.violationConfigs;
    createSingleFileComments = rhs.createSingleFileComments;
    createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    projectKey = rhs.projectKey;
    repoSlug = rhs.repoSlug;
    pullRequestId = rhs.pullRequestId;
    bitbucketServerUrl = rhs.bitbucketServerUrl;
    credentialsId = rhs.credentialsId;
    commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    commentOnlyChangedContentContext = rhs.commentOnlyChangedContentContext;
    commentOnlyChangedFiles = rhs.commentOnlyChangedFiles;
    this.minSeverity = rhs.minSeverity;
    this.keepOldComments = rhs.keepOldComments;
    this.commentTemplate = rhs.commentTemplate;
    this.createSingleFileCommentsTasks = rhs.createSingleFileCommentsTasks;
    this.maxNumberOfViolations = rhs.maxNumberOfViolations;
  }

  public void applyDefaults(final ViolationsToBitbucketServerGlobalConfiguration defaults) {
    if (isNullOrEmpty(bitbucketServerUrl)) {
      bitbucketServerUrl = defaults.getBitbucketServerUrl();
    }
    if (isNullOrEmpty(credentialsId)) {
      credentialsId = defaults.getCredentialsId();
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
    if (StringUtils.isBlank(credentialsId)) {
      if (personalAccessTokenId != null) {
        credentialsId = personalAccessTokenId;
      } else if (usernamePasswordCredentialsId != null) {
        credentialsId = usernamePasswordCredentialsId;
      }
    }
    if (StringUtils.isBlank(credentialsId) && username != null && password != null) {
      credentialsId = migrateCredentials(username, password);
    }
    return this;
  }

  @Override
  public boolean equals(final Object obj) {
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
    if (commentOnlyChangedFiles != other.commentOnlyChangedFiles) {
      return false;
    }
    if (commentOnlyChangedContentContext != other.commentOnlyChangedContentContext) {
      return false;
    }
    if (commentTemplate == null) {
      if (other.commentTemplate != null) {
        return false;
      }
    } else if (!commentTemplate.equals(other.commentTemplate)) {
      return false;
    }
    if (createCommentWithAllSingleFileComments != other.createCommentWithAllSingleFileComments) {
      return false;
    }
    if (createSingleFileComments != other.createSingleFileComments) {
      return false;
    }
    if (createSingleFileCommentsTasks != other.createSingleFileCommentsTasks) {
      return false;
    }
    if (credentialsId == null) {
      if (other.credentialsId != null) {
        return false;
      }
    } else if (!credentialsId.equals(other.credentialsId)) {
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

  @DataBoundSetter
  public void setCommentOnlyChangedFiles(final boolean commentOnlyChangedFiles) {
    this.commentOnlyChangedFiles = commentOnlyChangedFiles;
  }

  public boolean getCommentOnlyChangedFiles() {
    return commentOnlyChangedFiles;
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

  public String getCredentialsId() {
    return credentialsId;
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
    result = prime * result + (commentOnlyChangedFiles ? 1231 : 1237);
    result = prime * result + commentOnlyChangedContentContext;
    result = prime * result + (commentTemplate == null ? 0 : commentTemplate.hashCode());
    result = prime * result + (createCommentWithAllSingleFileComments ? 1231 : 1237);
    result = prime * result + (createSingleFileComments ? 1231 : 1237);
    result = prime * result + (createSingleFileCommentsTasks ? 1231 : 1237);
    result = prime * result + (credentialsId == null ? 0 : credentialsId.hashCode());
    result = prime * result + (keepOldComments ? 1231 : 1237);
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (projectKey == null ? 0 : projectKey.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repoSlug == null ? 0 : repoSlug.hashCode());
    result = prime * result + (violationConfigs == null ? 0 : violationConfigs.hashCode());
    return result;
  }

  @DataBoundSetter
  public void setBitbucketServerUrl(final String bitbucketServerUrl) {
    this.bitbucketServerUrl = Util.fixEmptyAndTrim(bitbucketServerUrl);
  }

  @DataBoundSetter
  public void setCommentOnlyChangedContent(final boolean commentOnlyChangedContent) {
    this.commentOnlyChangedContent = commentOnlyChangedContent;
  }

  @DataBoundSetter
  public void setCommentOnlyChangedContentContext(final int commentOnlyChangedContentContext) {
    this.commentOnlyChangedContentContext = commentOnlyChangedContentContext;
  }

  @DataBoundSetter
  public void setCreateCommentWithAllSingleFileComments(
      final boolean createCommentWithAllSingleFileComments) {
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  }

  @DataBoundSetter
  public void setCreateSingleFileComments(final boolean createSingleFileComments) {
    this.createSingleFileComments = createSingleFileComments;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  @DataBoundSetter
  public void setMinSeverity(final SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public void setProjectKey(final String projectKey) {
    this.projectKey = projectKey;
  }

  public void setPullRequestId(final String string) {
    pullRequestId = string;
  }

  public void setRepoSlug(final String repoSlug) {
    this.repoSlug = repoSlug;
  }

  @DataBoundSetter
  public void setCredentialsId(final String credentialsId) {
    this.credentialsId = Util.fixEmptyAndTrim(credentialsId);
  }

  @DataBoundSetter
  public void setViolationConfigs(final List<ViolationConfig> parsers) {
    violationConfigs = parsers;
  }

  public boolean getCreateSingleFileCommentsTasks() {
    return createSingleFileCommentsTasks;
  }

  @DataBoundSetter
  public void setCreateSingleFileCommentsTasks(final boolean createSingleFileCommentsTasks) {
    this.createSingleFileCommentsTasks = createSingleFileCommentsTasks;
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
        + ", credentialsId="
        + credentialsId
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

  public String getCommentTemplate() {
    return commentTemplate;
  }

  @DataBoundSetter
  public void setCommentTemplate(final String commentTemplate) {
    this.commentTemplate = commentTemplate;
  }

  @DataBoundSetter
  public void setKeepOldComments(final boolean keepOldComments) {
    this.keepOldComments = keepOldComments;
  }

  @DataBoundSetter
  public void setMaxNumberOfViolations(final Integer maxNumberOfViolations) {
    this.maxNumberOfViolations = maxNumberOfViolations;
  }

  public Integer getMaxNumberOfViolations() {
    return maxNumberOfViolations;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<ViolationsToBitbucketServerConfig> {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Violations To Bitbucket Server Config";
    }

    @Restricted(NoExternalUse.class)
    public ListBoxModel doFillMinSeverityItems() {
      final ListBoxModel items = new ListBoxModel();
      items.add("Default, Global Config or Info", "");
      for (final SEVERITY severity : SEVERITY.values()) {
        items.add(severity.name());
      }
      return items;
    }

    @SuppressWarnings("unused") // Used by stapler
    public ListBoxModel doFillCredentialsIdItems(
        @AncestorInPath final Item item,
        @QueryParameter final String credentialsId,
        @QueryParameter final String bitbucketServerUrl) {
      return CredentialsHelper.doFillCredentialsIdItems(item, credentialsId, bitbucketServerUrl);
    }

    @SuppressWarnings("unused") // Used by stapler
    public FormValidation doCheckCredentialsId(
        @AncestorInPath final Item item,
        @QueryParameter final String value,
        @QueryParameter final String bitbucketServerUrl) {
      return CredentialsHelper.doCheckFillCredentialsId(item, value, bitbucketServerUrl);
    }
  }
}
