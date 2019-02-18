[![Build Status](https://travis-ci.org/avast/metrics.svg?branch=master)](https://travis-ci.org/avast/metrics) [![Download](https://api.bintray.com/packages/avast/maven/metrics/images/download.svg) ](https://bintray.com/avast/maven/metrics/_latestVersion)

# Metrics

Java/Scala library defining API for metrics publishing. Implementation for Dropwizard Metrics is provided.  
The library is incubating and there are some planned improvements. There can be some breaking changes in following major releases.

## Introduction
Library for application monitoring. It's abstraction of metrics inspired by [Dropwizard Metrics](https://github.com/dropwizard/metrics).

Main advantages of this library:
1) Universal abstraction with misc. implementations
1) Support of multiple exports at once (`MultiMonitor`)
1) Scala API

The entry-point into the library is the interface `Monitor`. Your classes need to get an instance of a monitor which they can use to construct different metrics, e.g. meters, timers or histograms.
Instances of the individuals metrics can be used to monitor your application.

Currently there are multiple implementations/exports:
* [JMX](jmx) (+ Avast specific format of export [jmx-avast](jmx-avast))
* [Graphite](graphite)
* [StatsD](statsd)
* [Formatting](formatting) to string in configurable formats, e.g. for serving via HTTP server

There is Scala API available in `metrics-scala`. See the example below.

## Adding to project
The library is published to [Bintray](https://bintray.com/avast/maven/metrics/). Example usage of the [StatsD](statsd) in Gradle project: 
```gradle
repositories {
    jcenter()
}
dependencies {
    compile "com.avast.metrics:metrics-statsd:$versionHere"
}
```

### Naming of Monitors
Each monitor can be named several times which creates a hierarchy of names for the final metric.

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

## Scala API

An easy-to-use Scala API is available in `scala-api` module. Wrap the Java `Monitor` by `scalaapi.Monitor` to use the Scala version.

```scala
import com.avast.metrics.scalaapi.Monitor
import com.avast.metrics.dropwizard.JmxMetricsMonitor

val javaMonitor = getJavaMonitor()
val scalaMonitor = Monitor(javaMonitor)
```

### Scala Per-key API
Adds support for easier creating of counters and timers per some user given key.

```scala
val monitor = Monitor(new JmxMetricsMonitor("com.avast.some.app"))

val perPartnerRequestCounter = monitor.perKey.counter("requestPerVendor")

val x = perPartnerRequestCounter.forKey("a")
val a = perPartnerRequestCounter.forKey("b")

x.inc()
a.inc()

```

See [example in tests](scala-api/src/test/scala/com/avast/metrics/examples/PerKeyExample.scala).

## Unit Testing
There is a singleton [NoOpMonitor.INSTANCE](api/src/main/java/com/avast/metrics/test/NoOpMonitor.java) in the `metrics-api` submodule that can be used in tests.  
There is also available `Monitor.noOp` for Scala API.

## Disabling JMX
Sometimes you want to globally disable JMX monitoring on the server. You can do that by setting system property `avastMetricsDisableJmx=true`. To do that from bash, you can use:

```sh
 java -jar -DavastMetricsDisableJmx="true" program.jar
```

Any value that is not `true` will be ignored.







test
