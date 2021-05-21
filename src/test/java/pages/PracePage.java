package pages;

import base.WebDriverSingleton;
import enumerators.PositionLevel;
import helpers.ExcelEditor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PracePage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final int NUMBER_OF_PAGES_TO_CHECK = 30;
    private static final int TIMEOUT_IN_SECS = 5;

    private static final String BASE_URL = "https://www.prace.cz/nabidky/";
    private static final String WEBSITE_NAME = "prace.cz";
    private static final String SHEETNAME = "COLLECTED_DATA";

    private static final String IT_PROFESSION = "IT";
    private static final String POSITIONS_XPATH = "//ul/li[descendant::*[contains(@class,'grid--rev')]" +
            "/div[contains(@class,'grid__item') and descendant::*[contains(@class,'grid')]]]";
    private static final String MAIN_NEXT_PAGE_BUTTON_XPATH = "//div[contains(@class,'pager')]";
    private static final String NEXT_PAGE_BUTTON_XPATH = MAIN_NEXT_PAGE_BUTTON_XPATH + "//span[@class='pager__next']";

    // Page with position constants
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_NAME_XPATH = "//h3";
    private static final String POSITION_COMPANY_XPATH = "//div[contains(@class,'company')]";
    private static final String POSITION_LINK_XPATH = "//h3/a";
    private static final String SALARY_LABEL_XPATH = "//*[contains(@class,'--salary')]";
    private static final String HOMEOFFICE_LABEL_XPATH = "//*[contains(@class,'search-list__home-office--label')]";

    // Position detail page constants
    private static final String DETAIL_INFO_XPATH = "//div[@class='double-standalone']/dl";
    private static final String CONTACT_NAME_CLASS = "advert__recruiter";
    private static final String CONTACT_INFO_CLASS = "advert__recruiter__phone";

    // In Czech
    private static final String EDUCATION_TEXT_CZ = "Vzdělání:";
    private static final String LANGUAGES_TEXT_CZ = "Jazyky:";
    private static final String BENEFITS_TEXT_CZ = "Benefity:";
    //    private static final String WORK_TAGS_TEXT_CZ = "Zařazeno: ";
    private static final String EMPLOYMENT_FORM_TEXT_CZ = "Pracovní poměr:";
    //    private static final String CONTRACT_DURATION_TEXT_CZ = "Délka pracovního poměru: ";
    private static final String TYPE_OF_CONTRACT_TEXT_CZ = "Smluvní vztah:";
    private static final String EMPLOYER_TEXT_CZ = "Firma:";
    private static final String SUITABLE_FOR_TEXT_CZ = "Vhodné i pro:";
    private static final String SALARY_XPATH = "//h3[contains(@class,'salary')]";
    private static final String EMPLOYER_XPATH = "//dd[contains(@class,'company-name')]//strong";


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

    // Position detail page
    @FindBy(xpath = DETAIL_INFO_XPATH)
    private WebElement detailInfoElement;


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
            getPositionsAndSaveThemToExcel(positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
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
            String company = getCompanyText(position, POSITION_COMPANY_XPATH);
            String linkAddress = cropPositionLink(position, POSITION_LINK_XPATH);
            String salaryValue = getLabelValue(position, SALARY_LABEL_XPATH);
//            String homeOfficeValue = getLabelValue(position, HOMEOFFICE_LABEL_XPATH);

//            System.out.println(timestamp + "----------------------------------------------------------------------");
//            System.out.println(positionName);
//            System.out.println(company);
//            System.out.println(linkAddress);
//            System.out.println(salaryValue);

//            ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue);

            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());
//
//            // open the current position in new tab
//            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
            openLinkInTab(linkAddress, currentTabs);
//            System.out.println("----TAB OPENED----");

            // get position level and decide what to do with the position
            switch (getPositionLevel(linkAddress)) {
                case BASIC:
                case MEDIUM:
                    // get the detailed information from the page for BASIC and MEDIUM
                    // print what info from which position you save
                    System.out.println("Detail info: " + positionName);
                    getDetailedInformation(timestamp, positionName, company, linkAddress);
                    break;
                case ADVANCED:
                    // otherwise save basic info to the excel, reading it from different layouts is not an option here
                    // print what info from which position you save
                    System.out.println("Basic info: " + positionName);
//                    ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue, homeOfficeValue);
                    break;
                case CLOSED:
                    // position is closed and there is nothing we can do
                    System.out.println("Closed: " + positionName);
                    break;
            }

            //close the current tab with the position and focus back on the position list page
            closeTabWithPosition(currentTabs);
//            System.out.println("----------------------------------------------------TAB CLOSED----");
//            System.out.println();
        }
    }

    private void getDetailedInformation(String timestamp, String positionName, String companyValue, String link)
            throws IOException {

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
            contactName = getContactInfo(CONTACT_NAME_CLASS);
            contactPhone = getContactInfo(CONTACT_INFO_CLASS);

//            System.out.println("Link: " + link);
//            System.out.println("Contact name: " + contactName);
//            System.out.println("Contact phone: " + contactPhone);
//
//            // determine which language is used and save the info to variables
//            switch (determineLanguage(DETAIL_INFO_XPATH)) {
//
//                // if the language is CZECH, use CZ text
//                case "CZECH":
                    education = getInformationText(EDUCATION_TEXT_CZ);
                    languages = getInformationText(LANGUAGES_TEXT_CZ);
                    salary = getOtherInfoText(SALARY_XPATH);
                    benefits = getInformationText(BENEFITS_TEXT_CZ);
                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_CZ);
                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_CZ);
                    authority = getOtherInfoText(EMPLOYER_XPATH);

//            System.out.println("Education: " + education);
//            System.out.println("Languages: " + languages);
//            System.out.println("Salary: " + salary);
//            System.out.println("Benefits: " + benefits);
//            System.out.println("Type of employment: " + typeOfEmployment);
//            System.out.println("Type of contract: " + typeOfContract);
//            System.out.println("Authority: " + authority);

//                    break;
//
//                // if the language is ENGLISH, use EN text
//                case "ENGLISH":
//                    education = getInformationText(EDUCATION_TEXT_EN);
//                    languages = getInformationText(LANGUAGES_TEXT_EN);
//                    salary = getInformationText(SALARY_TEXT_EN);
//                    benefits = getInformationText(BENEFITS_TEXT_EN);
//                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_EN);
//                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_EN);
//                    authority = getInformationText(EMPLOYER_TEXT_EN);
//                    break;
//                // if the language is UNKNOWN, don't bother and save UNKNOWN only
//                case "UNKNOWN":
//                    education = UNKNOWN_LANGUAGE;
//                    languages = UNKNOWN_LANGUAGE;
//                    salary = UNKNOWN_LANGUAGE;
//                    benefits = UNKNOWN_LANGUAGE;
//                    typeOfEmployment = UNKNOWN_LANGUAGE;
//                    typeOfContract = UNKNOWN_LANGUAGE;
//                    authority = UNKNOWN_LANGUAGE;
//                    break;
//        }

            // write values to Excel
            ExcelWriter(timestamp, positionName, companyValue, link, salary, contactName, contactPhone,
                    education, languages, salary, benefits, typeOfEmployment, typeOfContract, authority);
        } else {
            System.out.println("this should not happen - it means that the position was determined as detailed " +
                    "but the detailed info is not on the page (possibly operator changed the code of the page)");
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

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getElementText(WebElement parentElement, String xpath) {
        // return text of the element based on the parentElement and xpath
        return parentElement.findElement(By.xpath("." + xpath)).getText();
    }

    private String getCompanyText(WebElement parentElement, String xpath) {
        // crop the •\n from the company name and return
        String originalString = parentElement.findElement(By.xpath("." + xpath)).getText();

        return originalString.substring(originalString.indexOf("•") + 2);
    }

    private String cropPositionLink(WebElement parentElement, String xpath) {
        // get the href value from the element
        String originalString = parentElement.findElement(By.xpath("." + xpath)).getAttribute("href");

        // crop the link so that the part after ? including is not present
        // link must contain ? otherwise it fails
        return originalString.substring(0, originalString.indexOf("?"));
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

    private PositionLevel getPositionLevel(String linkAddress) {
        PositionLevel level;
        boolean isDetailPresent = isElementPresentByXpath("//div[@class='double-standalone']");
        boolean isUrlSame = driver.getCurrentUrl().equals(linkAddress);
        boolean isPositionClosed = isPositionClosed();

        if (isPositionClosed) {
            return PositionLevel.CLOSED;
        }

        if (isDetailPresent && isUrlSame) {
            // ↑ if both are true
            level = PositionLevel.BASIC;
        } else if (isDetailPresent ^ isUrlSame) {
            // ↑ if one is true but not both
            level = PositionLevel.MEDIUM;
        } else {
            // ↑ if both are false,
            // ↓ could be also UNKNOWN, but it doesn't matter
            level = PositionLevel.ADVANCED;
        }

        return level;
    }

    private boolean isElementPresentByXpath(String elementXpath) {
        return driver.findElements(By.xpath(elementXpath)).size() > 0;
    }

    private boolean isElementPresentByClass(String className) {
        return driver.findElements(By.className(className)).size() > 0;
    }

    private boolean isPositionClosed() {
        // check if the position is closed, return boolean
        // TBH not fully tested may cause problems
//        if (isElementPresentByXpath(CLOSED_POSITION_XPATH)) {
//            return driver.findElement(By.xpath(CLOSED_POSITION_XPATH))
//                    .getText().equals(CLOSED_POSITION_TEXT);
//        }

        return false;
    }

    private boolean doesElementExist(String xpath) {
        // same as isElementPresentByXpath - REFACTOR
        return driver.findElements(By.xpath(xpath)).size() > 0;
    }

//    private String getContactInfo(String xpath) {
//        // create empty string variable
//        String contactName = "";
//
//        //if the element is present
//        if (isElementPresentByXpath(xpath)) {
//
//            // save it's value to variable
//            contactName = driver.findElement(By.xpath(xpath)).getText();
//        }
//
//        return contactName;
//    }

    private String getContactInfo(String className) {
        //create empty string variable
        String contactInfo = "";

        // if the element is present
        if (isElementPresentByClass(className)) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'",
                    driver.findElement(By.className(className)));

            contactInfo = driver.findElement(By.className(className)).getText();
        }

        return contactInfo;
    }

    private String getInformationText(String informationName) {

        // create variables
        String xpath = "." + "//dd[span[text()='" + informationName + "']]";
        String fullText = "";

        // if the required information element exists
        if (detailInfoElement.findElements(By.xpath(xpath)).size() > 0) {

            // save it's value to the variable
            fullText = detailInfoElement.findElement(By.xpath(xpath)).getText();

            // and return it
            return fullText.substring(fullText.indexOf(":") + 1);
        }

        // otherwise return empty string
        return fullText;
    }

    private String getOtherInfoText(String xpath){

        // create variables
        String fullText = "";

        // if the required information element exists
        if(driver.findElements(By.xpath(xpath)).size() > 0) {

            // save it's value to the variable
            fullText = driver.findElement(By.xpath(xpath)).getText();

            // if the salary is not listed, leave it empty
            if (fullText.equals("Plat neuveden"))
                return fullText = "";

            return fullText.substring(fullText.indexOf(":") + 1);
        }

        return fullText;
    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter(String timestamp, String positionName, String company,
                             String link, String salary) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, PracePage.WEBSITE_NAME, positionName, company, link, salary, "",
                "NO", "", "", "", "", "", "", "", "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, PracePage.SHEETNAME, valueToWrite);
    }

    private void ExcelWriter(String timestamp, String positionName, String company, String link, String salary,
                             String contactName, String contactPhone, String education,
                             String languages, String detailSalary, String benefits, String typeOfEmployment,
                             String typeOfContract, String authority) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, PracePage.WEBSITE_NAME, positionName, company, link, salary, "",
                "YES", contactName, contactPhone, education, languages, detailSalary, benefits, typeOfEmployment,
                typeOfContract, authority};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, PracePage.SHEETNAME, valueToWrite);
    }

//    public void recheckAllInputs() {
//
//
//    }

}
