package models;

import java.time.LocalDate;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private String numerotelephone;
    private LocalDate datedenaissance;
    private String statut;
    private String role;
    private String banStatus;
    private LocalDate registrationDate;
    private boolean isVerified;

    // ─── Constructeurs ───────────────────────────────────────────────────────

    public User() {}

    public User(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public User(String nom, String prenom, String role, String motdepasse,
                String email, String statut, String banStatus, boolean isVerified) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.motdepasse = motdepasse;
        this.email = email;
        this.statut = statut;
        this.banStatus = banStatus;
        this.isVerified = isVerified;
    }

    public User(int id, String nom, String prenom, String email,
                String motdepasse, String numerotelephone,
                LocalDate datedenaissance, String statut, String role,
                String banStatus, boolean isVerified, LocalDate registrationDate) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motdepasse = motdepasse;
        this.numerotelephone = numerotelephone;
        this.datedenaissance = datedenaissance;
        this.statut = statut;
        this.role = role;
        this.banStatus = banStatus;
        this.isVerified = isVerified;
        this.registrationDate = registrationDate;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public boolean setNom(String nom) {
        if (nom == null || nom.isEmpty()) return false;
        this.nom = nom;
        return true;
    }

    public String getPrenom() { return prenom; }
    public boolean setPrenom(String prenom) {
        if (prenom == null || prenom.isEmpty()) return false;
        if (!prenom.matches("[\\p{L} '-]+")) return false;
        this.prenom = prenom;
        return true;
    }

    public String getEmail() { return email; }
    public boolean setEmail(String email) {
        if (email == null || !email.contains("@")) return false;
        this.email = email;
        return true;
    }

    public String getMotdepasse() { return motdepasse; }
    public boolean setMotdepasse(String motdepasse) {
        if (motdepasse == null) return false;
        this.motdepasse = motdepasse;
        return true;
    }

    public String getNumerotelephone() { return numerotelephone; }
    public boolean setNumerotelephone(String numerotelephone) {
        if (numerotelephone == null || !numerotelephone.matches("\\d{8}")) return false;
        this.numerotelephone = numerotelephone;
        return true;
    }

    public LocalDate getDatedenaissance() { return datedenaissance; }
    public boolean setDatedenaissance(LocalDate datedenaissance) {
        if (datedenaissance == null || datedenaissance.isAfter(LocalDate.now())) return false;
        this.datedenaissance = datedenaissance;
        return true;
    }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBanStatus() { return banStatus; }
    public void setBanStatus(String banStatus) { this.banStatus = banStatus; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }

    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', prenom='" + prenom +
               "', email='" + email + "', role='" + role + "', statut='" + statut +
               "', banStatus='" + banStatus + "'}";
    }
}
