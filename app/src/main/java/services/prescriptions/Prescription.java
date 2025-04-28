package services.prescriptions;

import android.graphics.Bitmap;

public class Prescription {
    private long id;
    private String title;
    private String doctorName;
    private String date;
    private String note;
    private String imagePath;

    // Constructeur vide
    public Prescription() {
    }

    // Constructeur avec tous les champs sauf l'ID
    public Prescription(String title, String doctorName, String date, String note, String imagePath) {
        this.title = title;
        this.doctorName = doctorName;
        this.date = date;
        this.note = note;
        this.imagePath = imagePath;
    }

    // Constructeur avec tous les champs
    public Prescription(long id, String title, String doctorName, String date, String note, String imagePath) {
        this.id = id;
        this.title = title;
        this.doctorName = doctorName;
        this.date = date;
        this.note = note;
        this.imagePath = imagePath;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}