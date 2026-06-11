package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnection {
    private static Connection connection = null;
    private final static String username="root";
    private final static String password="";
    private final static String url="jdbc:mysql://localhost:3306/project";


   public MaConnection() {
    }
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                System.err.println("SQLException occurred: " + e.getMessage());
            }
        }
        return connection;
    }
    }

