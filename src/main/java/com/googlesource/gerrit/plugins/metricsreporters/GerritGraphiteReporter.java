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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.metrics.GerritMetrics;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import org.eclipse.jgit.lib.Config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Singleton
public class GerritGraphiteReporter
    implements LifecycleListener, Module {
  private GraphiteReporter graphiteReporter = null;
  private Config config;

  @Inject
  public GerritGraphiteReporter(PluginConfigFactory configFactory,
      @PluginName String pluginName, GerritMetrics metrics) {
    config = configFactory.getGlobalPluginConfig(pluginName);
    String host =
        defVal(config.getString("graphite", null, "host"), "localhost");
    Graphite graphite = new Graphite(
        new InetSocketAddress(host, config.getInt("graphite", "port", 2003)));
    try {
      graphiteReporter = GraphiteReporter.forRegistry(metrics.getRegistry())
          .convertRatesTo(TimeUnit.MINUTES)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .prefixedWith(defVal(config.getString("graphite", null, "prefix"),
              name("gerrit", InetAddress.getLocalHost().getHostName())))
          .filter(MetricFilter.ALL).build(graphite);
    } catch (UnknownHostException e) {
      e.printStackTrace();
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

  @Override
  public void configure(Binder binder) {
    DynamicSet.bind(binder, LifecycleListener.class).to(GerritGraphiteReporter.class);
  }
}
