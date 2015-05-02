package org.jenkinsci.plugins.jvcts.stash;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvcts.JvctsLogger.doLog;
import hudson.model.BuildListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;

import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

public class StashInvoker {

 public enum Method {
  POST, DELETE, GET
 }

 public String invokeUrl(ViolationsToStashConfig config, String url, Method method, @Nullable String postContent,
   BuildListener listener) {
  doLog(listener, FINE, "Invoking: " + method.name() + " " + url + " Posting: " + postContent);
  HttpURLConnection conn = null;
  OutputStream output = null;
  BufferedReader reader = null;
  try {
   conn = (HttpURLConnection) new URL(url).openConnection();
   String userAndPass = config.getStashUser() + ":" + config.getStashPassword();
   String authString = new String(DatatypeConverter.printBase64Binary(userAndPass.getBytes()));
   conn.setRequestProperty("Authorization", "Basic " + authString);
   conn.setRequestMethod(method.name());
   String charset = "UTF-8";
   conn.setDoOutput(true);
   conn.setRequestProperty("Content-Type", "application/json");
   conn.setRequestProperty("Accept", "application/json");
   conn.connect();
   if (!isNullOrEmpty(postContent)) {
    output = conn.getOutputStream();
    output.write(postContent.getBytes(charset));
   }
   reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
   StringBuilder stringBuilder = new StringBuilder();
   String line = null;
   while ((line = reader.readLine()) != null) {
    stringBuilder.append(line + "\n");
   }
   String json = groovy.json.JsonOutput.prettyPrint(stringBuilder.toString());
   doLog(listener, FINE, json);
   return json;
  } catch (Exception e) {
   doLog(listener, SEVERE, url, e);
   return "";
  } finally {
   try {
    conn.disconnect();
    reader.close();
    if (output != null) {
     output.close();
    }
   } catch (IOException e) {
    doLog(listener, SEVERE, url, e);
   }
  }
 }
}
