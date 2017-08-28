package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StatsDGaugeTest {

    @Test
    public void testDoubleGauge() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDGauge<Double> dGauge = new StatsDGauge<>(client, name, () -> Math.PI);

        for (int i = 1; i <= 5; i++) {
            dGauge.send();
        }

        verify(client, times(5)).recordGaugeValue(Matchers.eq(name), Matchers.eq(Math.PI), Matchers.anyDouble());

        assertEquals(Math.PI, dGauge.getValue(), 0);
    }

    @Test
    public void testLongGauge() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDGauge<Long> dGauge = new StatsDGauge<>(client, name, () -> Long.MAX_VALUE);

        for (int i = 1; i <= 5; i++) {
            dGauge.send();
        }

        verify(client, times(5)).recordGaugeValue(Matchers.eq(name), Matchers.eq(Long.MAX_VALUE), Matchers.anyDouble());

        assertEquals(Long.MAX_VALUE, dGauge.getValue(), 0);
    }

    @Test
    public void testIntGauge() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDGauge<Integer> dGauge = new StatsDGauge<>(client, name, () -> Integer.MAX_VALUE);

        for (int i = 1; i <= 5; i++) {
            dGauge.send();
        }

        verify(client, times(5)).recordGaugeValue(Matchers.eq(name), Matchers.eq((long) Integer.MAX_VALUE), Matchers.anyDouble());

        assertEquals(Integer.MAX_VALUE, dGauge.getValue(), 0);
    }


    @Test
    public void testStringGauge() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDGauge<String> dGauge = new StatsDGauge<>(client, name, () -> name);

        for (int i = 1; i <= 5; i++) {
            dGauge.send();
        }

        verifyZeroInteractions(client);

        assertEquals(name, dGauge.getValue());
    }
}
