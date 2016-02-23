// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.metricsreporters;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.MoreObjects.firstNonNull;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Listen
@Singleton
public class GerritGraphiteReporter implements LifecycleListener {
  private static final Logger log =
      LoggerFactory.getLogger(GerritGraphiteReporter.class);

  private final GraphiteReporter graphiteReporter;

  @Inject
  public GerritGraphiteReporter(
      PluginConfigFactory configFactory,
      @PluginName String pluginName,
      MetricRegistry registry) {
    Config config = configFactory.getGlobalPluginConfig(pluginName);
    String host = firstNonNull(
        config.getString("graphite", null, "host"), "localhost");
    int port = config.getInt("graphite", "port", 2003);
    String prefix = config.getString("graphite", null, "prefix");
    if (prefix == null) {
      try {
        prefix = name("gerrit", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException e) {
        log.error("Failed to get hostname", e);
        throw new RuntimeException(e);
      }
    }
    log.info(
        String.format("Reporting to Graphite at host %s on port %d with prefix %s",
        host, port, prefix));

    graphiteReporter = GraphiteReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.MINUTES)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .prefixedWith(prefix)
        .filter(MetricFilter.ALL)
        .build(new Graphite(new InetSocketAddress(host, port)));
  }

  @Override
  public void start() {
    graphiteReporter.start(1, TimeUnit.MINUTES);
  }

  @Override
  public void stop() {
    graphiteReporter.stop();
  }
}
