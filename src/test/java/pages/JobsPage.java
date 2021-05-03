package pages;

import base.WebDriverSingleton;
import enumerators.PositionLevel;
import helpers.ExcelEditor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobsPage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final String REMOTE_DRIVE_PATH = "C:/DEVPACK_Synology/HR Shared Folder/Work Portal Crawler data/";
    private static final String ARCHIVE_PATH = "src/test/resources/archive/";
    private static final String FILE_NAME = "data.xlsx";
    private static final String EMPTY_FILE_NAME = "data_empty.xlsx";
    private static final String RESOURCES_FOL_PATH = "src/test/resources/";
    private static final int TIMEOUT_IN_SECS = 5;

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
    private static final String CONTACT_NAME_XPATH = "//span[@itemprop='name']/a";
    private static final String CONTACT_INFO_XPATH = "//span[@itemprop='telephone']";

    // In Czech
    private static final String EDUCATION_TEXT_CZ = "Požadované vzdělání: ";
    private static final String LANGUAGES_TEXT_CZ = "Požadované jazyky: ";
    private static final String SALARY_TEXT_CZ = "Plat: ";
    private static final String BENEFITS_TEXT_CZ = "Benefity: ";
    private static final String WORK_TAGS_TEXT_CZ = "Zařazeno: ";
    private static final String EMPLOYMENT_FORM_TEXT_CZ = "Typ pracovního poměru: ";
    private static final String CONTRACT_DURATION_TEXT_CZ = "Délka pracovního poměru: ";
    private static final String TYPE_OF_CONTRACT_TEXT_CZ = "Typ smluvního vztahu: ";
    private static final String EMPLOYER_TEXT_CZ = "Zadavatel: ";

    // In English
    private static final String EDUCATION_TEXT_EN = "Required education: ";
    private static final String LANGUAGES_TEXT_EN = "Required languages: ";
    private static final String SALARY_TEXT_EN = "Salary: ";
    private static final String BENEFITS_TEXT_EN = "Benefits: ";
    private static final String WORK_TAGS_TEXT_EN = "Listed in: ";
    private static final String EMPLOYMENT_FORM_TEXT_EN = "Employment form: ";
    private static final String CONTRACT_DURATION_TEXT_EN = "Contract duration: ";
    private static final String TYPE_OF_CONTRACT_TEXT_EN = "Employment contract: ";
    private static final String EMPLOYER_TEXT_EN = "Employer type: ";

    // In unknown language
    private static final String UNKNOWN_LANGUAGE = "Neznámý jazyk";

    // file management variables
    private static final String FILE_TS_PATTERN = "ddMMyy";
    private static final String FILE_PREFIX = "data";
    private static final String FILE_PORTAL = "jobscz";

    // position closed
    private final static String CLOSED_POSITION_XPATH = "//*[@id='recommended']//h1";
    private final static String CLOSED_POSITION_TEXT = "Tato nabídka zde již není";

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
        // can be changed so that it waits instead of nasty thread sleep
        // but it's not so important right now

        // create list of profession we desire
        List<String> workPositions = Arrays.asList(ISIT_SPRAVA,
                ISIT_KONZULTACE,
                ISIT_VYVOJ);

        // wait for the page to load
        waitForVisibilityOfElement(driver, workInput);

        // enter each profession, wait and click the loaded category
        for (String workPosition : workPositions) {
            workInput.sendKeys(workPosition);
            Thread.sleep(500);
            workInput.sendKeys(Keys.ENTER);
        }
    }

    public void selectRemoteWork() {
        // wait for the page to load
        waitForVisibilityOfElement(driver, workInput);

        // open on the dropdown
        homeOfficeDropdown.click();

        // click on "Možnost práce z domova"
        homeOfficeDropdown.findElement(By.xpath("//*[text()[contains(.,'Možnost práce z domova')]]")).click();
    }

    public void selectFullTime() {
        // wait for the page to load
        waitForVisibilityOfElement(driver, typeOfContractDropdown);

        // open on the dropdown
        typeOfContractDropdown.click();

        // click on "Plný úvazek"
        typeOfContractDropdown.findElement(By.xpath("//*[text()[contains(.,'Plný úvazek')]]")).click();
    }

    public void clickSearchButton() {
        // wait for the page to load
        waitForVisibilityOfElement(driver, searchButton);

        // click the search button
        searchButton.click();
    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
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

            // click the button to go to the next page
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
            String linkAddress = cropPositionLink(position, POSITION_LINK_XPATH);
            String salaryValue = getLabelValue(position, SALARY_LABEL_XPATH);
            String homeOfficeValue = getLabelValue(position, HOMEOFFICE_LABEL_XPATH);

            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());

            // open the current position in new tab
            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
            openLinkInTab(linkAddress, currentTabs);

            // get position level and decide what to do with the position
            switch (getPositionLevel(linkAddress)) {
                case BASIC:
                case MEDIUM:
                    // get the detailed information from the page for BASIC and MEDIUM
                    // print what info from which position you save
                    System.out.println("Detail info: " + positionName);
                    getDetailedInformation(timestamp, positionName, company, linkAddress, homeOfficeValue);
                    break;
                case ADVANCED:
                    // otherwise save basic info to the excel, reading it from different layouts is not an option here
                    // print what info from which position you save
                    System.out.println("Basic info: " + positionName);
                    ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue, homeOfficeValue);
                    break;
                case CLOSED:
                    // position is closed and there is nothing we can do
                    System.out.println("Closed: " + positionName);
            }

            //close the current tab with the position and focus back on the position list page
            closeTabWithPosition(currentTabs);
        }
    }

    private PositionLevel getPositionLevel(String linkAddress) {
        PositionLevel level;
        boolean isDetailPresent = isElementPresentByXpath("//div[@data-visited-position]");
        boolean isUrlSame = driver.getCurrentUrl().equals(linkAddress);
        boolean isPositionClosed = isPositionClosed();

        if (isPositionClosed) {
            return PositionLevel.CLOSED;
        }

        if (isDetailPresent && isUrlSame) {
            // if both are true
            level = PositionLevel.BASIC;
        } else if (isDetailPresent ^ isUrlSame) {
            // if one is true but not both
            level = PositionLevel.MEDIUM;
        } else {
            // if both are false,
            // could be also UNKNOWN, but it doesn't matter
            level = PositionLevel.ADVANCED;
        }

        return level;
    }

    private boolean isPositionClosed() {
        // check if the position is closed, return boolean
        // TBH not fully tested may cause problems
        if (isElementPresentByXpath(CLOSED_POSITION_XPATH)) {
            return driver.findElement(By.xpath(CLOSED_POSITION_XPATH))
                    .getText().equals(CLOSED_POSITION_TEXT);
        }

        return false;
    }

    private String getContactInfo(String xpath) {
        // create empty string variable
        String contactName = "";

        //if the element is present
        if (isElementPresentByXpath(xpath)) {

            // save it's value to variable
            contactName = driver.findElement(By.xpath(xpath)).getText();
        }

        return contactName;
    }

    private boolean isElementPresentByXpath(String elementXpath) {
        return driver.findElements(By.xpath(elementXpath)).size() > 0;
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

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getElementText(WebElement parentElement, String xpath) {
        // return text of the element based on the parentElement and xpath
        return parentElement.findElement(By.xpath("." + xpath)).getText();
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

    private void getDetailedInformation(String timestamp, String positionName, String companyValue, String link,
                                        String homeOfficeValue) throws IOException {

        // create empty string variables for the detailed information
        String contactName, contactPhone, education, languages, salary, benefits, typeOfEmployment, typeOfContract,
                authority;

        education = languages = salary = benefits = typeOfEmployment = typeOfContract
                = authority = "";

        // wait for the page to load
        waitForVisibilityOfElement(driver, detailInfoElement);

        // make sure the element exists before taking the info out of it
        if (doesElementExist(DETAIL_INFO_XPATH)) {

            // get contact name and phone information
            contactName = getContactInfo(CONTACT_NAME_XPATH);
            contactPhone = getContactInfo(CONTACT_INFO_XPATH);

            // determine which language is used and save the info to variables
            switch (determineLanguage(DETAIL_INFO_XPATH)) {

                // if the language is CZECH, use CZ text
                case "CZECH":
                    education = getInformationText(EDUCATION_TEXT_CZ);
                    languages = getInformationText(LANGUAGES_TEXT_CZ);
                    salary = getInformationText(SALARY_TEXT_CZ);
                    benefits = getInformationText(BENEFITS_TEXT_CZ);
                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_CZ);
                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_CZ);
                    authority = getInformationText(EMPLOYER_TEXT_CZ);
                    break;

                // if the language is ENGLISH, use EN text
                case "ENGLISH":
                    education = getInformationText(EDUCATION_TEXT_EN);
                    languages = getInformationText(LANGUAGES_TEXT_EN);
                    salary = getInformationText(SALARY_TEXT_EN);
                    benefits = getInformationText(BENEFITS_TEXT_EN);
                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_EN);
                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_EN);
                    authority = getInformationText(EMPLOYER_TEXT_EN);
                    break;
                // if the language is UNKNOWN, don't bother and save UNKNOWN only
                case "UNKNOWN":
                    education = UNKNOWN_LANGUAGE;
                    languages = UNKNOWN_LANGUAGE;
                    salary = UNKNOWN_LANGUAGE;
                    benefits = UNKNOWN_LANGUAGE;
                    typeOfEmployment = UNKNOWN_LANGUAGE;
                    typeOfContract = UNKNOWN_LANGUAGE;
                    authority = UNKNOWN_LANGUAGE;
                    break;
            }

            // write values to Excel
            ExcelWriter(timestamp, positionName, companyValue, link, salary, homeOfficeValue, contactName, contactPhone,
                    education, languages, salary, benefits, typeOfEmployment, typeOfContract, authority);
        } else {
            System.out.println("this should not happen - it means that the position was determined as detailed " +
                    "but the detailed info is not on the page (possibly operator changed the code of the page)");
        }
    }

    private String getInformationText(String informationName) {

        // create variables
        String xpath = "." + "/dd[span[text()='" + informationName + "']]";
        String fullText = "";

        // if the required information element existst
        if (detailInfoElement.findElements(By.xpath(xpath)).size() > 0) {

            // save it's value to the variable
            fullText = detailInfoElement.findElement(By.xpath(xpath)).getText();

            // and return it
            return fullText.substring(fullText.indexOf(":") + 2);
        }

        // otherwise return empty string
        return fullText;
    }

    private boolean doesElementExist(String xpath) {
        return driver.findElements(By.xpath(xpath)).size() > 0;
    }

    private String determineLanguage(String xpath) {

        String elementText = driver.findElement(By.xpath(xpath)).getText();

        if (elementText.contains(EDUCATION_TEXT_CZ) || elementText.contains(EMPLOYER_TEXT_CZ)) {
            return "CZECH";
        } else if (elementText.contains(CONTRACT_DURATION_TEXT_EN) || elementText.contains(EMPLOYER_TEXT_EN)) {
            return "ENGLISH";
        }

        return "UNKNOWN";
    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter(String timestamp, String positionName, String company,
                             String link, String salary, String workFromHome) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, JobsPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome,
                "NO", "", "", "", "", "", "", "", "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, JobsPage.SHEETNAME, valueToWrite);
    }

    private void ExcelWriter(String timestamp, String positionName, String company, String link, String salary,
                             String workFromHome, String contactName, String contactPhone, String education,
                             String languages, String detailSalary, String benefits, String typeOfEmployment,
                             String typeOfContract, String authority) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, JobsPage.WEBSITE_NAME, positionName, company, link, salary, workFromHome,
                "YES", contactName, contactPhone, education, languages, detailSalary, benefits, typeOfEmployment,
                typeOfContract, authority};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, JobsPage.SHEETNAME, valueToWrite);
    }

    public void copyFileToFolder() throws IOException {

        File sourceFile = new File(RESOURCES_FOL_PATH + FILE_NAME);
        File newFile = new File(ARCHIVE_PATH + getFileName());

        Files.copy(sourceFile.toPath(), newFile.toPath());
    }

    public void replaceFileWithTemplate() throws IOException {

        File sourceFile = new File(RESOURCES_FOL_PATH + EMPTY_FILE_NAME); // data_empty.xlsx
        File newFile = new File(RESOURCES_FOL_PATH + FILE_NAME); // data.xlsx

        Files.deleteIfExists(newFile.toPath());
        Files.copy(sourceFile.toPath(), newFile.toPath());
    }

    public void copyToRemoteDriver() throws IOException {
        final String fileName = getFileName();

        File sourceFile = new File(ARCHIVE_PATH + fileName);
        File newFile = new File(REMOTE_DRIVE_PATH + fileName);

        Files.copy(sourceFile.toPath(), newFile.toPath());
    }

    private String getFileName() {
        // e.g. data_jobscz_220421.xlsx
        return String.format("%s_%s_%s.xlsx", FILE_PREFIX, FILE_PORTAL, getTimeStamp(FILE_TS_PATTERN));
    }

    /*
    public void recheckAllInputs() {
        // this is prepared for the future development
        // it should check if the filter is correctly set up
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
     */
}
