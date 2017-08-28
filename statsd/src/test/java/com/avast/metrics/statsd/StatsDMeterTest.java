package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StatsDMeterTest {

    @Test
    public void testCounts() {
        final String name = TestUtils.randomString();

        final StatsDClient client = mock(StatsDClient.class);
        final StatsDMeter meter = new StatsDMeter(client, name);

        for (int i = 1; i <= 5; i++) {
            meter.mark();
        }

        verify(client, times(5)).count(Matchers.eq(name), Matchers.anyLong(), Matchers.anyDouble(), Matchers.<String>anyVararg());
        assertEquals(5, meter.count());
    }
}
