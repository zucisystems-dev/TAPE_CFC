package framework.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBQueryExecutor {

    private DBQueryExecutor(){
        throw new UnsupportedOperationException("DB Query Executor class — do not instantiate.");
    }

    public static void fetchData(String query) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                // EBased on our need the below script has to be changed
                String username = rs.getString("username");
                System.out.println("Username: " + username);
            }

        } catch (SQLException e) {
            System.err.println("Query execution failed: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            DBConnectionManager.closeConnection(conn);
        }
    }
}

