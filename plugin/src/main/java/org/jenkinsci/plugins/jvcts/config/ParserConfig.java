package org.jenkinsci.plugins.jvcts.config;

public class ParserConfig {
 private String pattern;
 private String parserTypeDescriptorName;

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
}
