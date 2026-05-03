package org.tin.oop2_capstone.model.entities;

public class User {
    private String fullname;
    private String email;
    private String username;
    private double weightKg;
    private double heightM;
    private String password;

    public User() {}

    public User(String username, String password, double weightKg, double heightM) {
        this.username = username;
        this.weightKg = weightKg;
        this.heightM = heightM;
        this.password = password;
    }

    public String getFullname() { return fullname; }
    public String getEmail()    { return email; }
    public String getUsername() { return username; }
    public double getWeightKg() { return weightKg; }
    public double getHeightM()  { return heightM; }
    public String getPassword() { return password; }

    public void setEmail(String email)       { this.email = email; }
    public void setFullname(String fullname) { this.fullname = fullname;}
    public void setUsername(String username) { this.username = username; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public void setHeightM(double heightM)   { this.heightM = heightM; }

    public void setPassword(String password) {
        // set appropriate hashing algo here
        this.password = password;
    }
}
