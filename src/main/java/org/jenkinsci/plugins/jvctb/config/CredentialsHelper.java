package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.google.common.base.Optional;

import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;

public class CredentialsHelper {
  public static ListBoxModel doFillCredentialsIdItems(
      Item item, String credentialsId, String bitbucketServerUrl) {
    if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
      return new StandardListBoxModel().includeCurrentValue(credentialsId);
    }
    return new StandardListBoxModel() //
        .includeEmptyValue() //
        .includeMatchingAs(
            item instanceof Queue.Task ? Tasks.getAuthenticationOf((Queue.Task) item) : ACL.SYSTEM,
            item,
            StandardCredentials.class,
            URIRequirementBuilder.fromUri(bitbucketServerUrl).build(),
            CredentialsMatchers.anyOf(
                CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                CredentialsMatchers.instanceOf(StringCredentials.class)));
  }

  public static <C extends Credentials> Optional<C> findCredentials(
      Class<C> clazz, Job job, String credentialId, String bitbucketServerUrl) {
    if (isNullOrEmpty(credentialId)) {
      return absent();
    }
    return fromNullable(
        CredentialsMatchers.firstOrNull(
            CredentialsProvider.lookupCredentials(
                clazz, job, ACL.SYSTEM, URIRequirementBuilder.fromUri(bitbucketServerUrl).build()),
            CredentialsMatchers.allOf(
                CredentialsMatchers.withId(credentialId), CredentialsMatchers.instanceOf(clazz))));
  }

  public static String migrateCredentials(final String username, final String password) {
    String credentialsId = null;
    final DomainRequirement domainRequirement = null;
    final List<StandardUsernamePasswordCredentials> credentials =
        CredentialsMatchers.filter(
            CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM,
                domainRequirement),
            CredentialsMatchers.withUsername(username));
    for (final StandardUsernamePasswordCredentials cred : credentials) {
      if (StringUtils.equals(password, Secret.toString(cred.getPassword()))) {
        // If some credentials have the same username/password, use those.
        credentialsId = cred.getId();
        break;
      }
    }
    if (StringUtils.isBlank(credentialsId)) {
      // If we couldn't find any existing credentials,
      // create new credentials with the principal and secret and use it.
      final StandardUsernamePasswordCredentials newCredentials =
          new UsernamePasswordCredentialsImpl(
              CredentialsScope.SYSTEM,
              null,
              "Migrated by Violation comments to bitbucket plugin",
              username,
              password);
      SystemCredentialsProvider.getInstance().getCredentials().add(newCredentials);
      credentialsId = newCredentials.getId();
    }
    if (StringUtils.isNotEmpty(credentialsId)) {
      return credentialsId;
    } else {
      return null;
    }
  }
}
