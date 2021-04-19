package test;

import base.TestBase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//public class ZZZ_playground extends TestBase {
public class ZZZ_playground {
    static int numberOfFailedTests;

    @BeforeClass
    public static void setUpClass() {
        System.out.println("setup class \n");
        numberOfFailedTests = 0;
    }

    @Before
    public void setUp() {
        System.out.println("setup \n");
    }

    @Test
    public void testA() {
        System.out.println("A");
        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
    }

    @Test
    public void testB() {
        System.out.println("B");
        numberOfFailedTests++;
        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
    }

    @Test
    public void testC() {
        System.out.println("B");
        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
    }
}
