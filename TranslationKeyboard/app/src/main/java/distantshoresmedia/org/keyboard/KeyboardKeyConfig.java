package distantshoresmedia.org.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import distantshoresmedia.org.keyboard.KeyboardKeyConfigInterface;
import distantshoresmedia.org.keyboard.KeySizeOptions;
import distantshoresmedia.org.model.KeyPosition;


/**
 * Class for describing the position and characteristics of a single key in the keyboardConfig.
 *
 * @attr ref android.R.styleable#Keyboard_keyWidth
 * @attr ref android.R.styleable#Keyboard_keyHeight
 * @attr ref android.R.styleable#Keyboard_horizontalGap
 * @attr ref android.R.styleable#Keyboard_Key_codes
 * @attr ref android.R.styleable#Keyboard_Key_keyIcon
 * @attr ref android.R.styleable#Keyboard_Key_keyLabel
 * @attr ref android.R.styleable#Keyboard_Key_iconPreview
 * @attr ref android.R.styleable#Keyboard_Key_isSticky
 * @attr ref android.R.styleable#Keyboard_Key_isRepeatable
 * @attr ref android.R.styleable#Keyboard_Key_isModifier
 * @attr ref android.R.styleable#Keyboard_Key_popupKeyboard
 * @attr ref android.R.styleable#Keyboard_Key_popupCharacters
 * @attr ref android.R.styleable#Keyboard_Key_keyOutputText
 */
public class KeyboardKeyConfig {


    private KeySizeOptions keyOptions;
    /**
     * All the key codes (unicode or custom code) that this key could generate, zero'th
     * being the most important.
     */
    public int[] codes;

    /** Labels to display */
    public CharSequence[] labelCharacterList;

    /** Actual text to write when key is pressed **/
    public CharSequence[] textCharacterList;

    /** Icon to display instead of a label. Icon takes precedence over a label */
    public Drawable icon;
    /** Preview version of the icon, for the preview popup */
    public Drawable iconPreview;

    /** Whether this key is sticky, i.e., a toggle key */
    public boolean sticky;

    /** The current pressed state of this key */
    public boolean pressed;
    /** If this is a sticky key, is it on or locked? */
    public boolean on;
    public boolean locked;

    public boolean popupReversed;
    public boolean isCursor;
    public String hint; // Set by LatinKeyboardBaseView
    public String altHint; // Set by LatinKeyboardBaseView

    /**
     * Flags that specify the anchoring to edges of the keyboardConfig for detecting touch events
     * that are just out of the boundary of the key. This is a bit mask of
     * {@link org.distantshoresmedia.translationkeyboard.KeyboardConfig#EDGE_LEFT}, {@link org.distantshoresmedia.translationkeyboard.KeyboardConfig#EDGE_RIGHT}, {@link org.distantshoresmedia.translationkeyboard.KeyboardConfig#EDGE_TOP} and
     * {@link org.distantshoresmedia.translationkeyboard.KeyboardConfig#EDGE_BOTTOM}.
     */
    public int edgeFlags;
    /** Whether this is a modifier key, such as Shift or Alt */
    public boolean modifier;
    /** The keyboardConfig that this key belongs to */
    private KeyboardKeyConfigInterface configInterface;
    /**
     * If this key pops up a mini keyboardConfig, this is the resource id for the XML layout for that
     * keyboardConfig.
     */
    public int popupResId;
    /** Whether this key repeats itself when held down */
    public boolean repeatable;
    /** Is the shifted character the uppercase equivalent of the unshifted one? */
    private boolean isSimpleUppercase;
    /** Is the shifted character a distinct uppercase char that's different from the shifted char? */
    private boolean isDistinctUppercase;

    private final static int[] KEY_STATE_NORMAL_ON = {
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_PRESSED_ON = {
            android.R.attr.state_pressed,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_NORMAL_LOCK = {
            android.R.attr.state_active,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_PRESSED_LOCK = {
            android.R.attr.state_active,
            android.R.attr.state_pressed,
            android.R.attr.state_checkable,
            android.R.attr.state_checked
    };

    private final static int[] KEY_STATE_NORMAL_OFF = {
            android.R.attr.state_checkable
    };

    private final static int[] KEY_STATE_PRESSED_OFF = {
            android.R.attr.state_pressed,
            android.R.attr.state_checkable
    };

    private final static int[] KEY_STATE_NORMAL = {
    };

    private final static int[] KEY_STATE_PRESSED = {
            android.R.attr.state_pressed
    };

    /** Create an empty key with no attributes. */
    public KeyboardKeyConfig(KeyboardKeyConfigInterface configInterface, KeySizeOptions options) {
        this.configInterface = configInterface;
        this.keyOptions = options;


    }

    /** Create a key with the given top-left coordinate and extract its attributes from
     * the XML parser.
     * @param res resources associated with the caller's context
     */
    public KeyboardKeyConfig(Resources res, KeyboardKeyConfigInterface configInterface, KeySizeOptions options, KeyPosition key, XmlResourceParser parser) {
        this(configInterface, options);
        this.keyOptions = options;

        this.codes = key.getCharacterCodes();
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard);
        iconPreview = a.getDrawable(R.styleable.Keyboard_Key_iconPreview);
        if (iconPreview != null) {
            iconPreview.setBounds(0, 0, iconPreview.getIntrinsicWidth(),
                    iconPreview.getIntrinsicHeight());
        }

        popupResId = a.getResourceId(
                R.styleable.Keyboard_Key_popupKeyboard, 0);
        repeatable = a.getBoolean(
                R.styleable.Keyboard_Key_isRepeatable, false);
        modifier = a.getBoolean(
                R.styleable.Keyboard_Key_isModifier, false);
        sticky = a.getBoolean(
                R.styleable.Keyboard_Key_isSticky, false);
        isCursor = a.getBoolean(
                R.styleable.Keyboard_Key_isCursor, false);

        icon = a.getDrawable(
                R.styleable.Keyboard_Key_keyIcon);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        }

        a.recycle();
    }

    private int getHeight(){
        return Math.round(this.keyOptions.height);
    }
    private int getWidth(){
        return Math.round(this.keyOptions.width);
    }

    private int getX(){
        return Math.round(this.keyOptions.x);
    }

    private int getY(){
        return Math.round(this.keyOptions.x);
    }

    private int getGap(){
        return Math.round((this.keyOptions.horizontalGap));
    }

                               public boolean isDeadKey() {
        if (codes == null || codes.length < 1) return false;
        return Character.getType(codes[0]) == Character.NON_SPACING_MARK;
    }


    private String getPopupKeyboardContent(boolean isShiftCaps, boolean isShifted, boolean addExtra) {
        int mainChar = getPrimaryCode(false, false);
        int shiftChar = getPrimaryCode(false, true);
        int capsChar = getPrimaryCode(true, true);

        // Remove duplicates
        if (shiftChar == mainChar) shiftChar = 0;
        if (capsChar == shiftChar || capsChar == mainChar) capsChar = 0;

        int popupLen = (popupCharacters == null) ? 0 : popupCharacters.length();
        StringBuilder popup = new StringBuilder(popupLen);
        for (int i = 0; i < popupLen; ++i) {
            char c = popupCharacters.charAt(i);
            if (isShifted || isShiftCaps) {
                String upper = Character.toString(c).toUpperCase(LatinIME.sKeyboardSettings.inputLocale);
                if (upper.length() == 1) c = upper.charAt(0);
            }

            if (c == mainChar || c == shiftChar || c == capsChar) continue;
            popup.append(c);
        }

        if (addExtra) {
            StringBuilder extra = new StringBuilder(3 + popup.length());
            int flags = LatinIME.sKeyboardSettings.popupKeyboardFlags;
            if ((flags & POPUP_ADD_SELF) != 0) {
                // if shifted, add unshifted key to extra, and vice versa
                if (isDistinctUppercase && isShiftCaps) {
                    if (capsChar > 0) { extra.append((char) capsChar); capsChar = 0; }
                } else if (isShifted) {
                    if (shiftChar > 0) { extra.append((char) shiftChar); shiftChar = 0; }
                } else {
                    if (mainChar > 0) { extra.append((char) mainChar); mainChar = 0; }
                }
            }

            if ((flags & POPUP_ADD_CASE) != 0) {
                // if shifted, add unshifted key to popup, and vice versa
                if (isDistinctUppercase && isShiftCaps) {
                    if (mainChar > 0) { extra.append((char) mainChar); mainChar = 0; }
                    if (shiftChar > 0) { extra.append((char) shiftChar); shiftChar = 0; }
                } else if (isShifted) {
                    if (mainChar > 0) { extra.append((char) mainChar); mainChar = 0; }
                    if (capsChar > 0) { extra.append((char) capsChar); capsChar = 0; }
                } else {
                    if (shiftChar > 0) { extra.append((char) shiftChar); shiftChar = 0; }
                    if (capsChar > 0) { extra.append((char) capsChar); capsChar = 0; }
                }
            }

            if (!isSimpleUppercase && (flags & POPUP_ADD_SHIFT) != 0) {
                // if shifted, add unshifted key to popup, and vice versa
                if (isShifted) {
                    if (mainChar > 0) { extra.append((char) mainChar); mainChar = 0; }
                } else {
                    if (shiftChar > 0) { extra.append((char) shiftChar); shiftChar = 0; }
                }
            }

            extra.append(popup);
            return extra.toString();
        }

        return popup.toString();
    }

    public org.distantshoresmedia.translationkeyboard.KeyboardConfig getPopupKeyboard(Context context, int padding) {
        if (popupCharacters == null) {
            if (popupResId != 0) {
                return new org.distantshoresmedia.translationkeyboard.KeyboardConfig(context, keyboardConfig.mDefaultHeight, popupResId);
            } else {
                if (modifier) return null; // Space, Return etc.
            }
        }

        if ((LatinIME.sKeyboardSettings.popupKeyboardFlags & POPUP_DISABLE) != 0) return null;

        String popup = getPopupKeyboardContent(keyboardConfig.isShiftCaps(), keyboardConfig.isShifted(isSimpleUppercase), true);
        //Log.i(TAG, "getPopupKeyboard: popup='" + popup + "' for " + this);
        if (popup.length() > 0) {
            int resId = popupResId;
            if (resId == 0) resId = R.xml.kbd_popup_template;
            return new org.distantshoresmedia.translationkeyboard.KeyboardConfig(context, keyboardConfig.mDefaultHeight, resId, popup, popupReversed, -1, padding);
        } else {
            return null;
        }
    }

    public String getHintLabel(boolean wantAscii, boolean wantAll) {
        if (hint == null) {
            hint = "";
            if (shiftLabel != null && !isSimpleUppercase) {
                char c = shiftLabel.charAt(0);
                if (wantAll || wantAscii && is7BitAscii(c)) {
                    hint = Character.toString(c);
                }
            }
        }
        return hint;
    }

    public String getAltHintLabel(boolean wantAscii, boolean wantAll) {
        if (altHint == null) {
            altHint = "";
            String popup = getPopupKeyboardContent(false, false, false);
            if (popup.length() > 0) {
                char c = popup.charAt(0);
                if (wantAll || wantAscii && is7BitAscii(c)) {
                    altHint = Character.toString(c);
                }
            }
        }
        return altHint;
    }

    private static boolean is7BitAscii(char c) {
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return false;
        return c >= 32 && c < 127;
    }

    /**
     * Informs the key that it has been pressed, in case it needs to change its appearance or
     * state.
     * @see #onReleased(boolean)
     */
    public void onPressed() {
        pressed = !pressed;
    }

    /**
     * Changes the pressed state of the key. Sticky key indicators are handled explicitly elsewhere.
     * @param inside whether the finger was released inside the key
     * @see #onPressed()
     */
    public void onReleased(boolean inside) {
        pressed = !pressed;
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

    /**
     * Detects if a point falls inside this key.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return whether or not the point falls inside the key. If the key is attached to an edge,
     * it will assume that all points between the key and the edge are considered to be inside
     * the key.
     */
    public boolean isInside(int x, int y) {
        boolean leftEdge = (edgeFlags & EDGE_LEFT) > 0;
        boolean rightEdge = (edgeFlags & EDGE_RIGHT) > 0;
        boolean topEdge = (edgeFlags & EDGE_TOP) > 0;
        boolean bottomEdge = (edgeFlags & EDGE_BOTTOM) > 0;
        if ((x >= this.x || (leftEdge && x <= this.x + this.width))
                && (x < this.x + this.width || (rightEdge && x >= this.x))
                && (y >= this.y || (topEdge && y <= this.y + this.height))
                && (y < this.y + this.height || (bottomEdge && y >= this.y))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the square of the distance between the center of the key and the given point.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the square of the distance of the point from the center of the key
     */
    public int squaredDistanceFrom(int x, int y) {
        int xDist = this.x + width / 2 - x;
        int yDist = this.y + height / 2 - y;
        return xDist * xDist + yDist * yDist;
    }

    /**
     * Returns the drawable state for the key, based on the current state and type of the key.
     * @return the drawable state of the key.
     * @see android.graphics.drawable.StateListDrawable#setState(int[])
     */
    public int[] getCurrentDrawableState() {
        int[] states = KEY_STATE_NORMAL;

        if (locked) {
            if (pressed) {
                states = KEY_STATE_PRESSED_LOCK;
            } else {
                states = KEY_STATE_NORMAL_LOCK;
            }
        } else if (on) {
            if (pressed) {
                states = KEY_STATE_PRESSED_ON;
            } else {
                states = KEY_STATE_NORMAL_ON;
            }
        } else {
            if (sticky) {
                if (pressed) {
                    states = KEY_STATE_PRESSED_OFF;
                } else {
                    states = KEY_STATE_NORMAL_OFF;
                }
            } else {
                if (pressed) {
                    states = KEY_STATE_PRESSED;
                }
            }
        }
        return states;
    }

    public String toString() {
        int code = (codes != null && codes.length > 0) ? codes[0] : 0;
        String edges = (
                ((edgeFlags & org.distantshoresmedia.translationkeyboard.KeyboardConfig.EDGE_LEFT) != 0 ? "L" : "-") +
                        ((edgeFlags & org.distantshoresmedia.translationkeyboard.KeyboardConfig.EDGE_RIGHT) != 0 ? "R" : "-") +
                        ((edgeFlags & org.distantshoresmedia.translationkeyboard.KeyboardConfig.EDGE_TOP) != 0 ? "T" : "-") +
                        ((edgeFlags & org.distantshoresmedia.translationkeyboard.KeyboardConfig.EDGE_BOTTOM) != 0 ? "B" : "-"));
        return "KeyDebugFIXME(label=" + label +
                (shiftLabel != null ? " shift=" + shiftLabel : "") +
                (capsLabel != null ? " caps=" + capsLabel : "") +
                (text != null ? " text=" + text : "" ) +
                " code=" + code +
                (code <= 0 || Character.isWhitespace(code) ? "" : ":'" + (char)code + "'" ) +
                " x=" + x + ".." + (x+width) + " y=" + y + ".." + (y+height) +
                " edgeFlags=" + edges +
                (popupCharacters != null ? " pop=" + popupCharacters : "" ) +
                " res=" + popupResId +
                ")";
    }
}