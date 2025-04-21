package services.Medication;

public class Medication {

    private int id;
    private int userId;
    private String nom;
    private String posologie;
    private String frequence;
    private String heure;
    private String remarque;

    private String photoPath; // Nouveau champ pour stocker le chemin de l'image


    // Constructeur sans ID (pour ajout)
    public Medication(int userId, String nom, String posologie, String frequence, String heure, String remarque ,  String photoPath) {
        this.userId = userId;
        this.nom = nom;
        this.posologie = posologie;
        this.frequence = frequence;
        this.heure = heure;
        this.remarque = remarque;
        this.photoPath = photoPath;

    }

    // Constructeur avec ID (pour modification)
    public Medication(int id, int userId, String nom, String posologie, String frequence, String heure, String remarque,  String photoPath) {
        this.id = id;
        this.userId = userId;
        this.nom = nom;
        this.posologie = posologie;
        this.frequence = frequence;
        this.heure = heure;
        this.remarque = remarque;
        this.photoPath = photoPath;

    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPosologie() { return posologie; }
    public void setPosologie(String posologie) { this.posologie = posologie; }

    public String getFrequence() { return frequence; }
    public void setFrequence(String frequence) { this.frequence = frequence; }

    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}


