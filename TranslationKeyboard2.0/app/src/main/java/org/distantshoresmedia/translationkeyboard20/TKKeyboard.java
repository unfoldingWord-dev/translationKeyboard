package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;

import org.distantshoresmedia.basickeyboard.BasicKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;

import java.util.List;

/**
 * Created by Fechner on 12/1/14.
 */
public class TKKeyboard extends BasicKeyboard{

    /** Key instance for the shift key, if present */
    private TKKey[] mShiftKeys = { null, null };


    public TKKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public TKKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    static class TKKey extends BasicKeyboard.BasicKey {

        private Drawable mDefaultIcon;
        private Drawable mDefaultIconPreview;

        private Drawable mShiftLockIcon;
        private Drawable mShiftLockPreviewIcon;

        public TKKey(Resources res, Keyboard.Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
            mShiftLockIcon = res.getDrawable(R.drawable.sym_keyboard_shift_locked);
            mShiftLockPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_shift_locked);
            mDefaultIcon = res.getDrawable(R.drawable.sym_keyboard_shift);
            mDefaultIconPreview = res.getDrawable(R.drawable.sym_keyboard_feedback_shift);
        }

        public void setShifted(boolean shifted){
            System.out.println("Shifted: " + shifted);
            this.icon = (shifted)? mDefaultIcon : mShiftLockIcon;
            this.iconPreview = (shifted)? mDefaultIconPreview : mShiftLockPreviewIcon;
        }

        @Override
        public void onPressed() {
            pressed = !pressed;
            System.out.println("Keycode: " + this.codes[0] + " pressed");
        }
    }

    @Override
    public boolean setShifted(boolean shiftState) {
        boolean shifted = super.setShifted(shiftState);
        System.out.println("got here");
        if (mShiftKeys != null) {

            for(TKKey mShiftKey : mShiftKeys) {
                // Tri-state LED tracks "on" and "lock" states, icon shows Caps state.
                mShiftKey.setShifted(shifted);
            }
        }
        return shifted;
    }

    public List<Key> getKeys() {
        List<Key> keys = super.getKeys();
        for (Key key : keys) {
            System.out.print("Key: ");
            try {
                System.out.println(key.label.charAt(0));
            } catch (NullPointerException exception) {
                System.out.println("error! Code: " + key.codes[0]);
            }
        }
        return keys;
    }

    public void setSpaceIcon(final Drawable icon) {
        super.setSpaceIcon(icon);
    }

}
