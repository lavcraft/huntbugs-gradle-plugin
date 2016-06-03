package one.util.huntbugs.gradle

import nebula.test.PluginProjectSpec

class HuntbugsPluginSpec extends PluginProjectSpec {

  @Override
  String getPluginName() {
    'one.util.huntbugs'
  }

  def 'add huntbugs task'() {
    when:
    project.plugins.apply(HuntBugsPlugin)

    then:
    project.tasks.getByName('huntbugs')
    project.extensions.getByName('huntbugs')
  }

}
