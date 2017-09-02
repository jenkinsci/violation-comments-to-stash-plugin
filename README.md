# Violation Comments to Bitbucket Server

[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-stash-plugin/)

It comments pull requests in  Bitbucket Server (or Stash) with violations found in report files from static code analysis.

It uses [Violation Comments to Bitbucket Server Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-server-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It supports:
 * [_AndroidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/)
   * [_Detekt_](https://github.com/arturbosch/detekt) with `--output-format xml`.
   * [_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`.
   * [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer) with `phpcs api.php --report=checkstyle`.
 * [_CLang_](https://clang-analyzer.llvm.org/)
   * [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/) with `rubycop -f clang file.rb`
 * [_CodeNarc_](http://codenarc.sourceforge.net/)
 * [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/)
   * [_AnsibleLint_](https://github.com/willthames/ansible-lint) with `-p`
   * [_Mccabe_](https://pypi.python.org/pypi/mccabe)
   * [_Pep8_](https://github.com/PyCQA/pycodestyle)
   * [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)
 * [_GoLint_](https://github.com/golang/lint)
   * [_GoVet_](https://golang.org/cmd/vet/) Same format as GoLint.
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)
 * [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)
 * [_MyPy_](https://pypi.python.org/pypi/mypy-lang)
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)
 * [_PyLint_](https://www.pylint.org/)
 * [_PMD_](https://pmd.github.io/)
   * [_Infer_](http://fbinfer.com/) Facebook Infer. With `--pmd-xml`.
   * [_PHPPMD_](https://phpmd.org/) with `phpmd api.php xml ruleset.xml`.
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_SbtScalac_](http://www.scala-sbt.org/)
 * [_Simian_](http://www.harukizaemon.com/simian/)
 * [_StyleCop_](https://stylecop.codeplex.com/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)
 * [_ZPTLint_](https://pypi.python.org/pypi/zptlint)

 
The pull request will be commented like this.

![Pull request comment](https://raw.githubusercontent.com/jenkinsci/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png)

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin).

## Example use case
Here is an example use case where a pull request is triggered from Bitbucket Server, merged, checked and comments added to pull request in Bitbucket Server.

You may also use it for an ordinary build job, to simply comment the commit that was built.

### Notify Jenkins from Bitbucket Server
You can use [Generic Webhook Trigger plugin](https://github.com/tomasbjerre/generic-webhook-trigger-plugin) to get the variables you need.

You may also use [Pull Request Notifier for Bitbucket Server](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket) to trigger a Jenkins build from an event in Bitbucket Server. It can supply any parameters and variables you may need. Here is an example URL.

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

This plugin can be used with the Job DSL Plugin. Here is an example.

I trigger it with [Pull Request Notifier for Bitbucket Server](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket) with URL like `http://jenkins:8080/job/Bitbucket_Server_PR_Builder/buildWithParameters?${EVERYTHING_URL}`,  I report back to Bitbucket Server with [HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin) and [Conditional BuildStep Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Conditional+BuildStep+Plugin).

```
job('Bitbucket_Server_PR_Builder') {
 concurrentBuild()
 quietPeriod(0)
 parameters {
  stringParam('PULL_REQUEST_TO_HTTP_CLONE_URL', '')
  stringParam('PULL_REQUEST_TO_HASH', '')
  stringParam('PULL_REQUEST_FROM_HTTP_CLONE_URL', '')
  stringParam('PULL_REQUEST_FROM_HASH', '')
  stringParam('PULL_REQUEST_TO_REPO_PROJECT_KEY', '')
  stringParam('PULL_REQUEST_TO_REPO_SLUG', '')
  stringParam('PULL_REQUEST_ID','')
 }
 steps {
  httpRequest {
   url("http://admin:admin@bitbucket:7990/rest/api/1.0/projects/\$PULL_REQUEST_TO_REPO_PROJECT_KEY/repos/\$PULL_REQUEST_TO_REPO_SLUG/pull-requests/\$PULL_REQUEST_ID/comments")
   consoleLogResponseBody(true)
   httpMode("POST")
   acceptType('APPLICATION_JSON')
   contentType('APPLICATION_JSON')
   requestBody('{ "text": "Building... \$BUILD_URL" }')
  }

  shell('''
echo ---
echo --- Merging from $FROM in $FROMREPO to $TO in $TOREPO
echo ---
git clone $PULL_REQUEST_TO_HTTP_CLONE_URL
cd *
git reset --hard $PULL_REQUEST_TO_HASH
git status
git remote add from $PULL_REQUEST_FROM_HTTP_CLONE_URL
git fetch from
git merge $PULL_REQUEST_FROM_HASH
git --no-pager log --max-count=10 --graph --abbrev-commit

./gradlew build
  ''')

  conditionalBuilder {
   runCondition {
    statusCondition {
     worstResult('SUCCESS')
     bestResult('SUCCESS')
    }
    runner {
     runUnstable()
    }
    conditionalbuilders {
     httpRequest {
      url("http://admin:admin@bitbucket:7990/rest/api/1.0/projects/\$PULL_REQUEST_TO_REPO_PROJECT_KEY/repos/\$PULL_REQUEST_TO_REPO_SLUG/pull-requests/\$PULL_REQUEST_ID/comments")
      consoleLogResponseBody(true)
      httpMode("POST")
      acceptType('APPLICATION_JSON')
      contentType('APPLICATION_JSON')
      requestBody('{ "text": "Success... \$BUILD_URL" }')
     }
    }
   }
  }

  conditionalBuilder {
   runCondition {
    statusCondition {
     worstResult('FAILURE')
     bestResult('FAILURE')
    }
    runner {
     runUnstable()
    }
    conditionalbuilders {
     httpRequest {
      url("http://admin:admin@bitbucket:7990/rest/api/1.0/projects/\$PULL_REQUEST_TO_REPO_PROJECT_KEY/repos/\$PULL_REQUEST_TO_REPO_SLUG/pull-requests/\$PULL_REQUEST_ID/comments")
      consoleLogResponseBody(true)
      httpMode("POST")
      acceptType('APPLICATION_JSON')
      contentType('APPLICATION_JSON')
      requestBody('{ "text": "Failure... \$BUILD_URL" }')
     }
    }
   }
  }
 }
 publishers {
  violationsToBitbucketServerRecorder {
   config {
    bitbucketServerUrl("http://bitbucket:7990")
    projectKey("\$PULL_REQUEST_TO_REPO_PROJECT_KEY")
    repoSlug("\$PULL_REQUEST_TO_REPO_SLUG")
    pullRequestId("\$PULL_REQUEST_ID")

    useUsernamePasswordCredentials(false)
    usernamePasswordCredentialsId(null)

    useUsernamePassword(true)
    username("admin")
    password("admin")

    minSeverity('INFO')
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(false)
    commentOnlyChangedContent(true)
    commentOnlyChangedContentContext(5)
    keepOldComments(false)

    violationConfigs {
     violationConfig {
      parser("FINDBUGS")
      reporter("Findbugs")
      pattern(".*/findbugs/.*\\.xml\$")
     }
     violationConfig {
      parser("CHECKSTYLE")
      reporter("Checkstyle")
      pattern(".*/checkstyle/.*\\.xml\$")
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
 def mvnHome = tool 'Maven 3.3.9'
 deleteDir()
 
 stage('Merge') {
  sh "git init"
  sh "git fetch --no-tags --progress git@git:group/reponame.git +refs/heads/*:refs/remotes/origin/* --depth=200"
  sh "git checkout origin/${env.targetBranch}"
  sh "git merge origin/${env.sourceBranch}"
  sh "git log --graph --abbrev-commit --max-count=10"
 }

 stage('Static code analysis') {
  sh "${mvnHome}/bin/mvn package -DskipTests -Dmaven.test.failure.ignore=false -Dsurefire.skip=true -Dmaven.compile.fork=true -Dmaven.javadoc.skip=true"

  step([
   $class: 'ViolationsToBitbucketServerRecorder', 
   config: [
    bitbucketServerUrl: 'http://localhost:7990/bitbucket', 
    createCommentWithAllSingleFileComments: false, 
    createSingleFileComments: true, 
    projectKey: 'PROJECT_1', 
    repoSlug: 'rep_1', 
    pullRequestId: '1', 
    useUsernamePassword: true, 
    username: 'admin', 
    password: 'admin', 
    useUsernamePasswordCredentials: false, 
    minSeverity: 'INFO',
    keepOldComments: false, 
    violationConfigs: [
     [ pattern: '.*/checkstyle-result\\.xml$', parser: 'CHECKSTYLE', reporter: 'Checkstyle' ], 
     [ pattern: '.*/findbugsXml\\.xml$', parser: 'FINDBUGS', reporter: 'Findbugs' ], 
     [ pattern: '.*/pmd\\.xml$', parser: 'PMD', reporter: 'PMD' ], 
    ]
   ]
  ])
 }
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

If you have release-permissions this is how you do a release:

```
mvn release:prepare release:perform
```
