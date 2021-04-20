package test;

import base.TestBase;
import base.WebDriverSingleton;
import enumerators.PositionLevel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ZZZ_playground extends TestBase {
//public class ZZZ_playground {
//    static int numberOfFailedTests;
//
//    @BeforeClass
//    public static void setUpClass() {
//        System.out.println("setup class \n");
//        numberOfFailedTests = 0;
//    }
//
//    @Before
//    public void setUp() {
//        System.out.println("setup \n");
//    }
//
//    @Test
//    public void testA() {
//        System.out.println("A");
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }
//
//    @Test
//    public void testB() {
//        System.out.println("B");
//        numberOfFailedTests++;
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }
//
//    @Test
//    public void testC() {
//        System.out.println("B");
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }

    @Test
    public void test() {
        final String basic = "https://www.jobs.cz/rpd/1556485411/";
        final String medium = "https://www.jobs.cz/rpd/1552850796/";
        final String advanced = "https://www.jobs.cz/rpd/1555558490/";

        String link = advanced;


        WebDriver driver = WebDriverSingleton.getInstance().getDriver();
        driver.get(link);

        new WebDriverWait(driver, 5);

        System.out.println(getPositionLevel(link, driver));

    }

    private PositionLevel getPositionLevel(String linkAddress, WebDriver driver) {
        PositionLevel level;
        boolean isDetailPresent = isElementPresentByXpath("//div[@data-visited-position]", driver);
        boolean isUrlSame = driver.getCurrentUrl().equals(linkAddress);


        if (isDetailPresent && isUrlSame) { // if both are true
            level = PositionLevel.BASIC;
        } else if (isDetailPresent ^ isUrlSame) { // if one is true but not both
            level = PositionLevel.MEDIUM;
        } else { // if both are false
            level = PositionLevel.ADVANCED;
        }

        return level;
    }

    private boolean isElementPresentByXpath(String elementXpath, WebDriver driver) {
        return driver.findElements(By.xpath(elementXpath)).size() > 0;
    }
}
