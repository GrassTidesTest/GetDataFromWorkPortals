package pages;

import base.WebDriverSingleton;
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
import java.util.List;

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
    //    private static final String POSITION_LINK_XPATH = "//h3/a";
    private static final String POSITION_LINK_CSS = "a.jobCard";
    private static final String POSITIONS_XPATH = "//*[@id='snippet--list']/a";


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

    @FindBy(xpath = NEXT_PAGE_BUTTON_XP)
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

    public void selectTypeOfWork() throws InterruptedException {

        // wait for the page to load
        waitForElementToBeClickable(driver, typeOfWorkInput);

        //try this wait instead
//        waitUntilSomething();
        waitUntilPageLoaded(driver);

        // click on dropdown
        typeOfWorkInput.click();

        waitForVisibilityOfElement(driver, typeOfWorkResult);

        // select "Živnostenský list"
        typeOfWorkResult.findElement(By.xpath("." + "//*[contains(@id,'contractor')]")).click();

        waitForElementToBeClickable(driver, searchButton);
        waitUntilPageLoaded(driver);
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
//            System.out.println(i + ": " + positions_size);

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
//            String salaryValue = getLabelValue(position, SALARY_LABEL_XPATH);
            String salaryValue = getElementText(position, POSITION_SALARY_XPATH);
//            String homeOfficeValue = getLabelValue(position, HOMEOFFICE_LABEL_XPATH);

            System.out.println(timestamp);
            System.out.println("Position: " + positionName);
            System.out.println("Company: " + company);
            System.out.println("Link: " + linkAddress);
            System.out.println("Salary: " + salaryValue);
//            System.out.println(homeOfficeValue);
            System.out.println("----------------------------------------------------------");

//            // open new tab and create ArrayList with windowHandles
//            ((JavascriptExecutor) driver).executeScript("window.open()");
//            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());
//
//            // open the current position in new tab
//            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
//            openLinkInTab(linkAddress, currentTabs);
//
//            // get position level and decide what to do with the position
//            switch (getPositionLevel(linkAddress)) {
//                case BASIC:
//                case MEDIUM:
//                    // get the detailed information from the page for BASIC and MEDIUM
//                    // print what info from which position you save
//                    System.out.println("Detail info: " + positionName);
//                    getDetailedInformation(timestamp, positionName, company, linkAddress, homeOfficeValue);
//                    break;
//                case ADVANCED:
//                    // otherwise save basic info to the excel, reading it from different layouts is not an option here
//                    // print what info from which position you save
//                    System.out.println("Basic info: " + positionName);
//                    ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue, homeOfficeValue);
//                    break;
//                case CLOSED:
//                    // position is closed and there is nothing we can do
//                    System.out.println("Closed: " + positionName);
//            }
//
//            //close the current tab with the position and focus back on the position list page
//            closeTabWithPosition(currentTabs);
        }
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
//        try {
////            // body nittro-transition-auto nittro-transition-bar - this is loaded
////            System.out.println("it waited 1");
////        } catch (Exception e) {
////            System.out.println("it didnt wait 1");
////        }
////
////        try {
////            // body nittro-transition-auto nittro-transition-bar nittro-transition-middle - this is loading
////            System.out.println("it waited 2");
////        } catch (Exception e) {
////            System.out.println("it didnt wait 2");
////        }
////
////        try {
////            // //div[@style='position: absolute; left: 0px; top: 2615px; width: 100%; height: 1px; margin-top: -1px;']
////            System.out.println("it waited 3");
////        } catch (Exception e) {
////            System.out.println("it didnt wait 3");
////        }
    }

    private void waitUntilSomething() {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_IN_SECS);

        WebElement blaElement = driver.findElement(By.xpath("//*[@class='d-print-none']"));

        wait.until(ExpectedConditions.visibilityOf(blaElement));
        wait.until(ExpectedConditions.invisibilityOf(blaElement));
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
            System.out.println("Error here: " + e);
        }
        return "Error occurred";
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

    private String cropPositionLink(WebElement parentElement, String xpath) {
        // get the href value from the element
        String originalString = parentElement.findElement(By.xpath("." + xpath)).getAttribute("href");

        // crop the link so that the part after ? including is not present
        // link must contain ? otherwise it fails
        return originalString.substring(0, originalString.indexOf("?"));
    }

    private String getLinkUrl(WebElement parentElement) {
        return parentElement.getAttribute("href");
    }

    public void recheckAllInputs() {
    }
}
