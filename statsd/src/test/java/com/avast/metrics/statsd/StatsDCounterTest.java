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

        verify(client, times(10)).count(Matchers.eq(name), Matchers.anyLong(), Matchers.<String>anyVararg());

        assertEquals(-2, counter.count());
    }
}
