package org.tin.oop2_capstone.services;

import java.util.ArrayList;
import java.util.List;

public abstract class Logger<T> {

    protected final List<T> observers = new ArrayList<>();

    // this is our template pattern
    public final boolean logData(){
        if(isValid()){
            saveToDB();
            notifyObservers();
            return true;
        }
        return false;
    }

    public final void addObserver(T observer){
        observers.add(observer);
    }

    public final void removeObserver(T observer){
        observers.remove(observer);
    }

    public abstract boolean isValid();
    public abstract boolean saveToDB();
    public abstract  void notifyObservers();
}
