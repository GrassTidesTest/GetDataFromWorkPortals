package test;

import base.TestBase;
import org.junit.Test;
import pages.StartupJobsPage;

public class StartupJobsTest extends TestBase {

    @Test
    public void openPageTest() {
        StartupJobsPage startupJobsPage = new StartupJobsPage();

        startupJobsPage.openPage();
        startupJobsPage.checkVyvojCheckbox();
        startupJobsPage.checkRemoteCheckbox();
        startupJobsPage.checkFullTimeCheckbox();
        startupJobsPage.recheckAllInputs();
        startupJobsPage.clickSearchButton();

    }
}
