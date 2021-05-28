package test;

import base.TestBase;
import org.junit.Test;
import pages.JobsPage;

import java.io.IOException;

public class JobsTest extends TestBase {

    @Test
    public void getPositionsFromThePage() throws InterruptedException, IOException {
        JobsPage jobsPage = new JobsPage();

        jobsPage.openPage();
        jobsPage.selectISITPositions();
        jobsPage.selectRemoteWork();
        jobsPage.selectFullTime();
//        jobsPage.recheckAllInputs();
        jobsPage.clickSearchButton();

        jobsPage.savePositionsToExcel();

//        jobsPage.copyFileToFolder();
//        jobsPage.replaceFileWithTemplate();
//        jobsPage.copyToRemoteDriver();
    }
}
