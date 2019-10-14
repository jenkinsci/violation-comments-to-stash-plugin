package org.jenkinsci.plugins.jvctb;

import static hudson.tasks.BuildStepMonitor.NONE;
import static org.jenkinsci.plugins.jvctb.perform.JvctbPerformer.jvctsPerform;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.FilePath;
import hudson.Launcher;
import hudson.ProxyConfiguration;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.kohsuke.stapler.DataBoundConstructor;

public class ViolationsToBitbucketServerRecorder extends Recorder implements SimpleBuildStep {

  public static final BuildStepDescriptor<Publisher> DESCRIPTOR =
      new ViolationsToBitbucketServerDescriptor();

  private ViolationsToBitbucketServerConfig config;

  public ViolationsToBitbucketServerRecorder() {}

  @DataBoundConstructor
  public ViolationsToBitbucketServerRecorder(final ViolationsToBitbucketServerConfig config) {
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
      @NonNull final Run<?, ?> build,
      @NonNull final FilePath filePath,
      @NonNull final Launcher launcher,
      @NonNull final TaskListener listener)
      throws InterruptedException, IOException {

    final ViolationsToBitbucketServerConfig combinedConfig =
        new ViolationsToBitbucketServerConfig(this.config);
    final ViolationsToBitbucketServerGlobalConfiguration defaults =
        ViolationsToBitbucketServerGlobalConfiguration.get()
            .or(new ViolationsToBitbucketServerGlobalConfiguration());

    combinedConfig.applyDefaults(defaults);
    final ProxyConfigDetails proxyConfigDetails =
        createProxyConfigDetails(listener.getLogger(), combinedConfig.getBitbucketServerUrl());
    jvctsPerform(proxyConfigDetails, combinedConfig, filePath, build, listener);
  }

  /** The Jenkins.getInstance() will return null if not run on master! */
  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  private ProxyConfigDetails createProxyConfigDetails(
      final PrintStream logger, final String urlString) throws MalformedURLException {
    final Jenkins jenkins = Jenkins.getInstance();
    if (jenkins == null) {
      logger.println("Not using proxy, no Jenkins instance.");
      return null;
    }

    final ProxyConfiguration proxyConfig = jenkins.proxy;
    if (proxyConfig == null) {
      logger.println("Not using proxy, no proxy configured.");
      return null;
    }

    final Proxy proxy = proxyConfig.createProxy(new URL(urlString).getHost());
    if (proxy == null || proxy.type() != Proxy.Type.HTTP) {
      logger.println("Not using proxy, not HTTP.");
      return null;
    }

    final SocketAddress addr = proxy.address();
    if (addr == null || !(addr instanceof InetSocketAddress)) {
      logger.println("Not using proxy, addr not InetSocketAddress.");
      return null;
    }

    final InetSocketAddress proxyAddr = (InetSocketAddress) addr;
    final String proxyHost = proxyAddr.getAddress().getHostAddress();
    final int proxyPort = proxyAddr.getPort();
    final ProxyConfigDetails proxyConfigDetails = new ProxyConfigDetails(proxyHost, proxyPort);

    final String proxyUser = proxyConfig.getUserName();
    if (proxyUser != null) {
      final String proxyPass = proxyConfig.getPassword();
      proxyConfigDetails.setUser(proxyUser);
      proxyConfigDetails.setPass(proxyPass);
    }
    logger.println(
        "Using proxy: " + proxyConfigDetails.getHost() + ":" + proxyConfigDetails.getPort());
    return proxyConfigDetails;
  }

  public void setConfig(final ViolationsToBitbucketServerConfig config) {
    this.config = config;
  }
}
