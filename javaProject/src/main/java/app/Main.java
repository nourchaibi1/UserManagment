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
        
        service1Imp service = new service1Imp();
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





