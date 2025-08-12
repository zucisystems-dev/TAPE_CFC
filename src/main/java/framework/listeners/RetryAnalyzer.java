package framework.listeners;

import framework.config.PropertyReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int maxRetry = Integer.parseInt(PropertyReader.readProperty("retryCount"));

    @Override
    public boolean retry(ITestResult result) {
        if (count < maxRetry) {
            count++;
            return true;
        }
        return false;
    }
}

