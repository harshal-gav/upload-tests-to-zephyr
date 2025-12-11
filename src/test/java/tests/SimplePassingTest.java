package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SimplePassingTest {

    @Test(description = "A trivial passing test to validate test infrastructure")
    public void alwaysPasses() {
        Assert.assertTrue(true, "This test always passes");
    }

    @Test(description = "Basic arithmetic sanity check")
    public void arithmeticCheck() {
        Assert.assertEquals(2 + 2, 4, "Basic arithmetic should hold");
    }
}

