package org.jenkinsci.plugins.jvctb.config;

import java.util.List;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
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
import org.acegisecurity.Authentication;
import org.apache.commons.lang.StringUtils;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.allOf;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.firstOrNull;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static hudson.security.ACL.SYSTEM;

public class CredentialsHelper {
  public static ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    List<StandardUsernamePasswordCredentials> credentials = getAllCredentials();
    AbstractIdCredentialsListBoxModel<StandardUsernameListBoxModel, StandardUsernameCredentials>
        listBoxModel = new StandardUsernameListBoxModel().includeEmptyValue();
    for (StandardUsernamePasswordCredentials credential : credentials) {
      listBoxModel.with(credential);
    }
    return listBoxModel;
  }

  public static Optional<StandardUsernamePasswordCredentials> findCredentials(
      String usernamePasswordCredentialsId) {
    if (isNullOrEmpty(usernamePasswordCredentialsId)) {
      return absent();
    }

    return fromNullable(
        firstOrNull(getAllCredentials(), allOf(withId(usernamePasswordCredentialsId))));
  }

  public static List<StandardUsernamePasswordCredentials> getAllCredentials() {
    Class<StandardUsernamePasswordCredentials> type = StandardUsernamePasswordCredentials.class;
    ItemGroup<?> itemGroup = null;
    Authentication authentication = SYSTEM;
    DomainRequirement domainRequirement = null;

    return lookupCredentials(type, itemGroup, authentication, domainRequirement);
  }

  public static String migrateCredentials(String username, String password) {
    String credentialsId = null;
    DomainRequirement domainRequirement = null;
    List<StandardUsernamePasswordCredentials> credentials =
        CredentialsMatchers.filter(
            CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM,
                domainRequirement),
            CredentialsMatchers.withUsername(username));
    for (StandardUsernamePasswordCredentials cred : credentials) {
      if (StringUtils.equals(password, Secret.toString(cred.getPassword()))) {
        // If some credentials have the same username/password, use those.
        credentialsId = cred.getId();
        break;
      }
    }
    if (StringUtils.isBlank(credentialsId)) {
      // If we couldn't find any existing credentials,
      // create new credentials with the principal and secret and use it.
      StandardUsernamePasswordCredentials newCredentials =
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
