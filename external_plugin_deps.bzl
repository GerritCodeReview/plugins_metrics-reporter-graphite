load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "metrics_graphite",
        artifact = "io.dropwizard.metrics:metrics-graphite:4.0.3",
        sha1 = "557cb9adbd442852e12f8f41ddb81a889f670139",
    )
