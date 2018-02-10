package org.jenkinsci.plugins.jvctb.config;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.allOf;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.firstOrNull;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static hudson.security.ACL.SYSTEM;

import java.util.List;

import org.acegisecurity.Authentication;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.google.common.base.Optional;

import hudson.model.ItemGroup;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;

public class CredentialsHelper {
  public static ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    final List<StandardUsernamePasswordCredentials> credentials = getAllCredentials();
    final AbstractIdCredentialsListBoxModel<
            StandardUsernameListBoxModel, StandardUsernameCredentials>
        listBoxModel = new StandardUsernameListBoxModel().includeEmptyValue();
    for (final StandardUsernamePasswordCredentials credential : credentials) {
      listBoxModel.with(credential);
    }
    return listBoxModel;
  }

  public static ListBoxModel doFillPersonalAccessTokenIdItems() {
    final List<StringCredentials> credentials = getAllCredentials(StringCredentials.class);
    final ListBoxModel listBoxModel =
        new StandardListBoxModel() //
            .includeEmptyValue() //
            .withAll(credentials);
    return listBoxModel;
  }

  public static Optional<StandardUsernamePasswordCredentials> findCredentials(
      final String usernamePasswordCredentialsId) {
    if (isNullOrEmpty(usernamePasswordCredentialsId)) {
      return absent();
    }

    return fromNullable(
        firstOrNull(getAllCredentials(), allOf(withId(usernamePasswordCredentialsId))));
  }

  public static Optional<StringCredentials> findStringCredentials(final String stringCredentials) {
    if (isNullOrEmpty(stringCredentials)) {
      return absent();
    }

    return fromNullable(
        firstOrNull(getAllCredentials(StringCredentials.class), allOf(withId(stringCredentials))));
  }

  private static <C extends Credentials> List<C> getAllCredentials(final Class<C> type) {
    final ItemGroup<?> itemGroup = null;
    final Authentication authentication = SYSTEM;
    final DomainRequirement domainRequirement = null;

    return lookupCredentials(type, itemGroup, authentication, domainRequirement);
  }

  public static List<StandardUsernamePasswordCredentials> getAllCredentials() {
    final Class<StandardUsernamePasswordCredentials> type =
        StandardUsernamePasswordCredentials.class;
    final ItemGroup<?> itemGroup = null;
    final Authentication authentication = SYSTEM;
    final DomainRequirement domainRequirement = null;

    return lookupCredentials(type, itemGroup, authentication, domainRequirement);
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
