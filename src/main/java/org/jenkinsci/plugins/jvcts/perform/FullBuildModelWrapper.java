package org.jenkinsci.plugins.jvcts.perform;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import hudson.plugins.violations.TypeDescriptor;
import hudson.plugins.violations.model.FullBuildModel;
import hudson.plugins.violations.model.FullFileModel;
import hudson.plugins.violations.model.Violation;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

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
import org.jenkinsci.plugins.jvcts.config.ViolationsToBitbucketServerConfig;

import com.google.common.annotations.VisibleForTesting;

public class FullBuildModelWrapper implements FileCallable<Map<String, List<Violation>>> {

 private final Map<String, FullBuildModel> models = newHashMap();
 private final ViolationsToBitbucketServerConfig config;

 private static final Logger logger = Logger.getLogger(FullBuildModelWrapper.class.getName());

 public FullBuildModelWrapper(ViolationsToBitbucketServerConfig config) {
  this.config = config;
 }

 @Override
 public Map<String, List<Violation>> invoke(File workspace, VirtualChannel channel) throws IOException {
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   for (String pattern : parserConfig.getPattern().split(",")) {
    for (String fileName : findFilesFromPattern(workspace, pattern)) {
     String[] sourcePaths = {};
     TypeDescriptor type = parserConfig.getParserTypeDescriptor();
     try {
      if (!models.containsKey(parserConfig.getParserTypeDescriptorName())) {
       models.put(parserConfig.getParserTypeDescriptorName(), new FullBuildModel());
      }
      type.createParser().parse(models.get(parserConfig.getParserTypeDescriptorName()), workspace, fileName,
        sourcePaths);
     } catch (IOException e) {
      logger.log(SEVERE, "Error while parsing \"" + fileName + "\" for type "
        + parserConfig.getParserTypeDescriptorName(), e);
     }
    }
   }
  }

  return getViolationsPerFile();
 }

 public Map<String, List<Violation>> getViolationsPerFile() {
  Map<String, List<Violation>> violationsPerFile = newHashMap();
  for (ParserConfig parserConfig : config.getParserConfigs()) {
   String typeDescriptorName = parserConfig.getParserTypeDescriptorName();
   if (models.containsKey(typeDescriptorName)) {
    for (String fileModel : models.get(typeDescriptorName).getFileModelMap().keySet()) {
     String sourceFile = usingForwardSlashes(determineSourcePath(fileModel, parserConfig));
     if (sourceFile == null) {
      logger.log(SEVERE, "Could not determine source file from: " + fileModel);
      continue;
     }
     TreeMap<String, TreeSet<Violation>> typeMap = models.get(typeDescriptorName).getFileModel(fileModel).getTypeMap();
     for (String type : typeMap.keySet()) {
      for (Violation violation : typeMap.get(type)) {
       if (!violationsPerFile.containsKey(sourceFile)) {
        violationsPerFile.put(sourceFile, new ArrayList<Violation>());
       }
       violationsPerFile.get(sourceFile).add(violation);
      }
     }
    }
   }
  }
  logger.log(FINE, "Found " + violationsPerFile.size() + " violations");
  return violationsPerFile;
 }

 @VisibleForTesting
 static String usingForwardSlashes(String unknownSlashed) {
  return unknownSlashed.replaceAll("\\\\", "/");
 }

 private String determineSourcePath(String fileModel, ParserConfig parserConfig) {
  logger.log(FINE, "Determining source path for " + fileModel);
  FullBuildModel model = models.get(parserConfig.getParserTypeDescriptorName());
  FullFileModel fullFileModel = model.getFileModel(fileModel);
  if (fullFileModel.getSourceFile() != null) {
   if (fullFileModel.getSourceFile().exists()) {
    return fullFileModel.getSourceFile().getAbsolutePath();
   } else {
    logger.log(FINE, "Not found: " + fullFileModel.getSourceFile().getAbsolutePath());
   }
  }
  File withDisplayName = new File(fullFileModel.getDisplayName());
  if (withDisplayName.exists()) {
   return withDisplayName.getAbsolutePath();
  } else {
   logger.log(FINE, "Not found: " + withDisplayName.getAbsolutePath());
  }
  if (parserConfig.getPathPrefixOpt().isPresent()) {
   File withPrefix = new File(parserConfig.getPathPrefixOpt().get() + fullFileModel.getDisplayName());
   if (withPrefix.exists()) {
    return withPrefix.getAbsolutePath();
   } else {
    logger.log(FINE, "Not found: " + withPrefix.getAbsolutePath());
   }
  }
  logger.log(FINE, "Using: " + fullFileModel.getDisplayName());
  return fullFileModel.getDisplayName();
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

 // Needed to build with Jenkins 1.609, dont @Override since it will cause
 // errors when building for older Jenkins
 public void checkRoles(RoleChecker checker) throws SecurityException {
 }
}
