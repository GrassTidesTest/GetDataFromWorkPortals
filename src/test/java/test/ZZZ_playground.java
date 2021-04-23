package test;

import org.junit.Test;
import pages.ZZZ_playgroundPage;

import java.io.IOException;
import java.text.SimpleDateFormat;

//public class ZZZ_playground extends TestBase {
public class ZZZ_playground {

    @Test
    public void test() throws IOException {
        ZZZ_playgroundPage zzz_playgroundPage = new ZZZ_playgroundPage();

        zzz_playgroundPage.copyToRemoteDriver();
    }
}