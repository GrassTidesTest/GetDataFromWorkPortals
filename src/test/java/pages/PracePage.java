package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class PracePage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final int TIMEOUT_IN_SECS = 5;


    private static final String BASE_URL = "https://www.prace.cz/nabidky/";
    private static final String IT_PROFESSION = "IT";
    private static final String POSITIONS_XPATH = "//ul/li/div[contains(@class,'grid--rev')]";
    private static final String MAIN_NEXT_PAGE_BUTTON_XPATH = "//div[contains(@class,'pager')]";
    private static final String NEXT_PAGE_BUTTON_XPATH = MAIN_NEXT_PAGE_BUTTON_XPATH + "//span[@class='pager__next']";

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

    @FindBy(xpath = "//div[@class='wrapper' and descendant::*[ul[contains(@class,'search')]]]")
    private WebElement contentWrapper;

    @FindBy(xpath = MAIN_NEXT_PAGE_BUTTON_XPATH)
    private WebElement mainNextPageButton;

    @FindBy(xpath = NEXT_PAGE_BUTTON_XPATH)
    private WebElement nextPageButton;

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
        waitForVisibilityOfElement(driver, showSearchFormBtn);

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

    public void savePositionsToExcel() throws IOException {
        // go through set amount of pages that contain positions
        // first run = first page containing list of positions, etc.
        for (int i = 0; i < NUMBER_OF_PAGES_TO_CHECK; i++) {
            // wait for the page to load
            waitForVisibilityOfElement(driver, contentWrapper);

            // get amount of positions on the page, on this page, it should be 30
            int positions_size = contentWrapper.findElements(By.xpath(POSITIONS_XPATH)).size();

            // send positions size to the function
            // go through the list of positions and save them to excel
//            getPositionsAndSaveThemToExcel(positions_size);
            System.out.println(i + " : " + positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }


    private boolean getBreakCondition(int i) {

        // check if the forward button is present
        // two types of forward button are present, on main page and the next pages
        if (driver.findElements(By.xpath(MAIN_NEXT_PAGE_BUTTON_XPATH)).size() > 0
                && i == 0) {

            // click the button to go to the next page
            scrollToElement(mainNextPageButton);
            mainNextPageButton.click();

        } else if (driver.findElements(By.xpath(NEXT_PAGE_BUTTON_XPATH)).size() > 0
                && i < NUMBER_OF_PAGES_TO_CHECK - 1) {

            // click the button to go to the next page
            scrollToElement(nextPageButton);
            nextPageButton.click();

        } else {
            // return true and break the cycle
            return true;
        }

        // return false and continue the cycle
        return false;
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

//    public void recheckAllInputs() {
//
//
//    }

}
