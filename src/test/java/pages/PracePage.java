package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PracePage {
    private WebDriver driver;

    private static final String BASE_URL = "https://www.prace.cz/nabidky/";
    private static final String IT_PROFESSION = "IT";

    @FindBy(id = "showSearchFormBtn")
    private WebElement showSearchFormBtn;

    @FindBy(xpath = "//label[descendant::*[@id='searchForm_employment_type_codes_0']]")
    private WebElement fulltimeJobCheckbox;

    @FindBy(xpath = "//label[descendant::*[@id='searchForm_employment_type_codes_1']]")
    private WebElement halftimeJobCheckbox;

    @FindBy(xpath = "//label[descendant::*[@id='searchForm_employment_type_codes_2']]")
    private WebElement summerJobCheckbox;

    @FindBy(id = "s2id_autogen1")
    private WebElement professionFieldTextbox;

    @FindBy(id = "searchForm_search")
    private WebElement searchButton;


    public PracePage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        // open the page and maximize the window
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void showSearchForm() {
        // wait for the page to load
        waitForVisibilityOfElement(driver, 5, showSearchFormBtn);

        // open the detailed view for search form
        showSearchFormBtn.click();
    }

    public void selectJobType() {
        // if the checkbox is unchecked, check it
        if (!fulltimeJobCheckbox.findElement(By.xpath("input")).isSelected()) {
            fulltimeJobCheckbox.click();
        }

        // if the checkbox is checked, uncheck it
        if (halftimeJobCheckbox.findElement(By.xpath("input")).isSelected()) {
            halftimeJobCheckbox.click();
        }

        // if the checkbox is checked, uncheck it
        if (summerJobCheckbox.findElement(By.xpath("input")).isSelected()) {
            summerJobCheckbox.click();
        }
    }

    public void enterProfessionField() throws InterruptedException {
        // type 'IT' into the textbox and hit enter
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
        professionFieldTextbox.sendKeys(Keys.ENTER);
    }

    public void clickSearchButton() {
        // click the search button
        searchButton.click();
    }

    public void recheckAllInputs() {


    }

    private void waitForVisibilityOfElement(WebDriver driver, int timeInSecs, WebElement webElement) {
        new WebDriverWait(driver, timeInSecs)
                .until(ExpectedConditions.visibilityOf(webElement));
    }
}
