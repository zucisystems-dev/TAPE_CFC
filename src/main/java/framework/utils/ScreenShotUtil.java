package framework.utils;

import com.aventstack.extentreports.ExtentTest;
import framework.base.DriverFactory;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;

import static framework.utils.ExtentReport.today;

public class ScreenShotUtil {

    public static void captureScreenshot(Status status, String message) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String screenshotPath = "Reports\\" + today + "\\Screenshots\\" + "ECS_" + timestamp + ".png";
        String relativePath = "Screenshots\\" + "ECS_" + timestamp + ".png";

        File screenshotFile = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.FILE);
        File destination = new File(screenshotPath);

        try {
            destination.getParentFile().mkdirs();
            Files.copy(screenshotFile.toPath(), destination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ExtentTest test = ExtentReport.getDetailedTest();
            if(status.getName().equalsIgnoreCase("Pass")){
                test.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }
            else if(status.getName().equalsIgnoreCase("Fail")) {
                if (test != null) {
                    test.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
                } else {
                    System.err.println("ExtentTest is null while capturing and logging screenshot.");
                }
            }
            else{
                test.info(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
