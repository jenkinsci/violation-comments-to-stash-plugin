# Violation Comments to Bitbucket Server

[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/)

It comments pull requests in  Bitbucket Server (or Stash) with violations found in report files from static code analysis.

It uses [Violation Comments to Bitbucket Server Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-server-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It supports:
 * [_AndoidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/) ([_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/) ([_Pep8_](https://github.com/PyCQA/pycodestyle), [_Mccabe_](https://pypi.python.org/pypi/mccabe), [_PyFlakes_](https://pypi.python.org/pypi/pyflakes))
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PMD_](https://pmd.github.io/)
 * [_PyLint_](https://www.pylint.org/)
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_StyleCop_](https://stylecop.codeplex.com/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)
 
The pull request will be commented like this.

![Pull request comment](https://raw.githubusercontent.com/jenkinsci/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png)

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin).

## Example use case
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
This plugin may be added as a post build step to analyze the workspace and report comments back to pull request in Bitbucket Server. [Here](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-config.png) is an example of how that may look like.

### The result
And finally [here](https://raw.githubusercontent.com/tomasbjerre/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png) is an example Bitbucket Server comment.

## Job DSL Plugin

This plugin can be used with the Job DSL Plugin.

```
job('example') {
 publishers {
  violationsToBitbucketServerRecorder {
   config {
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    projectKey("PROJECT_1")
    repoSlug("repo_1")
    password("admin")
    username("admin")
    pullRequestId("1")
    bitbucketServerUrl("http://localhost:7990/bitbucket")
    usernamePasswordCredentialsId(null)
    useUsernamePasswordCredentials(false)
    useUsernamePassword(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    commentOnlyChangedContentContext(5)
    violationConfigs {
     violationConfig {
      reporter("FINDBUGS")
      pattern(".*/findbugs/.*\\.xml\$")
     }
    }
   }
  }
 }
}
```

## Pipeline Plugin

This plugin can be used with the Pipeline Plugin:

```

node {

 sh '''
 rm -rf rep_1
 git clone http://admin:admin@localhost:7990/bitbucket/scm/project_1/rep_1.git
 cd *
 ./gradlew build
 '''

 step([
  $class: 'ViolationsToBitbucketServerRecorder', 
  config: [
   bitbucketServerUrl: 'http://localhost:7990/bitbucket', 
   createCommentWithAllSingleFileComments: true, 
   createSingleFileComments: true, 
   projectKey: 'PROJECT_1', 
   repoSlug: 'rep_1', 
   pullRequestId: '1', 
   useUsernamePassword: true, 
   username: 'admin', 
   password: 'admin', 
   useUsernamePasswordCredentials: false, 
   violationConfigs: [
    [ pattern: '.*/checkstyle/.*\\.xml$', reporter: 'CHECKSTYLE' ], 
    [ pattern: '.*/findbugs/.*\\.xml$', reporter: 'FINDBUGS' ], 
   ]
  ]
 ])
}
```

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
