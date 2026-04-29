package org.tin.oop2_capstone.services;

public abstract class Logger {

    // this is our template pattern
    public final void logData(){
        if(isValid()){
            saveToDB();
            notifyObservers();
        }
    }

    public abstract boolean isValid();
    public abstract void saveToDB();
    public abstract  void notifyObservers();
}
