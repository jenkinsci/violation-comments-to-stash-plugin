package org.jenkinsci.plugins.jvcts.config;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class ViolationsToStashConfig {
 private List<ParserConfig> parsers = newArrayList();
 private String stashBaseUrl;
 private String stashUser;
 private String stashPassword;
 private String stashProject;
 private String stashRepo;
 private String stashPullRequestId;

 public ViolationsToStashConfig() {

 }

 public List<ParserConfig> getParserConfigs() {
  return parsers;
 }

 public void setParsers(List<ParserConfig> parsers) {
  this.parsers = parsers;
 }

 public void setStashBaseUrl(String stashBaseUrl) {
  if (stashBaseUrl.endsWith("/")) {
   stashBaseUrl = stashBaseUrl.substring(0, stashBaseUrl.length() - 1);
  }
  this.stashBaseUrl = stashBaseUrl;
 }

 public void setStashProject(String stashProject) {
  this.stashProject = stashProject;
 }

 public void setStashRepo(String stashRepo) {
  this.stashRepo = stashRepo;
 }

 public void setStashPullRequestId(String stashPullRequestId) {
  this.stashPullRequestId = stashPullRequestId;
 }

 public String getStashBaseUrl() {
  return stashBaseUrl;
 }

 public String getStashProject() {
  return stashProject;
 }

 public String getStashRepo() {
  return stashRepo;
 }

 public String getStashPullRequestId() {
  return stashPullRequestId;
 }

 public void setStashPassword(String stashPassword) {
  this.stashPassword = stashPassword;
 }

 public void setStashUser(String stashUser) {
  this.stashUser = stashUser;
 }

 public String getStashPassword() {
  return stashPassword;
 }

 public String getStashUser() {
  return stashUser;
 }
}
