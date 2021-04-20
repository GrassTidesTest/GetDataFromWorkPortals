package test;

import base.TestBase;
import base.WebDriverSingleton;
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
        final String contactPhone = "//span[@itemprop='telephone']";
        final String contactName = "//span[@itemprop='name']/a";

        String link = medium;


        WebDriver driver = WebDriverSingleton.getInstance().getDriver();
        driver.get(link);

        new WebDriverWait(driver, 5);

        System.out.println("Phone: '" + getContactInfo(driver, contactPhone) + "'");
        System.out.println("Contact: '" + getContactInfo(driver, contactName) + "'");
    }

    private String getContactInfo(WebDriver driver, String xpath) {
        String contactName = "";

        if (isElementPresentByXpath(xpath, driver)) {
            contactName = driver.findElement(By.xpath(xpath)).getText();
        }

        return contactName;
    }

    private boolean isElementPresentByXpath(String elementXpath, WebDriver driver) {
        return driver.findElements(By.xpath(elementXpath)).size() > 0;
    }
}
