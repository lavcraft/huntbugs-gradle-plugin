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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.util.function.Predicate
/**
 * HuntBugs task for run static analyse on your classes
 *
 * @author tolkv
 * @since 30/05/16
 *
 * @see HuntBugsExtension - contains default values
 */
class HuntBugsTask extends DefaultTask {

  @InputDirectory
  def File classesDir

  @OutputDirectory
  File outputDirectory

  Predicate<String> classSimpleFilter
  int minScore
  boolean quiet
  String analyzePackage

  @TaskAction
  protected void runAnalysis() {

  }

}
