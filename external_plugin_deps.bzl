load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.1.6",
        sha1 = "9f7352925575904c3d3b6a085fc5a2c920c62275",
    )
