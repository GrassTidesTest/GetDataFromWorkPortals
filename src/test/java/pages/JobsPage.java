package pages;

import base.WebDriverSingleton;
import helpers.ExcelEditor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobsPage {
    private WebDriver driver;

    private static final String BASE_URL = "https://www.jobs.cz/prace/";
    private static final String WEBSITE_NAME = "jobs.cz";
    private static final String SHEETNAME = "COLLECTED_DATA";

    private static final String ISIT_KONZULTACE = "IS/IT: Konzultace, analýzy a projektové řízení";
    private static final String ISIT_SPRAVA = "IS/IT: Správa systémů a HW";
    private static final String ISIT_VYVOJ = "IS/IT: Vývoj aplikací a systémů";
    private static final String SEARCH_BUTTON_TEXT = "Hledat";
    private static final String POSITIONS_XPATH = "//div[contains(@class,'standalone search-list__item') and @data-position-id]";
    private static final String NEXT_PAGE_BUTTON_XPATH = "//ul[contains(@class,'pager')]/li[a[contains(@class,'pager__next')]]";
    private static final String SALARY_LABEL_XPATH = "//*[contains(@class,'label--success')]";
    private static final String HOMEOFFICE_LABEL_XPATH = "//*[contains(@class,'search-list__home-office--label')]";
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_NAME_XPATH = "//h3[contains(@class,'title')]";
    private static final String POSITION_COMPANY_XPATH = "//div[contains(@class,'company')]";
    private static final String POSITION_LINK_XPATH = "//h3/a";

    private static final String DETAIL_INFO_XPATH = "//dl";

    private static final int NUMBER_OF_PAGES_TO_CHECK = 5;

    // Search form
    @FindBy(xpath = "//div[@class='search-inputs']/div[1]//input")
    private WebElement workInput;

    @FindBy(xpath = "//div[@class='search-inputs']/div[2]//input")
    private WebElement locationInput;

    @FindBy(xpath = "//*[@id='results-search-box']//div[1][@class='dropdown']")
    private WebElement timeOfPublicationDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[2][@class='dropdown']")
    private WebElement salaryRangeDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[3][@class='dropdown']")
    private WebElement typeOfContractDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[4][@class='dropdown']")
    private WebElement educationDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[5][@class='dropdown']")
    private WebElement requiredLanguageDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[6][@class='dropdown']")
    private WebElement homeOfficeDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[7][@class='dropdown']")
    private WebElement authorDropdown;

    @FindBy(xpath = "//*[@id='results-search-box']//div[8][@class='dropdown']")
    private WebElement suitableForDropdown;

    @FindBy(xpath = "//div[@class='search-inputs']//button[@type='submit' and span='" + SEARCH_BUTTON_TEXT + "']")
    private WebElement searchButton;

    // Page with positions
    @FindBy(xpath = "//div[@class='content' and div[@class='wrapper']]")
    private WebElement contentWrapper;

    @FindBy(xpath = "//ul[contains(@class,'pager')]")
    private WebElement listOfPages;

    @FindBy(xpath = NEXT_PAGE_BUTTON_XPATH)
    private WebElement nextPageButton;

    @FindBy(xpath = "//ul[contains(@class,'pager')]/li[a[contains(@class,'pager__prev')]]")
    private WebElement previousPageButton;


    // functions for setting up the search
    public JobsPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        // open the page and maximize the window
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void selectISITPositions() throws InterruptedException {
        //dalo by se upravit, at pocka na to, az se nacte misto thread sleep

        //create list of profession we desire
        List<String> workPositions = Arrays.asList(ISIT_SPRAVA,
                ISIT_KONZULTACE,
                ISIT_VYVOJ);

        //wait for the page to load
        waitForVisibilityOfElement(driver, 5, workInput);

        //enter each profession, wait and confirm the loaded category
        for (String workPosition : workPositions) {
            workInput.sendKeys(workPosition);
            Thread.sleep(500);
            workInput.sendKeys(Keys.ENTER);
        }
    }

    public void selectRemoteWork() {
        //wait for the page to load,
        waitForVisibilityOfElement(driver, 5, workInput);
        homeOfficeDropdown.click();
        homeOfficeDropdown.findElement(By.xpath("//*[text()[contains(.,'Možnost práce z domova')]]")).click();
    }

    public void selectFullTime() {
        waitForVisibilityOfElement(driver, 5, typeOfContractDropdown);
        typeOfContractDropdown.click();
        typeOfContractDropdown.findElement(By.xpath("//*[text()[contains(.,'Plný úvazek')]]")).click();
    }

    public void clickSearchButton() {
        waitForVisibilityOfElement(driver, 5, searchButton);

        searchButton.click();
    }

    public void recheckAllInputs() {
        checkIfProfessionOnlyItIs();
        checkIfLocationEmpty();
        checkIfTimeDeselected();
        checkIfSalaryEmpty();
        checkIfFullTime();
        checkIfEducationEmpty();
        checkIfLanguageEmpty();
        checkIfRemoteAllowed();
        checkIfAuthorEmpty();
        checkIfStudentsEmpty();
    }

    private void waitForVisibilityOfElement(WebDriver driver, int timeInSecs, WebElement webElement) {
        new WebDriverWait(driver, timeInSecs)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    private void checkIfProfessionOnlyItIs() {
//        workInput;
    }

    private void checkIfLocationEmpty() {
//         locationInput;
    }

    private void checkIfTimeDeselected() {
//         timeOfPublicationDropdown;
    }

    private void checkIfSalaryEmpty() {
//         salaryRangeDropdown;
    }

    private void checkIfFullTime() {
//         typeOfContractDropdown;
    }

    private void checkIfEducationEmpty() {
//         educationDropdown;
    }

    private void checkIfLanguageEmpty() {
//         requiredLanguageDropdown;
    }

    private void checkIfRemoteAllowed() {
//         homeOfficeDropdown;
    }

    private void checkIfAuthorEmpty() {
//         authorDropdown;
    }

    private void checkIfStudentsEmpty() {
//         suitableForDropdown;
    }

    public void savePositionsToExcel() throws IOException {
        // go through set amount of pages that contain positions
        // first run = first page containing list of positions, etc.
        for (int i = 0; i < NUMBER_OF_PAGES_TO_CHECK; i++) {
            // wait for the page to load
            waitForVisibilityOfElement(driver, 5, contentWrapper);

            // get amount of positions on the page
            int positions_size = contentWrapper.findElements(By.xpath(POSITIONS_XPATH)).size();

            // send positions size to the function
            // go through the list of positions and save them to excel
            savePositionsToExcel(positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }

    private boolean getBreakCondition(int i) {
        boolean isForwardButtonPresent;

        // check if the forward button is present
        isForwardButtonPresent = driver.findElements(By.xpath(NEXT_PAGE_BUTTON_XPATH)).size() > 0;

        // if the button is present
        if (isForwardButtonPresent && i < NUMBER_OF_PAGES_TO_CHECK - 1) {
            // then click the button
            nextPageButton.click();
        } else {
            // otherwise return true and break the cycle
            return true;
        }

        // return false so that the cycle can repeat
        return false;
    }

    private void savePositionsToExcel(int positions_size) throws IOException {

        // go through the list of positions and for each position, determine if the basic info or detail info will be
        // saved to the excel
        for (int i = 1; i < positions_size + 1; i++) {

            // wait for the page to load
            waitForVisibilityOfElement(driver, 5, contentWrapper);

            // get webElement of the current position
            // use i in xpath to determine which position from the list to work with
            // i = 1, means it's the first position from the list etc.
            WebElement position = driver.findElement(By.xpath(POSITIONS_XPATH + "[" + i + "]"));

            // get basic information for the position and save it to corresponding variables
            String timestamp = getTimeStamp(TIMESTAMP_PATTERN);
            String positionName = getElementText(position, POSITION_NAME_XPATH);
            String company = getElementText(position, POSITION_COMPANY_XPATH);
            String linkAddress = cropPositionLink(position, POSITION_LINK_XPATH);
            String salaryValue = getLabelValue(position, SALARY_LABEL_XPATH);
            String homeOfficeValue = getLabelValue(position, HOMEOFFICE_LABEL_XPATH);

            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

            // open the current position in new tab
            // by doing this we avoid getting stuck by some aggressive popup
            openLinkInTab(linkAddress, tabs);

            // if the position's link and current url match, page was NOT redirected
            if (driver.getCurrentUrl().equals(linkAddress)) {
                // get the detailed information from the page
                getDetailedInformation(positionName);
                ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue, homeOfficeValue, "YES");
            } else {
                // otherwise save basic info to the excel
                System.out.println("Basic info: " + positionName);
                ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue, homeOfficeValue, "NO");
            }

            //close the current tab with the position and focus back on the position list page
            closeTabWithPosition(tabs);
        }
    }

    private String getLabelValue(WebElement parentElement, String xpath) {
        // define empty string
        String value = "";

        // if the element exists
        if (parentElement.findElements(By.xpath("." + xpath)).size() > 0) {

            // save it's value to the string
            value = parentElement.findElement(By.xpath("." + xpath)).getText().trim();
        }

        return value;
    }

    private String getElementText(WebElement parentElement, String xpath) {
        // return text of the element based on the parentElement and xpath as a string
        return parentElement.findElement(By.xpath("." + xpath)).getText();
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern as a string
        return new SimpleDateFormat(pattern).format(new java.util.Date());
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

    private void temporary_printOutInfo(String timestamp, String positionName, String company,
                                        String link, String salary, String workFromHome) {
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println(timestamp);
        System.out.println(positionName);
        System.out.println(company);
        System.out.println(link);
        System.out.println(salary);
        System.out.println(workFromHome);
    }

    private void getDetailedInformation(String positionName) {
        System.out.println("Detail info: " + positionName);

//        if (driver.findElements(By.xpath(DETAIL_INFO_XPATH)).size() > 0) {
//            WebElement detailInfoSection = driver.findElement(By.xpath(DETAIL_INFO_XPATH));
//
//            //heres a problem
//            int size = detailInfoSection.findElements(By.xpath("." + "/dd[span]")).size();
//
//            System.out.println(size);
//        }
//        else {
//            System.out.println("nah");
//        }

    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter(String timestamp, String positionName, String company,
                             String link, String salary, String workFromHome, String detail) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, JobsPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome, detail};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, JobsPage.SHEETNAME, valueToWrite);
    }

    // Calling ExcelEditor to read data from the excel file
    private String ExcelReader(String sheetName, String fileName) throws IOException {

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Prepare the path of excel file
        String filePath = System.getProperty("user.dir") + "\\src\\test\\resources";

        //Call read file method of the class to read data
        return objExcelFile.ReadFromExcel(filePath, fileName, sheetName);
    }
}
