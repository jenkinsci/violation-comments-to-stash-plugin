# Jenkins Violation Comments to Stash [![Build Status](https://travis-ci.org/tomasbjerre/jenkins-violation-comments-to-stash-plugin.svg?branch=master)](https://travis-ci.org/tomasbjerre/jenkins-violation-comments-to-stash-plugin)
This is much like the [Violations](https://wiki.jenkins-ci.org/display/JENKINS/Violations) plugin. Instead of publishing violation reports in Jenkins, it comments pull requests in Stash.

Code from the [Violations](https://wiki.jenkins-ci.org/display/JENKINS/Violations) is used through a submodule.

There is a screenshot of the configuration GUI [here](https://raw.githubusercontent.com/tomasbjerre/jenkins-violation-comments-to-stash-plugin/master/sandbox/screenshot-config.png).

#Features
* Comment pull requests with code analyzers comments
 * Supporting: CheckStyle, CSSLint, JSLint, CodeNarc, CPPLint, FindBugs, FxCop, Gendarme, JCEReport, PEP8, PerlCritic, PMD, PyLint, Simian, StyleCop

## Use case
Here is an example use case where a pull request is triggered from Stash, merged, checked and comments added to pull request in Stash.

### Notify Jenkins from Stash
You may use [Pull Request Notifier for Stash](https://github.com/tomasbjerre/pull-request-notifier-for-stash) to trigger a Jenkins build from an event in Stash. It can supply any parameters and variables you may need. Here is an example URL.

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
This plugin may be added as a post build step to analyse the workspace and report comments back to pull request in Stash. [Here](https://raw.githubusercontent.com/tomasbjerre/jenkins-violation-comments-to-stash-plugin/master/sandbox/screenshot-config.png) is an example of how that may look like.

### The result
And finally [here](https://raw.githubusercontent.com/tomasbjerre/jenkins-violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png) is an example stash comment.

## Developer instructions
Instructions for developers.

### Get the code

Clone repo, including submodules.

```
git clone --recursive git@github.com:tomasbjerre/jenkins-violation-comments-to-stash-plugin.git
```

Or if you already cloned the repo.

```
git submodule update --init --recursive
```

### Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin.

Some tests are implemented in maven project in ```/plugin-test```. This is to avoid classpath issues with the plugin. These are web tests that will start Jenkins with the plugin on localhost and perform some configuration tests.

The actual plugin is implemented in ```/plugin```.

A release is created like this, in ```plugin```.
```
mvn release:prepare
mvn release:perform
```
