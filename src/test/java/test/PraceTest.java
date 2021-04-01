package test;

import base.TestBase;
import org.junit.Test;
import pages.PracePage;

public class PraceTest extends TestBase {

    @Test
    public void openPageTest() throws InterruptedException {
        PracePage pracePage = new PracePage();

        pracePage.openPage();
        pracePage.showSearchForm();
        pracePage.selectJobType();
        pracePage.enterProfessionField();
        pracePage.recheckAllInputs();
        pracePage.clickSearchButton();



    }
}
