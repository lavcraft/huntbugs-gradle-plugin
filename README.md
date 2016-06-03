HuntBugs Gradle Plugin
======================

Gradle plugin for static analyzer tool [HuntBugs](https://github.com/amaembo/huntbugs)

[![Build Status](https://travis-ci.org/lavcraft/huntbugs-gradle-plugin.svg?branch=master)](https://travis-ci.org/lavcraft/huntbugs-gradle-plugin)
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
