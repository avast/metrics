# StatsD export

Unlike Graphite which is a _dumb_ storage the StatsD is used for aggregation of data from multiple sources 
(see e.g. [Statsite](http://statsite.github.io/statsite/)).

Use [StatsDMetricsMonitor](statsd/src/main/java/com/avast/metrics/statsd/StatsDMetrisMonitor.java) to write metrics to StatsD.

## Examples

### Java API

```java
import com.avast.metrics.api.*;
import com.avast.metrics.statsd.*;

Monitor monitor = new StatsDMetricsMonitor(statsDHost, statsDPort, prefix);
Handler handler = new Handler(monitor.named("Handler1"));
// ...
monitor.close();
```

### Scala API

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard._

val monitor = Monitor(new StatsDMetricsMonitor(statsDHost, statsDPort, prefix))
// ...
monitor.close()
```


## Sampling

`StatsDMetricsMonitor` supports sampling which prevents your statsd backend to be overloaded. It takes advantage of
[statsd metrics format](https://github.com/etsy/statsd/blob/master/docs/metric_types.md) feature directly supported
by statsd client libraries.

To define sample rate instantiate and configure `MetricsFilter`. It also allows you to fully disable a certain
tree of metrics.

```java
Config filterConfig = ConfigFactory.load().getConfig("filter");
MetricsFilter metricsFilter MetricsFilter.fromConfig(filterConfig, ".");

StatsDMetricsMonitor monitor = new StatsDMonitor(..., metricsFilter);
monitor.newCounter("requests");
// ...
monitor.close();
```

```
// TypeSafe config *.conf
filter {
    requests.enabled = true
    requests.sampleRate = 0.01    // ~ 1%, range 0.0 - 1.0
}
```
