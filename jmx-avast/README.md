# Dropwizard JMX export - Avast custom

See [AvastJmxMetricsMonitor](src/main/java/com/avast/metrics/dropwizard/AvastJmxMetricsMonitor.java) and
[AvastTreeObjectNameFactoryTest](src/test/java/com/avast/metrics/dropwizard/AvastTreeObjectNameFactoryTest.java).

```java
import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.*;

AvastJmxMetricsMonitor monitor = new AvastJmxMetricsMonitor("com.avast.myapp");
Handler handler = new Handler(monitor.named("Handler1"));
```

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard.AvastJmxMetricsMonitor

val javaMonitor = new AvastJmxMetricsMonitor("com.avast.myapp")
val scalaMonitor = Monitor(javaMonitor)
```