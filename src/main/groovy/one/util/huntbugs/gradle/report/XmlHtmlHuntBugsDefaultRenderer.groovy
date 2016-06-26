package one.util.huntbugs.gradle.report

import com.strobel.annotations.NotNull
import one.util.huntbugs.analysis.HuntBugsResult
import one.util.huntbugs.output.Reports

import java.nio.file.Path
/**
 * @author tolkv
 * @since 26/06/16
 */
class XmlHtmlHuntBugsDefaultRenderer implements Renderer {
  private Path xmlReport
  private Path htmlReport
  private HuntBugsResult huntBugsResult

  XmlHtmlHuntBugsDefaultRenderer(Path xmlReport, Path htmlReport) {
    this.xmlReport = xmlReport
    this.htmlReport = htmlReport
  }

  @Override
  void setResult(@NotNull HuntBugsResult huntBugsResult) {
    this.huntBugsResult = huntBugsResult
  }

  @Override
  void render() {
    Reports.write(xmlReport, htmlReport, huntBugsResult)
  }

  @Override
  List<String> getArtifacts() {
    if (xmlReport != null && htmlReport != null) {
      return [xmlReport.toAbsolutePath().toString(),
              htmlReport.toAbsolutePath().toString()]
    }

    return Collections.emptyList()
  }
}
