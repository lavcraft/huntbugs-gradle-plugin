HuntBugs Gradle Plugin
======================

Huntbugs gradle plugin

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
        classesDir = file("${project.buildDir}/classes")
        classSimpleFilter = { true } // { it.endsWith('MyClassForAnalyse') }
        outputDirectory = file("${project.buildDir}/classes")
        minScore = 30
        quiet = false
        analyzePackage = ''
    }
