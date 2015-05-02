package org.jenkinsci.plugins.jvcts.config;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

import com.google.common.base.Optional;

public class ParserConfig {
 private String pattern;
 private String parserTypeDescriptorName;
 private String pathPrefix;

 public ParserConfig() {

 }

 public ParserConfig(String typeDescriptorName, String pattern) {
  this.parserTypeDescriptorName = typeDescriptorName;
  this.pattern = pattern;
 }

 public String getPattern() {
  return pattern;
 }

 public void setPattern(String pattern) {
  this.pattern = pattern;
 }

 public String getParserTypeDescriptorName() {
  return parserTypeDescriptorName;
 }

 public void setParserTypeDescriptorName(String parserTypeDescriptorName) {
  this.parserTypeDescriptorName = parserTypeDescriptorName;
 }

 public void setPathPrefix(String pathPrefix) {
  this.pathPrefix = pathPrefix;
 }

 public String getPathPrefix() {
  return pathPrefix;
 }

 public Optional<String> getPathPrefixOpt() {
  return fromNullable(emptyToNull(nullToEmpty(pathPrefix).trim()));
 }
}
