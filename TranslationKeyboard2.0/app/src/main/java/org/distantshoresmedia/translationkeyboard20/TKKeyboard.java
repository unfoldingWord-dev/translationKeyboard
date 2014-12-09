package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.inputmethod.EditorInfo;

import org.distantshoresmedia.basickeyboard.BasicKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.model.KeyCharacter;
import org.distantshoresmedia.model.KeyPosition;
import org.distantshoresmedia.model.KeyboardVariant;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Fechner on 12/1/14.
 */
public class TKKeyboard extends BasicKeyboard{

    private static final String TAG_KEYBOARD = "Keyboard";
    private static final String TAG_ROW = "Row";
    private static final String TAG_KEY = "Key";

    private static final int START_ROW_VALUE = 1;
    private static final int MID_ROW_VALUE = 5;
    private static final int END_ROW_VALUE = 9;

    private int mProximityThreshold;
    /** Number of key widths from current touch point to search for nearest keys. */
    private static float SEARCH_DISTANCE = 1.8f;

    static final String TAG = "Keyboard";
    private Key mEnterKey;

    /** Horizontal gap default for all rows */
    private int mDefaultHorizontalGap;

    /** Default key width */
    private int mDefaultWidth;

    /** Default key height */
    private int mDefaultHeight;

    /** Default gap between rows */
    private int mDefaultVerticalGap;

    /** List of modifier keys such as Shift & Alt, if any */
    private List<TKKey> mModifierKeys;

    /** Width of the screen available to fit the keyboard */
    private int mDisplayWidth;

    /** Height of the screen */
    private int mDisplayHeight;


    /** Keyboard mode, or zero, if none.  */
    private int mKeyboardMode;

    /** Key instance for the shift key, if present */
    private TKKey[] mShiftKeys = { null, null };

    private ArrayList<TKRow> rows = new ArrayList<TKRow>();

    private KeyboardVariant keyboard;

    /** Total height of the keyboard, including the padding and keys */
    private int mTotalHeight;

    private int[] mShiftKeyIndices = {-1, -1};

    private ArrayList<TKKey> mKeys;

    /**
     * Total width of the keyboard, including left side gaps and keys, but not any gaps on the
     * right side.
     */
    private int mTotalWidth;


    public TKKeyboard(Context context, int xmlLayoutResId, int modeId, BaseKeyboard keyboardData) {
        super(context, xmlLayoutResId);
        this.keyboard = keyboardData.getKeyboardVariants()[0];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;
        //Log.v(TAG, "keyboard's display metrics:" + dm);

        mDefaultHorizontalGap = 0;
        mDefaultWidth = mDisplayWidth / 10;
        mDefaultVerticalGap = 0;
        mDefaultHeight = mDefaultWidth;
        mKeys = new ArrayList<TKKey>();
        mModifierKeys = new ArrayList<TKKey>();
        mKeyboardMode = modeId;
        loadKeyboard(context, context.getResources().getXml(xmlLayoutResId));
    }

    public TKKeyboard(Context context, int xmlLayoutResId, int modeId, KeyboardVariant keyModel) {
        super(context, xmlLayoutResId);
        this.keyboard = keyModel;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;
        //Log.v(TAG, "keyboard's display metrics:" + dm);

        mDefaultHorizontalGap = 0;
        mDefaultWidth = mDisplayWidth / 10;
        mDefaultVerticalGap = 0;
        mDefaultHeight = mDefaultWidth;
        mKeys = new ArrayList<TKKey>();
        mModifierKeys = new ArrayList<TKKey>();
        mKeyboardMode = modeId;
        loadKeyboard(context, context.getResources().getXml(xmlLayoutResId));
    }

    public TKKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    private void loadKeyboard(Context context, XmlResourceParser parser) {
        boolean inKey = false;
        boolean inRow = false;
        boolean keyStartedRow = false;
        boolean leftMostKey = false;
        int row = 0;
        int x = 0;
        int y = 0;
        TKKey key = null;
        TKRow currentRow = null;
        Resources res = context.getResources();
        boolean skipRow = false;
        mKeys = new ArrayList<TKKey>();
        rows = new ArrayList<TKRow>();

        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_ROW.equals(tag)) {
                        inRow = true;
                        x = 0;
                        currentRow = createRowFromXml(res, parser);
                        rows.add(currentRow);
                        skipRow = currentRow.mode != 0 && currentRow.mode != mKeyboardMode;
                        if (skipRow) {
                            skipToEndOfRow(parser);
                            inRow = false;
                        }
                    } else if (TAG_KEY.equals(tag)) {
                        inKey = true;
                        key = createKeyFromXml(res, currentRow, x, y, parser);

                        if( key.codes[0] == START_ROW_VALUE){
                            System.out.println("START_ROW_VALUE");
                            keyStartedRow = true;
                            KeyPosition[] positions = this.keyboard.getKeysAtIndex(y);
                            createKeyFromXml(res, currentRow, x, y, parser, positions[0]);
                            mKeys.add(x+y, key);

                            inKey = false;
                            x += key.gap + key.width;
                            if (x > mTotalWidth) {
                                mTotalWidth = x;
                            }
                        }
                        else if( key.codes[0] == MID_ROW_VALUE){
                            System.out.println("MID_ROW_VALUE");

                            KeyPosition[] positions = this.keyboard.getKeysAtIndex(y);

                            for (int i = (keyStartedRow)? 1 : 0; i < positions.length - 1; i++) {
                                key = createKeyFromXml(res, currentRow, x, y, parser, positions[i]);
                                mKeys.add(x+y, key);
                                x += key.gap + key.width;
                                if (x > mTotalWidth) {
                                    mTotalWidth = x;
                                }
                            }
                            keyStartedRow = false;
                        }

                        else if( key.codes[0] == END_ROW_VALUE){
                            System.out.println("END_ROW_VALUE");
                            KeyPosition[] positions = this.keyboard.getKeysAtIndex(y);
                            key = createKeyFromXml(res, currentRow, x, y, parser, positions[positions.length - 1]);
                            mKeys.set(x+y, key);
                            continue;
                        }

                        else {

                            mKeys.add(x+y, key);
                            if (key.codes[0] == KEYCODE_SHIFT) {
                                // Find available shift key slot and put this shift key in it
                                for (int i = 0; i < mShiftKeys.length; i++) {
                                    if (mShiftKeys[i] == null) {
                                        mShiftKeys[i] = key;
                                        mShiftKeyIndices[i] = mKeys.size() - 1;
                                        break;
                                    }
                                }
                                mModifierKeys.add(key);
                            } else if (key.codes[0] == KEYCODE_ALT) {
                                mModifierKeys.add(key);
                            }
                            currentRow.mKeys.add(key);
                        }
                    } else if (TAG_KEYBOARD.equals(tag)) {
                        parseKeyboardAttributes(res, parser);
                    }
                } else if (event == XmlResourceParser.END_TAG) {
                    if (inKey) {
                        inKey = false;
                        x += key.gap + key.width;
                        if (x > mTotalWidth) {
                            mTotalWidth = x;
                        }
                    } else if (inRow) {
                        inRow = false;
                        y += currentRow.verticalGap;
                        y += currentRow.defaultHeight;
                        row++;
                    } else {
                        // TODO: error or extend?
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error:" + e);
            e.printStackTrace();
        }
        mTotalHeight = y - mDefaultVerticalGap;
    }

    private void parseKeyboardAttributes(Resources res, XmlResourceParser parser) {

        int[] neededArray = {Resources.getSystem().getIdentifier("Keyboard","styleable", "android")};
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), neededArray);
//                com.android.internal.R.styleable.Keyboard);

        mDefaultWidth = getDimensionOrFraction(a,
                Resources.getSystem().getIdentifier("Keyboard_keyWidth","styleable", "android"),
//                com.android.internal.R.styleable.Keyboard_keyWidth,Keyboard_keyWidth
                mDisplayWidth, mDisplayWidth / 10);
        mDefaultHeight = getDimensionOrFraction(a,
                Resources.getSystem().getIdentifier("Keyboard_keyHeight","styleable", "android"),
//                com.android.internal.R.styleable.Keyboard_keyHeight,
                mDisplayHeight, 50);
        mDefaultHorizontalGap = getDimensionOrFraction(a,
                Resources.getSystem().getIdentifier("Keyboard_horizontalGap","styleable", "android"),
//                com.android.internal.R.styleable.Keyboard_horizontalGap,
                mDisplayWidth, 0);
        mDefaultVerticalGap = getDimensionOrFraction(a,
                Resources.getSystem().getIdentifier("Keyboard_verticalGap","styleable", "android"),
//                com.android.internal.R.styleable.Keyboard_verticalGap,
                mDisplayHeight, 0);
        mProximityThreshold = (int) (mDefaultWidth * SEARCH_DISTANCE);
        mProximityThreshold = mProximityThreshold * mProximityThreshold; // Square it for comparison
        a.recycle();
    }

    private void skipToEndOfRow(XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        int event;
        while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
            if (event == XmlResourceParser.END_TAG
                    && parser.getName().equals(TAG_ROW)) {
                break;
            }
        }
    }

    protected TKRow createRowFromXml(Resources res, XmlResourceParser parser) {
        return new TKRow(res, this, parser);
    }

    protected TKKey createKeyFromXml(Resources res, TKRow parent, int x, int y,
                                   XmlResourceParser parser, KeyPosition position) {
        return new TKKey(res, parent, x, y, parser, position);
    }

    protected TKKey createKeyFromXml(Resources res, TKRow parent, int x, int y,
                                     XmlResourceParser parser) {
        return new TKKey(res, parent, x, y, parser);
    }


    public static class TKRow extends BasicRow {

        ArrayList<TKKey> mKeys = new ArrayList<TKKey>();

        private TKKeyboard parent;

        public TKRow(TKKeyboard parent) {
            super(parent);
            this.parent = parent;
        }

        public TKRow(Resources res, TKKeyboard parent, XmlResourceParser parser) {

            super(res, parent, parser);

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

        public TKKey(Resources res, TKRow parent, int x, int y, XmlResourceParser parser, KeyPosition position) {
            this(parent);
            KeyCharacter[] characters = position.getCharacters();
            this.codes = new int[characters.length];

            for (int i = 0; i < characters.length; i++) {
                KeyCharacter keyChar = characters[i];

                int[] value = keyChar.getUtf8hex();

                this.codes[i] = value[0];
            }
        }

//        public TKKey(Resources res, TKRow parent, int x, int y, XmlResourceParser parser) {
//            this(parent);
//
//            System.out.println("got here");
//
//            this.x = x;
//            this.y = y;
//
//            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
//                    R.styleable.Keyboard);
//            System.out.println("Before Width");
//            try {
//                width = getDimensionOrFraction(a,
//                        Resources.getSystem().getIdentifier("Keyboard_keyWidth", "styleable", "android"),
////                    com.android.internal.R.styleable.Keyboard_keyWidth,
//                        keyboard.mDisplayWidth, parent.defaultWidth);
//            }
//            catch(NullPointerException e){
//                System.out.println("NullPointerException: " + keyboard.mDisplayWidth);
//            }
//            System.out.println("After Width");
//            height = getDimensionOrFraction(a,
//                    Resources.getSystem().getIdentifier("Keyboard_keyHeight","styleable", "android"),
////                    com.android.internal.R.styleable.Keyboard_keyHeight,
//                    keyboard.mDisplayHeight, parent.defaultHeight);
//            gap =  0;//getDimensionOrFraction(a,
////                    Resources.getSystem().getIdentifier("Keyboard_horizontalGap","styleable", "android"),
//////                    com.android.internal.R.styleable.Keyboard_horizontalGap,
////                    keyboard.mDisplayWidth, parent.defaultHorizontalGap);
//            a.recycle();
//            a = res.obtainAttributes(Xml.asAttributeSet(parser),
//                    R.styleable.Keyboard_Key);
//            this.x += gap;
//            TypedValue codesValue = new TypedValue();
//            a.getValue(Resources.getSystem().getIdentifier("Keyboard_Key_codes","styleable", "android"),
//                    codesValue);
////                    com.android.internal.R.styleable.Keyboard_Key_codes,
////                    codesValue);
//            if (codesValue.type == TypedValue.TYPE_INT_DEC
//                    || codesValue.type == TypedValue.TYPE_INT_HEX) {
//                codes = new int[] { codesValue.data };
//            } else if (codesValue.type == TypedValue.TYPE_STRING) {
//                codes = parseCSV(codesValue.string.toString());
//            }
//
//            iconPreview = a.getDrawable(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_iconPreview","styleable", "android"));
////                    com.android.internal.R.styleable.Keyboard_Key_iconPreview);
//            if (iconPreview != null) {
//                iconPreview.setBounds(0, 0, iconPreview.getIntrinsicWidth(),
//                        iconPreview.getIntrinsicHeight());
//            }
//            popupCharacters = a.getText(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_popupCharacters","styleable", "android"));
////                    com.android.internal.R.styleable.Keyboard_Key_popupCharacters);
//            popupResId = a.getResourceId(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_popupKeyboard", "styleable", "android"), 0);
////                    com.android.internal.R.styleable.Keyboard_Key_popupKeyboard, 0);
//            repeatable = a.getBoolean(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_isRepeatable","styleable", "android"), false);
////                    com.android.internal.R.styleable.Keyboard_Key_isRepeatable, false);
//            modifier = a.getBoolean(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_isModifier","styleable", "android"), false);
////                    com.android.internal.R.styleable.Keyboard_Key_isModifier, false);
//            sticky = a.getBoolean(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_isSticky","styleable", "android"), false);
////                    com.android.internal.R.styleable.Keyboard_Key_isSticky, false);
//            edgeFlags = a.getInt(
//                    Resources.getSystem().getIdentifier("Keyboard_Key_keyEdgeFlags","styleable", "android"), 0);
////                    com.android.internal.R.styleable.Keyboard_Key_keyEdgeFlags, 0);
//            edgeFlags |= parent.rowEdgeFlags;
//
//            icon = a.getDrawable(Resources.getSystem().getIdentifier("Keyboard_Key_keyIcon","styleable", "android"));
////                    com.android.internal.R.styleable.Keyboard_Key_keyIcon);
//            if (icon != null) {
//                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//            }
//            label = a.getText(Resources.getSystem().getIdentifier("Keyboard_Key_keyLabel","styleable", "android"));
////                    com.android.internal.R.styleable.Keyboard_Key_keyLabel);
//            text = a.getText(Resources.getSystem().getIdentifier("Keyboard_Key_keyLabel","styleable", "android"));
////            com.android.internal.R.styleable.Keyboard_Key_keyOutputText);
//
//            if (codes == null && !TextUtils.isEmpty(label)) {
//                codes = new int[] { label.charAt(0) };
//            }
//            a.recycle();
//        }

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
        System.out.println("here");
        TypedValue value = a.peekValue(index);
        if (value == null) return defValue;
        if (value.type == TypedValue.TYPE_DIMENSION) {
            return a.getDimensionPixelOffset(index, defValue);
        } else if (value.type == TypedValue.TYPE_FRACTION) {
            // Round it to avoid values like 47.9999 from getting truncated
            return Math.round(a.getFraction(index, base, base, defValue));
        }
        System.out.println("Final Val: " + defValue);
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
