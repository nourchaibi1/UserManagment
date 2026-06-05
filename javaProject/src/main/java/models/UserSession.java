package models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import models.User;
public class UserSession {
    private static UserSession instance = null;

    // Core user properties
    private int userId;
    private String userlastname;
    private String username;
    private String userRole;
    private LocalDateTime loginTime;
    private boolean loggedIn = false;

    // Additional flexible storage for any other user data
    private Map<String, Object> userData = new HashMap<>();

    // Private constructor to prevent instantiation
    private UserSession() {
        // Initialize if needed
    }

    /**
     * Gets the single instance of UserSession

     * @return The UserSession instance
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Initializes a new user session after successful login.
     * @param userId The user's unique ID
     * @param username The user's username
     * @param userRole The user's role (e.g., "ADMIN", "USER", etc.)
     */
    public void startSession(int userId,String userlastname,String username, String userRole) {
        this.userId = userId;
        this.userlastname = userlastname;
        this.username = username;
        this.userRole = userRole;
        this.loginTime = LocalDateTime.now();
        this.loggedIn = true;

        // Clear any previous user data
        this.userData.clear();
    }

    /**
     * Ends the current user session (logout).
     */
    public void endSession() {
        this.userId = 0;
        this.userlastname=null;
        this.username = null;
        this.userRole = null;
        this.loginTime = null;
        this.loggedIn = false;
        this.userData.clear();
    }

    /**
     * Stores additional user data in the session.
     * @param key The key for the data
     * @param value The value to store
     */
    public void setUserData(String key, Object value) {
        userData.put(key, value);
    }

    /**
     * Retrieves additional user data from the session.
     * @param key The key for the data
     * @return The stored value, or null if not found
     */
    public Object getUserData(String key) {
        return userData.get(key);
    }

    /**
     * Removes a user data item from the session.
     * @param key The key for the data to remove
     */
    public void removeUserData(String key) {
        userData.remove(key);
    }

    // Getters and setters for core properties
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
public String getUserlastname() {
        return userlastname;
    }
    public String getUserRole() {
        return userRole;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public String getFormattedLoginTime() {
        if (loginTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return loginTime.format(formatter);  // Format the login time
        }
        return "N/A";  // If no login time is available
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Checks if the current user has a specific role.
     * @param role The role to check
     * @return true if the user has the specified role
     */
    public boolean hasRole(String role) {
        return userRole != null && userRole.equals(role);
    }


      /**
      * Updates the user information in the current session.
     *  * @param updatedUser The updated User object
     *  */

    public void updateUserInfo(models.User updatedUser) {
        // Only update if there's an active session
        if (isLoggedIn()) {
            // Store the user object directly in userData for easy access
            setUserData("userObject", updatedUser);

            // Update any session fields that might be affected
            setUserData("email", updatedUser.getEmail());

            // If your User class has these methods:
            // Update appropriate session data
            if (updatedUser.getId() > 0) {
                this.userId = updatedUser.getId();
            }
            if (updatedUser.getNom() != null) {
                this.userlastname = updatedUser.getNom();
            }
            if (updatedUser.getPrenom() != null) {
                this.username = updatedUser.getPrenom();
            }
            if (updatedUser.getRole() != null) {
                this.userRole = updatedUser.getRole();
            }

            // Additional logging for debugging
            System.out.println("User session updated with new credentials");
        } else {
            System.out.println("Cannot update user info: No active session");
        }
    }


    private User userObject;  // add this field

    public void setUserObject(User user) {
        this.userObject = user;
    }

    public User getUserObject() {
        return this.userObject;
    }
    public void startSession2(int userId, String userlastname, String username, String userRole) {
        this.userId = userId;
        this.userlastname = userlastname;
        this.username = username;
        this.userRole = userRole;
        this.loginTime = LocalDateTime.now();
        this.loggedIn = true;
        this.userData.clear();
        this.userObject = null;  // clear any previous full user object
    }
    public void endSession2() {
        this.userId = 0;
        this.userlastname = null;
        this.username = null;
        this.userRole = null;
        this.loginTime = null;
        this.loggedIn = false;
        this.userData.clear();
        this.userObject = null;  // clear full user object
    }

}
