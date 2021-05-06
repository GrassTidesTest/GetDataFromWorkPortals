package test;

import base.TestBase;
import org.junit.Test;
import pages.PracePage;

import java.io.IOException;

public class PraceTest extends TestBase {

    @Test
    public void openPageTest() throws InterruptedException, IOException {
        PracePage pracePage = new PracePage();

        pracePage.openPage();
        pracePage.showSearchForm();
        pracePage.selectJobType();
        pracePage.enterProfessionField();
//        pracePage.recheckAllInputs();
        pracePage.clickSearchButton();

        pracePage.savePositionsToExcel();

    }
}
