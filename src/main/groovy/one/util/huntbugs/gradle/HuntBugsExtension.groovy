/*
 * Copyright 2016 HuntBugs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.util.huntbugs.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

import java.util.function.Predicate
/**
 * HuntBugs gradle extension. It provide dsl for configure plugin in build.gradle files
 *
 * @author tolkv
 * @since 30/05/16
 *
 * @see HuntBugsPlugin#HUNTBUGS_EXTENSION_NAME for default dsl root
 */
class HuntBugsExtension {
  private final Project project

  /**
   * Directory was contained analysing classes
   */
  File classesDir

  /**
   * Exclude filters
   */
  Predicate<String> classSimpleFilter = { true } as Predicate<String>

  /**
   * Directory for output data and reports
   */
  File outputDirectory = new File(project.buildDir, 'huntbugs')

  /**
   * HuntBugs minimum score for all analysers
   */
  int minScore = 30

  /**
   * Quiet mode, suppress significant part of logging
   */
  boolean quiet = false

  /**
   * Package for analyze
   * @see one.util.huntbugs.analysis.Context#analyzePackage(java.lang.String)
   */
  String analyzePackage = ''

  HuntBugsExtension() {}

  HuntBugsExtension(Project project) {
    this.project = project
    JavaPluginConvention javaPluginConvention = project.convention.findPlugin(JavaPluginConvention)

    this.classesDir = javaPluginConvention?.sourceSets?.getByName(SourceSet.MAIN_SOURCE_SET_NAME)?.output?.classesDir
  }
}
