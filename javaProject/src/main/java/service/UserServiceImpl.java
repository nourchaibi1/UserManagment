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

public class UserServiceImpl implements UserService<User> {

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Mappe un ResultSet vers un objet User. */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
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
        return user;
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @Override
    public void add(User user) {
        String req = "INSERT INTO `user` (nom, prenom, email, motDePasse, dateNaissance, " +
                     "numTelephone, role, statut, banStatus, registration_da) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, AuthUtils.hashPassword(user.getMotdepasse()));
            ps.setDate(5, java.sql.Date.valueOf(user.getDatedenaissance()));
            ps.setString(6, user.getNumerotelephone());
            ps.setString(7, user.getRole());
            ps.setString(8, user.getStatut());
            ps.setString(9, user.getBanStatus());
            ps.setDate(10, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM `user` WHERE id = ?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Utilisateur supprimé." : "Aucun utilisateur trouvé avec ID " + id);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modify(User user) {
        String req = "UPDATE `user` SET nom=?, prenom=?, email=?, motDePasse=?, " +
                     "dateNaissance=?, numTelephone=?, role=?, statut=?, banStatus=? WHERE id=?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getMotdepasse());
            ps.setDate(5, java.sql.Date.valueOf(user.getDatedenaissance()));
            ps.setString(6, user.getNumerotelephone());
            ps.setString(7, user.getRole());
            ps.setString(8, user.getStatut());
            ps.setString(9, user.getBanStatus());
            ps.setInt(10, user.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Utilisateur modifié." : "Aucun utilisateur trouvé avec ID " + user.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
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
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage : " + e.getMessage());
        }
        return users;
    }

    @Override
    public User search(int id) {
        String req = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return null;
    }

    // ─── Méthodes supplémentaires ─────────────────────────────────────────────

    public User searchByEmail(String email) {
        String req = "SELECT * FROM user WHERE email = ?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            System.out.println("Erreur searchByEmail : " + e.getMessage());
        }
        return null;
    }

    public User searchByName(String nom) {
        String req = "SELECT * FROM user WHERE nom = ?";
        try {
            PreparedStatement ps = MaConnection.getConnection().prepareStatement(req);
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            System.out.println("Erreur searchByName : " + e.getMessage());
        }
        return null;
    }

    public User searchByEmailAndPassword(String email, String password) {
        User user = searchByEmail(email);
        if (user != null && AuthUtils.checkPassword(password, user.getMotdepasse())) {
            return user;
        }
        return null;
    }

    public boolean changerMotDePasse(int id, String ancienMdp, String nouveauMdp) {
        String checkQuery = "SELECT motDePasse FROM user WHERE id = ?";
        String updateQuery = "UPDATE user SET motDePasse = ? WHERE id = ?";
        try {
            PreparedStatement checkStmt = MaConnection.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && AuthUtils.checkPassword(ancienMdp, rs.getString("motDePasse"))) {
                PreparedStatement updateStmt = MaConnection.getConnection().prepareStatement(updateQuery);
                updateStmt.setString(1, AuthUtils.hashPassword(nouveauMdp));
                updateStmt.setInt(2, id);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur changerMotDePasse : " + e.getMessage());
        }
        return false;
    }
}
