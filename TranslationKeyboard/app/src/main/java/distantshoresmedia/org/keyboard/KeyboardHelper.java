package distantshoresmedia.org.keyboard;

import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * Created by Fechner on 11/16/14.
 */


public class KeyboardHelper {

    public final static int[] KEY_STATE_NORMAL_ON = {
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    public final static int[] KEY_STATE_PRESSED_ON = {
            android.R.attr.state_pressed,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    public final static int[] KEY_STATE_NORMAL_LOCK = {
            android.R.attr.state_active,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    public final static int[] KEY_STATE_PRESSED_LOCK = {
            android.R.attr.state_active,
            android.R.attr.state_pressed,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    public final static int[] KEY_STATE_NORMAL_OFF = {
            android.R.attr.state_checkable
    };

    public final static int[] KEY_STATE_PRESSED_OFF = {
            android.R.attr.state_pressed,
            android.R.attr.state_checkable
    };

    public final static int[] KEY_STATE_NORMAL = {
    };

    public final static int[] KEY_STATE_PRESSED = {
            android.R.attr.state_pressed
    };


    public static float getDimensionOrFraction(TypedArray a, int index, int base, float defValue) {
        TypedValue value = a.peekValue(index);
        if (value == null) return defValue;
        if (value.type == TypedValue.TYPE_DIMENSION) {
            return a.getDimensionPixelOffset(index, Math.round(defValue));
        } else if (value.type == TypedValue.TYPE_FRACTION) {
            // Round it to avoid values like 47.9999 from getting truncated
            //return Math.round(a.getFraction(index, base, base, defValue));
            return a.getFraction(index, base, base, defValue);
        }
        return defValue;
    }

    private static boolean is7BitAscii(char c) {
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            return false;
        }
        return (c >= 32 && c < 127);
    }

    public static int[] getButtonStateForKeyState(KeyState keyState, KeyType type){
        int[] states = KEY_STATE_NORMAL;

        switch(type){
            case SHIFT:
            case ALT:
                states = (keyState == KeyState.PRESSED)? KEY_STATE_PRESSED_OFF : KEY_STATE_NORMAL_OFF;
                break;
            case CAPS:
                states = (keyState == keyState.PRESSED)? KEY_STATE_PRESSED_ON : KEY_STATE_NORMAL_ON;
                break;
            case LOCKED:
                states = (keyState == keyState.PRESSED)? KEY_STATE_PRESSED_LOCK : KEY_STATE_NORMAL_LOCK;
            default:
                states = (keyState == KeyState.PRESSED)? KEY_STATE_PRESSED : KEY_STATE_NORMAL;
                break;

        }
        return states;
    }
}
