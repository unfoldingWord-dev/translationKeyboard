package distantshoresmedia.org.keyboard;

import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * Created by Fechner on 11/16/14.
 */
public class KeyboardHelper {

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
}

public enum keyType{

}