package test;

import base.TestBase;
import org.junit.Test;
import pages.ProfesiaCzPage;

public class ProfesiaCzTest extends TestBase {

    @Test
    public void openCzPageTest() throws InterruptedException {
        ProfesiaCzPage profesiaCzPage = new ProfesiaCzPage();

        profesiaCzPage.openPage();
        profesiaCzPage.closeCookiesMessage();
        profesiaCzPage.enterProfessionField();
        profesiaCzPage.selectFullTimeJob();
        profesiaCzPage.selectOfferLanguage();
//        profesiaCzPage.recheckAllInputs();
        profesiaCzPage.clickSearchButton();


    }
}
