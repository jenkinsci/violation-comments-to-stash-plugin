package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static hudson.plugins.violations.TypeDescriptor.TYPES;
import static java.util.logging.Level.SEVERE;
import hudson.plugins.violations.TypeDescriptor;
import hudson.plugins.violations.model.FullBuildModel;
import hudson.plugins.violations.model.FullFileModel;
import hudson.plugins.violations.model.Violation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.jenkinsci.plugins.jvcts.config.ParserConfig;
import org.jenkinsci.plugins.jvcts.config.ViolationsToStashConfig;

public class FullBuildModelWrapper {

 private static final Logger logger = Logger.getLogger(FullBuildModelWrapper.class.getName());
 private final FullBuildModel model = new FullBuildModel();

 public FullBuildModelWrapper(ViolationsToStashConfig config, File workspace) {
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   for (String fileName : findFilesFromPattern(workspace, parserConfig.getPattern())) {
    String[] sourcePaths = {};
    TypeDescriptor type = TYPES.get(parserConfig.getParserTypeDescriptorName());
    try {
     type.createParser().parse(model, workspace, fileName, sourcePaths);
    } catch (IOException e) {
     logger.log(SEVERE,
       "Error while parsing \"" + fileName + "\" for type " + parserConfig.getParserTypeDescriptorName(), e);
    }
   }
  }
 }

 public Map<String, List<Violation>> getViolationsPerFile() {
  Map<String, List<Violation>> violationsPerFile = newHashMap();
  for (String fileModel : model.getFileModelMap().keySet()) {
   FullFileModel fullFileModel = model.getFileModel(fileModel);
   String sourceFile = null;
   if (fullFileModel.getSourceFile() != null) {
    sourceFile = fullFileModel.getSourceFile().getAbsolutePath();
   } else if (model.getFileModel(fileModel).getDisplayName() != null) {
    sourceFile = model.getFileModel(fileModel).getDisplayName();
   }
   if (sourceFile == null) {
    logger.log(SEVERE, "Could not determine source file from: " + fileModel);
   }
   TreeMap<String, TreeSet<Violation>> typeMap = model.getFileModel(fileModel).getTypeMap();
   for (String type : typeMap.keySet()) {
    for (Violation violation : typeMap.get(type)) {
     if (!violationsPerFile.containsKey(sourceFile)) {
      violationsPerFile.put(sourceFile, new ArrayList<Violation>());
     }
     violationsPerFile.get(sourceFile).add(violation);
    }
   }
  }
  return violationsPerFile;
 }

 /**
  * Include matching pattern within workspace.
  *
  * @see FileSet#setIncludes(String)
  */
 private String[] findFilesFromPattern(final File workspace, String pattern) {
  if (nullToEmpty(pattern).trim().isEmpty()) {
   return new String[] {};
  }
  FileSet fileSet = new FileSet();
  Project project = new Project();
  fileSet.setProject(project);
  fileSet.setDir(workspace);
  fileSet.setIncludes(pattern);
  return fileSet.getDirectoryScanner(project).getIncludedFiles();
 }
}
