package one.util.huntbugs.gradle

import nebula.test.functional.ExecutionResult
/**
 * @author tolkv
 * @since 30/05/16
 */
class HuntbugsEmptyProjectIntegrationSpec extends ExtendedClasspathIntegrationSpec {

  def 'runs build'() {
    given:
    buildFile << """
            apply plugin: 'java'
            ${applyPlugin(HuntBugsPlugin)}

            repositories {
              mavenLocal()
              jcenter()
            }
            huntbugs {
              minScore=10
            }
            dependencies {
              compile 'org.apache.commons:commons-lang3:3.1'
            }
        """.stripIndent()

    when:
    ExecutionResult result = runTasks('huntbugs')

    then:
    println result.standardOutput
    println result.standardError

    result.failure == null

  }
}
