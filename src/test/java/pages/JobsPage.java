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

    // Search form constants
    private static final String ISIT_KONZULTACE = "IS/IT: Konzultace, analýzy a projektové řízení";
    private static final String ISIT_SPRAVA = "IS/IT: Správa systémů a HW";
    private static final String ISIT_VYVOJ = "IS/IT: Vývoj aplikací a systémů";
    private static final String SEARCH_BUTTON_TEXT = "Hledat";
    private static final String POSITIONS_XPATH = "//div[contains(@class,'standalone search-list__item') and @data-position-id]";
    private static final String NEXT_PAGE_BUTTON_XPATH = "//ul[contains(@class,'pager')]/li[a[contains(@class,'pager__next')]]";
    private static final String SALARY_LABEL_XPATH = "//*[contains(@class,'label--success')]";
    private static final String HOMEOFFICE_LABEL_XPATH = "//*[contains(@class,'search-list__home-office--label')]";

    // Page with positions constants
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_NAME_XPATH = "//h3[contains(@class,'title')]";
    private static final String POSITION_COMPANY_XPATH = "//div[contains(@class,'company')]";
    private static final String POSITION_LINK_XPATH = "//h3/a";

    // Position detail page constants
    private static final String DETAIL_INFO_XPATH = "//div[h3[text()='Informace o pozici']]/dl";
    private static final String EDUCATION_TEXT = "Požadované vzdělání: ";
    private static final String LANGUAGES_TEXT = "Požadované jazyky: ";
    private static final String SALARY_TEXT = "Plat: ";
    private static final String BENEFITS_TEXT = "Benefity: ";
    private static final String WORK_TAGS_TEXT = "Zařazeno: ";
    private static final String TYPE_OF_EMPLOYMENT_TEXT = "Typ pracovního poměru: ";
    private static final String LENGTH_OF_EMPLOYMENT_TEXT = "Délka pracovního poměru: ";
    private static final String TYPE_OF_CONTRACT_TEXT = "Typ smluvního vztahu: ";
    private static final String AUTHORITY_TEXT = "Zadavatel: ";


    // Number of pages to check constant
    private static final int NUMBER_OF_PAGES_TO_CHECK = 1;

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

    // Position detail page
    @FindBy(xpath = DETAIL_INFO_XPATH)
    private WebElement detailInfoElement;


    // methods for setting up the search
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
            getPositionsAndSaveThemToExcel(positions_size);

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

    private void getPositionsAndSaveThemToExcel(int positions_size) throws IOException {

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

    private void getDetailedInformation(String positionName, String timestamp, String link, String homeOfficeValue) throws IOException {
        System.out.println("Detail info: " + positionName);

        // wait for the page to load
        waitForVisibilityOfElement(driver, 5, detailInfoElement);

        if (driver.findElements(By.xpath(DETAIL_INFO_XPATH)).size() > 0) {

            // get size of the list with detailed information
            int size = detailInfoElement.findElements(By.xpath("." + "/dd[span]")).size();

            // create empty string variables for the detailed information
            String company = "";
            String address = "";

            String education = getInformationText(EDUCATION_TEXT);
            String languages = getInformationText(LANGUAGES_TEXT);
            String salary = getInformationText(SALARY_TEXT);
            String benefits = getInformationText(BENEFITS_TEXT);
            String workTags = getInformationText(WORK_TAGS_TEXT);
            String typeOfEmployment = getInformationText(TYPE_OF_EMPLOYMENT_TEXT);
            String lengthOfEmployment = getInformationText(LENGTH_OF_EMPLOYMENT_TEXT);
            String typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT);
            String authority = getInformationText(AUTHORITY_TEXT);

            System.out.println("-------------------- DETAIL INFORMATION PRINTOUT --------------------");
            System.out.println("Vzdelani: " + education);
            System.out.println("Jazyky: " + languages);
            System.out.println("Plat: " + salary);
            System.out.println("Benefity: " + benefits);
            System.out.println("Tagy: " + workTags);
            System.out.println("Typ uvazku: " + typeOfEmployment);
            System.out.println("Delka uvazku: " + lengthOfEmployment);
            System.out.println("Typ smlouvy: " + typeOfContract);
            System.out.println("Zadavatel: " + authority);

            ExcelWriter(timestamp, positionName, company, link, salary, homeOfficeValue,
                    "YES", education, languages, salary, benefits, workTags, typeOfEmployment, lengthOfEmployment,
                    typeOfContract, authority);

        } else {
            System.out.println("nah");
        }

    }

    private String getInformationText(String informationName) {
        String xpath = "." + "/dd[span[text()='" + informationName + "']]";

        if (detailInfoElement.findElements(By.xpath(xpath)).size() > 0) {

            String fullText = detailInfoElement.findElement(By.xpath(xpath)).getText();

            return fullText.substring(fullText.indexOf(":") + 2);
        }

        return "";
    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter(String timestamp, String positionName, String company,
                             String link, String salary, String workFromHome, String isDetail) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, JobsPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome,
                isDetail};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, JobsPage.SHEETNAME, valueToWrite);
    }

    private void ExcelWriter(String timestamp, String positionName, String company, String link, String salary,
                             String workFromHome, String isDetail, String education, String languages,
                             String detailSalary, String benefits, String workTags, String typeOfEmployment,
                             String lengthOfEmployment, String typeOfContract, String authority) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, JobsPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome,
                isDetail, education, languages, detailSalary, benefits, workTags, typeOfEmployment, lengthOfEmployment,
                typeOfContract, authority};

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
