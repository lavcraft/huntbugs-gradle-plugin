package one.util.huntbugs.gradle

import nebula.test.functional.ExecutionResult
import spock.lang.Unroll

import java.nio.file.Path
/**
 * @author tolkv
 * @since 30/05/16
 */
class HuntbugPluginDiffIntegrationSpec extends ExtendedClasspathIntegrationSpec {

  @Unroll
  def 'setup and run build with property diff is #diff'() {
    given:
    writeHelloWorld('huntbugs.hello')

    def testBadMatch = createFile('src/main/java/huntbugs/hello/TestBadMatch.java')
    def reportXml = createFile('build/huntbugs/report.xml')

    testBadMatch.text = defaultMainJava
    reportXml.text = defaultReportXml

    buildFile << defaultBuildGradle(applyPlugin(HuntBugsPlugin), diff)

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

    def warnings = new XmlParser().parse(resolve.toFile()).WarningList.Warning
    warnings?.size() == 2

    def redundantCodeWarning = warnings.find {
      it.@Category == 'RedundantCode' && it.@Type == 'UselessOrWithZero' && it.@Status == status
    }

    redundantCodeWarning
    if (redundantCodeWarning) {
      redundantCodeWarning.Class.@SourceFile == ['TestBadMatch.java']
      redundantCodeWarning.LongDescription.text()
      warnings.find { it.@Category == 'Correctness' && it.@Type == 'BitAddSignedByte' }
    }

    where:
    diff << [true, false]
    status << ['added', 'default']
  }

  def 'setup and run build with specific diffFile'() {
    given:
    writeHelloWorld('huntbugs.hello')

    def testBadMatch = createFile('src/main/java/huntbugs/hello/TestBadMatch.java')
    def reportXml = createFile('truth.xml')

    testBadMatch.text = defaultMainJava
    reportXml.text = defaultReportXml

    buildFile << """
            apply plugin: 'java'
            ${applyPlugin(HuntBugsPlugin)}

            repositories {
              mavenLocal()
              jcenter()
            }
            huntbugs {
              minScore=10
              diff=true
              diffFile=file('truth.xml')
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

    def warnings = new XmlParser().parse(resolve.toFile()).WarningList.Warning
    warnings?.size() == 2

    def redundantCodeWarning = warnings.find {
      it.@Category == 'RedundantCode' && it.@Type == 'UselessOrWithZero' && it.@Status == 'added'
    }

    redundantCodeWarning
    if (redundantCodeWarning) {
      redundantCodeWarning.Class.@SourceFile == ['TestBadMatch.java']
      redundantCodeWarning.LongDescription.text()
      warnings.find { it.@Category == 'Correctness' && it.@Type == 'BitAddSignedByte' }
    }
  }

  private static String defaultReportXml = '''<?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <HuntBugs>
      <WarningList>
        <Warning Category="Correctness" Score="35" Status="default" Type="BitAddSignedByte">
          <Title>Adding a signed byte to the value with low 8 bits clear</Title>
          <Description>Adding a signed byte to the value with low 8 bits clear in TestBadMatch.bitAdd2().</Description>
          <LongDescription><![CDATA[This code adds a signed byte value to another value which has low 8 bits clear like <code>(x << 8) + byteValue</code>.
          Probably it was intended to add unsigned byte value like <code>(x << 8) + (byteValue &amp; 0xFF)</code>.]]></LongDescription>
          <Class Name="huntbugs/hello/TestBadMatch" SourceFile="TestBadMatch.java"/>
          <Method Name="bitAdd2" Signature="(IB)I"/>
          <Location Line="4" Offset="6" SourceFile="TestBadMatch.java"/>
        </Warning>
      </WarningList>
    </HuntBugs>
    '''.stripIndent().stripMargin()

  private static String defaultMainJava = '''package huntbugs.hello;
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

  public static String defaultBuildGradle(String plugin, Boolean diff) {
    return """
            apply plugin: 'java'
            ${plugin}

            repositories {
              mavenLocal()
              jcenter()
            }
            huntbugs {
              minScore=10
              diff=${diff}
            }
            dependencies {
              compile 'org.apache.commons:commons-lang3:3.1'
            }
        """.stripIndent()
  }
}
