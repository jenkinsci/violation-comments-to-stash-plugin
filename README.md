# Violation Comments to Bitbucket Server

[![Build Status](https://ci.jenkins.io/job/Plugins/job/violation-comments-to-stash-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/violation-comments-to-stash-plugin)

It comments pull requests in  Bitbucket Server (or Stash) with violations found in report files from static code analysis.

It uses [Violation Comments to Bitbucket Server Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-server-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It supports:
 * [_AndroidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/)
   * [_Detekt_](https://github.com/arturbosch/detekt) with `--output-format xml`.
   * [_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`.
   * [_KTLint_](https://github.com/shyiko/ktlint)
   * [_SwiftLint_](https://github.com/realm/SwiftLint) with `--reporter checkstyle`.
   * [_TSLint_](https://palantir.github.io/tslint/usage/cli/) with `-t checkstyle`
   * [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer) with `phpcs api.php --report=checkstyle`.
 * [_CLang_](https://clang-analyzer.llvm.org/)
   * [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/) with `rubycop -f clang file.rb`
   * [_GCC_](https://gcc.gnu.org/)
   * [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)
   * [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)
 * [_CodeNarc_](http://codenarc.sourceforge.net/)
 * [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_DocFX_](http://dotnet.github.io/docfx/)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
   * [_Spotbugs_](https://spotbugs.github.io/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/)
   * [_AnsibleLint_](https://github.com/willthames/ansible-lint) with `-p`
   * [_Mccabe_](https://pypi.python.org/pypi/mccabe)
   * [_Pep8_](https://github.com/PyCQA/pycodestyle)
   * [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)
 * [_GoLint_](https://github.com/golang/lint)
   * [_GoVet_](https://golang.org/cmd/vet/) Same format as GoLint.
 * [_GoogleErrorProne_](https://github.com/google/error-prone)
   * [_NullAway_](https://github.com/uber/NullAway) Same format as Google Error Prone.
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)
 * [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)
 * [_MyPy_](https://pypi.python.org/pypi/mypy-lang)
 * [_PCLint_](http://www.gimpel.com/html/pcl.htm) PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)
 * [_PyLint_](https://www.pylint.org/) with `pylint --output-format=parseable`.
 * [_PMD_](https://pmd.github.io/)
   * [_Infer_](http://fbinfer.com/) Facebook Infer. With `--pmd-xml`.
   * [_PHPPMD_](https://phpmd.org/) with `phpmd api.php xml ruleset.xml`.
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_SbtScalac_](http://www.scala-sbt.org/)
 * [_Simian_](http://www.harukizaemon.com/simian/)
 * [_StyleCop_](https://stylecop.codeplex.com/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)
 * [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)
 * [_ZPTLint_](https://pypi.python.org/pypi/zptlint)

 
The pull request will be commented like this.

![Pull request comment](https://raw.githubusercontent.com/jenkinsci/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png)

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin).

## Notify Jenkins from Bitbucket Server

* You may trigger with a [webhook](https://confluence.atlassian.com/bitbucketserver/managing-webhooks-in-bitbucket-server-938025878.html) in Bitbucket Server. And consume it with [Generic Webhook Trigger plugin](https://github.com/jenkinsci/generic-webhook-trigger-plugin) to get the variables you need.

* Or, trigger with [Pull Request Notifier for Bitbucket Server](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket). It can supply any parameters and variables you may need. Here is an example URL. `http://localhost:8080/jenkins/job/builder/buildWithParameters?FROM=${PULL_REQUEST_FROM_HASH}&TO=${PULL_REQUEST_TO_HASH}&TOSLUG=${PULL_REQUEST_TO_REPO_SLUG}&TOREPO=${PULL_REQUEST_TO_HTTP_CLONE_URL}&FROMREPO=${PULL_REQUEST_FROM_HTTP_CLONE_URL}&ID=${PULL_REQUEST_ID}&PROJECT=${PULL_REQUEST_TO_REPO_PROJECT_KEY}`

## Merge

**You must perform the merge before build**. If you don't perform the merge, the reported violations will refer to other lines then those in the pull request. The merge can be done with a shell script like this.

```
echo ---
echo --- Merging from $FROM in $FROMREPO to $TO in $TOREPO
echo ---
git clone $TOREPO
cd *
git reset --hard $TO
git status
git remote add from $FROMREPO
git fetch from
git merge $FROM
git --no-pager log --max-count=10 --graph --abbrev-commit

Your build command here!
```

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

    credentialsId('bitbucketservercredentials')

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
 deleteDir()
 
 stage('Merge') {
  sh '''
  git clone git@github.com:tomasbjerre/violations-test.git .
  git checkout master
  git merge origin/feature/addingcrap
  '''
 }

 stage('Build') {
  sh '''
  ./build.sh || ls
  '''
 }

 stage('Static code analysis') {
  ViolationsToBitbucketServer([
   bitbucketServerUrl: 'http://localhost:7990/',
   commentOnlyChangedContent: true,
   commentOnlyChangedContentContext: 5,
   createCommentWithAllSingleFileComments: false,
   createSingleFileComments: true,
   keepOldComments: true,
   projectKey: 'PROJ', // Use environment variable here
   pullRequestId: '1', // Use environment variable here
   repoSlug: 'violations-test', // Use environment variable here
   
   credentialsId: 'theid',
   
   violationConfigs: [
    // Many more formats available, check https://github.com/tomasbjerre/violations-lib
    [parser: 'FINDBUGS', pattern: '.*/findbugs/.*\\.xml\$', reporter: 'Findbugs'],
    [parser: 'CHECKSTYLE', pattern: '.*/checkstyle/.*\\.xml\$', reporter: 'Checkstyle']
   ]
  ])
 }
}
```

## Developer instructions
Instructions for developers.

### Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin.

If you have release-permissions this is how you do a release:

```
mvn release:prepare release:perform
```
