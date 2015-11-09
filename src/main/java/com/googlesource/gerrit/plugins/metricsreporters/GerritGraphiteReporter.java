// Copyright (C) 2012 The Android Open Source Project
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

import com.google.common.base.MoreObjects;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginMetrics;
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Listen
@Singleton
public class GerritGraphiteReporter
    implements LifecycleListener {
  private final GraphiteReporter graphiteReporter;

  @Inject
  public GerritGraphiteReporter(PluginConfigFactory configFactory,
      @PluginName String pluginName, @PluginMetrics MetricRegistry registry) {
    Config config = configFactory.getGlobalPluginConfig(pluginName);
    String host =
        MoreObjects.firstNonNull(config.getString("graphite", null, "host"), "localhost");
    try {
      Graphite graphite = new Graphite(
          new InetSocketAddress(host, config.getInt("graphite", "port", 2003)));

      graphiteReporter = GraphiteReporter.forRegistry(registry)
          .convertRatesTo(TimeUnit.MINUTES)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .prefixedWith(defVal(config.getString("graphite", null, "prefix"),
              name("gerrit", InetAddress.getLocalHost().getHostName())))
          .filter(MetricFilter.ALL).build(graphite);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public String defVal(String str, String defaultValue) {
    return str != null ? str : defaultValue;
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
