package com.avast.metrics.statsd;

import com.avast.metrics.api.Gauge;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public class StatsDGauge<T> implements Gauge<T>, StatsDMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsDGauge.class);

    private final StatsDClient client;
    private final String name;
    private final Supplier<T> supplier;
    private double sampleRate;

    StatsDGauge(final StatsDClient client, final String name, final Supplier<T> supplier) {
        this(client, name, supplier, 1.0);
    }

    StatsDGauge(final StatsDClient client, final String name, final Supplier<T> supplier, double sampleRate) {
        this.client = client;
        this.name = name;
        this.supplier = supplier;
        this.sampleRate = sampleRate;
    }

    @Override
    public T getValue() {
        return supplier.get();
    }

    void send() {
        sendValue(getValue());
    }

    // copied from Dropwizard Graphite reporter
    private void sendValue(Object o) {
        if (o instanceof Float) {
            sendDoubleValue(((Float) o).doubleValue());
        } else if (o instanceof Double) {
            sendDoubleValue((Double) o);
        } else if (o instanceof Byte) {
            sendLongValue(((Byte) o).longValue());
        } else if (o instanceof Short) {
            sendLongValue(((Short) o).longValue());
        } else if (o instanceof Integer) {
            sendLongValue(((Integer) o).longValue());
        } else if (o instanceof Long) {
            sendLongValue((Long) o);
        } else if (o instanceof BigInteger) {
            sendDoubleValue(((BigInteger) o).doubleValue());
        } else if (o instanceof BigDecimal) {
            sendDoubleValue(((BigDecimal) o).doubleValue());
        } else if (o instanceof Boolean) {
            sendLongValue(((Boolean) o) ? 1 : 0);
        } else {
            LOGGER.warn("Unsupported gauge type: {}", o.getClass());
        }
    }

    private void sendLongValue(long l) {
        if (sampleRate == 1.0) client.recordGaugeValue(name, l); else client.recordGaugeValue(name, l, sampleRate);
    }

    private void sendDoubleValue(double d) {
        if (sampleRate == 1.0) client.recordGaugeValue(name, d); else client.recordGaugeValue(name, d, sampleRate);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init() {
        client.recordGaugeValue(name, 0);
    }
}
