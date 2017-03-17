package org.jenkinsci.plugins.jvctb.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.jemos.podam.api.PodamFactoryImpl;

public class ViolationsToBitbucketServerConfigTest {

  @Test
  public void testThatCopyConstructorCopiesEverything() {
    ViolationsToBitbucketServerConfig original =
        new PodamFactoryImpl().manufacturePojo(ViolationsToBitbucketServerConfig.class);
    ViolationsToBitbucketServerConfig actual = new ViolationsToBitbucketServerConfig(original);
    assertEquals(original, actual);
  }
}
