package one.util.huntbugs.gradle.report

import one.util.huntbugs.analysis.HuntBugsResult
/**
 * @author tolkv
 * @since 26/06/16
 */
interface ReportsCenter {
  void addSource(HuntBugsResult context)
  void addRenderer(String type, Renderer renderer)
  List<String> computeAll()
}
