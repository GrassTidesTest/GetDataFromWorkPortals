package base;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;

public class TestBase {

    public static final String FILE_NAME = "data.xlsx";

    @Before
    public void setUp() {
        WebDriverSingleton.getInstance().initialize();
    }

    @After
    public void tearDown() {
//        WebDriverSingleton.getInstance().getDriver().close();
//        WebDriverSingleton.getInstance().getDriver().quit();
    }

    private WebDriver getDriver() {
        return WebDriverSingleton.getInstance().getDriver();
    }

}
