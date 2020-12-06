package org.jenkinsci.plugins.jvctb.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

public class IgnorePath extends AbstractDescribableImpl<IgnorePath> implements Serializable {
  private static final long serialVersionUID = -6308067752266925L;
  private String path;

  public IgnorePath() {}

  @DataBoundConstructor
  public IgnorePath(final String path) {
    this.path = path;
  }

  public String getPath() {
    return this.path;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<IgnorePath> {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Ignore path";
    }
  }
}
