//THEY SAY
package service;

import models.User;
import utils.AuthUtils;
import utils.MaConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserServiceImpl implements UserService <User> {

    @Override
    public void add(User user) {
        String req = "INSERT INTO `user`( `nom`, `prenom`, `email`," +
                " `motDePasse`, `dateNaissance`, `numTelephone`, `role`, " +
                "`statut` ,banStatus , registration_da) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, AuthUtils.hashPassword(user.getMotdepasse()));
            //ps.setString(4, user.getMotdepasse());
            ps.setString(6, user.getNumerotelephone());
            ps.setDate(5, java.sql.Date.valueOf(user.getDatedenaissance())); // Assuming it's LocalDate
            ps.setString(7, user.getStatut());
            ps.setString(8, user.getRole());
            ps.setString(9, user.getBanStatus());
            //ps.setDate(10, java.sql.Date.valueOf(user.getRegistrationDate()));
            ps.setDate(10, java.sql.Date.valueOf(LocalDate.now()));


            int lines = ps.executeUpdate();  // Assign to 'lines'

            // Return the number of rows affected

            System.out.println("User added successfully to the database." + lines);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM `user` WHERE id = ?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setInt(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User with ID " + id + " has been deleted successfully.");
            } else {
                System.out.println("No user found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }

    }

    @Override
    public void modify(User entity) {
        String req = "UPDATE `user` SET nom = ?, prenom = ?, email = ?, motDePasse = ?, " +
                "dateNaissance = ?, numTelephone = ?, role = ?, statut = ? , banStatus=? " +
                "WHERE id = ?";
        try {
            // Debugging: Check the entity being passed
            System.out.println("Attempting to modify user with ID: " + entity.getId());
            System.out.println("Nom: " + entity.getNom() + ", Prenom: " + entity.getPrenom() + ", Email: " + entity.getEmail());
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);

            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getPrenom());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getMotdepasse());
            ps.setDate(5, java.sql.Date.valueOf(entity.getDatedenaissance()));
            ps.setString(6, entity.getNumerotelephone());
            ps.setString(7, entity.getRole());
            ps.setString(8, entity.getStatut());
            ps.setString(9, entity.getBanStatus());

            //ps.setDate(11, java.sql.Date.valueOf(entity.getRegistrationDate()));
            ps.setInt(10, entity.getId()); // هنا نستخدم id في WHERE

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User with ID " + entity.getId() + " has been updated successfully.");
            } else {
                System.out.println("No user found with ID " + entity.getId() + ".");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }

    }

    @Override
    public List<User> display() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM `user`";

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                User user = new User();
                System.out.println("ResultSet ID: " + id);
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse"));
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setBanStatus(rs.getString("banStatus"));
                user.setRegistrationDate(rs.getDate("registration_da").toLocalDate());

                // إضافة المستخدم إلى القائمة
                users.add(user);
                System.out.println("User ID: " + id + ", Nom: " + user.getNom() + ", Prenom: " + user.getPrenom());
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
        }

        return users;
    }
    //return List.of();


    @Override
    public User search(int id) {
        String req = "SELECT * FROM user WHERE id = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse"));
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setBanStatus(rs.getString("banStatus"));
                user.setRegistrationDate(rs.getDate("registration_da").toLocalDate());

            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
        }

        return user;
        //return null;
    }

    public User searchByEmailAndPassword(String email, String password) {
        String req = "SELECT * FROM user WHERE email = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("motDePasse");

                if (AuthUtils.checkPassword(password, hashedPassword)) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setMotdepasse(hashedPassword);
                    user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                    user.setNumerotelephone(rs.getString("numTelephone"));
                    user.setRole(rs.getString("role"));
                    user.setStatut(rs.getString("statut"));
                    user.setBanStatus(rs.getString("banStatus"));
                    user.setRegistrationDate(rs.getDate("registration_da").toLocalDate());

                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'utilisateur par email et mot de passe : " + e.getMessage());
        }

        return user;
    }
    /*public User searchByEmailAndPassword(String email, String password) {
        String req = "SELECT * FROM user WHERE email = ? ";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            String hashedPassword = rs.getString("motDePasse");
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(hashedPassword);
                //user.setMotdepasse(rs.getString("motDePasse"));
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setFaceid(rs.getString("faceid"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'utilisateur par email et mot de passe : " + e.getMessage());
        }

        return user;
    }*/public boolean changerMotDePasse(int id, String ancienMdp, String nouveauMdp) {
        String checkQuery = "SELECT motDePasse FROM user WHERE id = ?";
        String updateQuery = "UPDATE user SET motDePasse = ? WHERE id = ?";

        try {
            PreparedStatement checkStmt = MaConnection.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentHashedPassword = rs.getString("motDePasse");

                // Use BCrypt to compare the old password entered with the hashed one
                if (AuthUtils.checkPassword(ancienMdp, currentHashedPassword)) {
                    // Hash the new password before saving
                    String hashedNewPassword = AuthUtils.hashPassword(nouveauMdp);

                    PreparedStatement updateStmt = MaConnection.getConnection().prepareStatement(updateQuery);
                    updateStmt.setString(1, hashedNewPassword);
                    updateStmt.setInt(2, id);
                    int rows = updateStmt.executeUpdate();
                    return rows > 0;
                } else {
                    System.out.println("Mot de passe actuel incorrect.");
                    return false;
                }
            } else {
                System.out.println("Utilisateur introuvable.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du changement de mot de passe : " + e.getMessage());
            return false;
        }
    }

    /*public boolean changerMotDePasse(int id, String ancienMdp, String nouveauMdp) {
        String checkQuery = "SELECT motDePasse FROM user WHERE id = ?";
        String updateQuery = "UPDATE user SET motDePasse = ? WHERE id = ?";

        try {
            nouveauMdp = AuthUtils.hashPassword(nouveauMdp);
            PreparedStatement checkStmt = MaConnection.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("motDePasse");
                if (AuthUtils.checkPassword(ancienMdp, currentPassword)) {
                //if (currentPassword.equals(ancienMdp)) {
                    PreparedStatement updateStmt = MaConnection.getConnection().prepareStatement(updateQuery);
                    updateStmt.setString(1, AuthUtils.hashPassword(nouveauMdp));
                    updateStmt.setInt(2, id);
                    int rows = updateStmt.executeUpdate();
                    return rows > 0;
                } else {
                    System.out.println("Mot de passe actuel incorrect.");
                    return false;
                }
            } else {
                System.out.println("Utilisateur introuvable.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du changement de mot de passe : " + e.getMessage());
            return false;
        }
    }*/
    public User searchByEmail(String email) {
        String req = "SELECT * FROM user WHERE email = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Debugging to check the values returned from the database
                System.out.println("ID from DB: " + rs.getInt("id"));
                System.out.println("Email from DB: " + rs.getString("email"));
                System.out.println("Role from DB: " + rs.getString("role"));

                // If the user is found in the database, map the result set to the User object
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse")); // Already hashed
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setBanStatus(rs.getString("banStatus"));
                user.setRegistrationDate(rs.getDate("registration_da").toLocalDate());

                // Debugging the user object after mapping
                System.out.println("Mapped User: " + user.getEmail() + ", Role: " + user.getRole());
            } else {
                System.out.println("No user found with email: " + email); // Debugging line if no user is found
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for user by email: " + e.getMessage());
        }

        return user;
    }
   /* public User searchByEmail(String email) {
        String req = "SELECT * FROM user WHERE email = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // If the user is found in the database, map the result set to the User object
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse")); // Already hashed
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setFaceid(rs.getString("faceid"));

                System.out.println("User found: " + user.getEmail()); // Debugging line to confirm the user is found
            } else {
                System.out.println("No user found with email: " + email); // Debugging line if no user is found
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for user by email: " + e.getMessage());
        }

        return user;
    }*/
   /* public User searchByEmail(String email) {
        String req = "SELECT * FROM user WHERE email = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse")); // Already hashed
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setFaceid(rs.getString("faceid"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'utilisateur par email : " + e.getMessage());
        }

        return user;
    }*/

    public User searchByName(String name) {
        String req = "SELECT * FROM user WHERE nom = ?";
        User user = null;

        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotdepasse(rs.getString("motDePasse"));
                user.setDatedenaissance(rs.getDate("dateNaissance").toLocalDate());
                user.setNumerotelephone(rs.getString("numTelephone"));
                user.setRole(rs.getString("role"));
                user.setStatut(rs.getString("statut"));
                user.setBanStatus(rs.getString("banStatus"));
                user.setRegistrationDate(rs.getDate("registration_da").toLocalDate());

            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'utilisateur par nom : " + e.getMessage());
        }

        return user;
    }

}
