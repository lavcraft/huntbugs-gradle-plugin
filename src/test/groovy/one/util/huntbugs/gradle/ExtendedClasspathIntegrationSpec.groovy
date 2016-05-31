package one.util.huntbugs.gradle

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import com.google.common.base.StandardSystemProperty
import nebula.test.IntegrationSpec
import nebula.test.functional.GradleRunner

import java.nio.file.Paths

/**
 * @author tolkv
 * @since 5/31/2016
 */
class ExtendedClasspathIntegrationSpec extends IntegrationSpec {
    def setup(){
        classpathFilter = Predicates.<URL>or(
                { URL url ->
                    File userDir = new File(StandardSystemProperty.USER_DIR.value())
                    Paths.get(url.toURI()).startsWith(userDir.toPath())
                } as Predicate,
                GradleRunner.CLASSPATH_GRADLE_CACHE)

    }
}
