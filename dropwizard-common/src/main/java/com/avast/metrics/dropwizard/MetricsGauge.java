package com.avast.metrics.dropwizard;

import com.avast.metrics.api.Gauge;

import java.util.function.Supplier;

public class MetricsGauge<T> implements Gauge<T> {

    private final String name;
    private final com.codahale.metrics.Gauge<T> gauge;

    public MetricsGauge(String name, com.codahale.metrics.Gauge<T> gauge) {
        this.name = name;
        this.gauge = gauge;
    }

    @Override
    public T getValue() {
        return gauge.getValue();
    }

    static class SupplierGauge<T> implements com.codahale.metrics.Gauge<T> {

        private final Supplier<T> supplier;

        public SupplierGauge(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T getValue() {
            return supplier.get();
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
