/*
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.autoconfigure.prometheus;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipkinPrometheusMetricsAutoConfigurationTest {
  AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  public void refresh() {
    context.register(
      PropertyPlaceholderAutoConfiguration.class,
      MetricsAutoConfiguration.class,
      PrometheusMetricsExportAutoConfiguration.class,
      ZipkinPrometheusMetricsAutoConfiguration.class
    );
    context.refresh();
  }

  @After public void close() {
    context.close();
  }

  @Test public void providesHttpRequestDurationCustomizer() {
    refresh();

    context.getBean(UndertowDeploymentInfoCustomizer.class);
  }

  @Test public void defaultMetricName() {
    refresh();

    assertThat(context.getBean(ZipkinPrometheusMetricsAutoConfiguration.class).metricName)
      .isEqualTo("http.server.requests");
  }

  @Test public void overrideMetricName() {
    TestPropertyValues.of("management.metrics.web.server.requests-metric-name:foo").applyTo(context);
    refresh();

    assertThat(context.getBean(ZipkinPrometheusMetricsAutoConfiguration.class).metricName)
      .isEqualTo("foo");
  }
}
