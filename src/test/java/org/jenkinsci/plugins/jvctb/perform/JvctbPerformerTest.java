package org.jenkinsci.plugins.jvctb.perform;

import static org.assertj.core.api.Assertions.assertThat;
import hudson.EnvVars;

import org.jenkinsci.plugins.jvctb.config.ViolationsToBitbucketServerConfig;
import org.junit.Test;

import uk.co.jemos.podam.api.PodamFactoryImpl;

public class JvctbPerformerTest {

  @Test
  public void testThatAllAttributesAreExpanded() {
    ViolationsToBitbucketServerConfig config =
        new PodamFactoryImpl().manufacturePojo(ViolationsToBitbucketServerConfig.class);

    ViolationsToBitbucketServerConfig expanded =
        JvctbPerformer.expand(
            config,
            new EnvVars() {
              private static final long serialVersionUID = -8129221705855292193L;

              @Override
              public String expand(String s) {
                return s;
              }
            });

    assertThat(expanded).isEqualTo(config);
  }
}
