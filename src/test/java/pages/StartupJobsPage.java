package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StartupJobsPage {
    private WebDriver driver;

    private static final String BASE_URL = "https://www.startupjobs.cz/nabidky";
    private static final String VYVOJ_ELEMENT_XPATH = "//div[1][@class='category-wrapper']/div";
    private static final String REMOTE_ELEMENT_XPATH = "//*[@class='d-flex filter-categories']/div[2]//div[1][@class='category-wrapper']/div";
    private static final String FULLTIME_ELEMENT_XPATH = "//*[@class='d-flex filter-categories']/div[3]/div/div[1]/div[1][@class='category-wrapper']";

    @FindBy(xpath = "//*[@class='d-flex filter-categories']/div[1]")
    private WebElement oborDropdown;

    @FindBy(xpath = "//*[@class='d-flex filter-categories']/div[2]")
    private WebElement mistoDropdown;

    @FindBy(xpath = "//*[@class='d-flex filter-categories']/div[3]")
    private WebElement spolupraceDropdown;

    @FindBy(xpath = "//form[@class='sj-filter']/div/div/button")
    private WebElement searchButton;


    public StartupJobsPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void checkVyvojCheckbox() {
        waitForVisibilityOfElement(driver, 5, oborDropdown);
        oborDropdown.click();

        oborDropdown.findElement(By.xpath(VYVOJ_ELEMENT_XPATH)).click();
    }

    public void checkRemoteCheckbox() {
        waitForVisibilityOfElement(driver, 5, mistoDropdown);
        mistoDropdown.click();

        WebElement remoteCheckbox = driver.findElement(By.xpath(REMOTE_ELEMENT_XPATH));
        remoteCheckbox.click();
    }

    public void checkFullTimeCheckbox() {
        waitForVisibilityOfElement(driver, 5, spolupraceDropdown);
        spolupraceDropdown.click();

        WebElement fulltimeCheckbox = driver.findElement(By.xpath(FULLTIME_ELEMENT_XPATH));
        fulltimeCheckbox.click();
    }

    public void clickSearchButton() {
        waitForVisibilityOfElement(driver, 5, searchButton);
        searchButton.click();
    }

    public void recheckAllInputs() {


    }


    private void waitForVisibilityOfElement(WebDriver driver, int timeInSecs, WebElement webElement) {
        new WebDriverWait(driver, timeInSecs)
                .until(ExpectedConditions.visibilityOf(webElement));
    }
}
