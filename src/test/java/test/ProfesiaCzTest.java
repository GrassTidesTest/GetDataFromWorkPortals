package test;

import base.TestBase;
import org.junit.Test;
import pages.ProfesiaCzPage;

import java.io.IOException;

public class ProfesiaCzTest extends TestBase {

    @Test
    public void openCzPageTest() throws InterruptedException, IOException {
        ProfesiaCzPage profesiaCzPage = new ProfesiaCzPage();

        profesiaCzPage.openPage();
        profesiaCzPage.closeCookiesMessage();
        profesiaCzPage.enterProfessionField();
        profesiaCzPage.selectFullTimeJob();
        profesiaCzPage.selectOfferLanguage();
//        profesiaCzPage.recheckAllInputs();
        profesiaCzPage.clickSearchButton();

        profesiaCzPage.savePositionsToExcel();


    }
}
