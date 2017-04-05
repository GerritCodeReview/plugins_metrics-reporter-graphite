load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'metrics_graphite',
    artifact = 'io.dropwizard.metrics:metrics-graphite:3.2.2',
    sha1 = '908e8cbec1bbdb2f4023334e424c7de2832a95af',
  )
