# Metrics

* **Developer:** janecek@avast.com
* **SCM:** https://git.int.avast.com/ff/metrics
* **CI:** https://teamcity.int.avast.com/project.html?projectId=CloudSystems_Metrics&tab=projectOverview
* **SCM:** [ABE](https://butr.avast.com/browse/ABE/?selectedTab=com.atlassian.jira.jira-projects-plugin:components-panel)

The **latest stable release** can be found in our [release blog](https://cml.avast.com/label/FF/metrics).

## Introduction
Library for application monitoring. It's abstraction of metrics is inspired by [Dropwizard Metrics](https://github.com/dropwizard/metrics).

The library is divided into submodules:
* **metrics-api** - API layer that you can depend on if you are creating a library which should not force any metrics implementation,
* **metrics-dropwizard** - for now the only implementation via Dropwizard Metrics.
* **metrics-scala** - Scala wrapper around `metrics-api` implementation (such as `metrics-dropwizard`) which provides a nicer API 

The entry-point into the library is the interface `Monitor`. Your classes need to get an instance of a monitor which they can use to construct different metrics, e.g. meters, timers or histograms.
Instances of the individuals metrics can be used to monitor your application. The default implementation that will most probably be used is [JmxMetricsMonitor](dropwizard/src/main/java/com/avast/metrics/dropwizard/JmxMetricsMonitor.java)
from `metrics-dropwizard` submodule which automatically publishes the metrics via **JMX**.

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

JmxMetricsMonitor monitor = new JmxMetricsMonitor("com.avast.myapp");
Handler handler = new Handler(monitor.named("Handler1"));
```

## Unit Testing
There is a singleton [NoOpMonitor.INSTANCE](api/src/main/java/com/avast/metrics/test/NoOpMonitor.java) in the `metrics-api` submodule that can be used in tests.

## Disabling JMX
Sometimes you want to globally disable JMX monitoring on the server (for example on our testing servers). You can do that by setting system property `avastMetricsDisableJmx=true`. To do that from bash, you can use:

```sh
 java -jar -DavastMetricsDisableJmx="true" program.jar
```

Any value that is not `true` will be ignored.
