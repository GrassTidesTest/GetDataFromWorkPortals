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

import java.io.IOException;

public class ProfesiaCzPage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final String BASE_URL = "https://www.profesia.cz/search_offers.php";
    private static final String WEBSITE_NAME = "profesia.cz";
    private static final String SHEETNAME = "COLLECTED_DATA";
    private static final String FILE_NAME = "data.xlsx";

    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final int TIMEOUT_IN_SECS = 5;

    // Filter positions page
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úvazek')]]";

    // Page with positions constants
    private static final String POSITIONS_XPATH = "//li[@class='list-row']";
    private static final String NEXT_PAGE_BUTTON_CSS = "ul.pagination a.next";

    @FindBy(id = "offerCriteriaSuggesterInputId")
    private WebElement professionFieldTextbox;

    @FindBy(id = "idSearchOffers5")
    private WebElement jobTypeElement;

    @FindBy(id = "idSearchOffers33")
    private WebElement inWhatLanguageElement;

    @FindBy(xpath = "//button[text()='Hledat nabídky']")
    private WebElement searchButton;

    @FindBy(css = "ul.list")
    private WebElement listElement;

    @FindBy(css = NEXT_PAGE_BUTTON_CSS)
    private WebElement nextPageButton;

    public ProfesiaCzPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        // open the page and maximize the window
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void closeCookiesMessage() {
        // wait for the page to load
        new WebDriverWait(driver,5);

        // if the cookies message appears disable it
        if (driver.findElements(By.cssSelector("button[aria-label='NESOUHLASÍM']")).size() > 0) {
            driver.findElement(By.cssSelector("button[aria-label='NESOUHLASÍM']")).click();
        }
    }

    public void enterProfessionField() throws InterruptedException {
        // wait for the page to load
        waitForVisibilityOfElement(driver, professionFieldTextbox);

        // type 'IT' into the textbox and hit enter to select the IT from the suggested options
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
        professionFieldTextbox.sendKeys(Keys.ENTER);
    }

    public void selectFullTimeJob() {
        // find
        WebElement fullTimeCheckbox = jobTypeElement.findElement(By.xpath(FULLTIME_CHECKBOX_XP));

        if (!fullTimeCheckbox.isSelected()) {
            fullTimeCheckbox.click();
        }
    }

    public void selectOfferLanguage() {
        WebElement czechBox = inWhatLanguageElement.findElement(By.id("idSearchOffers28"));
        WebElement slovakCheckbox = inWhatLanguageElement.findElement(By.id("idSearchOffers29"));
        WebElement englishCheckbox = inWhatLanguageElement.findElement(By.id("idSearchOffers30"));
        WebElement germanCheckbox = inWhatLanguageElement.findElement(By.id("idSearchOffers31"));
        WebElement agencyCheckbox = inWhatLanguageElement.findElement(By.id("idSearchOffers32"));

        // if not checked, check it
        if (!czechBox.isSelected()) {
            czechBox.click();
        }

        // if not checked, check it
        if (!slovakCheckbox.isSelected()) {
            slovakCheckbox.click();
        }

        // if not checked, check it
        if (!englishCheckbox.isSelected()) {
            englishCheckbox.click();
        }

        // if not checked, check it
        if (!germanCheckbox.isSelected()) {
            germanCheckbox.click();
        }

        // if checked, uncheck it
        if (agencyCheckbox.isSelected()) {
            agencyCheckbox.click();
        }
    }

    public void clickSearchButton() {
        searchButton.click();
    }

    public void savePositionsToExcel() throws IOException {
        // go through set amount of pages that contain positions
        // first run = first page containing list of positions, etc.
        for (int i = 0; i < NUMBER_OF_PAGES_TO_CHECK; i++) {
            // wait for the page to load
            waitForVisibilityOfElement(driver, listElement);

            // get amount of positions on the page, on this page, it should be 30
            int positions_size = listElement.findElements(By.xpath(POSITIONS_XPATH)).size();

            // send positions size to the function
            // go through the list of positions and save them to excel
//            getPositionsAndSaveThemToExcel(positions_size);
            System.out.println(positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }

    private boolean getBreakCondition(int i) {
        boolean isForwardButtonPresent;

        // check if the forward button is present
        isForwardButtonPresent = driver.findElements(By.cssSelector(NEXT_PAGE_BUTTON_CSS)).size() > 0;

        // if the button is present
        if (isForwardButtonPresent && i < NUMBER_OF_PAGES_TO_CHECK - 1) {

            // click the button to go to the next page
            nextPageButton.click();
        } else {

            // otherwise return true and break the cycle
            return true;
        }

        // return false so that the cycle can repeat
        return false;
    }

    public void recheckAllInputs() {


    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }
}
