package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class ZZZ_playgroundPage {
    private WebDriver driver;

    public ZZZ_playgroundPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void stringManipulation() {
        driver.get("https://www.jobs.cz/rpd/1556336089/");
        new WebDriverWait(driver, 5);

        String fullText = driver.findElement(By.xpath("//div[h3[text()='Informace o pozici']]/dl/dd[span[text()='Požadované vzdělání: ']]")).getText();
        String parentText = fullText.substring(fullText.indexOf(":") + 2);

        System.out.println(fullText);
        System.out.println(parentText);
    }
}
