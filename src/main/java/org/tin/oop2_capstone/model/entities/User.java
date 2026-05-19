package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int uid;
    private String fullname;
    private String email;
    private String username;
    private double weightKg;
    private double heightCm;
    private String passwordHashed;
    private int age;
    private boolean isMale;
    private String activityLevel;
    private LocalDate dateOfBirth;

    public User() {}

    public User(int uid, String fullname, String email, String username, double weightKg, double heightCm, String password, boolean isMale, String activityLevel, LocalDate dateOfBirth) {
        this.uid = uid;
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.passwordHashed = String.valueOf(password.hashCode()); // simple built-in hashing
        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        this.isMale = isMale;
        this.activityLevel = activityLevel;
        this.dateOfBirth = dateOfBirth;
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
    public String getPasswordHashed() { return passwordHashed; }
    public boolean getIsMale(){ return isMale; }

    public String getActivityLevel() {
        return activityLevel;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        /* calculate age diri*/
        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void setAge(int age){
        this.age = age;
    }

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

    public void setPasswordHashed(String password) {
        this.passwordHashed = String.valueOf(password.hashCode()); // simple built-in hashing
    }

    public void setPasswordNonHashed(String passwordHashed){
        this.passwordHashed = passwordHashed; // this is for retrieving from database since it is already hashed there
    }


    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", weightKg=" + weightKg +
                ", heightCm=" + heightCm +
                ", password='" + passwordHashed + '\'' +
                ", age=" + age +
                ", isMale=" + isMale +
                ", activityLevel='" + activityLevel + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }

}
