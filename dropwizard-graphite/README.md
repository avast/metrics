# Dropwizard Graphite export

See [GraphiteMetricsMonitor](dropwizard-graphite/src/main/java/com/avast/metrics/dropwizard/GraphiteMetricsMonitor.java).

```java
import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.*;

Monitor monitor = new GraphiteMetricsMonitor(graphiteHost, publishDomain);
Handler handler = new Handler(monitor.named("Handler1"));
```

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard._

val rootMonitor = Monitor(new GraphiteMetricsMonitor(graphiteHost, publishDomain))
```