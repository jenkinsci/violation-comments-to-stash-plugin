package org.jenkinsci.plugins.jvcts.config;

import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.CHECKSTYLE_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.CODENARC_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.CPD_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.CPPLINT_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.CSSLINT_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FINDBUGS_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.FXCOP_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.GENDARME_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.JCREPORT_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.JSLINT_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.PEP8_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.PERLCRITIC_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.PMD_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.PYLINT_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.SIMIAN_NAME;
import static org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfigHelper.STYLECOP_NAME;

public enum Parser {
 CHECKSTYLE(CHECKSTYLE_NAME), //
 CODENARC(CODENARC_NAME), //
 CPD(CPD_NAME), //
 CPPLINT(CPPLINT_NAME), //
 FINDBUGS(FINDBUGS_NAME), //
 FXCOP(FXCOP_NAME), //
 GENDARME(GENDARME_NAME), //
 JCREPORT(JCREPORT_NAME), //
 PEP8(PEP8_NAME), //
 PERLCRITIC(PERLCRITIC_NAME), //
 PMD(PMD_NAME), //
 PYLINT(PYLINT_NAME), //
 SIMIAN(SIMIAN_NAME), //
 STYLECOP(STYLECOP_NAME), //
 JSLINT(JSLINT_NAME), //
 CSSLINT(CSSLINT_NAME);

 private String typeDescriptorName;

 Parser(String typeDescriptorName) {
  this.typeDescriptorName = typeDescriptorName;
 }

 public String getTypeDescriptorName() {
  return typeDescriptorName;
 }
}
