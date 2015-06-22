package org.jenkinsci.plugins.jvcts.test;

import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_24;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.lang.System.getProperty;
import static java.lang.Thread.sleep;
import static java.util.logging.Level.OFF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.name;
import static org.openqa.selenium.By.xpath;

import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ConfigurationWebTest {
 private static final String CHECKSTYLE_PATTERN = "descriptor.config.parserConfigs[0].pattern";
 private static final String PATH_PREFIX = "descriptor.config.parserConfigs[0].pathPrefix";
 private static final String CODENARC_PATTERN = "descriptor.config.parserConfigs[1].pattern";
 private static final String CPD_PATTERN = "descriptor.config.parserConfigs[2].pattern";
 private static final String CPPLINT_PATTERN = "descriptor.config.parserConfigs[3].pattern";
 private static final String FINDBUGS_PATTERN = "descriptor.config.parserConfigs[4].pattern";
 private static final String FXCOP_PATTERN = "descriptor.config.parserConfigs[5].pattern";
 private static final String GENDARME_PATTERN = "descriptor.config.parserConfigs[6].pattern";
 private static final String JCEREPORT_PATTERN = "descriptor.config.parserConfigs[7].pattern";
 private static final String PEP8_PATTERN = "descriptor.config.parserConfigs[8].pattern";
 private static final String PERLCRITIC_PATTERN = "descriptor.config.parserConfigs[9].pattern";
 private static final String PMD_PATTERN = "descriptor.config.parserConfigs[10].pattern";
 private static final String PYLINT_PATTERN = "descriptor.config.parserConfigs[11].pattern";
 private static final String SIMIAN_PATTERN = "descriptor.config.parserConfigs[12].pattern";
 private static final String STYLECOP_PATTERN = "descriptor.config.parserConfigs[13].pattern";
 private static final String JSLINT_PATTERN = "descriptor.config.parserConfigs[14].pattern";
 private static final String CSSLINT_PATTERN = "descriptor.config.parserConfigs[15].pattern";
 private static final String STASH_PULL_REQUEST_ID = "stashPullRequestId";
 private static final String COMMIT_HASH = "commitHash";
 private static final String STASH_REPO = "stashRepo";
 private static final String STASH_PROJECT = "stashProject";
 private static final String STASH_BASE_URL = "stashBaseUrl";
 private static final String STASH_USER = "stashUser";
 private static final String STASH_PASSWORD = "stashPassword";
 private static final String HTTP_LOCALHOST_8456 = "http://localhost:8456";
 private static final String PROP_JENKINS_URL = "jenkins";
 private static final String PROP_HEADLESS = "headless";
 private static final Logger logger = Logger.getLogger(ConfigurationWebTest.class.getName());
 private static final String TEST_JOB_NAME = "testJobb";
 private WebDriver webDriver;

 static {
  LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
  java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(OFF);
  java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(OFF);
 }

 private String getJenkinsBaseUrl() {
  return firstNonNull(getProperty(PROP_JENKINS_URL), "http://localhost:8080/jenkins");
 }

 private boolean isHeadless() {
  return !firstNonNull(getProperty(PROP_HEADLESS), "false").equals("false");
 }

 @Before
 public void before() throws InterruptedException {
  if (isHeadless()) {
   HtmlUnitDriver webDriverHtmlUnit = new HtmlUnitDriver(FIREFOX_24);
   webDriverHtmlUnit.setJavascriptEnabled(true);
   this.webDriver = webDriverHtmlUnit;
  } else {
   webDriver = new FirefoxDriver();
  }
  waitForJenkinsToStart();
 }

 @After
 public void after() {
  deleteJob();
  webDriver.quit();
 }

 @Test
 public void testPluginConfiguration() throws InterruptedException {
  /**
   * Create new job and enable plugin on it.
   */
  createJob();
  enablePlugin();

  /**
   * Enter valid details, start job and validate config output.
   */
  enterDetails();
  removeOnBeforeUnload();
  saveJob();
  startJob();
  waitForJob(1);
  String consoleText = consoleText(1);
  assertTrue(consoleText, consoleText.contains("stashBaseUrl: " + HTTP_LOCALHOST_8456));
  assertTrue(consoleText, consoleText.contains("stashProject: ABC"));
  assertTrue(consoleText, consoleText.contains("stashPullRequestId: 100"));
  assertTrue(consoleText, consoleText.contains("commitHash: abcd1234"));
  assertTrue(consoleText, consoleText.contains("stashUser: theuser"));
  assertTrue(consoleText, consoleText.contains("stashRepo: a_repo"));
  assertTrue(consoleText, consoleText.contains("checkstyle: **/checkstyle-report.xml"));
  assertTrue(consoleText, consoleText.contains("checkstyle pathPrefix: pathPrefix"));
  assertTrue(consoleText, consoleText.contains("pmd: **/pmd-report.xml"));
  assertTrue(consoleText, consoleText.contains("jslint: **/jslint-report.xml"));
  assertTrue(consoleText, consoleText.contains("csslint: **/csslint-report.xml"));
  assertTrue(consoleText, consoleText.contains("codenarc: **/codenarc-report.xml"));
  assertTrue(consoleText, consoleText.contains("cpd: **/cpd-report.xml"));
  assertTrue(consoleText, consoleText.contains("cpplint: **/cpplint-report.xml"));
  assertTrue(consoleText, consoleText.contains("findbugs: **/findbugs-report.xml"));
  assertTrue(consoleText, consoleText.contains("fxcop: **/fxcop-report.xml"));
  assertTrue(consoleText, consoleText.contains("gendarme: **/gendarme-report.xml"));
  assertTrue(consoleText, consoleText.contains("jcreport: **/jcreport-report.xml"));
  assertTrue(consoleText, consoleText.contains("pep8: **/pep8-report.xml"));
  assertTrue(consoleText, consoleText.contains("perlcritic: **/perlcritic-report.xml"));
  assertTrue(consoleText, consoleText.contains("pylint: **/pylint-report.xml"));
  assertTrue(consoleText, consoleText.contains("simian: **/simian-report.xml"));
  assertTrue(consoleText, consoleText.contains("stylecop: **/stylecop-report.xml"));

  /**
   * Enter new valid details, start job and check that config output gets
   * updated.
   */
  goToConfig();
  webDriver.findElement(name(STASH_BASE_URL)).clear();
  webDriver.findElement(name(STASH_USER)).clear();
  webDriver.findElement(name(STASH_PASSWORD)).clear();
  webDriver.findElement(name(STASH_PROJECT)).clear();
  webDriver.findElement(name(STASH_REPO)).clear();
  webDriver.findElement(name(STASH_PULL_REQUEST_ID)).clear();
  webDriver.findElement(name(COMMIT_HASH)).clear();
  webDriver.findElement(name(PATH_PREFIX)).clear();
  webDriver.findElement(name(CHECKSTYLE_PATTERN)).clear();
  webDriver.findElement(name(CODENARC_PATTERN)).clear();
  webDriver.findElement(name(CPD_PATTERN)).clear();
  webDriver.findElement(name(CPPLINT_PATTERN)).clear();
  webDriver.findElement(name(FINDBUGS_PATTERN)).clear();
  webDriver.findElement(name(FXCOP_PATTERN)).clear();
  webDriver.findElement(name(GENDARME_PATTERN)).clear();
  webDriver.findElement(name(JCEREPORT_PATTERN)).clear();
  webDriver.findElement(name(PEP8_PATTERN)).clear();
  webDriver.findElement(name(PERLCRITIC_PATTERN)).clear();
  webDriver.findElement(name(PMD_PATTERN)).clear();
  webDriver.findElement(name(PYLINT_PATTERN)).clear();
  webDriver.findElement(name(SIMIAN_PATTERN)).clear();
  webDriver.findElement(name(STYLECOP_PATTERN)).clear();
  webDriver.findElement(name(JSLINT_PATTERN)).clear();
  webDriver.findElement(name(CSSLINT_PATTERN)).clear();
  webDriver.findElement(name(STASH_BASE_URL)).sendKeys("http://changed.com");
  webDriver.findElement(name(STASH_USER)).sendKeys("theotheruser");
  webDriver.findElement(name(STASH_PASSWORD)).sendKeys("theotherpassword");
  webDriver.findElement(name(STASH_PROJECT)).sendKeys("DEF");
  webDriver.findElement(name(STASH_REPO)).sendKeys("a_repo2");
  webDriver.findElement(name(STASH_PULL_REQUEST_ID)).sendKeys("101");
  webDriver.findElement(name(COMMIT_HASH)).sendKeys("abcd12345");
  webDriver.findElement(name(CHECKSTYLE_PATTERN)).sendKeys("**/new-checkstyle-report.xml");
  webDriver.findElement(name(PMD_PATTERN)).sendKeys("**/new-pmd-report.xml");
  webDriver.findElement(name(JSLINT_PATTERN)).sendKeys("**/new-jslint-report.xml");
  webDriver.findElement(name(CSSLINT_PATTERN)).sendKeys("**/new-csslint-report.xml");
  webDriver.findElement(name(CODENARC_PATTERN)).sendKeys("**/new-codenarc-report.xml");
  webDriver.findElement(name(CPD_PATTERN)).sendKeys("**/new-cpd-report.xml");
  webDriver.findElement(name(CPPLINT_PATTERN)).sendKeys("**/new-cpplint-report.xml");
  webDriver.findElement(name(FINDBUGS_PATTERN)).sendKeys("**/new-findbugs-report.xml");
  webDriver.findElement(name(FXCOP_PATTERN)).sendKeys("**/new-fxcop-report.xml");
  webDriver.findElement(name(GENDARME_PATTERN)).sendKeys("**/new-gendarme-report.xml");
  webDriver.findElement(name(JCEREPORT_PATTERN)).sendKeys("**/new-jcreport-report.xml");
  webDriver.findElement(name(PEP8_PATTERN)).sendKeys("**/new-pep8-report.xml");
  webDriver.findElement(name(PERLCRITIC_PATTERN)).sendKeys("**/new-perlcritic-report.xml");
  webDriver.findElement(name(PYLINT_PATTERN)).sendKeys("**/new-pylint-report.xml");
  webDriver.findElement(name(SIMIAN_PATTERN)).sendKeys("**/new-simian-report.xml");
  webDriver.findElement(name(STYLECOP_PATTERN)).sendKeys("**/new-stylecop-report.xml");
  removeOnBeforeUnload();
  saveJob();
  startJob();
  waitForJob(2);
  consoleText = consoleText(2);
  assertTrue(consoleText, consoleText.contains("stashBaseUrl: http://changed.com"));
  assertTrue(consoleText, consoleText.contains("stashProject: DEF"));
  assertTrue(consoleText, consoleText.contains("stashPullRequestId: 101"));
  assertTrue(consoleText, consoleText.contains("commitHash: abcd12345"));
  assertTrue(consoleText, consoleText.contains("stashRepo: a_repo2"));
  assertTrue(consoleText, consoleText.contains("checkstyle: **/new-checkstyle-report.xml"));
  assertTrue(consoleText, consoleText.contains("pmd: **/new-pmd-report.xml"));
  assertTrue(consoleText, consoleText.contains("jslint: **/new-jslint-report.xml"));
  assertTrue(consoleText, consoleText.contains("csslint: **/new-csslint-report.xml"));
  assertTrue(consoleText, consoleText.contains("codenarc: **/new-codenarc-report.xml"));
  assertTrue(consoleText, consoleText.contains("cpd: **/new-cpd-report.xml"));
  assertTrue(consoleText, consoleText.contains("cpplint: **/new-cpplint-report.xml"));
  assertTrue(consoleText, consoleText.contains("findbugs: **/new-findbugs-report.xml"));
  assertTrue(consoleText, consoleText.contains("fxcop: **/new-fxcop-report.xml"));
  assertTrue(consoleText, consoleText.contains("gendarme: **/new-gendarme-report.xml"));
  assertTrue(consoleText, consoleText.contains("jcreport: **/new-jcreport-report.xml"));
  assertTrue(consoleText, consoleText.contains("pep8: **/new-pep8-report.xml"));
  assertTrue(consoleText, consoleText.contains("perlcritic: **/new-perlcritic-report.xml"));
  assertTrue(consoleText, consoleText.contains("pylint: **/new-pylint-report.xml"));
  assertTrue(consoleText, consoleText.contains("simian: **/new-simian-report.xml"));
  assertTrue(consoleText, consoleText.contains("stylecop: **/new-stylecop-report.xml"));

  /**
   * Open config page and check that configured values are there.
   */
  goToConfig();
  assertEquals("http://changed.com", webDriver.findElement(name(STASH_BASE_URL)).getAttribute("value"));
  assertEquals("theotheruser", webDriver.findElement(name(STASH_USER)).getAttribute("value"));
  assertEquals("theotherpassword", webDriver.findElement(name(STASH_PASSWORD)).getAttribute("value"));
  assertEquals("DEF", webDriver.findElement(name(STASH_PROJECT)).getAttribute("value"));
  assertEquals("a_repo2", webDriver.findElement(name(STASH_REPO)).getAttribute("value"));
  assertEquals("101", webDriver.findElement(name(STASH_PULL_REQUEST_ID)).getAttribute("value"));
  assertEquals("abcd12345", webDriver.findElement(name(COMMIT_HASH)).getAttribute("value"));
  assertEquals("**/new-checkstyle-report.xml", webDriver.findElement(name(CHECKSTYLE_PATTERN)).getAttribute("value"));
  assertEquals("**/new-pmd-report.xml", webDriver.findElement(name(PMD_PATTERN)).getAttribute("value"));
  assertEquals("**/new-jslint-report.xml", webDriver.findElement(name(JSLINT_PATTERN)).getAttribute("value"));
  assertEquals("**/new-csslint-report.xml", webDriver.findElement(name(CSSLINT_PATTERN)).getAttribute("value"));
  assertEquals("**/new-codenarc-report.xml", webDriver.findElement(name(CODENARC_PATTERN)).getAttribute("value"));
  assertEquals("**/new-cpd-report.xml", webDriver.findElement(name(CPD_PATTERN)).getAttribute("value"));
  assertEquals("**/new-cpplint-report.xml", webDriver.findElement(name(CPPLINT_PATTERN)).getAttribute("value"));
  assertEquals("**/new-findbugs-report.xml", webDriver.findElement(name(FINDBUGS_PATTERN)).getAttribute("value"));
  assertEquals("**/new-fxcop-report.xml", webDriver.findElement(name(FXCOP_PATTERN)).getAttribute("value"));
  assertEquals("**/new-gendarme-report.xml", webDriver.findElement(name(GENDARME_PATTERN)).getAttribute("value"));
  assertEquals("**/new-jcreport-report.xml", webDriver.findElement(name(JCEREPORT_PATTERN)).getAttribute("value"));
  assertEquals("**/new-pep8-report.xml", webDriver.findElement(name(PEP8_PATTERN)).getAttribute("value"));
  assertEquals("**/new-perlcritic-report.xml", webDriver.findElement(name(PERLCRITIC_PATTERN)).getAttribute("value"));
  assertEquals("**/new-pylint-report.xml", webDriver.findElement(name(PYLINT_PATTERN)).getAttribute("value"));
  assertEquals("**/new-simian-report.xml", webDriver.findElement(name(SIMIAN_PATTERN)).getAttribute("value"));
  assertEquals("**/new-stylecop-report.xml", webDriver.findElement(name(STYLECOP_PATTERN)).getAttribute("value"));

  /**
   * Use variable in fields to make sure it gets expanded.
   */
  goToConfig();
  webDriver.findElement(name(STASH_BASE_URL)).clear();
  webDriver.findElement(name(STASH_USER)).clear();
  webDriver.findElement(name(STASH_PASSWORD)).clear();
  webDriver.findElement(name(STASH_PROJECT)).clear();
  webDriver.findElement(name(STASH_REPO)).clear();
  webDriver.findElement(name(STASH_PULL_REQUEST_ID)).clear();
  webDriver.findElement(name(COMMIT_HASH)).clear();
  webDriver.findElement(name(CHECKSTYLE_PATTERN)).clear();
  webDriver.findElement(name(CODENARC_PATTERN)).clear();
  webDriver.findElement(name(CPD_PATTERN)).clear();
  webDriver.findElement(name(CPPLINT_PATTERN)).clear();
  webDriver.findElement(name(FINDBUGS_PATTERN)).clear();
  webDriver.findElement(name(FXCOP_PATTERN)).clear();
  webDriver.findElement(name(GENDARME_PATTERN)).clear();
  webDriver.findElement(name(JCEREPORT_PATTERN)).clear();
  webDriver.findElement(name(PEP8_PATTERN)).clear();
  webDriver.findElement(name(PERLCRITIC_PATTERN)).clear();
  webDriver.findElement(name(PMD_PATTERN)).clear();
  webDriver.findElement(name(PYLINT_PATTERN)).clear();
  webDriver.findElement(name(SIMIAN_PATTERN)).clear();
  webDriver.findElement(name(STYLECOP_PATTERN)).clear();
  webDriver.findElement(name(JSLINT_PATTERN)).clear();
  webDriver.findElement(name(CSSLINT_PATTERN)).clear();
  webDriver.findElement(name(STASH_BASE_URL)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STASH_USER)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STASH_PASSWORD)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STASH_PROJECT)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STASH_REPO)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STASH_PULL_REQUEST_ID)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(COMMIT_HASH)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(CHECKSTYLE_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(CODENARC_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(CPD_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(CPPLINT_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(FINDBUGS_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(FXCOP_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(GENDARME_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(JCEREPORT_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(PEP8_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(PERLCRITIC_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(PMD_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(PYLINT_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(SIMIAN_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(STYLECOP_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(JSLINT_PATTERN)).sendKeys("$BUILD_NUMBER");
  webDriver.findElement(name(CSSLINT_PATTERN)).sendKeys("$BUILD_NUMBER");
  removeOnBeforeUnload();
  saveJob();
  startJob();
  waitForJob(3);
  consoleText = consoleText(3);
  assertTrue(consoleText, consoleText.contains("stashBaseUrl: 3"));
  assertTrue(consoleText, consoleText.contains("stashProject: 3"));
  assertTrue(consoleText, consoleText.contains("stashPullRequestId: 3"));
  assertTrue(consoleText, consoleText.contains("commitHash: 3"));
  assertTrue(consoleText, consoleText.contains("stashRepo: 3"));
  assertTrue(consoleText, consoleText.contains("checkstyle: 3"));
  assertTrue(consoleText, consoleText.contains("pmd: 3"));
  assertTrue(consoleText, consoleText.contains("jslint: 3"));
  assertTrue(consoleText, consoleText.contains("csslint: 3"));
  assertTrue(consoleText, consoleText.contains("codenarc: 3"));
  assertTrue(consoleText, consoleText.contains("cpd: 3"));
  assertTrue(consoleText, consoleText.contains("cpplint: 3"));
  assertTrue(consoleText, consoleText.contains("findbugs: 3"));
  assertTrue(consoleText, consoleText.contains("fxcop: 3"));
  assertTrue(consoleText, consoleText.contains("gendarme: 3"));
  assertTrue(consoleText, consoleText.contains("jcreport: 3"));
  assertTrue(consoleText, consoleText.contains("pep8: 3"));
  assertTrue(consoleText, consoleText.contains("perlcritic: 3"));
  assertTrue(consoleText, consoleText.contains("pylint: 3"));
  assertTrue(consoleText, consoleText.contains("simian: 3"));
  assertTrue(consoleText, consoleText.contains("stylecop: 3"));

  /**
   * Open config page and check that configured variables are there, unexpanded!
   */
  goToConfig();
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_BASE_URL)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_USER)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_PASSWORD)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_PROJECT)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_REPO)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STASH_PULL_REQUEST_ID)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(COMMIT_HASH)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(CHECKSTYLE_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(CODENARC_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(CPD_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(CPPLINT_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(FINDBUGS_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(FXCOP_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(GENDARME_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(JCEREPORT_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(PEP8_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(PERLCRITIC_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(PMD_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(PYLINT_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(SIMIAN_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(STYLECOP_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(JSLINT_PATTERN)).getAttribute("value"));
  assertEquals("$BUILD_NUMBER", webDriver.findElement(name(CSSLINT_PATTERN)).getAttribute("value"));
 }

 private void goToConfig() {
  webDriver.get(getJenkinsBaseUrl() + "/job/" + TEST_JOB_NAME + "/configure");
 }

 private void enterDetails() {
  webDriver.findElement(name(STASH_USER)).sendKeys("theuser");
  webDriver.findElement(name(STASH_PASSWORD)).sendKeys("thepassword");
  webDriver.findElement(name(STASH_BASE_URL)).sendKeys(HTTP_LOCALHOST_8456);
  webDriver.findElement(name(STASH_PROJECT)).sendKeys("ABC");
  webDriver.findElement(name(STASH_REPO)).sendKeys("a_repo");
  webDriver.findElement(name(STASH_PULL_REQUEST_ID)).sendKeys("100");
  webDriver.findElement(name(COMMIT_HASH)).sendKeys("abcd1234");
  webDriver.findElement(name(PATH_PREFIX)).sendKeys("pathPrefix");
  webDriver.findElement(name(CHECKSTYLE_PATTERN)).sendKeys("**/checkstyle-report.xml");
  webDriver.findElement(name(PMD_PATTERN)).sendKeys("**/pmd-report.xml");
  webDriver.findElement(name(JSLINT_PATTERN)).sendKeys("**/jslint-report.xml");
  webDriver.findElement(name(CSSLINT_PATTERN)).sendKeys("**/csslint-report.xml");
  webDriver.findElement(name(CODENARC_PATTERN)).sendKeys("**/codenarc-report.xml");
  webDriver.findElement(name(CPD_PATTERN)).sendKeys("**/cpd-report.xml");
  webDriver.findElement(name(CPPLINT_PATTERN)).sendKeys("**/cpplint-report.xml");
  webDriver.findElement(name(FINDBUGS_PATTERN)).sendKeys("**/findbugs-report.xml");
  webDriver.findElement(name(FXCOP_PATTERN)).sendKeys("**/fxcop-report.xml");
  webDriver.findElement(name(GENDARME_PATTERN)).sendKeys("**/gendarme-report.xml");
  webDriver.findElement(name(JCEREPORT_PATTERN)).sendKeys("**/jcreport-report.xml");
  webDriver.findElement(name(PEP8_PATTERN)).sendKeys("**/pep8-report.xml");
  webDriver.findElement(name(PERLCRITIC_PATTERN)).sendKeys("**/perlcritic-report.xml");
  webDriver.findElement(name(PYLINT_PATTERN)).sendKeys("**/pylint-report.xml");
  webDriver.findElement(name(SIMIAN_PATTERN)).sendKeys("**/simian-report.xml");
  webDriver.findElement(name(STYLECOP_PATTERN)).sendKeys("**/stylecop-report.xml");
 }

 private void waitForJob(int index) throws InterruptedException {
  while (!consoleText(index).contains("Finished: SUCCESS")) {
   logger.info("Waiting for jenkins job to finnish");
   sleep(500);
  }
 }

 private String consoleText(int index) {
  try {
   webDriver.get(getJenkinsBaseUrl() + "/job/" + TEST_JOB_NAME + "/" + index + "/consoleText");
   return webDriver.getPageSource();
  } catch (Exception e) {
   return "";
  }
 }

 private void startJob() {
  webDriver.get(getJenkinsBaseUrl() + "/job/" + TEST_JOB_NAME + "/build?delay=0sec");
 }

 private void saveJob() {
  webDriver.findElement(xpath("//span[@name='Submit']/span/button")).click();
 }

 private void createJob() {
  webDriver.get(getJenkinsBaseUrl() + "/view/All/newJob");
  webDriver.findElement(id("name")).sendKeys(TEST_JOB_NAME);
  webDriver.findElement(xpath("//input[@value='hudson.model.FreeStyleProject']")).click();
  webDriver.findElement(id("ok-button")).click();
 }

 private void enablePlugin() throws InterruptedException {
  scrollDown();
  webDriver.findElement(xpath("//button[@suffix='publisher']")).click();
  webDriver.findElement(xpath("//a[text()='Report Violations to Stash']")).click();
  scrollDown();
 }

 private void deleteJob() {
  webDriver.get(getJenkinsBaseUrl() + "/job/" + TEST_JOB_NAME + "/doDelete");
  webDriver.findElement(xpath("//input[@type='submit']")).click();
 }

 private void removeOnBeforeUnload() {
  ((JavascriptExecutor) webDriver).executeScript("window.onbeforeunload=null;", "");
 }

 private void scrollDown() throws InterruptedException {
  for (int i = 0; i < 10; i++) {
   ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0,1000)", "");
   sleep(50);
  }
 }

 private void waitForJenkinsToStart() throws InterruptedException {
  webDriver.get(getJenkinsBaseUrl());
  while (webDriver.getPageSource().contains("getting ready")) {
   logger.info("Jenkins not ready...");
   sleep(500);
  }
  logger.info("Jenkins ready!");
  sleep(3000);
 }
}
