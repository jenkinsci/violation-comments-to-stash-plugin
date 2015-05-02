package org.jenkinsci.plugins.jvcts;

import static hudson.tasks.BuildStepMonitor.NONE;
import static java.lang.Boolean.TRUE;
import static org.jenkinsci.plugins.jvcts.perform.JvctsPerformer.jvctsPerform;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

public class ViolationsToStashRecorder extends Recorder {
 @Extension
 public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new ViolationsToStashDescriptor();
 private ViolationsToStashConfig config;

 @Override
 public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
   throws InterruptedException, IOException {
  jvctsPerform(config, build, listener);
  return TRUE;
 }

 @Override
 public BuildStepDescriptor<Publisher> getDescriptor() {
  return DESCRIPTOR;
 }

 public ViolationsToStashRecorder() {
 }

 @Override
 public BuildStepMonitor getRequiredMonitorService() {
  return NONE;
 }

 public void setConfig(ViolationsToStashConfig config) {
  this.config = config;
 }

 public ViolationsToStashConfig getConfig() {
  return config;
 }
}
