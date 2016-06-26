package one.util.huntbugs.gradle.report

import one.util.huntbugs.analysis.HuntBugsResult
import one.util.huntbugs.output.Reports
/**
 * @author tolkv
 * @since 26/06/16
 */
class DefaultReportsCenter implements ReportsCenter {
  List<HuntBugsResult> contexts
  Map<String, Renderer> renderers

  DefaultReportsCenter() {
    this.contexts = []
    this.renderers = new LinkedHashMap<>()
  }

  @Override
  void addSource(HuntBugsResult context) {
    contexts.add context
  }

  @Override
  void addRenderer(String name, Renderer renderer) {
    renderers.put name, renderer
  }

  @Override
  List<String> computeAll() {
    HuntBugsResult result = Reports.merge(contexts)

    return renderers.collect { String k, Renderer r ->
      r.setResult(result)
      r.render()
      return r.getArtifacts()
    }.flatten() as List<String>
  }
}
