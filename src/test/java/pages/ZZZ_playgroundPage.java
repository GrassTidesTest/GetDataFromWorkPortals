package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ZZZ_playgroundPage {
    private WebDriver driver;

    private static final String CONTACT_NAME_CLASS = "advert__recruiter";
    private static final String CONTACT_INFO_CLASS = "advert__recruiter__phone";
    private static final String CONTACT_NAME_CSS = "span.advert__recruiter";
    private static final String CONTACT_INFO_CSS = "span.advert__recruiter__phone";
    private static final String CONTACT_INFO_XPATH = "//span[@class='advert__recruiter__phone']";
    private static final String CONTACT_NAME_XPATH = "//span[@class='advert__recruiter']";

    public ZZZ_playgroundPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void useClassSelector() {
//        driver.get("https://www.prace.cz/nabidka/1556299434/");
        driver.get("https://www.prace.cz/firma/335936766-mblue-czech-s-r-o/nabidka/1560460295/");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement element1 = driver.findElement(By.className(CONTACT_NAME_CLASS));
        WebElement element2 = driver.findElement(By.className(CONTACT_INFO_CLASS));
//        WebElement element1 = driver.findElement(By.cssSelector(CONTACT_NAME_CSS));
//        WebElement element2 = driver.findElement(By.cssSelector(CONTACT_INFO_CSS));
//        WebElement element1 = driver.findElement(By.xpath(CONTACT_NAME_XPATH));
//        WebElement element2 = driver.findElement(By.xpath(CONTACT_INFO_XPATH));

        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid blue'", element2);

        System.out.println("Nezavirej");
    }
}
