package org.jenkinsci.plugins.jvcts.config;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

public class ViolationsToBitbucketServerConfig implements Serializable {
 private static final long serialVersionUID = -2681014104552938569L;
 private List<ParserConfig> parsers = newArrayList();
 private String bitbucketServerBaseUrl;
 private String bitbucketServerUser;
 private String bitbucketServerPassword;
 private String bitbucketServerProject;
 private String bitbucketServerRepo;
 private String bitbucketServerPullRequestId;
 private String commitHash;

 public ViolationsToBitbucketServerConfig() {

 }

 public List<ParserConfig> getParserConfigs() {
  return parsers;
 }

 public void setParsers(List<ParserConfig> parsers) {
  this.parsers = parsers;
 }

 public void setBitbucketServerBaseUrl(String bitbucketServerBaseUrl) {
  if (bitbucketServerBaseUrl.endsWith("/")) {
   bitbucketServerBaseUrl = bitbucketServerBaseUrl.substring(0, bitbucketServerBaseUrl.length() - 1);
  }
  this.bitbucketServerBaseUrl = bitbucketServerBaseUrl;
 }

 public void setBitbucketServerProject(String bitbucketServerProject) {
  this.bitbucketServerProject = bitbucketServerProject;
 }

 public void setBitbucketServerRepo(String bitbucketServerRepo) {
  this.bitbucketServerRepo = bitbucketServerRepo;
 }

 public void setBitbucketServerPullRequestId(String bitbucketServerPullRequestId) {
  this.bitbucketServerPullRequestId = bitbucketServerPullRequestId;
 }

 public String getBitbucketServerBaseUrl() {
  return bitbucketServerBaseUrl;
 }

 public String getBitbucketServerProject() {
  return bitbucketServerProject;
 }

 public String getBitbucketServerRepo() {
  return bitbucketServerRepo;
 }

 public String getBitbucketServerPullRequestId() {
  return bitbucketServerPullRequestId;
 }

 public void setBitbucketServerPassword(String bitbucketServerPassword) {
  this.bitbucketServerPassword = bitbucketServerPassword;
 }

 public void setBitbucketServerUser(String bitbucketServerUser) {
  this.bitbucketServerUser = bitbucketServerUser;
 }

 public String getBitbucketServerPassword() {
  return bitbucketServerPassword;
 }

 public String getBitbucketServerUser() {
  return bitbucketServerUser;
 }

 public void setCommitHash(String commitHash) {
  this.commitHash = commitHash;
 }

 public String getCommitHash() {
  return commitHash;
 }
}
