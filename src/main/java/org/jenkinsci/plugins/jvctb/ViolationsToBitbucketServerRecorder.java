package org.jenkinsci.plugins.jvctb;

import static hudson.tasks.BuildStepMonitor.NONE;
import static org.jenkinsci.plugins.jvctb.perform.JvctbPerformer.jvctsPerform;
import static se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi.violationCommentsToBitbucketServerApi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import se.bjurr.violations.comments.bitbucketserver.lib.ViolationCommentsToBitbucketServerApi;

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
      @NonNull Run<?, ?> build,
      @NonNull FilePath filePath,
      @NonNull Launcher launcher,
      @NonNull TaskListener listener)
      throws InterruptedException, IOException {

    ViolationsToBitbucketServerConfig combinedConfig =
        new ViolationsToBitbucketServerConfig(this.config);
    ViolationsToBitbucketServerGlobalConfiguration defaults =
        ViolationsToBitbucketServerGlobalConfiguration.get()
            .or(new ViolationsToBitbucketServerGlobalConfiguration());

    combinedConfig.applyDefaults(defaults);
    ViolationCommentsToBitbucketServerApi violationCommentsToBitbucketServerApi =
        violationCommentsToBitbucketServerApi();
    configureProxy(
        listener.getLogger(),
        violationCommentsToBitbucketServerApi,
        config.getBitbucketServerUrl());
    jvctsPerform(violationCommentsToBitbucketServerApi, combinedConfig, filePath, build, listener);
  }

  /** The Jenkins.getInstance() will return null if not run on master! */
  private void configureProxy(
      final PrintStream logger,
      final ViolationCommentsToBitbucketServerApi b,
      final String urlString)
      throws MalformedURLException {
    final Jenkins jenkins = Jenkins.getInstance();
    if (jenkins == null) {
      logger.println("Not using proxy, no Jenkins instance.");
      return;
    }

    final ProxyConfiguration proxyConfig = jenkins.proxy;
    if (proxyConfig == null) {
      logger.println("Not using proxy, no proxy configured.");
      return;
    }

    final Proxy proxy = proxyConfig.createProxy(new URL(urlString).getHost());
    if (proxy == null || proxy.type() != Proxy.Type.HTTP) {
      logger.println("Not using proxy, not HTTP.");
      return;
    }

    final SocketAddress addr = proxy.address();
    if (addr == null || !(addr instanceof InetSocketAddress)) {
      logger.println("Not using proxy, addr not InetSocketAddress.");
      return;
    }

    final InetSocketAddress proxyAddr = (InetSocketAddress) addr;
    final String proxyHost = proxyAddr.getAddress().getHostAddress();
    final int proxyPort = proxyAddr.getPort();
    b.withProxyHostNameOrIp(proxyHost);
    b.withProxyHostPort(proxyPort);

    final String proxyUser = proxyConfig.getUserName();
    if (proxyUser != null) {
      final String proxyPass = proxyConfig.getPassword();
      b.withProxyUser(proxyUser);
      b.withProxyPassword(proxyPass);
    }
  }

  public void setConfig(ViolationsToBitbucketServerConfig config) {
    this.config = config;
  }
}
