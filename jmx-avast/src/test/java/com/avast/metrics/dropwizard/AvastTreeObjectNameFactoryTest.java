package com.avast.metrics.dropwizard;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class AvastTreeObjectNameFactoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createName() {
        final AvastTreeObjectNameFactory nameFactory = AvastTreeObjectNameFactory.getInstance();

        final String name = nameFactory.createName("theType", "theDomain", String.join(AvastTreeObjectNameFactory.SEPARATOR, "l1", "l2", "l3", "l4", "l5")).toString();

        assertEquals("theDomain:type=l1,scope=l2,name=l3/l4/l5", name);
    }

    @Test
    public void correctQuoting() {
        final AvastTreeObjectNameFactory nameFactory = AvastTreeObjectNameFactory.getInstance();

        final String name = nameFactory.createName("theType", "theDomain",
                String.join(AvastTreeObjectNameFactory.SEPARATOR, "l1.name", "l2?name", "l3*name", "l4", "l5")
        ).toString();

        assertEquals("theDomain:type=l1_name,scope=l2_name,name=l3_name/l4/l5", name);
    }
}
