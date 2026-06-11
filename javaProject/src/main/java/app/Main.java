//they say
package models;


import service.service1Imp;
import utils.AuthUtils;
import utils.MaConnection;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        File file = new File("C:\\jv\\javafx-sdk-17.0.14\\jdbc1\\resources\\admin_messages.txt");
        System.out.println(file.getAbsolutePath());
        // Load native OpenCV library
      /*  try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("OpenCV loaded successfully: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV library: " + e.getMessage());
            System.err.println("Make sure OpenCV is properly installed and in the path");
            System.exit(1);
        }
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.VERSION);
        // Open default camera (index 0)
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not detected");
            return;
        }

        Mat frame = new Mat();
        while (true) {
            if (camera.read(frame)) {
                HighGui.imshow("Camera Feed", frame);
                if (HighGui.waitKey(30) == 27) { // Exit on ESC key
                    break;
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
*/


        MaConnection maConnection = new MaConnection();
        // Get the singleton instance of the connection
        Connection connection = MaConnection.getConnection();
        // Check if the connection was successful
        if (connection != null) {
            // If the connection is successful, print this message
            System.out.println("Connected to the database successfully.");
        } else {
            // If the connection failed, print this message
            System.out.println("Unable to connect to the database.");
        }
        // User user = new User();
        /*
user.setNom("dd");
user.setPrenom("pre");
user.setEmail("dd");
user.setMotdepasse("password");
user.setNumerotelephone("123456789");
user.setDatedenaissance(LocalDate.of(1990, 5, 15));
user.setStatut("Active");
user.setRole("Admin");
user.setFaceid("faceid123");*/
        service1Imp service = new service1Imp();
/*


//service.add(user);
//service.delete(8);
       //tester update
       /* User updatedUser = new User();

        updatedUser.setId(8);
        updatedUser.setNom("Ahmed");
        updatedUser.setPrenom("Ben Ali");
        updatedUser.setEmail("ahmed.benali@example.com");
        updatedUser.setMotdepasse("newpassword123");
        updatedUser.setDatedenaissance(LocalDate.of(1999, 5, 10));
        updatedUser.setNumerotelephone("12345678");
        updatedUser.setRole("admin");
        updatedUser.setStatut("actif");
        updatedUser.setFaceid("newFaceID123");

        service.modify(updatedUser);

        List<User> users = service.display();

        // طباعة بيانات المستخدمين
        for (User u : users) {
            System.out.println(u.getId() + ": " + u.getNom() + " " + u.getPrenom());
        }

        User searchedUser = service.search(10); // استبدل 11 بأي id تريده
        if (searchedUser != null) {
            System.out.println("Nom: " + searchedUser.getNom() + ", Prénom: " + searchedUser.getPrenom());
        } else {
            System.out.println("Utilisateur introuvable.");
        }
        //tester le nouv mot de passe
        Scanner scanner = new Scanner(System.in);

        // Ask for user ID
        System.out.print("Enter your user ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer

        // Ask for old password
        System.out.print("Enter your current password: ");
        String oldPassword = scanner.nextLine();

        // Ask for new password
        System.out.print("Enter the new password: ");
        String newPassword = scanner.nextLine();

        // Change password after checking the old one
        boolean success = service.changerMotDePasse(userId, oldPassword, newPassword);

        if (success) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password.");
        }

        scanner.close(); */
      /*  List<User> users = service.display();
        User userToUpdate = new User();
        userToUpdate.setId(30);  // Set the ID of the user you want to modify (make sure this ID exists in your database)
        userToUpdate.setNom("UpdatedNom");
        userToUpdate.setPrenom("UpdatedPrenom");
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setMotdepasse("newpassword123");  // Ensure you have a hashed password if needed
        userToUpdate.setDatedenaissance(LocalDate.of(1990, 5, 20)); // Example date of birth
        userToUpdate.setNumerotelephone("12345678");  // Ensure this is a valid phone number
        userToUpdate.setRole("Admin");  // Example role
        userToUpdate.setStatut("Active");  // Example status
        userToUpdate.setFaceid("newFaceID");  // Example FaceID


        service1Imp userService = new service1Imp();
        // Call the modify method to update the user in the database
        userService.modify(userToUpdate);
        Scanner scanner = new Scanner(System.in); */
   /* // Ask for user ID
        System.out.print("Enter your user ID: ");
    int userId = Integer.parseInt(scanner.nextLine());

    // Ask for old password
        System.out.print("Enter your current password: ");
    String oldPassword = scanner.nextLine();

    // Ask for new password
        System.out.print("Enter the new password: ");
    String newPassword = scanner.nextLine();

    // Call the service
    service1Imp service1 = new service1Imp();
    /*boolean success = service.changerMotDePasse(userId, oldPassword, newPassword);

        if (success) {
        System.out.println("✅ Password changed successfully.");
    } else {
        System.out.println("❌ Failed to change password.");
    }*/
       /* System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        User user1 = service.searchByEmail(email);
        // Check if a user was found with the provided email
        if (user1 != null) {
            System.out.println("User found:");
            System.out.println("User ID: " + user1.getId());
            System.out.println("User Email: " + user1.getEmail());
            System.out.println("User Role: " + user1.getRole());
        } else {
            System.out.println("No account found for this email.");
        }

        // Close the scanner
        scanner.close();
    }*/
        Scanner scanner = new Scanner(System.in);

// Ask for the ID of the user to modify
        System.out.println("Enter the user ID to modify: ");
        int userId = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

// Ask for new user details
        System.out.println("Enter the new name: ");
        String newNom = scanner.nextLine();
        System.out.println("Enter the new surname: ");
        String newPrenom = scanner.nextLine();
        System.out.println("Enter the new email: ");
        String newEmail = scanner.nextLine();
        System.out.println("Enter the new password: ");
        String newPassword = scanner.nextLine();
        System.out.println("Enter the new phone number: ");
        String newPhone = scanner.nextLine();
        System.out.println("Enter the new role: ");
        String newRole = scanner.nextLine();
        System.out.println("Enter the new status: ");
        String newStatus = scanner.nextLine();
        System.out.println("Enter the new birthdate (YYYY-MM-DD) or leave blank to skip: ");
        String birthdateStr = scanner.nextLine();
        System.out.println("face id");
        String newfaceid = scanner.nextLine();
// Convert the input birthdate string to LocalDate if provided,
        LocalDate newBirthDate = null;
        if (!birthdateStr.isEmpty()) {
            try {
                newBirthDate = LocalDate.parse(birthdateStr);
            } catch (Exception e) {
                System.out.println("Invalid birthdate format. Please enter in YYYY-MM-DD format.");
            }
        }

//  the updated user object
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setNom(newNom);
        userToUpdate.setPrenom(newPrenom);
        userToUpdate.setEmail(newEmail);
        userToUpdate.setMotdepasse(AuthUtils.hashPassword(newPassword)); // Handle password hashing if needed
        userToUpdate.setNumerotelephone(newPhone);
        userToUpdate.setRole(newRole);
        userToUpdate.setStatut(newStatus);
        userToUpdate.setDatedenaissance(newBirthDate);
        //userToUpdate.setFaceid(newfaceid);
// Call the modify method to update the user in the database
        service1Imp userService = new service1Imp();
        userService.modify(userToUpdate);

// Confirm the update
        System.out.println("User updated successfully.");






    }


}





