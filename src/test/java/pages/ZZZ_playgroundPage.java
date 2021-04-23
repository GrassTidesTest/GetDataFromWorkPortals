package pages;

import base.WebDriverSingleton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

public class ZZZ_playgroundPage {
    private WebDriver driver;

    private static final String FILE_TS_PATTERN = "ddMMyy";
    private static final String FILE_PREFIX = "data";
    private static final String FILE_PORTAL = "jobscz";

    public ZZZ_playgroundPage() {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void copyFileToFolder() throws IOException {
        final String fileName = "data.xlsx";
        final String path = "src/test/resources/test_folder/";
        final String targetPath = "src/test/resources/test_folder/archive/";

        File sourceFile = new File(path + fileName);
        File newFile = new File(targetPath + getFileName());

        Files.copy(sourceFile.toPath(), newFile.toPath());
    }

    public void replaceFileWithTemplate() throws IOException {
        final String fileName = "data_empty.xlsx";
        final String newFileName = "data.xlsx";
        final String path = "src/test/resources/test_folder/";

        File sourceFile = new File(path + fileName); // data_empty.xlsx
        File newFile = new File(path + newFileName); // data.xlsx

        Files.deleteIfExists(newFile.toPath());
        Files.copy(sourceFile.toPath(), newFile.toPath());
    }

    private String getTimeStamp(String pattern) {
        // return time stamp based on a pattern as a string
        return new SimpleDateFormat(pattern).format(new java.util.Date());
    }

    private String getFileName() {
        // data_jobscz_220421.xlsx
        return String.format("%s_%s_%s.xlsx", FILE_PREFIX, FILE_PORTAL, getTimeStamp(FILE_TS_PATTERN));
    }

    public void copyToRemoteDriver() throws IOException {
        final String sourcePath = "src/test/resources/archive/";
        final String targetPath = "C:/DEVPACK_Synology/HR Shared Folder/Work Portal Crawler data/";
        final String fileName = getFileName();

        File sourceFile = new File(sourcePath + fileName);
        File newFile = new File(targetPath + fileName);

        Files.copy(sourceFile.toPath(), newFile.toPath());
    }
}
