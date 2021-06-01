package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ZZZ_playgroundPage {
    private WebDriver driver;

    private static final String CONTACT_NAME_CLASS = "advert__recruiter";
    private static final String CONTACT_INFO_CLASS = "advert__recruiter__phone";
    private static final String CONTACT_NAME_CSS = "span.advert__recruiter";
    private static final String CONTACT_INFO_CSS = "span.advert__recruiter__phone";
    private static final String CONTACT_INFO_XPATH = "//span[@class='advert__recruiter__phone']";
    private static final String CONTACT_NAME_XPATH = "//span[@class='advert__recruiter']";

    public ZZZ_playgroundPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void useClassSelector() {
//        driver.get("https://www.prace.cz/nabidka/1556299434/");
        driver.get("https://www.prace.cz/firma/335936766-mblue-czech-s-r-o/nabidka/1560460295/");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement element1 = driver.findElement(By.className(CONTACT_NAME_CLASS));
        WebElement element2 = driver.findElement(By.className(CONTACT_INFO_CLASS));
//        WebElement element1 = driver.findElement(By.cssSelector(CONTACT_NAME_CSS));
//        WebElement element2 = driver.findElement(By.cssSelector(CONTACT_INFO_CSS));
//        WebElement element1 = driver.findElement(By.xpath(CONTACT_NAME_XPATH));
//        WebElement element2 = driver.findElement(By.xpath(CONTACT_INFO_XPATH));

        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid blue'", element2);

        System.out.println("Nezavirej");
    }

    public void testFunctionForStringManipulation() {

        String[] contactInfo = getContactInformation(ORIGINAL_STRING);

        System.out.println("person: " + contactInfo[0]);
        System.out.println("phone: " + contactInfo[1]);
        System.out.println("email: " + contactInfo[2]);


    }


    private static final String ORIGINAL_STRING =
            "Kontaktní osoba: Ing. Lukáš Benedikt\n" +
            "Tel.: 02 654 40 421-102\n" +
            "E-mail: poslat životopis";
//            "E-mail: jsem@super.cz";

    private String[] getContactInformation(String originalString) {
        // split original string to an array
        String[] originalStrings = originalString.split("\n");

        // declare new string array with three elements
        String[] strings = new String[3];

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
            if (string.contains("E-mail") && !string.contains("životopis")){
                strings[2] = substring;
            }
        }

        return strings;
    }

    public void isEmptyStringEmpty() {
        String x = null;

        System.out.println("empty:" + x.isEmpty());
        System.out.println("blank:" + x.isBlank());
    }
}
