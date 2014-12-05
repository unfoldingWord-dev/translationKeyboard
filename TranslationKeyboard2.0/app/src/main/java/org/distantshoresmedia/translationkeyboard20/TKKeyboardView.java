package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

import org.distantshoresmedia.basickeyboard.BasicKeyboardView;

/**
 * Created by Fechner on 12/1/14.
 */
public class TKKeyboardView extends BasicKeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    private TKKeyboard mKeyboard;

    public TKKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TKKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean setShifted(boolean shifted) {

        System.out.println("View Set Shifted");

        return super.setShifted(shifted);
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final TKKeyboard keyboard = (TKKeyboard)getKeyboard();
//        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    public TKKeyboard getKeyboard(){
        return this.mKeyboard;
    }
}
