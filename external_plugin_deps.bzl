load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.0.5",
        sha1 = "76e8758356373d5aed5abacbda429b38f6e8fa98",
    )
