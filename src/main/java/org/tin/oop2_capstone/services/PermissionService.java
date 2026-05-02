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
     *
     * just ask permission to overlay, and check current app focused if it changes
     * "     *  Here we do the observing for when user opens an app or make it the active window, app is not excluded, and x mins or hours of time has passed since the last user exercise prompt, then we do the exercise prompt
     *      *  Perhaps we may need to ask for permissions for this, such as overlay or like... process list access?
     *      *  Perhaps poll the os every few seconds or smth
     *
     * "
     * -From exercisMonitor
     */
}
