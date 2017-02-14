load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'metrics_graphite',
    artifact = 'io.dropwizard.metrics:metrics-graphite:3.1.2',
    sha1 = '15a68399652c6123fe6e4c82ac4f0749e2eb6583',
  )
