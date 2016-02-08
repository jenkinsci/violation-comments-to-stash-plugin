package org.jenkinsci.plugins.jvcts.bitbucketserver;

import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvcts.JvctsLogger.doLog;
import hudson.model.BuildListener;

import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;

import org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfig;

import com.google.common.annotations.VisibleForTesting;
import com.jayway.jsonpath.JsonPath;

public class JvctsBitbucketServerClient {
 private static final String VERSION = "version";
 private static final String ID = "id";
 private static BitbucketServerInvoker bitbucketServerInvoker = new BitbucketServerInvoker();
 private final ViolationsToBitbucketServerConfig config;
 private final BuildListener listener;

 public JvctsBitbucketServerClient(ViolationsToBitbucketServerConfig config, BuildListener listener) {
  this.config = config;
  this.listener = listener;
 }

 @VisibleForTesting
 public static void setBitbucketServerInvoker(BitbucketServerInvoker bitbucketServerInvoker) {
  JvctsBitbucketServerClient.bitbucketServerInvoker = bitbucketServerInvoker;
 }

 public List<String> getChangedFileInPullRequest() {
  return invokeAndParse(getBitbucketServerPulLRequestBase() + "/changes?limit=999999", "$..path.toString");
 }

 public void commentPullRequest(String changedFile, int line, String message) {
  String postContent = "{ \"text\": \"" + message.replaceAll("\"", "") + "\", \"anchor\": { \"line\": \"" + line
    + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + changedFile + "\" }}";
  bitbucketServerInvoker.invokeUrl(config, getBitbucketServerPulLRequestBase() + "/comments",
    BitbucketServerInvoker.Method.POST, postContent, listener);
 }

 @SuppressWarnings("rawtypes")
 public void removeCommentsFromPullRequest(String changedFile) {
  for (Object comment : getCommentsOnPullRequest(changedFile)) {
   if (toMap(toMap(comment).get("author")).get("name").equals(config.getBitbucketServerUser())) {
    removeCommentFromPullRequest((Map) comment);
   }
  }
 }

 @SuppressWarnings("unchecked")
 private Map<String, Object> toMap(Object o) {
  return (Map<String, Object>) o;
 }

 private JSONArray getCommentsOnPullRequest(String changedFile) {
  return invokeAndParse(getBitbucketServerPulLRequestBase() + "/comments?path=" + changedFile + "&limit=999999",
    "$.values[*]");
 }

 private <T> T invokeAndParse(String url, String jsonPath) {
  String json = bitbucketServerInvoker.invokeUrl(config, url, BitbucketServerInvoker.Method.GET, null, listener);
  try {
   return JsonPath.read(json, jsonPath);
  } catch (Exception e) {
   doLog(listener, SEVERE, url + ":\n" + json, e);
   return null;
  }
 }

 private void removeCommentFromPullRequest(@SuppressWarnings("rawtypes") Map comment) {
  bitbucketServerInvoker.invokeUrl(config, getBitbucketServerPulLRequestBase() + "/comments/" + comment.get(ID)
    + "?version=" + comment.get(VERSION), BitbucketServerInvoker.Method.DELETE, "", listener);
 }

 private String getBitbucketServerPulLRequestBase() {
  return config.getBitbucketServerBaseUrl() + "/rest/api/1.0/projects/" + config.getBitbucketServerProject()
    + "/repos/" + config.getBitbucketServerRepo() + "/pull-requests/" + config.getBitbucketServerPullRequestId();
 }

 private String getBitbucketServerCommitsBase() {
  return config.getBitbucketServerBaseUrl() + "/rest/api/1.0/projects/" + config.getBitbucketServerProject()
    + "/repos/" + config.getBitbucketServerRepo() + "/commits/" + config.getCommitHash();
 }

 public List<String> getChangedFileInCommit() {
  return invokeAndParse(getBitbucketServerCommitsBase() + "/changes?limit=999999", "$..path.toString");
 }

 private JSONArray getCommentsOnCommit(String changedFile) {
  return invokeAndParse(getBitbucketServerCommitsBase() + "/comments?path=" + changedFile + "&limit=999999", "$.values[*]");
 }

 @SuppressWarnings("rawtypes")
 public void removeCommentsCommit(String changedFile) {
  for (Object comment : getCommentsOnCommit(changedFile)) {
   if (toMap(toMap(comment).get("author")).get("name").equals(config.getBitbucketServerUser())) {
    removeCommentFromCommit((Map) comment);
   }
  }
 }

 private void removeCommentFromCommit(@SuppressWarnings("rawtypes") Map comment) {
  bitbucketServerInvoker.invokeUrl(config, getBitbucketServerCommitsBase() + "/comments/" + comment.get(ID) + "?version="
    + comment.get(VERSION), BitbucketServerInvoker.Method.DELETE, "", listener);
 }

 public void commentCommit(String changedFile, int line, String message) {
  String postContent = "{ \"text\": \"" + message.replaceAll("\"", "") + "\", \"anchor\": { \"line\": \"" + line
    + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + changedFile + "\" }}";
  bitbucketServerInvoker.invokeUrl(config, getBitbucketServerCommitsBase() + "/comments", BitbucketServerInvoker.Method.POST,
    postContent, listener);
 }
}
