package test;

import base.TestBase;
import org.junit.Test;
import pages.ZZZ_playgroundPage;

public class ZZZ_playground extends TestBase {

    @Test
    public void playground() {
        ZZZ_playgroundPage zzz_playgroundPage = new ZZZ_playgroundPage();

        zzz_playgroundPage.testTabs();
    }
}
