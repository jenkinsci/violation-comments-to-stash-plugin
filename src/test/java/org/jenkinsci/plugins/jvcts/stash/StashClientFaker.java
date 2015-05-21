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

 public static final String COMMENTS_PMDFILE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/PMDFile.java&limit=999999 GET";
 public static final String COMMENTS_PMDANDCHECKSTYLE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/PMDAndCheckstyle.java&limit=999999 GET";
 public static final String COMMENTS_2_DELETE = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments/13781?version=1 DELETE";
 public static final String COMMENTS_1_DELETE = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments/13780?version=0 DELETE";
 public static final String COMMENTS_CHECKSTYLEFILE_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/code/CheckstyleFile.java&limit=999999 GET";
 public static final String COMMENTS_CHECKSTYLEFILE_REL_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=module/src/main/java/se/bjurr/code/CheckstyleFileRel.java&limit=999999 GET";
 public static final String COMMENTS_FINDBUGS_GET = "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=analyzer/Code.java&limit=999999 GET";
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

  fakeResponses.put(CHANGES_GET, readFile("pullrequestchanges_1_GET.json"));
  fakeResponses.put(COMMENTS_CHECKSTYLEFILE_GET, readFile("pullrequestcomments_GET.json"));
  fakeResponses.put(COMMENTS_CHECKSTYLEFILE_REL_GET, readFile("pullrequestcomments_GET_none.json"));
  fakeResponses.put(COMMENTS_1_DELETE, readFile("pullrequestcomments_GET.json"));
  fakeResponses.put(COMMENTS_2_DELETE, "");
  fakeResponses.put(COMMENTS_PMDANDCHECKSTYLE_GET, readFile("pullrequestcomments_GET_none.json"));
  fakeResponses.put(COMMENTS_PMDFILE_GET, readFile("pullrequestcomments_GET_none.json"));
  fakeResponses.put(COMMENTS_FINDBUGS_GET, readFile("pullrequestcomments_GET_none.json"));
  fakeResponses.put(readFile("checkstyle_checkstylefile_1.json"), "");
  fakeResponses.put(readFile("checkstyle_checkstylefile_2.json"), "");
  fakeResponses.put(readFile("checkstyle_checkstylefile_3_relativePath.json"), "");
  fakeResponses.put(readFile("checkstyle_pmdandcheckstyle.json"), "");
  fakeResponses.put(readFile("pmd_pmdandcheckstyle.json"), "");
  fakeResponses.put(readFile("pmd_pmdfile.json"), "");
  fakeResponses.put(readFile("findbugs_code.json"), "");
  fakeResponses
    .put(
      "http://stash.server/rest/api/1.0/projects/stashProject/repos/stashRepo/pull-requests/1/comments?path=src/se/main/java/bjurr/FileThatHasNoReportables.java&limit=999999 GET",
      readFile("pullrequestcomments_GET_none.json"));
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
}
