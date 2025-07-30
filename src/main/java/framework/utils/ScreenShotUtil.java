package framework.utils;

import framework.base.DriverFactory;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;

public class ScreenShotUtil extends ExtentReport {

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
            if(status.getName().equalsIgnoreCase("Pass")){
                ExtentReport.getTest().pass(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }
            else if(status.getName().equalsIgnoreCase("Fail")){
                ExtentReport.getTest().fail(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }
            else{
                ExtentReport.getTest().info(message, MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
