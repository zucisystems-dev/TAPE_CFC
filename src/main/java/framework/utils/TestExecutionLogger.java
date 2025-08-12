package framework.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.google.gson.GsonBuilder;

public class TestExecutionLogger {

    private final String testName;
    private final String browser;
    private final String status;
    private final String failureReason;
    private final String environment;
    private final String testExecutionTime;
    private final String testStartTime;
    private final String testEndTime;

    private TestExecutionLogger(TestResultData testResultData){
        testName = testResultData.testName;
        browser = testResultData.browser;
        status = testResultData.status;
        failureReason = testResultData.failureReason;
        environment = testResultData.environment;
        testExecutionTime = testResultData.testExecutionTime;
        testStartTime = testResultData.testStartTime;
        testEndTime = testResultData.testEndTime;
    }

    public static class TestResultData {

        private String testName;
        private String browser;
        private String status;
        private String failureReason;
        private String environment;
        private String testExecutionTime;
        private String testStartTime;
        private String testEndTime;

        public static TestResultData builder() {
            return new TestResultData();
        }

        public TestResultData setTestName(String testName) {
            this.testName = testName;
            return this;
        }

        public TestResultData setBrowser(String browser) {
            this.browser = browser;
            return this;
        }

        public TestResultData setStatus(String status) {
            this.status = status;
            return this;
        }

        public TestResultData setFailureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public TestResultData setEnvironment(String environment) {
            this.environment = environment;
            return this;
        }

        public TestResultData setTestStartTime(String testStartTime) {
            this.testStartTime = testStartTime;
            return this;
        }

        public TestResultData setTestEndTime(String testEndTime) {
            this.testEndTime = testEndTime;
            return this;
        }

        public TestResultData setTestExecutionTime(String testExecutionTime) {
            this.testExecutionTime = testExecutionTime;
            return this;
        }

        public TestExecutionLogger build(){
            return new TestExecutionLogger(this);
        }

    }

    private static final List<TestExecutionLogger> results = Collections.synchronizedList(new ArrayList<>());

    public static void add(TestResultData testResultData) {
        results.add(testResultData.build());
    }

    public static List<TestExecutionLogger> getResults() {
        return results;
    }

    public static void writeToJsonFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            new GsonBuilder().setPrettyPrinting().serializeNulls().create()
                    .toJson(results, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTestName() {
        return testName;
    }

    public String getBrowser() {
        return browser;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getTestExecutionTime() {
        return testExecutionTime;
    }

    public String getTestStartTime() {
        return testStartTime;
    }

    public String getTestEndTime() {
        return testEndTime;
    }

}

