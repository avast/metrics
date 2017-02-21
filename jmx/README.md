# Dropwizard JMX export

See [JmxMetricsMonitor](jmx/src/main/java/com/avast/metrics/dropwizard/JmxMetricsMonitor.java).

```java
import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.*;

JmxMetricsMonitor monitor = new JmxMetricsMonitor("com.avast.myapp");
Handler handler = new Handler(monitor.named("Handler1"));
```

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard.JmxMetricsMonitor

val javaMonitor = new JmxMetricsMonitor("com.avast.myapp")
val scalaMonitor = Monitor(javaMonitor)
```