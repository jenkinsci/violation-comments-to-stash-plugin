package org.jenkinsci.plugins.jvctb;

import java.io.Serializable;

import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMHead;
import com.google.common.base.Optional;

import hudson.model.Job;
import hudson.model.Run;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSource;

public class ViolationsToBitbucketServerJobConfiguration implements Serializable {

  public static Optional<ViolationsToBitbucketServerConfig> getConfig(final Run<?, ?> buildRun) {
    final Job<?, ?> buildJob = buildRun.getParent();

    final SCMSource jobScmSource = SCMSource.SourceByItem.findSource(buildJob);
    if (!(jobScmSource instanceof BitbucketSCMSource)) {
      return Optional.absent();
    }

    final SCMHead jobScmHead = SCMHead.HeadByItem.findHead(buildJob);
    if (!(jobScmHead instanceof PullRequestSCMHead)) {
      return Optional.absent();
    }

    final BitbucketSCMSource bitbucketScmSource = (BitbucketSCMSource) jobScmSource;
    final PullRequestSCMHead pullRequestScmHead = (PullRequestSCMHead) jobScmHead;

    final ViolationsToBitbucketServerConfig config = new ViolationsToBitbucketServerConfig();
    config.setBitbucketServerUrl(bitbucketScmSource.getServerUrl());
    config.setCredentialsId(bitbucketScmSource.getCredentialsId());
    config.setProjectKey(pullRequestScmHead.getRepoOwner());
    config.setRepoSlug(pullRequestScmHead.getRepository());
    config.setPullRequestId(pullRequestScmHead.getId());

    return Optional.of(config);
  }
}
