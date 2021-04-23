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

        zzz_playgroundPage.copyFileToFolder();
        zzz_playgroundPage.replaceFileWithTemplate();
    }

    @Test
    public void string() {
        System.out.println("data_jobscz_220421.xlsx");
        System.out.println(getFileName());
    }

    private static final String FILE_TS_PATTERN = "ddMMyy";
    private static final String FILE_PREFIX = "data";
    private static final String FILE_PORTAL = "jobscz";

    private String getFileName() {
        // data_jobscz_220421.xlsx
        return String.format("%s_%s_%s.xlsx",FILE_PREFIX, FILE_PORTAL, getTimeStamp(FILE_TS_PATTERN));
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern as a string
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }
}