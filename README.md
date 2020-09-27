# Violation Comments to Bitbucket Server

[![Build Status](https://ci.jenkins.io/job/Plugins/job/violation-comments-to-stash-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/violation-comments-to-stash-plugin)

It comments pull requests in  Bitbucket Server (or Stash) with violations found in report files from static code analysis.

It uses [Violation Comments to Bitbucket Server Lib](https://github.com/tomasbjerre/violation-comments-to-bitbucket-server-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

**Note:** Using **Bitbucket Cloud**? You may have a look at [Violation Comments to Bitbucket Cloud Command Line](https://github.com/tomasbjerre/violation-comments-to-bitbucket-cloud-command-line). This will only work with Bitbucket Server.

You can also do this with a [command line tool](https://www.npmjs.com/package/violation-comments-to-bitbucket-server-command-line).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Reporter | Parser | Notes
| --- | --- | ---
| [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)               | `CLANG`              | 
| [_AndroidLint_](http://developer.android.com/tools/help/lint.html)                    | `ANDROIDLINT`        | 
| [_AnsibleLint_](https://github.com/willthames/ansible-lint)                           | `FLAKE8`             | With `-p`
| [_Bandit_](https://github.com/PyCQA/bandit)                                           | `CLANG`              | With `bandit -r examples/ -f custom -o bandit.out --msg-template "{abspath}:{line}: {severity}: {test_id}: {msg}"`
| [_CLang_](https://clang-analyzer.llvm.org/)                                           | `CLANG`              | 
| [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)                                | `CPD`                | 
| [_CPPCheck_](http://cppcheck.sourceforge.net/)                                        | `CPPCHECK`           | With `cppcheck test.cpp --output-file=cppcheck.xml --xml`
| [_CPPLint_](https://github.com/theandrewdavis/cpplint)                                | `CPPLINT`            | 
| [_CSSLint_](https://github.com/CSSLint/csslint)                                       | `CSSLINT`            | 
| [_Checkstyle_](http://checkstyle.sourceforge.net/)                                    | `CHECKSTYLE`         | 
| [_CodeClimate_](https://codeclimate.com/)                                             | `CODECLIMATE`        | 
| [_CodeNarc_](http://codenarc.sourceforge.net/)                                        | `CODENARC`           | 
| [_Detekt_](https://github.com/arturbosch/detekt)                                      | `CHECKSTYLE`         | With `--output-format xml`.
| [_DocFX_](http://dotnet.github.io/docfx/)                                             | `DOCFX`              | 
| [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)                                   | `CLANG`              | 
| [_ERB_](https://www.puppetcookbook.com/posts/erb-template-validation.html)            | `CLANG`              | With `erb -P -x -T '-' "${it}" \| ruby -c 2>&1 >/dev/null \| grep '^-' \| sed -E 's/^-([a-zA-Z0-9:]+)/${filename}\1 ERROR:/p' > erbfiles.out`.
| [_ESLint_](https://github.com/sindresorhus/grunt-eslint)                              | `CHECKSTYLE`         | With `format: 'checkstyle'`.
| [_Findbugs_](http://findbugs.sourceforge.net/)                                        | `FINDBUGS`           | 
| [_Flake8_](http://flake8.readthedocs.org/en/latest/)                                  | `FLAKE8`             | 
| [_FxCop_](https://en.wikipedia.org/wiki/FxCop)                                        | `FXCOP`              | 
| [_GCC_](https://gcc.gnu.org/)                                                         | `CLANG`              | 
| [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)        | `GENDARME`           | 
| [_Generic reporter_]()                                                                | `GENERIC`            | Will create one single violation with all the content as message.
| [_GoLint_](https://github.com/golang/lint)                                            | `GOLINT`             | 
| [_GoVet_](https://golang.org/cmd/vet/)                                                | `GOLINT`             | Same format as GoLint.
| [_GolangCI-Lint_](https://github.com/golangci/golangci-lint/)                         | `CHECKSTYLE`         | With `--out-format=checkstyle`.
| [_GoogleErrorProne_](https://github.com/google/error-prone)                           | `GOOGLEERRORPRONE`   | 
| [_HadoLint_](https://github.com/hadolint/hadolint/)                                   | `CHECKSTYLE`         | With `-f checkstyle`
| [_IAR_](https://www.iar.com/iar-embedded-workbench/)                                  | `IAR`                | With `--no_wrap_diagnostics`
| [_Infer_](http://fbinfer.com/)                                                        | `PMD`                | Facebook Infer. With `--pmd-xml`.
| [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)                         | `JCREPORT`           | 
| [_JSHint_](http://jshint.com/)                                                        | `JSLINT`             | With `--reporter=jslint` or the CHECKSTYLE parser with `--reporter=checkstyle`
| [_JUnit_](https://junit.org/junit4/)                                                  | `JUNIT`              | It only contains the failures.
| [_KTLint_](https://github.com/shyiko/ktlint)                                          | `CHECKSTYLE`         | 
| [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)  | `KLOCWORK`           | 
| [_KotlinGradle_](https://github.com/JetBrains/kotlin)                                 | `KOTLINGRADLE`       | Output from Kotlin Gradle Plugin.
| [_KotlinMaven_](https://github.com/JetBrains/kotlin)                                  | `KOTLINMAVEN`        | Output from Kotlin Maven Plugin.
| [_Lint_]()                                                                            | `LINT`               | A common XML format, used by different linters.
| [_MSCpp_](https://visualstudio.microsoft.com/vs/features/cplusplus/)                  | `MSCPP`              | 
| [_Mccabe_](https://pypi.python.org/pypi/mccabe)                                       | `FLAKE8`             | 
| [_MyPy_](https://pypi.python.org/pypi/mypy-lang)                                      | `MYPY`               | 
| [_NullAway_](https://github.com/uber/NullAway)                                        | `GOOGLEERRORPRONE`   | Same format as Google Error Prone.
| [_PCLint_](http://www.gimpel.com/html/pcl.htm)                                        | `PCLINT`             | PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
| [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer)                               | `CHECKSTYLE`         | With `phpcs api.php --report=checkstyle`.
| [_PHPPMD_](https://phpmd.org/)                                                        | `PMD`                | With `phpmd api.php xml ruleset.xml`.
| [_PMD_](https://pmd.github.io/)                                                       | `PMD`                | 
| [_Pep8_](https://github.com/PyCQA/pycodestyle)                                        | `FLAKE8`             | 
| [_PerlCritic_](https://github.com/Perl-Critic)                                        | `PERLCRITIC`         | 
| [_PiTest_](http://pitest.org/)                                                        | `PITEST`             | 
| [_ProtoLint_](https://github.com/yoheimuta/protolint)                                 | `PROTOLINT`          | 
| [_Puppet-Lint_](http://puppet-lint.com/)                                              | `CLANG`              | With `-log-format %{fullpath}:%{line}:%{column}: %{kind}: %{message}`
| [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)                               | `PYDOCSTYLE`         | 
| [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)                                   | `FLAKE8`             | 
| [_PyLint_](https://www.pylint.org/)                                                   | `PYLINT`             | With `pylint --output-format=parseable`.
| [_ReSharper_](https://www.jetbrains.com/resharper/)                                   | `RESHARPER`          | 
| [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/)                      | `CLANG`              | With `rubycop -f clang file.rb`
| [_SbtScalac_](http://www.scala-sbt.org/)                                              | `SBTSCALAC`          | 
| [_Scalastyle_](http://www.scalastyle.org/)                                            | `CHECKSTYLE`         | 
| [_Simian_](http://www.harukizaemon.com/simian/)                                       | `SIMIAN`             | 
| [_Sonar_](https://www.sonarqube.org/)                                                 | `SONAR`              | With `mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json`. Removed in 7.7, see [SONAR-11670](https://jira.sonarsource.com/browse/SONAR-11670) but can be retrieved with: `curl --silent 'http://sonar-server/api/issues/search?componentKeys=unique-key&resolved=false' \| jq -f sonar-report-builder.jq > sonar-report.json`.
| [_Spotbugs_](https://spotbugs.github.io/)                                             | `FINDBUGS`           | 
| [_StyleCop_](https://stylecop.codeplex.com/)                                          | `STYLECOP`           | 
| [_SwiftLint_](https://github.com/realm/SwiftLint)                                     | `CHECKSTYLE`         | With `--reporter checkstyle`.
| [_TSLint_](https://palantir.github.io/tslint/usage/cli/)                              | `CHECKSTYLE`         | With `-t checkstyle`
| [_XMLLint_](http://xmlsoft.org/xmllint.html)                                          | `XMLLINT`            | 
| [_XUnit_](https://xunit.net/)                                                         | `XUNIT`              | It only contains the failures.
| [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)                    | `YAMLLINT`           | With `-f parsable`
| [_ZPTLint_](https://pypi.python.org/pypi/zptlint)                                     | `ZPTLINT`            |

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!

 
The pull request will be commented like this.

![Pull request comment](https://raw.githubusercontent.com/jenkinsci/violation-comments-to-stash-plugin/master/sandbox/screenshot-stash.png)

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Bitbucket+Server+Plugin).

## Notify Jenkins from Bitbucket Server

* You may trigger with a [webhook](https://confluence.atlassian.com/bitbucketserver/managing-webhooks-in-bitbucket-server-938025878.html) in Bitbucket Server. And consume it with [Generic Webhook Trigger plugin](https://github.com/jenkinsci/generic-webhook-trigger-plugin) to get the variables you need.

* Or, trigger with [Pull Request Notifier for Bitbucket Server](https://github.com/tomasbjerre/pull-request-notifier-for-bitbucket). It can supply any parameters and variables you may need. Here is an example URL. `http://localhost:8080/jenkins/job/builder/buildWithParameters?FROM=${PULL_REQUEST_FROM_HASH}&TO=${PULL_REQUEST_TO_HASH}&TOSLUG=${PULL_REQUEST_TO_REPO_SLUG}&TOREPO=${PULL_REQUEST_TO_HTTP_CLONE_URL}&FROMREPO=${PULL_REQUEST_FROM_HTTP_CLONE_URL}&ID=${PULL_REQUEST_ID}&PROJECT=${PULL_REQUEST_TO_REPO_PROJECT_KEY}`

## Merge

**You must perform the merge before build**. If you don't perform the merge, the reported violations will refer to other lines then those in the pull request. The merge can be done with a shell script like this.

```shell
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

```groovy
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
    maxNumberOfViolations(99999)
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(false)
    commentOnlyChangedContent(true)
    commentOnlyChangedContentContext(5)
    commentOnlyChangedFiles(true)
    keepOldComments(false)
    
    commentTemplate("""
    **Reporter**: {{violation.reporter}}{{#violation.rule}}
    
    **Rule**: {{violation.rule}}{{/violation.rule}}
    **Severity**: {{violation.severity}}
    **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
    
    **Source**: {{violation.source}}{{/violation.source}}
    
    {{violation.message}}
    """)

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

```groovy
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
   commentOnlyChangedFiles: true,
   createCommentWithAllSingleFileComments: false,
   createSingleFileComments: true,
   maxNumberOfViolations: 99999,
   keepOldComments: true,
   projectKey: 'PROJ', // Use environment variable here
   pullRequestId: '1', // Use environment variable here
   repoSlug: 'violations-test', // Use environment variable here
   
   credentialsId: 'theid',
   
   commentTemplate: """
   **Reporter**: {{violation.reporter}}{{#violation.rule}}
   
   **Rule**: {{violation.rule}}{{/violation.rule}}
   **Severity**: {{violation.severity}}
   **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
   
   **Source**: {{violation.source}}{{/violation.source}}
   
   {{violation.message}}
   """,
   
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
