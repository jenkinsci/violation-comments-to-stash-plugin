package org.jenkinsci.plugins.jvcts.config;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import hudson.plugins.violations.TypeDescriptor;

import com.google.common.base.Optional;

public class ParserConfig {
 private String pattern;
 private TypeDescriptor parserTypeDescriptor;
 private String pathPrefix;

 public ParserConfig() {

 }

 public ParserConfig(TypeDescriptor typeDescriptor, String pattern) {
  this.parserTypeDescriptor = typeDescriptor;
  this.pattern = pattern;
 }

 public String getPattern() {
  return pattern;
 }

 public void setPattern(String pattern) {
  this.pattern = pattern;
 }

 public TypeDescriptor getParserTypeDescriptor() {
  return parserTypeDescriptor;
 }

 public void setParserTypeDescriptor(TypeDescriptor parser) {
  this.parserTypeDescriptor = parser;
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
