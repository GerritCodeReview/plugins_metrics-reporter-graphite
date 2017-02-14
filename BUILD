load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "metrics-reporter-graphite",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: metrics-reporter-graphite",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@metrics_graphite//jar",
    ],
)
