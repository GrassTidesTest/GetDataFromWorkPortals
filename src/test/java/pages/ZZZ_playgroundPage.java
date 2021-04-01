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

    public void testTabs() {
        driver.get("https://www.jobs.cz/");
        new WebDriverWait(driver, 5);
        assertJobsTitle();

        // considering that there is only one tab opened in that point.
        String newLink = "https://seznam.cz";

        // open new tab
        ((JavascriptExecutor) driver).executeScript("window.open()");
        System.out.println(driver.getWindowHandles());
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

        // change focus to new windows
        driver.switchTo().window(tabs.get(1));

        // open new link
        driver.get(newLink);
        assertSeznamTitle();

        // Do what you want here, you are in the new tab

        // close the tab
        driver.close();

        // change back to main windows
        driver.switchTo().window(tabs.get(0));
        assertJobsTitle();

        // Do what you want here, you are in the old tab
    }

    private void assertJobsTitle() {
        assertEquals("Jobs.cz – Inspirujeme k úspěchu – nabídka práce, volná pracovní místa, brigády i vzdělávání a rozvoj", driver.getTitle().trim());
    }

    private void assertSeznamTitle() {
        assertEquals("Seznam – najdu tam, co neznám", driver.getTitle().trim());
    }
}
