package org.jenkinsci.plugins.jvctb;

import java.io.Serializable;

import com.google.common.base.Optional;
import hudson.Extension;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.jvctb.config.CredentialsHelper;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import se.bjurr.violations.lib.model.SEVERITY;

/** Created by magnayn on 07/04/2016. */
@Extension
public class ViolationsToBitbucketServerGlobalConfiguration extends GlobalConfiguration
    implements Serializable {

  private static final long serialVersionUID = -5458709657500220354L;

  /**
   * Returns this singleton instance.
   *
   * @return the singleton.
   */
  public static Optional<ViolationsToBitbucketServerGlobalConfiguration> get() {
    return Optional.fromNullable(
        GlobalConfiguration.all().get(ViolationsToBitbucketServerGlobalConfiguration.class));
  }

  public String bitbucketServerUrl;
  public String password;
  public String projectKey;
  public String repoSlug;
  public String username;
  private String usernamePasswordCredentialsId;
  private SEVERITY minSeverity = SEVERITY.INFO;

  public ViolationsToBitbucketServerGlobalConfiguration() {
    load();
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
    req.bindJSON(this, json);
    save();
    return true;
  }

  public static final ListBoxModel TYPES = new ListBoxModel(
    new ListBoxModel.Option("Info", SEVERITY.INFO.toString()),
    new ListBoxModel.Option("Warning", SEVERITY.WARN.toString()),
    new ListBoxModel.Option("Error", SEVERITY.ERROR.toString())
  );

  @Restricted(NoExternalUse.class)
  public ListBoxModel doFillMinSeverityItems() {
    return TYPES;
  }

  public ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    return CredentialsHelper.doFillUsernamePasswordCredentialsIdItems();
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

  public String getUsernamePasswordCredentialsId() {
    return this.usernamePasswordCredentialsId;
  }

  @DataBoundSetter
  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
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

  public void setUsernamePasswordCredentialsId(String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (bitbucketServerUrl == null ? 0 : bitbucketServerUrl.hashCode());
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (password == null ? 0 : password.hashCode());
    result = prime * result + (projectKey == null ? 0 : projectKey.hashCode());
    result = prime * result + (repoSlug == null ? 0 : repoSlug.hashCode());
    result = prime * result + (username == null ? 0 : username.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
    return result;
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
    ViolationsToBitbucketServerGlobalConfiguration other =
        (ViolationsToBitbucketServerGlobalConfiguration) obj;
    if (bitbucketServerUrl == null) {
      if (other.bitbucketServerUrl != null) {
        return false;
      }
    } else if (!bitbucketServerUrl.equals(other.bitbucketServerUrl)) {
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
    return true;
  }

  @Override
  public String toString() {
    return "ViolationsToBitbucketServerGlobalConfiguration [bitbucketServerUrl="
        + bitbucketServerUrl
        + ", password="
        + password
        + ", projectKey="
        + projectKey
        + ", repoSlug="
        + repoSlug
        + ", username="
        + username
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
        + ", minSeverity="
        + minSeverity
        + "]";
  }
}
