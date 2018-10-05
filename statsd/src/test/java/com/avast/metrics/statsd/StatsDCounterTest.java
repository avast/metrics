package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StatsDCounterTest {

    @Test
    public void testCounterValue() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDCounter counter = new StatsDCounter(client, name);

        for (int i = 1; i <= 5; i++) {
            counter.inc();
        }

        for (int i = 1; i <= 3; i++) {
            counter.dec();
        }

        counter.inc(4);

        counter.dec(8);

        assertEquals(-2, counter.count());
    }

    @Test
    public void incrementCallsCountOnClient() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDCounter counter = new StatsDCounter(client, name);

        for (int i = 1; i <= 5; i++) {
            counter.inc();
        }

        verify(client, times(5)).count(Matchers.eq(name), Matchers.anyLong(), Matchers.anyDouble(), Matchers.<String>anyVararg());
    }

    @Test
    public void decrementCallsCountOnClient() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDCounter counter = new StatsDCounter(client, name);

        for (int i = 1; i <= 3; i++) {
            counter.dec();
        }

        verify(client, times(3)).count(Matchers.eq(name), Matchers.anyLong(), Matchers.anyDouble(), Matchers.<String>anyVararg());
    }

    @Test
    public void incrementWithMultipleCallsCountOnClient() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDCounter counter = new StatsDCounter(client, name);

        counter.inc(4);

        verify(client, times(1)).count(Matchers.eq(name), eq(4L), Matchers.anyDouble(), Matchers.<String>anyVararg());
    }

    @Test
    public void samplingForwardedToClient() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDCounter counter = new StatsDCounter(client, name, 0.1);

        counter.inc(4);

        verify(client, times(1)).count(Matchers.eq(name), eq(4L), eq(0.1), Matchers.<String>anyVararg());
    }
}
