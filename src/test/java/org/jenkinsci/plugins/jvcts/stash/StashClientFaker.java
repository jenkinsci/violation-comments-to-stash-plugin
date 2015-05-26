package org.jenkinsci.plugins.jvcts.stash;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Resources.getResource;
import static org.jenkinsci.plugins.jvcts.stash.JvctsStashClient.setStashInvoker;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

import com.google.common.io.Resources;

public class StashClientFaker {

 public static final String NO_REPORTABLES = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=src/se/main/java/bjurr/FileThatHasNoReportables.java&limit=999999 GET";
 public static final String COMMENTS_PMDFILE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/PMDFile.java&limit=999999 GET";
 public static final String COMMENTS_PMDANDCHECKSTYLE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/PMDAndCheckstyle.java&limit=999999 GET";
 public static final String COMMENTS_2_DELETE = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments/13781?version=1 DELETE";
 public static final String COMMENTS_1_DELETE = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments/13780?version=0 DELETE";
 public static final String COMMENTS_CHECKSTYLEFILE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/code/CheckstyleFile.java&limit=999999 GET";
 public static final String COMMENTS_CHECKSTYLEFILE_REL_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/code/CheckstyleFileRel.java&limit=999999 GET";
 public static final String COMMENTS_FINDBUGS_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/se/bjurr/analyzer/Code.java&limit=999999 GET";
 public static final String CHANGES_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/changes?limit=999999 GET";
 private static Map<String, String> fakeResponses;
 private static List<String> requestsSentToStash;

 private StashClientFaker() {
 }

 public static void fakeStashClient() throws IOException {
  requestsSentToStash = newArrayList();
  fakeResponses = newHashMap();
  setStashInvoker(new StashInvoker() {
   @Override
   public String invokeUrl(ViolationsToStashConfig config, String url, Method method, String postContent,
     BuildListener listener) {
    String key = createFakeKey(url, method.name(), postContent);
    if (!fakeResponses.containsKey(key)) {
     throw new RuntimeException("\"" + key + "\" not faked!");
    }
    requestsSentToStash.add(key);
    return fakeResponses.get(key);
   }
  });

  fake(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  fake(COMMENTS_CHECKSTYLEFILE_GET, readFile("pullrequestcomments_GET.json"));
  fake(COMMENTS_CHECKSTYLEFILE_REL_GET, readFile("pullrequestcomments_GET_none.json"));
  fake(COMMENTS_1_DELETE, readFile("pullrequestcomments_GET.json"));
  fake(COMMENTS_2_DELETE, "");
  fake(COMMENTS_PMDANDCHECKSTYLE_GET, readFile("pullrequestcomments_GET_none.json"));
  fake(COMMENTS_PMDFILE_GET, readFile("pullrequestcomments_GET_none.json"));
  fake(COMMENTS_FINDBUGS_GET, readFile("pullrequestcomments_GET_none.json"));
  fake(NO_REPORTABLES, readFile("pullrequestcomments_GET_none.json"));
  fake(readFile("checkstyle_checkstylefile_1.json"), "");
  fake(readFile("checkstyle_checkstylefile_2.json"), "");
  fake(readFile("checkstyle_checkstylefile_3_relativePath.json"), "");
  fake(readFile("checkstyle_checkstylefile_3.json"), "");
  fake(readFile("checkstyle_pmdandcheckstyle.json"), "");
  fake(readFile("pmd_pmdandcheckstyle.json"), "");
  fake(readFile("pmd_pmdfile.json"), "");
  fake(readFile("findbugs_code.json"), "");
 }

 private static void fake(String request, String response) {
  fakeResponses.put(request, response);
  fakeResponses.put(prToCommit(request), prToCommit(response));
 }

 public static String readFile(String filename) throws IOException {
  return Resources.toString(getResource(filename), UTF_8);
 }

 private static String createFakeKey(String url, String method, String postContent) {
  return (url + " " + method + " " + nullToEmpty(postContent)).trim();
 }

 public static List<String> getRequestsSentToStash() {
  return requestsSentToStash;
 }

 public static String prToCommit(String string) {
  return string.replaceAll("pull-requests", "commits");
 }
}
