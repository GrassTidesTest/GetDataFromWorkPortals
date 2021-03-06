package pages;

import base.WebDriverSingleton;
import helpers.ExcelEditor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatacruitPage {
    private final WebDriver driver;

    // IMPORTANT VARIABLES
    private static final int NUMBER_OF_PAGES_TO_CHECK = 200;
    private static final String REMOTE_DRIVE_PATH = "C:/DEVPACK_Synology/HR Shared Folder/Work Portal Crawler data/";
    private static final String ARCHIVE_PATH = "src/test/resources/archive/";
    private static final String FILE_NAME = "data.xlsx";
    private static final String EMPTY_FILE_NAME = "data_empty.xlsx";
    private static final String RESOURCES_FOL_PATH = "src/test/resources/";
    private static final int TIMEOUT_IN_SECS = 5;

    private static final String BASE_URL = "https://jobs.datacruit.com/nabidky-prace/informacni-technologie/";
    private static final String WEBSITE_NAME = "datacruit.com";
    private static final String SHEETNAME = "COLLECTED_DATA";

    // Search form constants
    private static final String NEXT_PAGE_BUTTON_XP =
            "//*[name()='path' and @d='M3.223 0L2 1.25 5.6 5 2 8.75 3.223 10 8 5z']/ancestor::*[self::a]";
    private static final String CZECH_REP_DROPDOWN_ITEM = "//a[text()='Česká republika']";
    private static final String SLOVAKIA_DROPDOWN_ITEM = "//a[text()='Slovensko']";

    // Page with positions constants
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_NAME_XPATH = "//h2";
    private static final String POSITION_COMPANY_XPATH = "/ul/li[1]";
    private static final String POSITION_SALARY_XPATH = "/ul/li[3]";
    private static final String POSITION_LINK_CSS = "a.jobCard";
    private static final String POSITIONS_XPATH = "//*[@id='snippet--list']/a";

    // Position detail page constants
    private static final String DETAIL_INFO_CSS = ".jobDetail__params";
    private static final String DETAIL_COMPANY_XPATH = "//img[contains(@src,'building')]/ancestor::*[self::li]";
    private static final String DETAIL_LOCATION_XPATH = "//img[contains(@src,'map')]/ancestor::*[self::li]";
    private static final String DETAIL_TYPE_OF_EMPLOYMENT_XPATH = "//img[contains(@src,'briefcase')]/ancestor::*[self::li]";
    private static final String DETAIL_LANGUAGE_XPATH = "//img[contains(@src,'language')]/ancestor::*[self::li]";
    private static final String DETAIL_SALARY_XPATH = "//img[contains(@src,'money')]/ancestor::*[self::li]";
    private static final String DETAIL_HOMEOFFICE_XPATH = "//img[contains(@src,'house')]/ancestor::*[self::li]";
    private static final String DETAIL_EDUCATION_XPATH = "//img[contains(@src,'education')]/ancestor::*[self::li]";

    // Search form
    @FindBy(css = ".treeSelect__preview-search input")
    private WebElement locationDropdown;

    @FindBy(id = "select2-frm-searchForm-componentForm-workType-container")
    private WebElement typeOfWorkInput;

    @FindBy(id = "select2-frm-searchForm-componentForm-workType-results")
    private WebElement typeOfWorkResult;

    @FindBy(name = "send")
    private WebElement searchButton;

    // Positions list
    @FindBy(id = "snippet--list")
    private WebElement contentWrapper;

    @FindBy(xpath = NEXT_PAGE_BUTTON_XP)
    private WebElement nextPageButton;

    // Position detail page
    @FindBy(css = DETAIL_INFO_CSS)
    private WebElement detailInfoElement;

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

    public void selectTypeOfWork() throws InterruptedException {

        // wait for the page to load
        waitForElementToBeClickable(driver, typeOfWorkInput);

        //try this wait instead
        waitUntilPageLoaded(driver);

        // click on dropdown
        typeOfWorkInput.click();

        // select "Živnostenský list"
        typeOfWorkResult.findElement(By.xpath("." + "//*[contains(@id,'contractor')]")).click();

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
            waitForElementToBeClickable(driver, contentWrapper);

            // get amount of positions on the page, on this page, it should be 30
            int positions_size = contentWrapper.findElements(By.cssSelector(POSITION_LINK_CSS)).size();

            // send positions size to the function
            // go through the list of positions and save them to excel
            getPositionsAndSaveThemToExcel(positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }

    private boolean getBreakCondition(int i) {

        // check if the forward button is present
        boolean isForwardButtonPresent = driver.findElements(By.xpath(NEXT_PAGE_BUTTON_XP)).size() > 0;

        // if the button is present
        if (isForwardButtonPresent && i < NUMBER_OF_PAGES_TO_CHECK - 1) {

            // click the button to go to the next page
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextPageButton);
            nextPageButton.click();
        } else {

            // otherwise return true and break the cycle
            return true;
        }

        // return false so that the cycle can repeat
        return false;
    }

    private void getPositionsAndSaveThemToExcel(int positions_size) throws IOException {

        // go through the list of positions and for each position determine
        // if the basic info or detail info will be saved to the excel
        for (int i = 1; i < positions_size + 1; i++) {

            // wait for the page to load
            waitForVisibilityOfElement(driver, contentWrapper);

            // get webElement of the current position
            // use i in xpath to determine which position from the list to work with
            // i = 1, means it's the first position from the list etc.
            WebElement position = driver.findElement(By.xpath(POSITIONS_XPATH + "[" + i + "]"));

            // get basic information for the position and save it to corresponding variables
            String timestamp = getTimeStamp(TIMESTAMP_PATTERN);
            String positionName = getElementText(position, POSITION_NAME_XPATH);
            String company = getElementText(position, POSITION_COMPANY_XPATH);
            String linkAddress = getLinkUrl(position);

            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());

            // open the current position in new tab
            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
            openLinkInTab(linkAddress, currentTabs);

            getDetailedInformation(timestamp, positionName, company, linkAddress);

            //close the current tab with the position and focus back on the position list page
            closeTabWithPosition(currentTabs);
        }
    }

    private void getDetailedInformation(String timestamp, String positionName, String companyValue, String link)
            throws IOException {

        // create empty string variables for the detailed information
        String education, languages, salary, typeOfEmployment, homeOfficeValue;

        education = languages = salary = typeOfEmployment = homeOfficeValue = "";

        // wait for the page to load
        waitForVisibilityOfElement(driver, detailInfoElement);

        // make sure the element exists before taking the info out of it
        if (doesElementExist(DETAIL_INFO_CSS)) {

            education = getInformationText(DETAIL_EDUCATION_XPATH);
            languages = getInformationText(DETAIL_LANGUAGE_XPATH);
            salary = getInformationText(DETAIL_SALARY_XPATH);
            typeOfEmployment = getInformationText(DETAIL_TYPE_OF_EMPLOYMENT_XPATH);
            homeOfficeValue = getInformationText(DETAIL_HOMEOFFICE_XPATH);

            // write values to Excel
            ExcelWriter_detail(timestamp, positionName, companyValue, link, salary, homeOfficeValue, education,
                    languages, salary, typeOfEmployment);
            // String timestamp, String positionName, String company, String link, String salary,
            // String workFromHome, String education, String languages, String detailSalary,
            // String typeOfEmployment
            System.out.println("Detail info: " + positionName);
        } else {
            // if the element not present, save only basic info
            ExcelWriter_basic(timestamp, positionName, companyValue, link, salary);
            System.out.println("Basic info: " + positionName);
        }
    }

    private String getInformationText(String informationName) {

        // create variables
        String xpath = "." + informationName;
        String fullText = "";

        // if the required information element exist
        if (detailInfoElement.findElements(By.xpath(xpath)).size() > 0) {

            // save it's value to the variable
            fullText = detailInfoElement.findElement(By.xpath(xpath)).getText();
        }

        // return fullText
        return fullText;
    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    private void waitForElementToBeClickable(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.elementToBeClickable(webElement));
    }

    private void waitUntilPageLoaded(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_IN_SECS);

        wait.until(ExpectedConditions.attributeToBe(driver.findElement(By.cssSelector("body")), "class",
                "body nittro-transition-auto nittro-transition-bar nittro-transition-middle"));

        wait.until(ExpectedConditions.attributeToBe(driver.findElement(By.cssSelector("body")), "class",
                "body nittro-transition-auto nittro-transition-bar"));

        wait.until(ExpectedConditions.
                numberOfElementsToBe(By.xpath("//div[@style='position: absolute; left: 0px; top: 2615px; width: 100%; height: 1px; margin-top: -1px;']"),
                        0));
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getElementText(WebElement parentElement, String xpath) {
        try {
            // return text of the element based on the parentElement and xpath
            return parentElement.findElement(By.xpath("." + xpath)).getText();
        } catch (Exception e) {
            System.out.println("Error occurred getting the element text: " + e);
        }
        return "Error occurred getting the text";
    }

    private void openLinkInTab(String positionLink, ArrayList<String> tabs) {
        // change focus to new window
        driver.switchTo().window(tabs.get(1));

        // open new link
        driver.get(positionLink);
    }

    private void closeTabWithPosition(ArrayList<String> tabs) {
        // close the tab
        driver.close();

        // change back to main windows
        driver.switchTo().window(tabs.get(0));
    }

    private String getLinkUrl(WebElement parentElement) {
        return parentElement.getAttribute("href");
    }

    private boolean doesElementExist(String css) {
        return driver.findElements(By.cssSelector(css)).size() > 0;
    }

    private void ExcelWriter_basic(String timestamp, String positionName, String company,
                                   String link, String salary) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, DatacruitPage.WEBSITE_NAME, positionName, company, link, salary, "",
                "NO", "", "", "", "", "", "", "", "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, DatacruitPage.SHEETNAME, valueToWrite);
    }

    private void ExcelWriter_detail(String timestamp, String positionName, String company, String link, String salary,
                                    String workFromHome, String education, String languages, String detailSalary,
                                    String typeOfEmployment) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, DatacruitPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome,
                "YES", "", "", education, languages, detailSalary, "", typeOfEmployment, "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, DatacruitPage.SHEETNAME, valueToWrite);
    }

    public void recheckAllInputs() {
    }
}
