package org.jenkinsci.plugins.jvctb;

import java.io.Serializable;

public class ProxyConfigDetails implements Serializable {
  private final String host;
  private final int port;
  private String user;
  private String pass;

  public ProxyConfigDetails(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }

  public String getPass() {
    return pass;
  }

  public String getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "ProxyConfigDetails{"
        + "host='"
        + host
        + '\''
        + ", port="
        + port
        + ", user='"
        + user
        + '\''
        + ", pass='"
        + pass
        + '\''
        + '}';
  }
}
