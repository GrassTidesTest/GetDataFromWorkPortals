package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class DatacruitPage {
    private final WebDriver driver;

    // IMPORTANT VARIABLES
    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final String REMOTE_DRIVE_PATH = "C:/DEVPACK_Synology/HR Shared Folder/Work Portal Crawler data/";
    private static final String ARCHIVE_PATH = "src/test/resources/archive/";
    private static final String FILE_NAME = "data.xlsx";
    private static final String EMPTY_FILE_NAME = "data_empty.xlsx";
    private static final String RESOURCES_FOL_PATH = "src/test/resources/";
    private static final int TIMEOUT_IN_SECS = 30;

    private static final String BASE_URL = "https://jobs.datacruit.com/nabidky-prace/informacni-technologie/";
    private static final String WEBSITE_NAME = "datacruit.com";
    private static final String SHEETNAME = "COLLECTED_DATA";

    // Search form constants
//    private static final String ISIT_KONZULTACE = "IS/IT: Konzultace, analýzy a projektové řízení";
//    private static final String ISIT_SPRAVA = "IS/IT: Správa systémů a HW";
//    private static final String ISIT_VYVOJ = "IS/IT: Vývoj aplikací a systémů";
//    private static final String SEARCH_BUTTON_TEXT = "Hledat";
//    private static final String POSITIONS_XPATH = "//div[contains(@class,'standalone search-list__item') and @data-position-id]";
    private static final String NEXT_PAGE_BUTTON_CSS = "#snippet--loadMore a.btn";
    //    private static final String SALARY_LABEL_XPATH = "//*[contains(@class,'label--success')]";
//    private static final String HOMEOFFICE_LABEL_XPATH = "//*[contains(@class,'search-list__home-office--label')]";
    private static final String CZECH_REP_DROPDOWN_ITEM = "//a[text()='Česká republika']";
    private static final String SLOVAKIA_DROPDOWN_ITEM = "//a[text()='Slovensko']";

    // Page with positions constants
//    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
//    private static final String POSITION_NAME_XPATH = "//h3[contains(@class,'title')]";
//    private static final String POSITION_COMPANY_XPATH = "//div[contains(@class,'company')]";
//    private static final String POSITION_LINK_XPATH = "//h3/a";

    // Position detail page constants
//    private static final String DETAIL_INFO_XPATH = "//div[h3[text()='Informace o pozici']]/dl";
//    private static final String CONTACT_NAME_XPATH = "//span[@itemprop='name']/a";
//    private static final String CONTACT_INFO_XPATH = "//span[@itemprop='telephone']";

    // file management variables
//    private static final String FILE_TS_PATTERN = "ddMMyy";
//    private static final String FILE_PREFIX = "data";
//    private static final String FILE_PORTAL = "jobscz";

    // Search form
    @FindBy(css = ".treeSelect__preview-search input")
    private WebElement locationDropdown;

    @FindBy(id = "select2-frm-searchForm-componentForm-workType-container")
    private WebElement typeOfWorkInput;

    @FindBy(id = "select2-frm-searchForm-componentForm-workType-results")
    private WebElement typeOfWorkResult;

    @FindBy(name = "send")
    private WebElement searchButton;

    @FindBy(id = "snippet--list")
    private WebElement contentWrapper;

    @FindBy(css = ".paginationBox a.btn")
    private WebElement nextPageButton;

    // methods for setting up the search
    public DatacruitPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        // open the page and maximize the window
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void selectLocation() {

        // wait for the page to load
        waitForElementToBeClickable(driver, locationDropdown);

        // click on dropdown
        locationDropdown.click();

        // select Czech republic
        locationDropdown.findElement(By.xpath(CZECH_REP_DROPDOWN_ITEM)).click();

        // select Slovakia
        locationDropdown.findElement(By.xpath(SLOVAKIA_DROPDOWN_ITEM)).click();

        clickSearchButton();
    }

    public void selectTypeOfWork() {

        // wait for the page to load
        waitForElementToBeClickable(driver, typeOfWorkInput);

        //try this wait instead
        waitUntilSomething();

        // click on dropdown
        typeOfWorkInput.click();

        waitForVisibilityOfElement(driver, typeOfWorkResult);

        // select "Živnostenský list"
        typeOfWorkResult.findElement(By.xpath("//*[contains(@id,'contractor')]")).click();

        clickSearchButton();
    }

    public void clickSearchButton() {
        // wait for the page to load
        waitForElementToBeClickable(driver, searchButton);

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
            int positions_size = contentWrapper.findElements(By.xpath("." + "/a")).size();

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


    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    private void waitForElementToBeClickable(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.elementToBeClickable(webElement));
    }

    private void waitUntilSomething() {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_IN_SECS);

        WebElement blaElement = driver.findElement(By.xpath("//*[@class='d-print-none']"));

        wait.until(ExpectedConditions.visibilityOf(blaElement));
        wait.until(ExpectedConditions.invisibilityOf(blaElement));
    }

    public void recheckAllInputs() {
    }
}
