load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.0.7",
        sha1 = "1b4f638d8d7474345337f2cb12da5c56ed4443b7",
    )
