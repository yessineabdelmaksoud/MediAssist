package services.Medicaments;

import java.util.ArrayList;
import java.util.List;

public class Medicament {
    private long id;
    private String nom;
    private String posologie;
    private String frequence;
    private String remarque;
    private String imagePath;
    private List<String> heures;
    private List<String> jours;

    public Medicament() {
        this.heures = new ArrayList<>();
        this.jours = new ArrayList<>();
    }

    public Medicament(long id, String nom, String posologie, String frequence, String remarque, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.posologie = posologie;
        this.frequence = frequence;
        this.remarque = remarque;
        this.imagePath = imagePath;
        this.heures = new ArrayList<>();
        this.jours = new ArrayList<>();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public List<String> getHeures() {
        return heures;
    }

    public void setHeures(List<String> heures) {
        this.heures = heures;
    }

    public void addHeure(String heure) {
        this.heures.add(heure);
    }

    public List<String> getJours() {
        return jours;
    }

    public void setJours(List<String> jours) {
        this.jours = jours;
    }

    public void addJour(String jour) {
        this.jours.add(jour);
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getJoursAsString() {
        StringBuilder joursStr = new StringBuilder();
        for (int i = 0; i < jours.size(); i++) {
            joursStr.append(jours.get(i));
            if (i < jours.size() - 1) {
                joursStr.append(", ");
            }
        }
        return joursStr.toString();
    }

    public String getHeuresAsString() {
        StringBuilder heuresStr = new StringBuilder();
        for (int i = 0; i < heures.size(); i++) {
            heuresStr.append(heures.get(i));
            if (i < heures.size() - 1) {
                heuresStr.append(", ");
            }
        }
        return heuresStr.toString();
    }
}