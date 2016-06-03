HuntBugs Gradle Plugin
======================

Gradle plugin for static analyzer tool [HuntBugs](https://github.com/amaembo/huntbugs)

[![Build Status](https://travis-ci.org/lavcraft/huntbugs-gradle-plugin.svg?branch=master)](https://travis-ci.org/lavcraft/huntbugs-gradle-plugin)
[![Coverage Status](https://coveralls.io/repos/github/lavcraft/huntbugs-gradle-plugin/badge.svg?branch=master)](https://coveralls.io/github/lavcraft/huntbugs-gradle-plugin?branch=master)
[![Join the chat at https://gitter.im/amaembo/huntbugs](https://badges.gitter.im/amaembo/huntbugs.svg)](https://gitter.im/amaembo/huntbugs?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

### Applying the Plugin

To include, add the following to your build.gradle

    buildscript {
      repositories { jcenter() }

      dependencies {
        classpath 'one.util.huntbugs:huntbugs-gradle-plugin:x.x.+'
      }
    }

    apply plugin: 'one.util.huntbugs'

### Tasks Provided

`huntbugs` run static analyse

### Extensions Provided

    huntbugs {
        classesDir = file("${project.buildDir}/classes") // source class directory
        classSimpleFilter = { true } // { it.endsWith('MyClassForAnalyse') } // class filter
        outputDirectory = file("${project.buildDir}/classes") // directory for save reports and analyse data
        minScore = 30 // HuntBugs score
        quiet = false // quiet mode, deprecated. Use gradle --info/--debug/--stacktrace
        analyzePackage = '' //default package for start analyse
    }

### Reports

Reports may found in ${project.buildDir}/huntbugs/

### Test

Run `./gradlew test` an see results in console output or follow to `./build/test/one.util.huntbugs....IntegrationSpec/setup-and-run-buid_{RUN_NUMBER}`
This directory contain integration test data for you, explore it :)

### Build and publish

1. `./gradlew build` build
2. `./gradlew pTML` publish to maven local

Or make pull request. New code build in CI and give you feedback about status and coverage changes :)

For advanced information see build script in `./gradle/buildViaTravis.sh`

SNAPSHOTS and DEV artifacts contains in [HuntBugs Artifactory](https://huntbugs.jfrog.io/huntbugs/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/one/util/huntbugs/huntbugs-gradle-plugin). You may use it if you are a plugin developer.
If you have a credentials for access to artifactory, you can use it! Otherwise, chat with me in [gitter](https://gitter.im/lavcraft/huntbugs-gradle-plugin) [![Join the chat at https://gitter.im/lavcraft/huntbugs-gradle-plugin](https://badges.gitter.im/lavcraft/huntbugs-gradle-plugin.svg)](https://gitter.im/lavcraft/huntbugs-gradle-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) and i try to help you.

3. `./gradlew snapshot` use for build and deploy to snapshots repository

### Release flow in travis ci

1. Pull Request - *eys*
    * simple build by `./gradlew build`
2. Pull Request - *no* and Git Tag: *no*
    * build and deploy snapshot to artifactory by `./gradlew snapshot` command
3. Pull Request - *no* and Git Tag: *yes*
    * If tag is `*-rc\.*` - build release candidate by `./gradlew candidate`
    * Otherwise build release (final) - `./gradlew final publishPlugins` and publish plugins to [plugins.gradle.com](http://plugins.gradle.com)