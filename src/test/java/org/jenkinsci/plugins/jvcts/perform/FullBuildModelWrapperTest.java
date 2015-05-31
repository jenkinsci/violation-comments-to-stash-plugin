package org.jenkinsci.plugins.jvcts.perform;

import static org.jenkinsci.plugins.jvcts.perform.FullBuildModelWrapper.usingForwardSlashes;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FullBuildModelWrapperTest {
 @Test
 public void testThatSlashesAreReplacedCorrectly() {
  assertEquals("c:/some/path/file.txt", usingForwardSlashes("c:\\some\\path\\file.txt"));
 }
}
