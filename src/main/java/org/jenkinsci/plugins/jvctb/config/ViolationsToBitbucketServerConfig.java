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
import java.util.ArrayList;
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
  private List<IgnorePath> ignorePaths = newArrayList();

  public ViolationsToBitbucketServerConfig() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerConfig(
      final String projectKey, final String repoSlug, final String pullRequestId) {
    this.projectKey = Util.fixEmptyAndTrim(projectKey);
    this.repoSlug = Util.fixEmptyAndTrim(repoSlug);
    this.pullRequestId = Util.fixEmptyAndTrim(pullRequestId);
  }

  public ViolationsToBitbucketServerConfig(final ViolationsToBitbucketServerConfig rhs) {
    this.violationConfigs = rhs.violationConfigs;
    this.createSingleFileComments = rhs.createSingleFileComments;
    this.createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    this.projectKey = rhs.projectKey;
    this.repoSlug = rhs.repoSlug;
    this.pullRequestId = rhs.pullRequestId;
    this.bitbucketServerUrl = rhs.bitbucketServerUrl;
    this.credentialsId = rhs.credentialsId;
    this.commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    this.commentOnlyChangedContentContext = rhs.commentOnlyChangedContentContext;
    this.commentOnlyChangedFiles = rhs.commentOnlyChangedFiles;
    this.minSeverity = rhs.minSeverity;
    this.keepOldComments = rhs.keepOldComments;
    this.commentTemplate = rhs.commentTemplate;
    this.createSingleFileCommentsTasks = rhs.createSingleFileCommentsTasks;
    this.maxNumberOfViolations = rhs.maxNumberOfViolations;
    this.ignorePaths = rhs.ignorePaths;
  }

  public void applyDefaults(final ViolationsToBitbucketServerGlobalConfiguration defaults) {
    if (isNullOrEmpty(this.bitbucketServerUrl)) {
      this.bitbucketServerUrl = defaults.getBitbucketServerUrl();
    }
    if (isNullOrEmpty(this.credentialsId)) {
      this.credentialsId = defaults.getCredentialsId();
    }
    if (isNullOrEmpty(this.repoSlug)) {
      this.repoSlug = defaults.getRepoSlug();
    }
    if (isNullOrEmpty(this.projectKey)) {
      this.projectKey = defaults.getProjectKey();
    }
    if (this.minSeverity == null) {
      this.minSeverity = defaults.getMinSeverity();
    }
  }

  private Object readResolve() {
    if (StringUtils.isBlank(this.credentialsId)) {
      if (this.personalAccessTokenId != null) {
        this.credentialsId = this.personalAccessTokenId;
      } else if (this.usernamePasswordCredentialsId != null) {
        this.credentialsId = this.usernamePasswordCredentialsId;
      }
    }
    if (StringUtils.isBlank(this.credentialsId) && this.username != null && this.password != null) {
      this.credentialsId = migrateCredentials(this.username, this.password);
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
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ViolationsToBitbucketServerConfig other = (ViolationsToBitbucketServerConfig) obj;
    if (this.bitbucketServerUrl == null) {
      if (other.bitbucketServerUrl != null) {
        return false;
      }
    } else if (!this.bitbucketServerUrl.equals(other.bitbucketServerUrl)) {
      return false;
    }
    if (this.commentOnlyChangedContent != other.commentOnlyChangedContent) {
      return false;
    }
    if (this.commentOnlyChangedFiles != other.commentOnlyChangedFiles) {
      return false;
    }
    if (this.commentOnlyChangedContentContext != other.commentOnlyChangedContentContext) {
      return false;
    }
    if (this.commentTemplate == null) {
      if (other.commentTemplate != null) {
        return false;
      }
    } else if (!this.commentTemplate.equals(other.commentTemplate)) {
      return false;
    }
    if (this.createCommentWithAllSingleFileComments
        != other.createCommentWithAllSingleFileComments) {
      return false;
    }
    if (this.createSingleFileComments != other.createSingleFileComments) {
      return false;
    }
    if (this.createSingleFileCommentsTasks != other.createSingleFileCommentsTasks) {
      return false;
    }
    if (this.credentialsId == null) {
      if (other.credentialsId != null) {
        return false;
      }
    } else if (!this.credentialsId.equals(other.credentialsId)) {
      return false;
    }
    if (this.keepOldComments != other.keepOldComments) {
      return false;
    }
    if (this.minSeverity != other.minSeverity) {
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

  public boolean getCommentOnlyChangedContent() {
    return this.commentOnlyChangedContent;
  }

  public int getCommentOnlyChangedContentContext() {
    return this.commentOnlyChangedContentContext;
  }

  @DataBoundSetter
  public void setCommentOnlyChangedFiles(final boolean commentOnlyChangedFiles) {
    this.commentOnlyChangedFiles = commentOnlyChangedFiles;
  }

  public boolean getCommentOnlyChangedFiles() {
    return this.commentOnlyChangedFiles;
  }

  public boolean getCreateCommentWithAllSingleFileComments() {
    return this.createCommentWithAllSingleFileComments;
  }

  public boolean getCreateSingleFileComments() {
    return this.createSingleFileComments;
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

  public String getCredentialsId() {
    return this.credentialsId;
  }

  public List<ViolationConfig> getViolationConfigs() {
    return this.violationConfigs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + (this.bitbucketServerUrl == null ? 0 : this.bitbucketServerUrl.hashCode());
    result = prime * result + (this.commentOnlyChangedContent ? 1231 : 1237);
    result = prime * result + (this.commentOnlyChangedFiles ? 1231 : 1237);
    result = prime * result + this.commentOnlyChangedContentContext;
    result = prime * result + (this.commentTemplate == null ? 0 : this.commentTemplate.hashCode());
    result = prime * result + (this.createCommentWithAllSingleFileComments ? 1231 : 1237);
    result = prime * result + (this.createSingleFileComments ? 1231 : 1237);
    result = prime * result + (this.createSingleFileCommentsTasks ? 1231 : 1237);
    result = prime * result + (this.credentialsId == null ? 0 : this.credentialsId.hashCode());
    result = prime * result + (this.keepOldComments ? 1231 : 1237);
    result = prime * result + (this.minSeverity == null ? 0 : this.minSeverity.hashCode());
    result = prime * result + (this.projectKey == null ? 0 : this.projectKey.hashCode());
    result = prime * result + (this.pullRequestId == null ? 0 : this.pullRequestId.hashCode());
    result = prime * result + (this.repoSlug == null ? 0 : this.repoSlug.hashCode());
    result =
        prime * result + (this.violationConfigs == null ? 0 : this.violationConfigs.hashCode());
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
    return this.minSeverity;
  }

  @DataBoundSetter
  public void setMinSeverity(final SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public void setProjectKey(final String projectKey) {
    this.projectKey = projectKey;
  }

  public void setPullRequestId(final String string) {
    this.pullRequestId = string;
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
    this.violationConfigs = parsers;
  }

  public boolean getCreateSingleFileCommentsTasks() {
    return this.createSingleFileCommentsTasks;
  }

  @DataBoundSetter
  public void setCreateSingleFileCommentsTasks(final boolean createSingleFileCommentsTasks) {
    this.createSingleFileCommentsTasks = createSingleFileCommentsTasks;
  }

  @Override
  public String toString() {
    return "ViolationsToBitbucketServerConfig [commentOnlyChangedContent="
        + this.commentOnlyChangedContent
        + ", bitbucketServerUrl="
        + this.bitbucketServerUrl
        + ", createCommentWithAllSingleFileComments="
        + this.createCommentWithAllSingleFileComments
        + ", createSingleFileComments="
        + this.createSingleFileComments
        + ", projectKey="
        + this.projectKey
        + ", pullRequestId="
        + this.pullRequestId
        + ", repoSlug="
        + this.repoSlug
        + ", credentialsId="
        + this.credentialsId
        + ", violationConfigs="
        + this.violationConfigs
        + ", commentOnlyChangedContentContext="
        + this.commentOnlyChangedContentContext
        + ", minSeverity="
        + this.minSeverity
        + ", keepOldComments="
        + this.keepOldComments
        + "]";
  }

  public boolean isKeepOldComments() {
    return this.keepOldComments;
  }

  public String getCommentTemplate() {
    return this.commentTemplate;
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

  @DataBoundSetter
  public void setIgnorePaths(final List<IgnorePath> ignorePaths) {
    this.ignorePaths = ignorePaths;
  }

  public List<IgnorePath> getIgnorePaths() {
    return this.ignorePaths;
  }

  public List<String> getIgnorePathStrings() {
    final List<String> items = new ArrayList<String>();
    for (final IgnorePath ip : this.ignorePaths) {
      items.add(ip.getPath());
    }
    return items;
  }

  public Integer getMaxNumberOfViolations() {
    return this.maxNumberOfViolations;
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
