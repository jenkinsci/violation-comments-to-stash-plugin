package org.jenkinsci.plugins.jvcts.stash;

import static java.util.logging.Level.SEVERE;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minidev.json.JSONArray;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

import com.google.common.annotations.VisibleForTesting;
import com.jayway.jsonpath.JsonPath;

public class JvctsStashClient {
 private static final Logger logger = Logger.getLogger(JvctsStashClient.class.getName());
 private static final String VERSION = "version";
 private static final String ID = "id";
 private static StashInvoker stashInvoker = new StashInvoker();

 @VisibleForTesting
 static void setStashInvoker(StashInvoker stashInvoker) {
  JvctsStashClient.stashInvoker = stashInvoker;
 }

 public static List<String> getChangedFileInPullRequest(ViolationsToStashConfig config) {
  String url = getStashPulLRequestBase(config) + "/changes";
  return invokeAndParse(config, url, "$..path.toString");
 }

 public static void commentPullRequest(ViolationsToStashConfig config, String changedFile, int line, String message) {
  String postContent = "{ \"text\": \"" + message.replaceAll("\"", "") + "\", \"anchor\": { \"line\": \"" + line
    + "\", \"lineType\": \"ADDED\", \"fileType\": \"TO\", \"path\": \"" + changedFile + "\" }}";
  stashInvoker.invokeUrl(config, getStashPulLRequestBase(config) + "/comments", StashInvoker.Method.POST, postContent);
 }

 @SuppressWarnings("rawtypes")
 public static void removeCommentsFromPullRequest(ViolationsToStashConfig config, String changedFile) {
  for (Object comment : getCommentsOnPullRequest(config, changedFile)) {
   if (toMap(toMap(comment).get("author")).get("name").equals(config.getStashUser())) {
    removeCommentFromPullRequest(config, (Map) comment);
   }
  }
 }

 @SuppressWarnings("unchecked")
 private static Map<String, Object> toMap(Object o) {
  return (Map<String, Object>) o;
 }

 private static JSONArray getCommentsOnPullRequest(ViolationsToStashConfig config, String changedFile) {
  String url = getStashPulLRequestBase(config) + "/comments?path=" + changedFile + "&limit=999999";
  return invokeAndParse(config, url, "$.values[*]");
 }

 private static <T> T invokeAndParse(ViolationsToStashConfig config, String url, String jsonPath) {
  String json = stashInvoker.invokeUrl(config, url, StashInvoker.Method.GET, null);
  try {
   return JsonPath.read(json, jsonPath);
  } catch (Exception e) {
   logger.log(SEVERE, url + ":\n" + json, e);
   return null;
  }
 }

 private static void removeCommentFromPullRequest(ViolationsToStashConfig config,
   @SuppressWarnings("rawtypes") Map comment) {
  stashInvoker.invokeUrl(config, getStashPulLRequestBase(config) + "/comments/" + comment.get(ID) + "?version="
    + comment.get(VERSION), StashInvoker.Method.DELETE, "");
 }

 private static String getStashPulLRequestBase(ViolationsToStashConfig config) {
  return config.getStashBaseUrl() + "/rest/api/1.0/projects/" + config.getStashProject() + "/repos/"
    + config.getStashRepo() + "/pull-requests/" + config.getStashPullRequestId();
 }
}
