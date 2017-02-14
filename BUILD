load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-graphite",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-graphite",
    ],
    deps = [
        "@dropwizard-core//jar",
        "@metrics_graphite//jar",
    ],
)
