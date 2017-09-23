package core.exceptions;

/**
 * @author dardin88
 */
public class ADBNotFoundException extends Exception {

    public ADBNotFoundException() {
        super("error: adb not found!");
        System.out.println("error: adb not found!");
    }
}
