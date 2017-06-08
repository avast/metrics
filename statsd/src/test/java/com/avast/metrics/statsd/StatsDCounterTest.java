package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StatsDCounterTest {

    @Test
    public void testCounts() {
        final StatsDClient client = mock(StatsDClient.class);

        final StatsDCounter meter = new StatsDCounter(client, "name");

        for (int i = 1; i <= 5; i++) {
            meter.inc();
        }

        for (int i = 1; i <= 3; i++) {
            meter.dec();
        }

        meter.inc(4);

        meter.dec(8);

        verify(client, times(10)).count(Matchers.anyString(), Matchers.anyLong(), Matchers.<String>anyVararg());

        assertEquals(-2, meter.count());
    }
}
