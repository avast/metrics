# StatsD export

Unlike Graphite which is a _dumb_ storage the StatsD is used for aggregation of data from multiple sources (see e.g. [Statsite](http://statsite.github.io/statsite/)).

See [StatsDMetricsMonitor](statsd/src/main/java/com/avast/metrics/statsd/StatsDMetrisMonitor.java).

```java
import com.avast.metrics.api.*;
import com.avast.metrics.statsd.*;

Monitor monitor = new StatsDMetricsMonitor(statsDHost, statsDPort, prefix);
Handler handler = new Handler(monitor.named("Handler1"));
```

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard._

val rootMonitor = Monitor(new StatsDMetricsMonitor(statsDHost, statsDPort, prefix))
```