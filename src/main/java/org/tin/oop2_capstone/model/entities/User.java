package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int uid;
    private String fullname;
    private String email;
    private String username;
    private double weightKg;
    private double heightCm;
    private String password;
    private int age;
    private boolean isMale;
    private String activityLevel;

    public User() {}

    public User(int uid, String fullname, String email, String username, double weightKg, double heightCm, String password, int age, boolean isMale, String activityLevel) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.password = password;
        this.age = age;
        this.isMale = isMale;
        this.activityLevel = activityLevel;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAge() {
        return age;
    }

    public String getFullname() { return fullname; }
    public String getEmail()    { return email; }
    public String getUsername() { return username; }
    public double getWeightKg() { return weightKg; }
    public double getHeightCm()  { return heightCm; }
    public String getPassword() { return password; }

    public void setEmail(String email)       { this.email = email; }
    public void setFullname(String fullname) { this.fullname = fullname;}
    public void setUsername(String username) { this.username = username; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public void setHeightCm(double heightCm)   { this.heightCm = heightCm; }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public void setPassword(String password) {
        // set appropriate hashing algo here
        // For now wala lang sa
        // TODO:
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
