package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import java.io.*;

public class SessionManager implements Serializable {
    /**
     * A singleton
     * Store and read user object and user prefs into an .ser file after it is validated by UserRepo and SettingsRepo
     * .ser file should contain user object, user prefs,
     */

    private static final long serialVersionUID = 1L;
    private static SessionManager instance;
    private User currentUser;
    private UserPreferences currentUserPrefs;

    private static final String FILE = "session.ser";

    private SessionManager() {
        loadFromFile();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        saveToFile();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUserPrefs(UserPreferences prefs) {
        this.currentUserPrefs = prefs;
        saveToFile();
    }

    public UserPreferences getCurrentUserPrefs() {
        return currentUserPrefs;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(currentUser);
            oos.writeObject(currentUserPrefs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            currentUser = (User) ois.readObject();
            currentUserPrefs = (UserPreferences) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        currentUser = null;
        currentUserPrefs = null;
        new File(FILE).delete();
    }
}
