package framework.utils;

import framework.config.PropertyReader;

public class AppConfigUtil {
    public static String getAppName() {
        String appName = System.getProperty("appName");
        if (appName != null && !appName.trim().isEmpty()) {
            System.out.println("[INFO] App name found via system property: " + appName);
            return appName;
        }
        appName = PropertyReader.readProperty("appName");
        if (appName != null && !appName.trim().isEmpty()) {
            System.out.println("[INFO] App name loaded from config.properties: " + appName);
            return appName;
        } else {
            throw new RuntimeException("App name is missing in config.properties.");
        }

    }

}

