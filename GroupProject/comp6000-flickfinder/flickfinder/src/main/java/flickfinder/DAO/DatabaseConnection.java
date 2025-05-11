package flickfinder.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE = "flickfinder_userdata";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE + "?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "aboubacar15";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}