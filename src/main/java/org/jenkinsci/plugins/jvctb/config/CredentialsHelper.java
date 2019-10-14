package org.jenkinsci.plugins.jvctb.config;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import java.util.List;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

/* Read more about credentials api here: https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc */
public class CredentialsHelper {
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
  public static ListBoxModel doFillCredentialsIdItems(
      final Item item, final String credentialsId, final String uri) {
    final StandardListBoxModel result = new StandardListBoxModel();
    if (item == null) {
      if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
        return result.includeCurrentValue(credentialsId);
      }
    } else {
      if (!item.hasPermission(Item.EXTENDED_READ)
          && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
        return result.includeCurrentValue(credentialsId);
      }
    }
    return result //
        .includeEmptyValue() //
        .includeMatchingAs(
            item instanceof Queue.Task ? Tasks.getAuthenticationOf((Queue.Task) item) : ACL.SYSTEM,
            item,
            StandardCredentials.class,
            URIRequirementBuilder.fromUri(uri).build(),
            CredentialsMatchers.anyOf(
                CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                CredentialsMatchers.instanceOf(StringCredentials.class)))
        .includeCurrentValue(credentialsId);
  }

  public static FormValidation doCheckFillCredentialsId(
      final Item item, final String credentialsId, final String uri) {
    if (item == null) {
      if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
        return FormValidation.ok();
      }
    } else {
      if (!item.hasPermission(Item.EXTENDED_READ)
          && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
        return FormValidation.ok();
      }
    }
    if (isNullOrEmpty(credentialsId)) {
      return FormValidation.ok();
    }
    if (!findCredentials(item, credentialsId, uri).isPresent()) {
      return FormValidation.error("Cannot find currently selected credentials");
    }
    return FormValidation.ok();
  }

  public static Optional<StandardCredentials> findCredentials(
      final Item item, final String credentialsId, final String uri) {
    if (isNullOrEmpty(credentialsId)) {
      return absent();
    }
    return fromNullable(
        CredentialsMatchers.firstOrNull(
            CredentialsProvider.lookupCredentials(
                StandardCredentials.class,
                item,
                item instanceof Queue.Task
                    ? Tasks.getAuthenticationOf((Queue.Task) item)
                    : ACL.SYSTEM,
                URIRequirementBuilder.fromUri(uri).build()),
            CredentialsMatchers.allOf(
                CredentialsMatchers.withId(credentialsId),
                CredentialsMatchers.anyOf(
                    CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                    CredentialsMatchers.instanceOf(StringCredentials.class)))));
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
