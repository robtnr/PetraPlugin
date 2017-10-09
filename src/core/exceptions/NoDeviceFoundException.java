package core.exceptions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * @author dardin88
 */
public class NoDeviceFoundException extends Exception {

    public NoDeviceFoundException() {
        super("error: no device/emulator found!");
        System.out.println("error: no device/emulator found!");
        Notifications.Bus.notify(new Notification("IDDeviceMissing", "Error", "no device/emulator found!", NotificationType.ERROR));
    }
}
