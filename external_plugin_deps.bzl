load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'metrics_graphite',
    artifact = 'io.dropwizard.metrics:metrics-graphite:3.2.1',
    sha1 = '97b0457cab5ee94da3a946f6bb95053fac2181e2',
  )
