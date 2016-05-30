package one.util.huntbugs.gradle

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

import java.nio.file.Path
/**
 * @author tolkv
 * @since 30/05/16
 */
class HuntbugPluginIntegrationSpec extends IntegrationSpec {

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
    import one.util.huntbugs.registry.anno.AssertWarning;
    public class TestBadMatch {
        public static int bitAdd2(int src, byte add) {
          return (src & 0xFFFFFF00) + add;
        }
        @AssertWarning(type="UselessOrWithZero")
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

    buildFile << '''
            buildscript {
              repositories {
                mavenLocal()
                jcenter()
              }
              dependencies {
                classpath 'one.util:huntbugs:0.0.4'
              }
            }

            apply plugin: 'java'
            apply plugin: 'one.util.huntbugs'

            repositories {
              mavenLocal()
              jcenter()
            }
            huntbugs {
              minScore=10
            }
            dependencies {
              compile 'org.apache.commons:commons-lang3:3.1'
              compile 'one.util:huntbugs:0.0.4'
            }
        '''.stripIndent()

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
    resolve.readLines()
        .findAll { it.contains('TestBadMatch') && it.contains('UselessOrWithZero') }
        .size() == 1

  }
}
