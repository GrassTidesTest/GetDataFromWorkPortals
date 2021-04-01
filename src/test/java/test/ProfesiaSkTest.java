package test;

import base.TestBase;
import org.junit.Test;
import pages.ProfesiaSkPage;

public class ProfesiaSkTest extends TestBase {


    @Test
    public void openSkPageTest() throws InterruptedException {
        ProfesiaSkPage profesiaSkPage = new ProfesiaSkPage();

        profesiaSkPage.openPage();
        profesiaSkPage.openPage();
        profesiaSkPage.enterProfessionField();
        profesiaSkPage.selectFullTimeJob();
        profesiaSkPage.selectOfferLanguage();
        profesiaSkPage.recheckAllInputs();
        profesiaSkPage.clickSearchButton();


    }
}
