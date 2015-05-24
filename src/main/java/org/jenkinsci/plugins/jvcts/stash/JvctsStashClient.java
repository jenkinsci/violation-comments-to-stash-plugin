package org.jenkinsci.plugins.jvcts.stash;

import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvcts.JvctsLogger.doLog;
import hudson.model.BuildListener;

import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

import com.google.common.annotations.VisibleForTesting;
import com.jayway.jsonpath.JsonPath;

public class JvctsStashClient {
 private static final String VERSION = "version";
 private static final String ID = "id";
 private static StashInvoker stashInvoker = new StashInvoker();
 private final ViolationsToStashConfig config;
 private final BuildListener listener;

 public JvctsStashClient(ViolationsToStashConfig config, BuildListener listener) {
  this.config = config;
  this.listener = listener;
 }

 @VisibleForTesting
 static void setStashInvoker(StashInvoker stashInvoker) {
  JvctsStashClient.stashInvoker = stashInvoker;
 }

 public List<String> getChangedFileInPullRequest() {
  return invokeAndParse(getStashPulLRequestBase() + "/changes?limit=999999", "$..path.toString");
 }

 public void commentPullRequest(String changedFile, int line, String message) {
  String postContent = "{ \"text\": \"" + message.replaceAll("\"", "") + "\", \"anchor\": { \"line\": \"" + line
    + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + changedFile + "\" }}";
  stashInvoker.invokeUrl(config, getStashPulLRequestBase() + "/comments", StashInvoker.Method.POST, postContent,
    listener);
 }

 @SuppressWarnings("rawtypes")
 public void removeCommentsFromPullRequest(String changedFile) {
  for (Object comment : getCommentsOnPullRequest(changedFile)) {
   if (toMap(toMap(comment).get("author")).get("name").equals(config.getStashUser())) {
    removeCommentFromPullRequest((Map) comment);
   }
  }
 }

 @SuppressWarnings("unchecked")
 private Map<String, Object> toMap(Object o) {
  return (Map<String, Object>) o;
 }

 private JSONArray getCommentsOnPullRequest(String changedFile) {
  return invokeAndParse(getStashPulLRequestBase() + "/comments?path=" + changedFile + "&limit=999999", "$.values[*]");
 }

 private <T> T invokeAndParse(String url, String jsonPath) {
  String json = stashInvoker.invokeUrl(config, url, StashInvoker.Method.GET, null, listener);
  try {
   return JsonPath.read(json, jsonPath);
  } catch (Exception e) {
   doLog(listener, SEVERE, url + ":\n" + json, e);
   return null;
  }
 }

 private void removeCommentFromPullRequest(@SuppressWarnings("rawtypes") Map comment) {
  stashInvoker.invokeUrl(config,
    getStashPulLRequestBase() + "/comments/" + comment.get(ID) + "?version=" + comment.get(VERSION),
    StashInvoker.Method.DELETE, "", listener);
 }

 private String getStashPulLRequestBase() {
  return config.getStashBaseUrl() + "/rest/api/1.0/projects/" + config.getStashProject() + "/repos/"
    + config.getStashRepo() + "/pull-requests/" + config.getStashPullRequestId();
 }

 private String getStashCommitsBase() {
  return config.getStashBaseUrl() + "/rest/api/1.0/projects/" + config.getStashProject() + "/repos/"
    + config.getStashRepo() + "/commits/" + config.getCommitHash();
 }

 public List<String> getChangedFileInCommit() {
  return invokeAndParse(getStashCommitsBase() + "/changes?limit=999999", "$..path.toString");
 }

 private JSONArray getCommentsOnCommit(String changedFile) {
  return invokeAndParse(getStashCommitsBase() + "/comments?path=" + changedFile + "&limit=999999", "$.values[*]");
 }

 @SuppressWarnings("rawtypes")
 public void removeCommentsCommit(String changedFile) {
  for (Object comment : getCommentsOnCommit(changedFile)) {
   if (toMap(toMap(comment).get("author")).get("name").equals(config.getStashUser())) {
    removeCommentFromCommit((Map) comment);
   }
  }
 }

 private void removeCommentFromCommit(@SuppressWarnings("rawtypes") Map comment) {
  stashInvoker.invokeUrl(config,
    getStashCommitsBase() + "/comments/" + comment.get(ID) + "?version=" + comment.get(VERSION),
    StashInvoker.Method.DELETE, "", listener);
 }

 public void commentCommit(String changedFile, int line, String message) {
  String postContent = "{ \"text\": \"" + message.replaceAll("\"", "") + "\", \"anchor\": { \"line\": \"" + line
    + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + changedFile + "\" }}";
  stashInvoker.invokeUrl(config, getStashCommitsBase() + "/comments", StashInvoker.Method.POST, postContent, listener);
 }
}
