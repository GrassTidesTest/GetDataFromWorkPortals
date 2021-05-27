package pages;

import base.WebDriverSingleton;
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
    private static final int TIMEOUT_IN_SECS = 5;

    // Filter positions page
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úvazek')]]";
    private static final String COOKIES_BTN_CSS = "button[aria-label='NESOUHLASÍM']";

    // Page with positions constants
    private static final String POSITIONS_XPATH = "//li[@class='list-row']";
    private static final String NEXT_PAGE_BUTTON_CSS = "ul.pagination a.next";
    private static final String TIMESTAMP_PATTERN = "dd.MM.yyyy HH:mm";
    //    private static final String POSITION_NAME_XPATH = "//h3[contains(@class,'title')]";
//    private static final String POSITION_COMPANY_XPATH = "//div[contains(@class,'company')]";
//    private static final String POSITION_LINK_XPATH = "//h3/a";
    private static final String POSITION_LINK_CSS = "h2 > a[id]";

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
//            String positionName = getElementText(position, POSITION_NAME_XPATH);
//            String company = getElementText(position, POSITION_COMPANY_XPATH);
//            String linkAddress = cropPositionLink(position, POSITION_LINK_XPATH);
            String linkAddress = getPositionLinkCss(position, POSITION_LINK_CSS);
//            String salaryValue = getLabelValue(position, SALARY_LABEL_XPATH);
//            String homeOfficeValue = getLabelValue(position, HOMEOFFICE_LABEL_XPATH);

            System.out.println(timestamp);
            System.out.println(linkAddress);

            // open new tab and create ArrayList with windowHandles
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
            // close the current tab with the position and focus back on the position list page
//            closeTabWithPosition(currentTabs);
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

    private void waitUntilClickable(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, TIMEOUT_IN_SECS)
                .until(ExpectedConditions.elementToBeClickable(webElement));
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getElementText(WebElement parentElement, String xpath) {
        // return text of the element based on the parentElement and xpath
        return parentElement.findElement(By.xpath("." + xpath)).getText();
    }

    private String getCssElementText(WebElement parentElement, String cssSel) {
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
}
