package org.jenkinsci.plugins.jvcts.config;

import static com.google.common.collect.Lists.newArrayList;
import hudson.plugins.violations.TypeDescriptor;

import java.util.List;

public class ViolationsToStashConfigHelper {
 public static final String CHECKSTYLE_NAME = "checkstyle";
 public static final String CODENARC_NAME = "codenarc";
 public static final String CPD_NAME = "cpd";
 public static final String CPPLINT_NAME = "cpplint";
 public static final String CSSLINT_NAME = "csslint";
 public static final String FINDBUGS_NAME = "findbugs";
 public static final String FXCOP_NAME = "fxcop";
 public static final String GENDARME_NAME = "gendarme";
 public static final String JCREPORT_NAME = "jcreport";
 public static final String JSLINT_NAME = "jslint";
 public static final String PEP8_NAME = "pep8";
 public static final String PERLCRITIC_NAME = "perlcritic";
 public static final String PMD_NAME = "pmd";
 public static final String PYLINT_NAME = "pylint";
 public static final String SIMIAN_NAME = "simian";
 public static final String STYLECOP_NAME = "stylecop";
 public static final String FIELD_PATTERN = "pattern";
 public static final String FIELD_PREFIX = "pathPrefix";
 public static final String FIELD_STASH_PULL_REQUEST_ID = "stashPullRequestId";
 public static final String FIELD_COMMIT_HASH = "commitHash";
 public static final String FIELD_STASH_REPO = "stashRepo";
 public static final String FIELD_STASH_PROJECT = "stashProject";
 public static final String FIELD_STASH_BASE_URL = "stashBaseUrl";
 public static final String FIELD_STASH_USER = "stashUser";
 public static final String FIELD_STASH_PASSWORD = "stashPassword";

 public static ViolationsToStashConfig createNewConfig() {
  ViolationsToStashConfig config = new ViolationsToStashConfig();
  List<ParserConfig> parsers = newArrayList();
  for (TypeDescriptor parser : TypeDescriptor.TYPES.values()) {
   ParserConfig parserConfig = new ParserConfig();
   parserConfig.setParserTypeDescriptor(parser);
   parsers.add(parserConfig);
  }
  config.setParsers(parsers);
  return config;
 }
}
