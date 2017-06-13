package com.avast.metrics.statsd;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class StatsDHistogramTest {
    @Test
    public void testSendsValues() {
        final StatsDClient client = mock(StatsDClient.class);

        final String name = TestUtils.randomString();

        final StatsDHistogram histogram = new StatsDHistogram(client, name);

        for (int i = 1; i <= 5; i++) {
            histogram.update(10);
        }

        verify(client, times(5)).recordSetValue(name, "10");
    }
}
