package org.jenkinsci.plugins.jvctb;

import static hudson.tasks.BuildStepMonitor.NONE;
import static org.jenkinsci.plugins.jvctb.perform.JvctbPerformer.jvctsPerform;

import javax.annotation.Nonnull;
import java.io.IOException;

import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;

public class ViolationsToBitbucketServerRecorder extends Recorder implements SimpleBuildStep {

  public static final BuildStepDescriptor<Publisher> DESCRIPTOR =
      new ViolationsToBitbucketServerDescriptor();

  private ViolationsToBitbucketServerConfig config;

  public ViolationsToBitbucketServerRecorder() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerRecorder(ViolationsToBitbucketServerConfig config) {
    this.config = config;
  }

  public ViolationsToBitbucketServerConfig getConfig() {
    return config;
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
    return DESCRIPTOR;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return NONE;
  }

  @Override
  public void perform(
      @Nonnull Run<?, ?> build,
      @Nonnull FilePath filePath,
      @Nonnull Launcher launcher,
      @Nonnull TaskListener listener)
      throws InterruptedException, IOException {

    ViolationsToBitbucketServerConfig combinedConfig =
        new ViolationsToBitbucketServerConfig(this.config);
    ViolationsToBitbucketServerGlobalConfiguration defaults =
        ViolationsToBitbucketServerGlobalConfiguration.get()
            .or(new ViolationsToBitbucketServerGlobalConfiguration());

    combinedConfig.applyDefaults(defaults);

    jvctsPerform(combinedConfig, filePath, build, listener);
  }

  public void setConfig(ViolationsToBitbucketServerConfig config) {
    this.config = config;
  }
}
