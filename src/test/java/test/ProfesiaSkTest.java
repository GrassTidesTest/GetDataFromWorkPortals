package test;

import base.TestBase;
import org.junit.Test;
import pages.ProfesiaSkPage;

import java.io.IOException;

public class ProfesiaSkTest extends TestBase {


    @Test
    public void openSkPageTest() throws InterruptedException, IOException {
        ProfesiaSkPage profesiaSkPage = new ProfesiaSkPage();

        profesiaSkPage.openPage();
        profesiaSkPage.closeCookiesMessage();
        profesiaSkPage.enterProfessionField();
        profesiaSkPage.selectFullTimeJob();
        profesiaSkPage.selectOfferLanguage();
//        profesiaSkPage.recheckAllInputs();
        profesiaSkPage.clickSearchButton();

        profesiaSkPage.savePositionsToExcel();
        System.out.println("wait here");


    }
}
