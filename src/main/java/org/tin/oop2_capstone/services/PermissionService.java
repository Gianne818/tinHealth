package org.tin.oop2_capstone.services;

public class PermissionService {
    /**
     * os permission checks
     * This thing checks for if permissions have been given for the exercise prompt popup.
     *
     * need ba ug permission if mag check siya if naay internet ang user or wla?
     * if needed, it will be important for SyncMonitor to work
     *
     * Since this exception is thrown, the food object that was queried should be stored in a queue,
     * then that queue will be in the user session.ser file, like a queue of objects over there,
     * and then once magka internet, it will take those queued objects, requery it in the api so
     * that we can get the nutritional values
     *
     * If user is playing a video-game, interrupt of the gaming session "exercise prompt" (FROM GUI)
     */
}
