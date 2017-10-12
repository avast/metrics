package com.avast.metrics.dropwizard;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class TreeObjectNameFactoryTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createName() {
        final TreeObjectNameFactory nameFactory = TreeObjectNameFactory.getInstance();

        final String name = nameFactory.createName("theType", "theDomain", String.join(TreeObjectNameFactory.SEPARATOR, "l1", "l2", "l3", "l4", "l5")).toString();

        assertEquals("theDomain:level0=l1,level1=l2,level2=l3,level3=l4,level4=l5",name);
    }
}
