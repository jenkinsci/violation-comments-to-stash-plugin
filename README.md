# Violation Comments to Bitbucket Server

Travis CI: [![Build Status](https://travis-ci.org/tomasbjerre/violation-comments-to-stash-plugin.svg?branch=master)](https://travis-ci.org/tomasbjerre/violation-comments-to-stash-plugin)

CloudBees: [![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/)

This is much like the [Violations](https://wiki.jenkins-ci.org/display/JENKINS/Violations) plugin. Instead of publishing violation reports in Jenkins, it comments pull requests (or individual commits) in  Bitbucket Server (or Stash).

Code from the [Violations](https://wiki.jenkins-ci.org/display/JENKINS/Violations) is used through a dependency.

There is a screenshot of the configuration GUI [here](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-config.png) and a sample comment may look like [this](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png).

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin).

#Features
* Comment pull requests, or individual commits, with code analyzers comments. Supports:
  * CheckStyle
  * CodeNarc
  * CPPLint
  * CSSLint
  * FindBugs
  * FxCop
  * Gendarme
  * JCEReport
  * JSLint
  * PEP8
  * PerlCritic
  * PMD
  * PyFlakes
  * PyLint
  * Simian
  * StyleCop
  * ReSharper
  * XMLLint
  * ZPTLint

## Use case
Here is an example use case where a pull request is triggered from Bitbucket Server, merged, checked and comments added to pull request in Bitbucket Server.

You may also use it for an ordinary build job, to simply comment the commit that was built.

### Notify Jenkins from Bitbucket Server
You may use [Pull Request Notifier for Bitbucket Server](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket) to trigger a Jenkins build from an event in Bitbucket Server. It can supply any parameters and variables you may need. Here is an example URL.

```
http://localhost:8080/jenkins/job/builder/buildWithParameters?FROM=${PULL_REQUEST_FROM_HASH}&TO=${PULL_REQUEST_TO_HASH}&TOSLUG=${PULL_REQUEST_TO_REPO_SLUG}&TOREPO=${PULL_REQUEST_TO_HTTP_CLONE_URL}&FROMREPO=${PULL_REQUEST_FROM_HTTP_CLONE_URL}&ID=${PULL_REQUEST_ID}&PROJECT=${PULL_REQUEST_TO_REPO_PROJECT_KEY}
```

### Jenkins job
The Jenkins job may perform the merge, and run any checkers on it, with a shell script build step. It needs to be a parameterized build. To match URL in example above, these parameters are needed.
 * ID
 * TO
 * TOSLUG
 * TOREPO
 * FROM
 * FROMREPO
 * PROJECT

The shell script may look like this.

```
echo ---
echo --- Mergar from $FROM in $FROMREPO to $TO in $TOREPO
echo ---
git clone $TOREPO
cd *
git reset --hard $TO
git status
git remote add from $FROMREPO
git fetch from
git merge $FROM
git --no-pager log --max-count=10 --graph --abbrev-commit

your build command here!
```

### Configure plugin
This plugin may be added as a post build step to analyse the workspace and report comments back to pull request in Bitbucket Server. [Here](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-config.png) is an example of how that may look like.

### The result
And finally [here](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png) is an example Bitbucket Server comment.

## Developer instructions
Instructions for developers.

### Get the code

```
git clone git@github.com:tomasbjerre/violation-comments-to-stash-plugin.git
```

### Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin.

Some tests are implemented in maven project in ```/plugin-test```. This is to avoid classpath issues with the plugin. These are web tests that will start Jenkins with the plugin on localhost and perform some configuration tests.

A release is created like this. You need to clone from jenkinsci-repo, with https and have username/password in settings.xml.
```
mvn release:prepare release:perform
```
