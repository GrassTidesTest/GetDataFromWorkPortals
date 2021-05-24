package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfesiaCzPage {
    private WebDriver driver;

    private static final String BASE_URL = "https://www.profesia.cz/search_offers.php";
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úvazek')]]";

    @FindBy(id = "offerCriteriaSuggesterInputId")
    private WebElement professionFieldTextbox;

    @FindBy(id = "idSearchOffers5")
    private WebElement jobTypeElement;

    @FindBy(id = "idSearchOffers33")
    private WebElement inWhatLanguageElement;

    @FindBy(xpath = "//button[text()='Hledat nabídky']")
    private WebElement searchButton;

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
        // wait for the page to load
        new WebDriverWait(driver,5);

        // if the cookies message appears disable it
        if (driver.findElements(By.cssSelector("button[aria-label='NESOUHLASÍM']")).size() > 0) {
            driver.findElement(By.cssSelector("button[aria-label='NESOUHLASÍM']")).click();
        }
    }

    public void enterProfessionField() throws InterruptedException {
        // wait for the page to load
        waitForVisibilityOfElement(driver, 5, professionFieldTextbox);

        // type 'IT' into the textbox and hit enter to select the IT from the suggested options
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
        professionFieldTextbox.sendKeys(Keys.ENTER);
    }

    public void selectFullTimeJob() {
        // find
        WebElement fullTimeCheckbox = jobTypeElement.findElement(By.xpath(FULLTIME_CHECKBOX_XP));

        if (!fullTimeCheckbox.isSelected()) {
            fullTimeCheckbox.click();
        }
    }

    public void selectOfferLanguage() {
//        WebElement czechBox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers28']"));
//        WebElement slovakCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers29']"));
//        WebElement englishCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers30']"));
//        WebElement germanCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers31']"));
//        WebElement agencyCheckbox = inWhatLanguageElement.findElement(By.xpath("//label/input[@id='idSearchOffers32']"));
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

    public void recheckAllInputs() {


    }

    private void waitForVisibilityOfElement(WebDriver driver, int timeInSecs, WebElement webElement) {
        new WebDriverWait(driver, timeInSecs)
                .until(ExpectedConditions.visibilityOf(webElement));
    }
}
