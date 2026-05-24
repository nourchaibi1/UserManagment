package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnection {
    // Step 1: Declare a static instance of the class
    private static Connection connection = null;
    private final static String username="root";
    private final static String password="";
    private final static String url="jdbc:mysql://localhost:3306/project";


    // Step 2: Private constructor to prevent instantiation
   public MaConnection() {
    }
    // Step 3: Public method to get the instance of the connection
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Attempt to establish the connection
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                // Handle exception if connection fails
                System.err.println("SQLException occurred: " + e.getMessage());
            }
        }
        return connection;
    }
    }

