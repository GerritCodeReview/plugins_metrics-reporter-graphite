load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.0.2",
        sha1 = "c58939a16560d60d3f3d7fe941d2dc9f41ba3b6c",
    )
