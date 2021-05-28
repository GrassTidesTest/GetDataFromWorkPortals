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

public class ProfesiaCzPage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final String BASE_URL = "https://www.profesia.cz/search_offers.php";
    private static final String WEBSITE_NAME = "profesia.cz";
    private static final String SHEETNAME = "COLLECTED_DATA";
    private static final String FILE_NAME = "data.xlsx";

    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final int TIMEOUT_IN_SECS = 10;

    // Filter positions page
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úvazek')]]";
    private static final String COOKIES_BTN_CSS = "button[aria-label='NESOUHLASÍM']";

    // Page with positions constants
    private static final String POSITIONS_XPATH = "//li[@class='list-row']";
    private static final String NEXT_PAGE_BUTTON_CSS = "ul.pagination a.next";
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_LINK_CSS = "h2 > a[id]";
    private static final String POSITION_COMPANY_CSS = ".employer";
    private static final String SALARY_LABEL_CSS = "a[data-dimension7='Salary label']";

    // Page of the position
    private static final String POSITION_CONTACT_ELEMENT_XP = "//*[contains(text(),'Kontaktní osoba:')]";
    private static final String EMPLOYMENT_TYPE_CSS = "[itemprop='employmentType']";

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

    @FindBy(xpath = POSITION_CONTACT_ELEMENT_XP)
    private WebElement contactElement;

    @FindBy(id = "detail")
    private WebElement positionMainElement;

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
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_IN_SECS);

        // wait for the page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(COOKIES_BTN_CSS)));

        // if the cookies message appears disable it
        if (driver.findElements(By.cssSelector(COOKIES_BTN_CSS)).size() > 0) {
            driver.findElement(By.cssSelector(COOKIES_BTN_CSS)).click();
        }
    }

    public void enterProfessionField() throws InterruptedException {
        // wait for the page to load
        waitUntilClickable(driver, professionFieldTextbox);

        // type 'IT' into the textbox and hit enter to select the IT from the suggested options
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
        professionFieldTextbox.sendKeys(Keys.ENTER);
    }

    public void selectFullTimeJob() {
        // find the checkbox
        WebElement fullTimeCheckbox = jobTypeElement.findElement(By.xpath(FULLTIME_CHECKBOX_XP));

        // if not checked, check it
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
            getPositionsAndSaveThemToExcel(positions_size);
//            System.out.println(positions_size);

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }

    private void getPositionsAndSaveThemToExcel(int positions_size) throws IOException {

        // go through the list of positions and for each position determine
        // if the basic info or detail info will be saved to the excel
        for (int i = 1; i < positions_size + 1; i++) {

            // wait for the page to load
            waitUntilClickable(driver, listElement);

            // get webElement of the current position
            // use i in xpath to determine which position from the list to work with
            // i = 1, means it's the first position from the list etc.
            WebElement position = driver.findElement(By.xpath(POSITIONS_XPATH + "[" + i + "]"));

            // get basic information for the position and save it to corresponding variables
            String timestamp = getTimeStamp(TIMESTAMP_PATTERN);
            String positionName = getElementTextCss(position, POSITION_LINK_CSS);
            String company = getElementTextCss(position, POSITION_COMPANY_CSS);
            String linkAddress = getPositionLinkCss(position, POSITION_LINK_CSS);
            String salaryValue = getLabelValueCss(position, SALARY_LABEL_CSS);

            System.out.println(timestamp);
            System.out.println(positionName);
            System.out.println(linkAddress);
            System.out.println(company);
            System.out.println(salaryValue);
            System.out.println("----------------------------");


            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());

            // open the current position in new tab
            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
            openLinkInTab(linkAddress, currentTabs);

//            // get position level and decide what to do with the position
//            switch (getPositionLevel(linkAddress)) {
//                case BASIC:
//                case MEDIUM:
//                    // get the detailed information from the page for BASIC and MEDIUM
//                    // print what info from which position you save
//                    System.out.println("Detail info: " + positionName);
            getDetailedInformation(timestamp, positionName, company, linkAddress);
//                    break;
//                case ADVANCED:
//                    // otherwise save basic info to the excel, reading it from different layouts is not an option here
//                    // print what info from which position you save
//                    System.out.println("Basic info: " + positionName);
//                    ExcelWriter(timestamp, positionName, company, linkAddress, salaryValue);
//                    break;
//                case CLOSED:
//                    // position is closed and there is nothing we can do
//                    System.out.println("Closed: " + positionName);
//            }
//
            // close the current tab with the position and focus back on the position list page
            closeTabWithPosition(currentTabs);
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

    private void getDetailedInformation(String timestamp, String positionName, String companyValue, String link)
            throws IOException {

        String[] contactInfo;
        String typeOfEmployment = "";

        // wait for the page to load
        waitForVisibilityOfElement(driver, positionMainElement);

        if (driver.findElements(By.cssSelector(EMPLOYMENT_TYPE_CSS)).size() > 0) {
            typeOfEmployment = driver.findElement(By.cssSelector(EMPLOYMENT_TYPE_CSS)).getText();
        }

        System.out.println("Employment type: " + typeOfEmployment);


        if (driver.findElements(By.xpath(POSITION_CONTACT_ELEMENT_XP)).size() > 0) {
            contactInfo = getContactInformation(contactElement.getText());

            System.out.println("Contact name: " + contactInfo[0]);
            System.out.println("Contact phone: " + contactInfo[1]);
            System.out.println("Contact mail: " + contactInfo[2]);
        } else {
            System.out.println("NO CONTACT INFO");
        }
        System.out.println("------------------------------------------------------------------------------------");


        // make sure the element exists before taking the info out of it
//        if (doesElementExist(DETAIL_INFO_XPATH)) {

        // get contact name and phone information
//            contactName = getContactInfo(CONTACT_NAME_XPATH);
//            contactPhone = getContactInfo(CONTACT_INFO_XPATH);

        // determine which language is used and save the info to variables
//            switch (determineLanguage(DETAIL_INFO_XPATH)) {

        // if the language is CZECH, use CZ text
//                case "CZECH":
//                    education = getInformationText(EDUCATION_TEXT_CZ);
//                    languages = getInformationText(LANGUAGES_TEXT_CZ);
//                    salary = getInformationText(SALARY_TEXT_CZ);
//                    benefits = getInformationText(BENEFITS_TEXT_CZ);
//                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_CZ);
//                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_CZ);
//                    authority = getInformationText(EMPLOYER_TEXT_CZ);
//                    break;

        // if the language is ENGLISH, use EN text
//                case "ENGLISH":
//                    education = getInformationText(EDUCATION_TEXT_EN);
//                    languages = getInformationText(LANGUAGES_TEXT_EN);
//                    salary = getInformationText(SALARY_TEXT_EN);
//                    benefits = getInformationText(BENEFITS_TEXT_EN);
//                    typeOfEmployment = getInformationText(EMPLOYMENT_FORM_TEXT_EN);
//                    typeOfContract = getInformationText(TYPE_OF_CONTRACT_TEXT_EN);
//                    authority = getInformationText(EMPLOYER_TEXT_EN);
//                    break;
        // if the language is UNKNOWN, don't bother and save UNKNOWN only
//                case "UNKNOWN":
//                    education = UNKNOWN_LANGUAGE;
//                    languages = UNKNOWN_LANGUAGE;
//                    salary = UNKNOWN_LANGUAGE;
//                    benefits = UNKNOWN_LANGUAGE;
//                    typeOfEmployment = UNKNOWN_LANGUAGE;
//                    typeOfContract = UNKNOWN_LANGUAGE;
//                    authority = UNKNOWN_LANGUAGE;
//                    break;
//            }

        // write values to Excel
//            ExcelWriter(timestamp, positionName, companyValue, link, salary, homeOfficeValue, contactName, contactPhone,
//                    education, languages, salary, benefits, typeOfEmployment, typeOfContract, authority);
//        } else {
//            System.out.println("this should not happen - it means that the position was determined as detailed " +
//                    "but the detailed info is not on the page (possibly operator changed the code of the page)");
//        }
    }

    private String[] getContactInformation(String originalString) {
        // split original string to an array
        String[] originalStrings = originalString.split("\n");

        // declare new string array with three elements
        String[] strings = new String[]{"", "", ""};

        // iterate through originalStrings
        for (String string : originalStrings) {

            // manipulate string and get only the information you desire
            String substring = string.substring(string.indexOf(":") + 2);

            // if string contains "Kontaktní osoba" it's contact name = strings[0]
            if (string.contains("Kontaktní osoba")) {
                strings[0] = substring;
            }

            // if string contains "Tel." it's contact phone = strings[1]
            if (string.contains("Tel.")) {
                strings[1] = substring;
            }

            // if string contains "Email" and not "životopis" it's contact email = strings[2]
            if (string.contains("E-mail") && !string.contains("životopis")) {
                strings[2] = substring;
            }
        }

        return strings;
    }


    public void recheckAllInputs() {


    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    private void waitUntilClickable(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.elementToBeClickable(webElement));
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

//    private String getElementText(WebElement parentElement, String xpath) {
//        // return text of the element based on the parentElement and xpath
//        return parentElement.findElement(By.xpath("." + xpath)).getText();
//    }

    private String getElementTextCss(WebElement parentElement, String cssSel) {
        // return text of the element based on the parentElement and css selector
        return parentElement.findElement(By.cssSelector(cssSel)).getText();
    }

    private String cropPositionLink(WebElement parentElement, String xpath) {
        // get the href value from the element
        String originalString = parentElement.findElement(By.xpath("." + xpath)).getAttribute("href");

        // crop the link so that the part after ? including is not present
        // link must contain ? otherwise it fails
        return originalString.substring(0, originalString.indexOf("?"));
    }

    private String getPositionLinkCss(WebElement parentElement, String cssSel) {
        // get the href value from the element
        return parentElement.findElement(By.cssSelector(cssSel)).getAttribute("href");
    }

//    private String getLabelValue(WebElement parentElement, String xpath) {
//        // define empty string
//        String value = "";
//
//        // if the element exists
//        if (parentElement.findElements(By.xpath("." + xpath)).size() > 0) {
//
//            // save it's value to the string
//            value = parentElement.findElement(By.xpath("." + xpath)).getText().trim();
//        }
//
//        return value;
//    }

    private String getLabelValueCss(WebElement parentElement, String cssSel) {
        // define empty string
        String value = "";

        // if the element exists
        if (parentElement.findElements(By.cssSelector(cssSel)).size() > 0) {

            // save it's value to the string
            value = parentElement.findElement(By.cssSelector(cssSel)).getText().trim();
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

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter(String timestamp, String positionName, String company,
                             String link, String salary) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, ProfesiaCzPage.WEBSITE_NAME, positionName, company, link, salary, "",
                "NO", "", "", "", "", "", "", "", "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, ProfesiaCzPage.SHEETNAME, valueToWrite);
    }
}
