package test;

import base.TestBase;
import base.WebDriverSingleton;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

//public class ZZZ_playground extends TestBase {
public class ZZZ_playground {
//    static int numberOfFailedTests;
//
//    @BeforeClass
//    public static void setUpClass() {
//        System.out.println("setup class \n");
//        numberOfFailedTests = 0;
//    }
//
//    @Before
//    public void setUp() {
//        System.out.println("setup \n");
//    }
//
//    @Test
//    public void testA() {
//        System.out.println("A");
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }
//
//    @Test
//    public void testB() {
//        System.out.println("B");
//        numberOfFailedTests++;
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }
//
//    @Test
//    public void testC() {
//        System.out.println("B");
//        System.out.println("No. failed tests: " + numberOfFailedTests + "\n");
//    }

    private final String timestampPattern = "ddMMyy";
    private final String filePrefix = "data_";
    private final String filePortal = "jobscz_";
    private final String fileExtension = ".xlsx";

    @Test
    public void test() {
        final String fileName = "data.xlsx";
        final String path = "src/test/resources/test_folder/";
        final String targetPath = "src/test/resources/test_folder/archive/";

        File sourceFile = new File(path + fileName);
        File newFile = new File(targetPath + getFileName());

        try {
            Files.copy(sourceFile.toPath(), newFile.toPath());
        } catch (Exception e) {
            System.out.println("Error copying file to archive");
        }
    }

    @Test
    public void test2() {
        final String fileName = "data_empty.xlsx";
        final String newFileName = "data.xlsx";
        final String path = "src/test/resources/test_folder/";

        File sourceFile = new File(path + fileName); // data_empty.xlsx
        File newFile = new File(path + newFileName); // data.xlsx

        try {
            Files.deleteIfExists(newFile.toPath());
            Files.copy(sourceFile.toPath(), newFile.toPath());
        } catch (Exception e) {
            System.out.println("Error rewriting data with empty_data");
        }
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern as a string
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getFileName() {
        return filePrefix + filePortal + getTimeStamp(timestampPattern) + fileExtension;
    }
}
