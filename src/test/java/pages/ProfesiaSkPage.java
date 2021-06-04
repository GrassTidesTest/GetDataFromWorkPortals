package pages;

import base.WebDriverSingleton;
import helpers.ExcelEditor;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProfesiaSkPage {
    private WebDriver driver;

    // IMPORTANT VARIABLES
    private static final String BASE_URL = "https://www.profesia.sk/search_offers.php";
    private static final String WEBSITE_NAME = "profesia.sk";
    private static final String SHEETNAME = "COLLECTED_DATA";
    private static final String FILE_NAME = "data.xlsx";

    private static final int NUMBER_OF_PAGES_TO_CHECK = 20;
    private static final int TIMEOUT_IN_SECS = 10;

    // Filter positions page
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úväzok')]]";
    private static final String COOKIES_BTN_CSS = "button[aria-label='NESÚHLASÍM']";

    // Page with positions
    private static final String POSITIONS_XPATH = "//li[@class='list-row']";
    private static final String NEXT_PAGE_BUTTON_CSS = "ul.pagination a.next";
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String POSITION_LINK_CSS = "h2 > a[id]";
    private static final String POSITION_COMPANY_CSS = ".employer";
    private static final String SALARY_LABEL_CSS = "a[data-dimension7='Salary label']";

    // Page of the position
    private static final String POSITION_CONTACT_ELEMENT_XP =
            "//*[contains(@class,'company-info')]/*[contains(text(),'Kontaktná osoba:')]";
    private static final String EMPLOYMENT_TYPE_CSS = "[itemprop='employmentType']";

    @FindBy(id = "offerCriteriaSuggesterInputId")
    private WebElement professionFieldTextbox;

    @FindBy(id = "idSearchOffers5")
    private WebElement jobTypeElement;

    @FindBy(id = "idSearchOffers33")
    private WebElement inWhatLanguageElement;

    @FindBy(xpath = "//button[text()='Hľadať ponuky']")
    private WebElement searchButton;

    @FindBy(css = "ul.list")
    private WebElement listElement;

    @FindBy(css = NEXT_PAGE_BUTTON_CSS)
    private WebElement nextPageButton;

    @FindBy(xpath = POSITION_CONTACT_ELEMENT_XP)
    private WebElement contactElement;

    @FindBy(id = "detail")
    private WebElement positionMainElement;


    public ProfesiaSkPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
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
        waitForVisibilityOfElement(driver, professionFieldTextbox);

        // type 'IT' into the textbox and hit enter
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
        professionFieldTextbox.sendKeys(Keys.ARROW_UP);
        professionFieldTextbox.sendKeys(Keys.ENTER);
    }

    public void selectFullTimeJob() {
        WebElement fullTimeCheckbox = jobTypeElement.findElement(By.xpath(FULLTIME_CHECKBOX_XP));

        if (!fullTimeCheckbox.isSelected()) {
            fullTimeCheckbox.click();
        }
    }

    public void selectOfferLanguage() {
        WebElement slovakCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers28']"));
        WebElement czechBox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers29']"));
        WebElement englishCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers30']"));
        WebElement germanCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers31']"));
        WebElement hungarianCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers32']"));
        WebElement agencyCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers33']"));

        if (!czechBox.isSelected()) {
            czechBox.click();
        }
        if (!slovakCheckbox.isSelected()) {
            slovakCheckbox.click();
        }
        if (!englishCheckbox.isSelected()) {
            englishCheckbox.click();
        }
        if (!germanCheckbox.isSelected()) {
            germanCheckbox.click();
        }
        if (hungarianCheckbox.isSelected()) {
            hungarianCheckbox.click();
        }
        if (agencyCheckbox.isSelected()) {
            agencyCheckbox.click();
        }
    }

    public void clickSearchButton() {
        searchButton.click();
    }

    public void savePositionsToExcel() throws IOException, InterruptedException {
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

            // if the code reaches the last page or the set limit, break the cycle
            if (getBreakCondition(i)) break;
        }
    }

    private void getPositionsAndSaveThemToExcel(int positions_size) throws IOException, InterruptedException {

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

            // open new tab and create ArrayList with windowHandles
            ((JavascriptExecutor) driver).executeScript("window.open()");
            ArrayList<String> currentTabs = new ArrayList<String>(driver.getWindowHandles());

            // open the current position in new tab
            // by doing this we avoid getting stuck by some aggressive popups when closing the position page
            openLinkInTab(linkAddress, currentTabs);

            // check if the detailed information is present, if not save basic info to excel
            if (isDetailedInfo()) {
                getDetailedInformation(timestamp, positionName, company, linkAddress, salaryValue);
            } else {
                ExcelWriter_basic(timestamp, positionName, company, linkAddress, salaryValue);
                System.out.println("Basic info: " + positionName);
            }

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

    public void recheckAllInputs() {


    }

    private String getElementTextCss(WebElement parentElement, String cssSel) throws InterruptedException, IOException {
        String text = "";

        try {
            text = parentElement.findElement(By.cssSelector(cssSel)).getText();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", parentElement);
            Thread.sleep(2);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("profesiask_h2a_failed.png"));
        }

        return text;
    }

    private String getPositionLinkCss(WebElement parentElement, String cssSel) {
        // get the href value from the element
        return parentElement.findElement(By.cssSelector(cssSel)).getAttribute("href");
    }

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

    private void getDetailedInformation(String timestamp, String positionName, String companyValue, String link,
                                        String salaryValue) throws IOException {

        String contactName, contactPhoneMail, typeOfEmployment;
        contactName = contactPhoneMail = typeOfEmployment = "";

        // wait for the page to load
        waitForVisibilityOfElement(driver, positionMainElement);

        // if employment type is present, get text
        if (driver.findElements(By.cssSelector(EMPLOYMENT_TYPE_CSS)).size() > 0) {
            typeOfEmployment = driver.findElement(By.cssSelector(EMPLOYMENT_TYPE_CSS)).getText();
        }

        // if contact is present, get text and edit
        if (driver.findElements(By.xpath(POSITION_CONTACT_ELEMENT_XP)).size() > 0) {

            // [0] = name, [1] = phone, [2] = email
            String[] contactInfo = getContactInformation(contactElement.getText());

            contactName = contactInfo[0];

            contactPhoneMail = contactInfo[2] + ", " + contactInfo[1];
        }

        // write values to Excel
        ExcelWriter_detailed(timestamp, positionName, companyValue, link, salaryValue, contactName, contactPhoneMail,
                typeOfEmployment);

        System.out.println("Detailed info: " + positionName);
    }

    private String[] getContactInformation(String originalString) {
        // split original string to an array
        String[] originalStrings = originalString.split("\n");

        // declare new string array with three elements
        String[] strings = new String[]{"", "", ""};

        // iterate through originalStrings
        for (String string : originalStrings) {

            String substring = "";

            // manipulate string and get only the information you desire
            substring = string.substring(string.indexOf(":") + 2);

            // if string contains "Kontaktní osoba" it's contact name = strings[0]
            if (string.contains("Kontaktná osoba")) {
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

    private boolean isDetailedInfo() {

        // true if the employment element is present
        boolean isEmploymentPresent = driver.findElements(By.cssSelector(EMPLOYMENT_TYPE_CSS)).size() > 0;

        // true if the contact element is present
        boolean isContactPresent = driver.findElements(By.xpath(POSITION_CONTACT_ELEMENT_XP)).size() > 0;

        // if no detailed info is present, returns false
        return isEmploymentPresent || isContactPresent;
    }

    private void waitForVisibilityOfElement(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    private void waitUntilClickable(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.elementToBeClickable(webElement));
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

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter_detailed(String timestamp, String positionName, String company,
                                      String link, String salary, String contactPerson, String contactPhoneMail,
                                      String typeOfEmployment) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, ProfesiaSkPage.WEBSITE_NAME, positionName, company, link, salary, "",
                "YES", contactPerson, contactPhoneMail, "", "", "", "", typeOfEmployment, "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, ProfesiaSkPage.SHEETNAME, valueToWrite);
    }

    // Calling ExcelEditor to write data to the excel file
    private void ExcelWriter_basic(String timestamp, String positionName, String company,
                                   String link, String salary) throws IOException {

        //Create an array with the data in the same order in which you expect to be filled in excel file
        String[] valueToWrite = {timestamp, ProfesiaSkPage.WEBSITE_NAME, positionName, company, link, salary, "",
                "NO", "", "", "", "", "", "", "", "", ""};

        //Create an object of current class
        ExcelEditor objExcelFile = new ExcelEditor();

        //Write the file using file name, sheet name and the data to be filled
        objExcelFile.WriteToExcel(System.getProperty("user.dir") + "\\src\\test\\resources",
                base.TestBase.FILE_NAME, ProfesiaSkPage.SHEETNAME, valueToWrite);
    }

}
