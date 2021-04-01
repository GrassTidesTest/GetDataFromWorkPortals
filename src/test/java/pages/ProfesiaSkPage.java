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

public class ProfesiaSkPage {
    private WebDriver driver;

    private static final String BASE_URL = "https://www.profesia.sk/search_offers.php";
    private static final String IT_PROFESSION = "IT";
    private static final String FULLTIME_CHECKBOX_XP = "//label[text()[contains(.,'plný úväzok')]]";

    @FindBy(id = "offerCriteriaSuggesterInputId")
    private WebElement professionFieldTextbox;

    @FindBy(id = "idSearchOffers5")
    private WebElement jobTypeElement;

    @FindBy(id = "idSearchOffers33")
    private WebElement inWhatLanguageElement;

    @FindBy(xpath = "//button[text()='Hľadať ponuky']")
    private WebElement searchButton;

    public ProfesiaSkPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() {
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }

    public void enterProfessionField() throws InterruptedException {
        // wait for the page to load
        waitForVisibilityOfElement(driver, 5, professionFieldTextbox);

        // type 'IT' into the textbox and hit enter
        professionFieldTextbox.sendKeys(IT_PROFESSION);
        Thread.sleep(500);
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

    public void recheckAllInputs() {


    }

    private void waitForVisibilityOfElement(WebDriver driver, int timeInSecs, WebElement webElement) {
        new WebDriverWait(driver, timeInSecs)
                .until(ExpectedConditions.visibilityOf(webElement));
    }

}
