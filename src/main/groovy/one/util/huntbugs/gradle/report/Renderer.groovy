package one.util.huntbugs.gradle.report

import com.strobel.annotations.NotNull
import one.util.huntbugs.analysis.HuntBugsResult

/**
 * @author tolkv
 * @since 26/06/16
 */
interface Renderer {

  /**
   * Inject HuntBugs result to renderer
   *
   * @param huntBugsResult
   */
  public void setResult(@NotNull HuntBugsResult huntBugsResult)

  /**
   * Render report artifact
   */
  public void render()

  /**
   * For resolve coordinates of artifact, which will be constructed or was constructed
   *
   * @return Resulted report artifact like file or url
   */
  public List<String> getArtifacts()

}