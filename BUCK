gerrit_plugin(
  name = 'metrics-reporter-graphite',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  deps = [
    '//lib/dropwizard:dropwizard-core',
    '//lib/dropwizard:metrics-graphite'
  ],
  manifest_entries = [
    'Gerrit-PluginName: metrics-reporter-graphite',
  ],
)
