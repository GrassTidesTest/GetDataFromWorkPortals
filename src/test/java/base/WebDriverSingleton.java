package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverSingleton {
    private WebDriver driver;
    private static WebDriverSingleton instance;

    public void initialize() {
        initializeGoogleChrome();
    }

    public static WebDriverSingleton getInstance(){
        if (instance == null){
            instance = new WebDriverSingleton();
        }
        return instance;
    }

    private void initializeGoogleChrome() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();

//        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
//        driver = new ChromeDriver(options);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
