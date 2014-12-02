package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;

import org.distantshoresmedia.basickeyboard.BasicKeyboardView;

/**
 * Created by Fechner on 12/1/14.
 */
public class TKKeyboardView extends BasicKeyboardView {

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
}
