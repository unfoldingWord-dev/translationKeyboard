package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.inputmethod.EditorInfo;

import org.distantshoresmedia.basickeyboard.BasicKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.model.KeyPosition;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Fechner on 12/1/14.
 */
public class TKKeyboard extends BasicKeyboard{

    static final String TAG = "Keyboard";
    private Key mEnterKey;
    /** Width of the screen available to fit the keyboard */
    private int mDisplayWidth;

    /** Height of the screen */
    private int mDisplayHeight;

    /** Key instance for the shift key, if present */
    private TKKey[] mShiftKeys = { null, null };


    public TKKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public TKKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    public static class TKRow extends BasicRow {

        private TKKeyboard parent;

        public TKRow(TKKeyboard parent) {
            super(parent);
            this.parent = parent;
        }
    }

    static class TKKey extends BasicKey {

        private Drawable mDefaultIcon;
        private Drawable mDefaultIconPreview;

        private Drawable mShiftLockIcon;
        private Drawable mShiftLockPreviewIcon;

        private TKKeyboard keyboard;

//        public TKKey(Row parent) {
////            keyboard = parent.parent;
//            height = parent.defaultHeight;
//            width = parent.defaultWidth;
//            gap = parent.defaultHorizontalGap;
//            edgeFlags = parent.rowEdgeFlags;
//        }

        public TKKey(TKRow parent) {
            super(parent);
            keyboard = parent.parent;
            height = parent.defaultHeight;
            width = parent.defaultWidth;
            gap = parent.defaultHorizontalGap;
            edgeFlags = parent.rowEdgeFlags;
        }

        public TKKey(Resources res, TKRow parent, int x, int y, XmlResourceParser parser) {
            this(parent);

            System.out.println("got here");

            this.x = x;
            this.y = y;

            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                    R.styleable.Keyboard);

            width = getDimensionOrFraction(a,
                    Resources.getSystem().getIdentifier("Keyboard_keyWidth","styleable", "android"),
//                    com.android.internal.R.styleable.Keyboard_keyWidth,
                    keyboard.mDisplayWidth, parent.defaultWidth);
            height = getDimensionOrFraction(a,
                    Resources.getSystem().getIdentifier("Keyboard_keyHeight","styleable", "android"),
//                    com.android.internal.R.styleable.Keyboard_keyHeight,
                    keyboard.mDisplayHeight, parent.defaultHeight);
            gap =  0;//getDimensionOrFraction(a,
//                    Resources.getSystem().getIdentifier("Keyboard_horizontalGap","styleable", "android"),
////                    com.android.internal.R.styleable.Keyboard_horizontalGap,
//                    keyboard.mDisplayWidth, parent.defaultHorizontalGap);
            a.recycle();
            a = res.obtainAttributes(Xml.asAttributeSet(parser),
                    R.styleable.Keyboard_Key);
            this.x += gap;
            TypedValue codesValue = new TypedValue();
            a.getValue(Resources.getSystem().getIdentifier("Keyboard_Key_codes","styleable", "android"),
                    codesValue);
//                    com.android.internal.R.styleable.Keyboard_Key_codes,
//                    codesValue);
            if (codesValue.type == TypedValue.TYPE_INT_DEC
                    || codesValue.type == TypedValue.TYPE_INT_HEX) {
                codes = new int[] { codesValue.data };
            } else if (codesValue.type == TypedValue.TYPE_STRING) {
                codes = parseCSV(codesValue.string.toString());
            }

            iconPreview = a.getDrawable(
                    Resources.getSystem().getIdentifier("Keyboard_Key_iconPreview","styleable", "android"));
//                    com.android.internal.R.styleable.Keyboard_Key_iconPreview);
            if (iconPreview != null) {
                iconPreview.setBounds(0, 0, iconPreview.getIntrinsicWidth(),
                        iconPreview.getIntrinsicHeight());
            }
            popupCharacters = a.getText(
                    Resources.getSystem().getIdentifier("Keyboard_Key_popupCharacters","styleable", "android"));
//                    com.android.internal.R.styleable.Keyboard_Key_popupCharacters);
            popupResId = a.getResourceId(
                    Resources.getSystem().getIdentifier("Keyboard_Key_popupKeyboard", "styleable", "android"), 0);
//                    com.android.internal.R.styleable.Keyboard_Key_popupKeyboard, 0);
            repeatable = a.getBoolean(
                    Resources.getSystem().getIdentifier("Keyboard_Key_isRepeatable","styleable", "android"), false);
//                    com.android.internal.R.styleable.Keyboard_Key_isRepeatable, false);
            modifier = a.getBoolean(
                    Resources.getSystem().getIdentifier("Keyboard_Key_isModifier","styleable", "android"), false);
//                    com.android.internal.R.styleable.Keyboard_Key_isModifier, false);
            sticky = a.getBoolean(
                    Resources.getSystem().getIdentifier("Keyboard_Key_isSticky","styleable", "android"), false);
//                    com.android.internal.R.styleable.Keyboard_Key_isSticky, false);
            edgeFlags = a.getInt(
                    Resources.getSystem().getIdentifier("Keyboard_Key_keyEdgeFlags","styleable", "android"), 0);
//                    com.android.internal.R.styleable.Keyboard_Key_keyEdgeFlags, 0);
            edgeFlags |= parent.rowEdgeFlags;

            icon = a.getDrawable(Resources.getSystem().getIdentifier("Keyboard_Key_keyIcon","styleable", "android"));
//                    com.android.internal.R.styleable.Keyboard_Key_keyIcon);
            if (icon != null) {
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            }
            label = a.getText(Resources.getSystem().getIdentifier("Keyboard_Key_keyLabel","styleable", "android"));
//                    com.android.internal.R.styleable.Keyboard_Key_keyLabel);
            text = a.getText(Resources.getSystem().getIdentifier("Keyboard_Key_keyLabel","styleable", "android"));
//            com.android.internal.R.styleable.Keyboard_Key_keyOutputText);

            if (codes == null && !TextUtils.isEmpty(label)) {
                codes = new int[] { label.charAt(0) };
            }
            a.recycle();
        }

//        public TKKey(Resources res, Keyboard.Row parent, int x, int y, XmlResourceParser parser) {
//            super(res, parent, x, y, parser);
//            mShiftLockIcon = res.getDrawable(R.drawable.sym_keyboard_shift_locked);
//            mShiftLockPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_shift_locked);
//            mDefaultIcon = res.getDrawable(R.drawable.sym_keyboard_shift);
//            mDefaultIconPreview = res.getDrawable(R.drawable.sym_keyboard_feedback_shift);
//        }

//        public TKKey(Resources res, Row parent, int x, int y, KeyPosition keyPosition) {
//            this(parent);
//
//            this.x = x;
//            this.y = y;
//
//            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
//                    com.android.internal.R.styleable.Keyboard);
//
//            width = getDimensionOrFraction(a,
//                    com.android.internal.R.styleable.Keyboard_keyWidth,
//                    keyboard.mDisplayWidth, parent.defaultWidth);
//            height = getDimensionOrFraction(a,
//                    com.android.internal.R.styleable.Keyboard_keyHeight,
//                    keyboard.mDisplayHeight, parent.defaultHeight);
//            gap = getDimensionOrFraction(a,
//                    com.android.internal.R.styleable.Keyboard_horizontalGap,
//                    keyboard.mDisplayWidth, parent.defaultHorizontalGap);
//            a.recycle();
//            a = res.obtainAttributes(Xml.asAttributeSet(parser),
//                    com.android.internal.R.styleable.Keyboard_Key);
//            this.x += gap;
//            TypedValue codesValue = new TypedValue();
//            a.getValue(com.android.internal.R.styleable.Keyboard_Key_codes,
//                    codesValue);
//            if (codesValue.type == TypedValue.TYPE_INT_DEC
//                    || codesValue.type == TypedValue.TYPE_INT_HEX) {
//                codes = new int[] { codesValue.data };
//            } else if (codesValue.type == TypedValue.TYPE_STRING) {
//                codes = parseCSV(codesValue.string.toString());
//            }
//
//            iconPreview = a.getDrawable(com.android.internal.R.styleable.Keyboard_Key_iconPreview);
//            if (iconPreview != null) {
//                iconPreview.setBounds(0, 0, iconPreview.getIntrinsicWidth(),
//                        iconPreview.getIntrinsicHeight());
//            }
//            popupCharacters = a.getText(
//                    com.android.internal.R.styleable.Keyboard_Key_popupCharacters);
//            popupResId = a.getResourceId(
//                    com.android.internal.R.styleable.Keyboard_Key_popupKeyboard, 0);
//            repeatable = a.getBoolean(
//                    com.android.internal.R.styleable.Keyboard_Key_isRepeatable, false);
//            modifier = a.getBoolean(
//                    com.android.internal.R.styleable.Keyboard_Key_isModifier, false);
//            sticky = a.getBoolean(
//                    com.android.internal.R.styleable.Keyboard_Key_isSticky, false);
//            edgeFlags = a.getInt(com.android.internal.R.styleable.Keyboard_Key_keyEdgeFlags, 0);
//            edgeFlags |= parent.rowEdgeFlags;
//
//            icon = a.getDrawable(
//                    com.android.internal.R.styleable.Keyboard_Key_keyIcon);
//            if (icon != null) {
//                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//            }
//            label = a.getText(com.android.internal.R.styleable.Keyboard_Key_keyLabel);
//            text = a.getText(com.android.internal.R.styleable.Keyboard_Key_keyOutputText);
//
//            if (codes == null && !TextUtils.isEmpty(label)) {
//                codes = new int[] { label.charAt(0) };
//            }
//            a.recycle();
//        }

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

        int[] parseCSV(String value) {
            int count = 0;
            int lastIndex = 0;
            if (value.length() > 0) {
                count++;
                while ((lastIndex = value.indexOf(",", lastIndex + 1)) > 0) {
                    count++;
                }
            }
            int[] values = new int[count];
            count = 0;
            StringTokenizer st = new StringTokenizer(value, ",");
            while (st.hasMoreTokens()) {
                try {
                    values[count++] = Integer.parseInt(st.nextToken());
                } catch (NumberFormatException nfe) {
                    Log.e(TAG, "Error parsing keycodes " + value);
                }
            }
            return values;
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

    static int getDimensionOrFraction(TypedArray a, int index, int base, int defValue) {
        TypedValue value = a.peekValue(index);
        if (value == null) return defValue;
        if (value.type == TypedValue.TYPE_DIMENSION) {
            return a.getDimensionPixelOffset(index, defValue);
        } else if (value.type == TypedValue.TYPE_FRACTION) {
            // Round it to avoid values like 47.9999 from getting truncated
            return Math.round(a.getFraction(index, base, base, defValue));
        }
        return defValue;
    }

    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }

        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                mEnterKey.label = null;
                break;
        }
    }
}
