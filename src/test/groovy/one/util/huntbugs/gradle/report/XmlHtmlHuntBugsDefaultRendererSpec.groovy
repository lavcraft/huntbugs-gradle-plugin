package one.util.huntbugs.gradle.report

import spock.lang.Specification

/**
 * @author tolkv
 * @since 26/06/16
 */
class XmlHtmlHuntBugsDefaultRendererSpec extends Specification {
  def 'return empty list if artifacts not generated yet'(){
    given:
    def subject = new XmlHtmlHuntBugsDefaultRenderer(null, null)

    when:
    def result = subject.getArtifacts()

    then:
    result.isEmpty()
  }
}
