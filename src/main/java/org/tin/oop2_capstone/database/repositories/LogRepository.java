package org.tin.oop2_capstone.database.repositories;

public class LogRepository {
    /**
     * Gets food and activity log for current user using RetrieveData
     */

    public static volatile LogRepository instance;

    private LogRepository(){
        System.out.println("LogRepository is initialized for the first time.");
    }

    public static LogRepository getInstance(){
        if(instance == null){
            synchronized (LogRepository.class){
                if(instance == null){
                    instance = new LogRepository();
                }
            }
        }
        return instance;
    }


}
