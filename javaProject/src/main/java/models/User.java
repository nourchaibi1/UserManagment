package models;
import java.time.LocalDate;
import java.util.Date;

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
    //private String faceid;

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getDatedenaissance() {
        return datedenaissance;
    }

    public boolean setDatedenaissance(LocalDate datedenaissance) {
        if (datedenaissance == null) {
            System.out.println("Erreur : la date de naissance ne peut pas être vide.");
            return false;
        }

        LocalDate aujourdHui = LocalDate.now();
        if (datedenaissance.isAfter(aujourdHui)) {
            System.out.println("Erreur : la date de naissance ne peut pas être dans le futur.");
            return false;
        }

        this.datedenaissance = datedenaissance;
        return true;
    }
    /*public void setDatedenaissance(LocalDate datedenaissance) {
        this.datedenaissance = datedenaissance;
    }*/

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String email) {
        if (email == null || !email.contains("@")) {
            System.out.println("Email invalide");
            return false;
        }

        this.email = email;
        return true;
    }
   /* public void setEmail(String email) {
        this.email = email;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public String getFaceid() {
        return faceid;
    }*/

    /*public void setFaceid(String faceid) {
        this.faceid = faceid;
    }*/

    public String getMotdepasse() {
        return motdepasse;
    }

    public boolean setMotdepasse(String motdepasse) {
        if (motdepasse == null) {
            System.out.println("Mot de passe invalide");
            return false;
        }

        this.motdepasse = motdepasse;
        return true;
    }

    /*public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }
*/
    public String getNom() {
        return nom;
    }

    public boolean setNom(String nom) {
        if (nom == null || nom.isEmpty()) {
            System.out.println("remplir le nom");
            return false;
        }

       /* if (!nom.matches("[a-zA-Z]+")) {
            System.out.println("Nom invalide : il faut contenir uniquement des lettres.");
            return false;
        }*/

        this.nom = nom;
        return true;
    }
   /* public void setNom(String nom) {
        this.nom = nom;
    }*/

    public String getNumerotelephone() {
        return numerotelephone;
    }

    public boolean setNumerotelephone(String numerotelephone) {
        if (numerotelephone == null || numerotelephone.isEmpty()) {
            System.out.println("entrez le numero");
            return false;
        }

        if (!numerotelephone.matches("\\d{8}")) {
            System.out.println("Numéro de téléphone invalide : il faut contenir exactement 8 chiffres.");
            return false;
        }

        this.numerotelephone = numerotelephone;
        return true;
    }
    /*public void setNumerotelephone(String numerotelephone) {
        this.numerotelephone = numerotelephone;
    }*/

    public String getPrenom() {
        return prenom;
    }

    public boolean setPrenom(String prenom) {
        if (prenom == null || prenom.isEmpty()) {
            System.out.println("entrer le prenom");
            return false;
        }

        if (!prenom.matches("[\\p{L} '-]+")) {
            System.out.println("Prénom invalide : il faut contenir uniquement des lettres.");
            return false;
        }

        this.prenom = prenom;
        return true;
    }

   /* public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
*/


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "user{" +
                "datedenaissance='" + datedenaissance + '\'' +
                ", id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", motdepasse='" + motdepasse + '\'' +
                ", numerotelephone='" + numerotelephone + '\'' +
                ", statut='" + statut + '\'' +
                ", role='" + role + '\'' + banStatus +
                '\'' +
                '}';
    }

    public User() {

    }

    public User(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public User(int id, String nom, String prenom, String email,
                String motdepasse, String numerotelephone,
                LocalDate datedenaissance, String statut, String role, String banStatus, boolean isVerified,LocalDate registrationDate
    ) {
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
    // Ban/Unban status

    // Getter and setter for banStatus
    public String getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }

    private boolean isVerified;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public User(String nom, String prenom, String role, String motdepasse,
                String email, String image, Date dAt,
                String statut, String banStatus, boolean isVerified) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.motdepasse = motdepasse;
        this.email = email;
        this.statut = statut;
        this.banStatus = banStatus;
        this.isVerified = isVerified;
    }
    public User(String name, LocalDate registrationDate) {
        this.nom = name;
        this.registrationDate = registrationDate;
    }
}