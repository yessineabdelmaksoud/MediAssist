package account;

public class UserProfile {
    private int id;
    private int userId;  // ID référençant l'utilisateur dans la table users
    private String username;  // Gardé pour compatibilité
    private int age;
    private String gender;
    private String bloodType;
    private float weight;
    private float height;
    private String phone;
    private String address;
    private String medicalConditions;
    private String profileImagePath;

    public UserProfile() {
        // Empty constructor
    }

    // Constructeur complet
    public UserProfile(int id, int userId, String username, int age, String gender,
                       String bloodType, float weight, float height,
                       String phone, String address, String medicalConditions,
                       String profileImagePath) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.bloodType = bloodType;
        this.weight = weight;
        this.height = height;
        this.phone = phone;
        this.address = address;
        this.medicalConditions = medicalConditions;
        this.profileImagePath = profileImagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}