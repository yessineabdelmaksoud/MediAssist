package services.Appointement;

public class Appointment {
    private int id;
    private String title;
    private String doctor;
    private String date;
    private String time;
    private String place;
    private boolean autoDelete;

    public Appointment() {
    }

    public Appointment(String title, String doctor, String date, String time, String place, boolean autoDelete) {
        this.title = title;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.place = place;
        this.autoDelete = autoDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", doctor='" + doctor + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", autoDelete=" + autoDelete +
                '}';
    }
}