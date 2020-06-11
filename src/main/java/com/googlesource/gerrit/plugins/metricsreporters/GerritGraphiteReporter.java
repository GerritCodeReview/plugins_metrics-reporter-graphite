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
import static java.util.stream.Collectors.toList;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listen
@Singleton
public class GerritGraphiteReporter implements LifecycleListener {
  private static final Logger log = LoggerFactory.getLogger(GerritGraphiteReporter.class);

  private static final String SECTION_GRAPHITE = "graphite";
  private static final String SECTION_METRICS = "metrics";
  private static final String KEY_EXCLUDE = "exclude";
  private static final String KEY_HOST = "host";
  private static final String KEY_PORT = "port";
  private static final String KEY_PREFIX = "prefix";
  private static final String KEY_RATE = "rate";
  private static final int DEFAULT_PORT = 2003;
  private static final String DEFAULT_PREFIX = "gerrit";
  private static final TimeUnit DEFAULT_RATE_UNIT = TimeUnit.SECONDS;
  private static final int DEFAULT_RATE = 60;

  private final GraphiteReporter graphiteReporter;
  private final int rate;

  @Inject
  public GerritGraphiteReporter(
      PluginConfigFactory configFactory, @PluginName String pluginName, MetricRegistry registry) {
    Config config = configFactory.getGlobalPluginConfig(pluginName);
    String host = config.getString(SECTION_GRAPHITE, null, KEY_HOST);

    if (host != null) {
      int port;
      try {
        port = config.getInt(SECTION_GRAPHITE, KEY_PORT, DEFAULT_PORT);
      } catch (IllegalArgumentException e) {
        log.warn(String.format("Invalid port value; default to %d", DEFAULT_PORT));
        port = DEFAULT_PORT;
      }
      String prefix = config.getString(SECTION_GRAPHITE, null, KEY_PREFIX);
      if (prefix == null) {
        try {
          prefix = name(DEFAULT_PREFIX, InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
          log.error("Failed to get hostname", e);
          throw new RuntimeException(e);
        }
      }

      long configRate;
      try {
        configRate =
            config.getTimeUnit(SECTION_GRAPHITE, null, KEY_RATE, DEFAULT_RATE, DEFAULT_RATE_UNIT);
      } catch (IllegalArgumentException e) {
        log.warn(String.format("Invalid rate value; default to %ds", DEFAULT_RATE));
        configRate = DEFAULT_RATE;
      }
      if (configRate > 0) {
        rate = (int) configRate;
      } else {
        log.warn(String.format("Rate value must be positive; default to %ds", DEFAULT_RATE));
        rate = DEFAULT_RATE;
      }

      log.info(
          String.format(
              "Reporting to Graphite at %s:%d with prefix %s at rate %ds",
              host, port, prefix, rate));

      Set<String> excludes =
          new HashSet<>(Arrays.asList(config.getStringList(SECTION_METRICS, null, KEY_EXCLUDE)));

      List<Pattern> excludePatterns =
          excludes.stream().map(e -> Pattern.compile(e)).collect(toList());
      Predicate<String> exclusionFilter =
          s -> excludePatterns.stream().anyMatch(e -> e.matcher(s).matches());
      graphiteReporter =
          GraphiteReporter.forRegistry(registry)
              .filter((n, m) -> !exclusionFilter.test(n))
              .convertRatesTo(TimeUnit.MINUTES)
              .convertDurationsTo(TimeUnit.MILLISECONDS)
              .prefixedWith(prefix)
              .filter(MetricFilter.ALL)
              .build(new Graphite(new InetSocketAddress(host, port)));
    } else {
      log.warn("No hostname configured; not reporting to Graphite");
      graphiteReporter = null;
      rate = 0;
    }
  }

  @Override
  public void start() {
    if (graphiteReporter != null) {
      try {
        graphiteReporter.start(rate, DEFAULT_RATE_UNIT);
      } catch (IllegalArgumentException e) {
        String msg = e.getMessage();
        if ("Reporter already started".equals(msg)) {
          log.warn(msg);
        } else {
          throw e;
        }
      }
    }
  }

  @Override
  public void stop() {
    if (graphiteReporter != null) {
      graphiteReporter.stop();
    }
  }
}
