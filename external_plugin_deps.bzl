load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.1.12.1",
        sha1 = "f7a5ef6ac15eee9af221d052821d12a08bf8bcdf",
    )
