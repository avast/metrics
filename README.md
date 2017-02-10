# Metrics

## Introduction
Library for application monitoring. It's abstraction of metrics inspired by [Dropwizard Metrics](https://github.com/dropwizard/metrics).

Main advantages of this library:
1) Universal abstraction with (possibly) misc. implementations
1) Support of multiple exports at once (`MultiMonitor`)
1) Scala API

The entry-point into the library is the interface `Monitor`. Your classes need to get an instance of a monitor which they can use to construct different metrics, e.g. meters, timers or histograms.
Instances of the individuals metrics can be used to monitor your application.

Currently there are two available implementations/exports:
* [JMX](dropwizard) 
* [Graphite](dropwizard-graphite)

There is Scala API available in `metrics-scala`. See the example below.

### Naming of Monitors
Each monitor can be named several times which creates a hierarchy of names for the final metric. However, the end result really depends on the implementation used. In case of `JmxMetricsMonitor`
first three names are used for **type**, **scope** and **name** attributes of the metric, the rest is just concatenated using `/`.

> **Naming the monitors is very important!** Your metrics will be wrong if you give the same metric name to two unrelated metrics in different components. The `Monitor` behaves like a registry
so it creates each metric just once and returns it if asked again for the same name. All your components should receive an instance of `Monitor` that was properly named for that particular component.

```java
import com.avast.metrics.api.*;
import com.avast.metrics.dropwizard.*;

public class Handler {

    private final Meter requests;

    public Handler(Monitor monitor) {
        this.requests = monitor.newMeter("requests");
    }

    public void handle(String request) {
        requests.mark();
        ...
    }
}

Monitor monitor = null; // TODO specific monitor
Handler handler = new Handler(monitor.named("Handler1"));
```

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard.JmxMetricsMonitor

val javaMonitor = getJavaMonitor()
val scalaMonitor = Monitor(javaMonitor)
```

## Unit Testing
There is a singleton [NoOpMonitor.INSTANCE](api/src/main/java/com/avast/metrics/test/NoOpMonitor.java) in the `metrics-api` submodule that can be used in tests.  
There is also available `Monitor.noOp` for Scala API.

## Disabling JMX
Sometimes you want to globally disable JMX monitoring on the server. You can do that by setting system property `avastMetricsDisableJmx=true`. To do that from bash, you can use:

```sh
 java -jar -DavastMetricsDisableJmx="true" program.jar
```

Any value that is not `true` will be ignored.
