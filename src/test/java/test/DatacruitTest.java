package test;

import base.TestBase;
import org.junit.Test;
import pages.DatacruitPage;

import java.io.IOException;

public class DatacruitTest extends TestBase {

    @Test
    public void getDataFromDatacruitPage() throws IOException {
        DatacruitPage datacruitPage = new DatacruitPage();

        datacruitPage.openPage();
        datacruitPage.selectLocation();
        datacruitPage.selectTypeOfWork();
//        datacruitPage.recheckAllInputs();
        datacruitPage.savePositionsToExcel();

    }
}
