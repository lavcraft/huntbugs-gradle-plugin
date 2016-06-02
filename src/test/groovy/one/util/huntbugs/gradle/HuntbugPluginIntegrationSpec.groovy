package one.util.huntbugs.gradle

import nebula.test.functional.ExecutionResult

import java.nio.file.Path

/**
 * @author tolkv
 * @since 30/05/16
 */
class HuntbugPluginIntegrationSpec extends ExtendedClasspathIntegrationSpec {

  def 'runs build'() {
    when:
    ExecutionResult result = runTasks('dependencies')

    then:
    result.failure == null
  }

  def 'setup and run build'() {
    given:
    writeHelloWorld('huntbugs.hello')

    def testBadMatch = createFile('src/main/java/huntbugs/hello/TestBadMatch.java')
    testBadMatch.text = '''package huntbugs.hello;
    public class TestBadMatch {
        public static int bitAdd2(int src, byte add) {
          return (src & 0xFFFFFF00) + add;
        }
        public static int testOrZero(int x) {
          int arg = 0;
          return x | arg;
        }
        public static void main(String[] args){
          System.out.println("args = " + String.valueOf(bitAdd2(2,(byte)3)));
          System.out.println("args = " + String.valueOf(testOrZero(20)));
        }
    }
    '''.stripIndent().stripMargin()

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

    result.wasExecuted(':compileJava')
    result.wasExecuted(':classes')

    fileExists('build/huntbugs/report.xml')

    Path resolve = projectDir.toPath().resolve('build/huntbugs/report.xml')

    def report = new XmlParser().parse(resolve.toFile())
    def warnings = report.WarningList.Warning
    warnings
    warnings.size() == 2
    def redundantCodeWarning = warnings.find { it.@Category == 'RedundantCode' && it.@Type == 'UselessOrWithZero' }
    redundantCodeWarning.Class.@SourceFile == ['TestBadMatch.java']
    redundantCodeWarning.LongDescription.text()
    warnings.find { it.@Category == 'Correctness' && it.@Type == 'BitAddSignedByte' }
  }

}
