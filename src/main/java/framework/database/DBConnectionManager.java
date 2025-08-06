package framework.database;

import framework.config.PropertyReader;
import framework.utils.AppConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionManager {

    private DBConnectionManager(){
        throw new UnsupportedOperationException("DB Connection Manager class — do not instantiate.");
    }

    public static Connection getConnection() {
        Connection connection = null;
        String appName = AppConfigUtil.getAppName();
        String[] suiteArray = appName.split("_");
        String appModule = suiteArray[0];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(PropertyReader.readDynamicProperty(appModule,"dataBase_URL"),
                    PropertyReader.readDynamicProperty(appModule,"dataBase_UserName"),
                    PropertyReader.readDynamicProperty(appModule,"dataBase_Password"));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to DB: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.err.println("Error closing DB connection: " + e.getMessage());
        }
    }

}
