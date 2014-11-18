package distantshoresmedia.org.keyboard;

/**
 * Created by Fechner on 11/17/14.
 */
public enum KeyState {
    NONE, PRESSED, UNPRESSED, SHIFT, LONG_PRESSED;

    public static int getNumberForKeyState(KeyState state) {

        switch (state) {
            case NONE:
                return -1;
            case UNPRESSED:
            case PRESSED:
                return 0;
            case SHIFT:
                return 1;
            case LONG_PRESSED:
                return 2;
            default:
                return -2;
        }
    }
}