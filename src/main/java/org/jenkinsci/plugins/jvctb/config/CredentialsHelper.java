package org.jenkinsci.plugins.jvctb.config;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.allOf;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.firstOrNull;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static hudson.security.ACL.SYSTEM;
import hudson.model.ItemGroup;
import hudson.util.ListBoxModel;

import java.util.List;

import org.acegisecurity.Authentication;

import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.google.common.base.Optional;

public class CredentialsHelper {
 public static ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
  List<StandardUsernamePasswordCredentials> credentials = getAllCredentials();
  AbstractIdCredentialsListBoxModel<StandardUsernameListBoxModel, StandardUsernameCredentials> listBoxModel = new StandardUsernameListBoxModel()
    .includeEmptyValue();
  for (StandardUsernamePasswordCredentials credential : credentials) {
   listBoxModel.with(credential);
  }
  return listBoxModel;
 }

 public static Optional<StandardUsernamePasswordCredentials> findCredentials(String usernamePasswordCredentialsId) {
  if (isNullOrEmpty(usernamePasswordCredentialsId)) {
   return absent();
  }

  return fromNullable(firstOrNull(getAllCredentials(), allOf(withId(usernamePasswordCredentialsId))));
 }

 public static List<StandardUsernamePasswordCredentials> getAllCredentials() {
  Class<StandardUsernamePasswordCredentials> type = StandardUsernamePasswordCredentials.class;
  ItemGroup<?> itemGroup = null;
  Authentication authentication = SYSTEM;
  DomainRequirement domainRequirement = null;

  return lookupCredentials(type, itemGroup, authentication, domainRequirement);
 }

}
