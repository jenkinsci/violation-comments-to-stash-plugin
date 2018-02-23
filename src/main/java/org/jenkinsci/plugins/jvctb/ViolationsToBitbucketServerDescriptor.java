package org.jenkinsci.plugins.jvctb;

import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_MINSEVERITY;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

@Extension
@Symbol("ViolationsToBitbucketServer")
public final class ViolationsToBitbucketServerDescriptor extends BuildStepDescriptor<Publisher> {
  private ViolationsToBitbucketServerConfig config;

  public ViolationsToBitbucketServerDescriptor() {
    super(ViolationsToBitbucketServerRecorder.class);
    load();
    if (config == null) {
      config = new ViolationsToBitbucketServerConfig();
    }
  }

  @Override
  public String getDisplayName() {
    return "Report Violations to Bitbucket Server";
  }

  @Override
  public String getHelpFile() {
    return super.getHelpFile();
  }

  @Override
  public boolean isApplicable(
      @SuppressWarnings("rawtypes") final Class<? extends AbstractProject> jobType) {
    return true;
  }

  @Override
  @SuppressFBWarnings(
    value = "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE",
    justification = "Stapler Request cannot be null, but due to legacy it is marked as such"
  )
  public Publisher newInstance(final StaplerRequest req, @NonNull final JSONObject formData)
      throws hudson.model.Descriptor.FormException {
    if (formData.has("config")) {
      final JSONObject config = formData.getJSONObject("config");
      final String minSeverity = config.getString(FIELD_MINSEVERITY);
      if (StringUtils.isBlank(minSeverity)) {
        config.remove(FIELD_MINSEVERITY);
      }
    }

    return req.bindJSON(ViolationsToBitbucketServerRecorder.class, formData);
  }
}
