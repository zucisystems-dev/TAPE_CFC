package tests.web;

import framework.config.PropertyReader;
import framework.utils.TestNGXMLGenerator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.xml.XmlSuite;

import java.util.List;

public class DryRun {

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com/");
        System.out.println(driver.getTitle());
    }

}
