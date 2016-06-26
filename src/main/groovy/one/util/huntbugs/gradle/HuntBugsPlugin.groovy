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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <b>HuntBugs gradle plugin</b>
 * Configure gradle project. Add <i>huntbugs</i> task, which analyse all classes in dir <i>sourceSet.main.output</i>
 *
 *
 * @author tolkv
 * @since 30/05/16
 *
 * @see HuntBugsExtension - contains default values
 * @see HuntBugsTask execute analyse logic
 */
@Slf4j
@CompileStatic
class HuntBugsPlugin implements Plugin<Project> {

  public static final String HUNTBUGS_EXTENSION_NAME = 'huntbugs'
  public static final String HUNTBUGS_TASK_NAME = 'huntbugs'

  @Delegate
  Project project

  @Override
  void apply(Project project) {
    this.project = project
    def huntBugsExtension = project.extensions.create(HUNTBUGS_EXTENSION_NAME, HuntBugsExtension, project)

    HuntBugsTask huntBugsTask = tasks.create(
        name: HUNTBUGS_TASK_NAME,
        type: HuntBugsTask,
        group: 'Verification',
        description: 'Start static analyse your project by HuntBugs. See more info https://github.com/amaembo/huntbugs',
        dependsOn: 'classes'
    ) as HuntBugsTask

    afterEvaluate {
      huntBugsTask.classSimpleFilter = huntBugsExtension.classSimpleFilter
      huntBugsTask.outputDirectory = huntBugsExtension.outputDirectory
      huntBugsTask.minScore = huntBugsExtension.minScore
      huntBugsTask.classesDir = huntBugsExtension.classesDir
      huntBugsTask.quiet = huntBugsExtension.quiet
      huntBugsTask.analyzePackage = huntBugsExtension.analyzePackage
      huntBugsTask.diff = huntBugsExtension.diff
      huntBugsTask.diffFile = huntBugsExtension.diffFile
    }

  }

}
