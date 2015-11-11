include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//lib/maven.defs')

gerrit_plugin(
  name = 'metrics-reporter-graphite',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  deps = [
    '//lib/dropwizard:dropwizard-core',
    ':metrics-graphite'
  ],
  manifest_entries = [
    'Gerrit-PluginName: metrics-reporter-graphite',
  ],
)

maven_jar(
  name = 'metrics-graphite',
  id = 'io.dropwizard.metrics:metrics-graphite:3.1.2',
  sha1 = '15a68399652c6123fe6e4c82ac4f0749e2eb6583',
  license = 'Apache2.0',
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':metrics-reporter-graphite__plugin'],
)

