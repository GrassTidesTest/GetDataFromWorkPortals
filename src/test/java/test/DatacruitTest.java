package test;

import base.TestBase;
import org.junit.Test;
import pages.DatacruitPage;

public class DatacruitTest extends TestBase {

    @Test
    public void getDataFromDatacruitPage() {
        DatacruitPage datacruitPage = new DatacruitPage();

        datacruitPage.openPage();
        datacruitPage.selectLocation();
        datacruitPage.selectTypeOfWork();
//        datacruitPage.recheckAllInputs();


    }
}
