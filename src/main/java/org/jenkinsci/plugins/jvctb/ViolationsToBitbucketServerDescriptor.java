package org.jenkinsci.plugins.jvctb;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.StaplerRequest;

import static org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfigHelper.FIELD_MINSEVERITY;

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

  @SuppressWarnings("unchecked")
  @Override
  public Publisher newInstance(StaplerRequest req, JSONObject formData)
      throws hudson.model.Descriptor.FormException {
    if (formData != null) {
      JSONObject config = formData.getJSONObject("config");
      String minSeverity = config.getString(FIELD_MINSEVERITY);
      if (StringUtils.isBlank(minSeverity)) {
        config.remove(FIELD_MINSEVERITY);
      }
    }

    return req.bindJSON(ViolationsToBitbucketServerRecorder.class, formData);
  }
}
