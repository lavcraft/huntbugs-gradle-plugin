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

import com.strobel.annotations.NotNull
import com.strobel.assembler.metadata.ClasspathTypeLoader
import com.strobel.assembler.metadata.CompositeTypeLoader
import com.strobel.assembler.metadata.ITypeLoader
import com.strobel.assembler.metadata.JarTypeLoader
import one.util.huntbugs.analysis.AnalysisListener
import one.util.huntbugs.analysis.AnalysisOptions
import one.util.huntbugs.analysis.Context
import one.util.huntbugs.output.Reports
import one.util.huntbugs.repo.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.util.function.Predicate
import java.util.jar.JarFile

/**
 * HuntBugs task for run static analyse on your classes
 *
 * @author tolkv
 * @since 30/05/16
 *
 * @see HuntBugsExtension - contains default values
 */
class HuntBugsTask extends DefaultTask {
  public static final int FORMAT_BLOCK_WIDTH = 80

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
    def convention = extractJavaPluginConvention(project)

    if (!convention)
      return

    logger.debug 'HuntBugs plugin'.center(FORMAT_BLOCK_WIDTH, '=')

    tryToStartAnalyse(extractClassesDir(convention), project)
  }

  def tryToStartAnalyse(File classesDir, Project project) {
    logger.debug 'HuntBugs: +dir {}', classesDir

    try {
      List<ITypeLoader> dependencyLoaders = project.configurations.getByName('compile')
          .resolvedConfiguration.resolvedArtifacts
          .collect(this.&tryConstructTypeLoaderOrNull)
          .findAll { loader -> loader != null }

      Repository projectRepository = new FilteredRepository(
          new DirRepository(classesDir.toPath()), classSimpleFilter)

      Repository repositoryWithDependencies
      if (!dependencyLoaders) {
        repositoryWithDependencies = projectRepository
      } else {
        repositoryWithDependencies = new CompositeRepository([
            projectRepository,
            new AuxRepository(
                new CompositeTypeLoader(dependencyLoaders as ITypeLoader[])
            )
        ])
      }

      Context ctx = new Context(repositoryWithDependencies, constructOptions());

      addAnalysisProgressListener(ctx)

      ctx.analyzePackage(analyzePackage)

      Path path = outputDirectory.toPath()
      Path xmlFile = path.resolve("report.xml")
      Path htmlFile = path.resolve("report.html")

      Reports.write xmlFile, htmlFile, ctx
    } catch (e) {
      logger.error "error", e
      throw new GradleException('Error during configuring huntbugs context', e)
    }
  }

  private AnalysisOptions constructOptions() {
    def options = new AnalysisOptions()
    options.minScore = minScore
    return options
  }

  protected ITypeLoader tryConstructTypeLoaderOrNull(ResolvedArtifact artifact) {
    if (artifact.file.isFile()) {
      return new JarTypeLoader(new JarFile(artifact.file))
    } else if (artifact.file.isDirectory()) {
      return new ClasspathTypeLoader(artifact.file.toString())
    }

    logger.info 'cannot construct type loader for artifact {}', artifact.name
    return null
  }

  File extractClassesDir(@NotNull JavaPluginConvention convention) {

    if (classesDir && classesDir.exists())
      return classesDir

    return convention.sourceSets
        .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        .output
        .classesDir
  }

  private JavaPluginConvention extractJavaPluginConvention(Project project) {
    JavaPluginConvention javaPluginConvention = project.convention.findPlugin(JavaPluginConvention)

    if (javaPluginConvention) {
      return javaPluginConvention
    }

    project.logger.error 'Cannot find java or groovy plugin. Apply it to project {}', project.name
    return null
  }

  private void addAnalysisProgressListener(Context ctx) {
    long lastPrint = 0;
    ctx.addListener({ stepName, className, count, total ->

      if (count == total || System.currentTimeMillis() - lastPrint > 2000) {
        project.logger.info 'HuntBugs: {} [{}/{}]', stepName, count, total
        lastPrint = System.currentTimeMillis();
      }

      return true;
    } as AnalysisListener)
  }

}
