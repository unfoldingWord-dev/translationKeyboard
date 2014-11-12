package org.distantshoresmedia.keyboard;

import java.util.Locale;
import java.util.StringTokenizer;

import android.R;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import org.distantshoresmedia.keyboard.KeyboardConfig;
import org.distantshoresmedia.keyboard.KeyboardRowConfig;
import org.distantshoresmedia.translationkeyboard.LatinIME;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;

public class KeyboardKeyConfig {

    /**
     * All the key codes (unicode or custom code) that this key could generate, zero'th
     * being the most important.
     */
    public int[] codes;

    /** Label to display */
    public CharSequence label;
    public CharSequence shiftLabel;
    public CharSequence capsLabel;

    /** Icon to display instead of a label. Icon takes precedence over a label */
    public Drawable icon;
    /** Preview version of the icon, for the preview popup */
    public Drawable iconPreview;
    /** Width of the key, not including the gap */
    public int width;
    /** Height of the key, not including the gap */
    private float realWidth;
    public int height;
    /** The horizontal gap before this key */
    public int gap;
    private float realGap;
    /** Whether this key is sticky, i.e., a toggle key */
    public boolean sticky;
    /** X coordinate of the key in the keyboard layout */
    public int x;
    private float realX;
    /** Y coordinate of the key in the keyboard layout */
    public int y;
    /** The current pressed state of this key */
    public boolean pressed;
    /** If this is a sticky key, is it on or locked? */
    public boolean on;
    public boolean locked;
    /** Text to output when pressed. This can be multiple characters, like ".com" */
    public CharSequence text;
    /** Popup characters */
    public CharSequence popupCharacters;
    public boolean popupReversed;
    public boolean isCursor;
    public String hint; // Set by LatinKeyboardBaseView
    public String altHint; // Set by LatinKeyboardBaseView

    /**
     * Flags that specify the anchoring to edges of the keyboard for detecting touch events
     * that are just out of the boundary of the key. This is a bit mask of
     * {@link KeyboardConfig#EDGE_LEFT}, {@link KeyboardConfig#EDGE_RIGHT}, {@link KeyboardConfig#EDGE_TOP} and
     * {@link KeyboardConfig#EDGE_BOTTOM}.
     */
    public int edgeFlags;
    /** Whether this is a modifier key, such as Shift or Alt */
    public boolean modifier;
    /** The keyboard that this key belongs to */
    private KeyboardConfig keyboard;
    /**
     * If this key pops up a mini keyboard, this is the resource id for the XML layout for that
     * keyboard.
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
    public KeyboardKeyConfig(KeyboardConfig board, KeyboardRowConfig parentRow) {
        keyboard = board;
        height = parentRow.defaultHeight;
        width = Math.round(parentRow.defaultWidth);
        realWidth = parentRow.defaultWidth;
        gap = Math.round(parentRow.defaultHorizontalGap);
        realGap = parentRow.defaultHorizontalGap;
    }

    /** Create a key with the given top-left coordinate and extract its attributes from
     * the XML parser.
     * @param res resources associated with the caller's context
     * @param parent the row that this key belongs to. The row must already be attached to
     * a {@link KeyboardConfig}.
     * @param x the x coordinate of the top-left
     * @param y the y coordinate of the top-left
     * @param parser the XML parser containing the attributes for this key
     */
    public KeyboardKeyConfig(KeyboardConfig board, KeyboardRowConfig parentRow, Resources res, int x, int y, XmlResourceParser parser) {
        this(board, parentRow);

        this.x = x;
        this.y = y;

        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.KeyboardConfig);
       
        realWidth = getDimensionOrFraction(a,
                R.styleable.Keyboard_keyWidth,
                keyboard.mDisplayWidth, parent.defaultWidth);
        float realHeight = getDimensionOrFraction(a,
                R.styleable.Keyboard_keyHeight,
                keyboard.mDisplayHeight, parent.defaultHeight);
        realHeight -= parent.parent.mVerticalPad;
        height = Math.round(realHeight);
        this.y += parent.parent.mVerticalPad / 2;
        realGap = getDimensionOrFraction(a,
                R.styleable.Keyboard_horizontalGap,
                keyboard.mDisplayWidth, parent.defaultHorizontalGap);
        realGap += parent.parent.mHorizontalPad;
        realWidth -= parent.parent.mHorizontalPad;
        width = Math.round(realWidth);
        gap = Math.round(realGap);
        a.recycle();
        a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard_Key);
        this.realX = this.x + realGap - parent.parent.mHorizontalPad / 2;
        this.x = Math.round(this.realX);
        TypedValue codesValue = new TypedValue();
        a.getValue(R.styleable.Keyboard_Key_codes,
                codesValue);
        if (codesValue.type == TypedValue.TYPE_INT_DEC
                || codesValue.type == TypedValue.TYPE_INT_HEX) {
            codes = new int[] { codesValue.data };
        } else if (codesValue.type == TypedValue.TYPE_STRING) {
            codes = parseCSV(codesValue.string.toString());
        }

        iconPreview = a.getDrawable(R.styleable.Keyboard_Key_iconPreview);
        if (iconPreview != null) {
            iconPreview.setBounds(0, 0, iconPreview.getIntrinsicWidth(),
                    iconPreview.getIntrinsicHeight());
        }
        popupCharacters = a.getText(
                R.styleable.Keyboard_Key_popupCharacters);
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
        label = a.getText(R.styleable.Keyboard_Key_keyLabel);
        shiftLabel = a.getText(R.styleable.Keyboard_Key_shiftLabel);
        if (shiftLabel != null && shiftLabel.length() == 0) shiftLabel = null;
        capsLabel = a.getText(R.styleable.Keyboard_Key_capsLabel);
        if (capsLabel != null && capsLabel.length() == 0) capsLabel = null;
        text = a.getText(R.styleable.Keyboard_Key_keyOutputText);

        if (codes == null && !TextUtils.isEmpty(label)) {
            codes = getFromString(label);
            if (codes != null && codes.length == 1) {
                final Locale locale = LatinIME.sKeyboardSettings.inputLocale;
                String upperLabel = label.toString().toUpperCase(locale);
                if (shiftLabel == null) {
                    // No shiftLabel supplied, auto-set to uppercase if possible.
                    if (!upperLabel.equals(label.toString()) && upperLabel.length() == 1) {
                        shiftLabel = upperLabel;
                        isSimpleUppercase = true;
                    }
                } else {
                    // Both label and shiftLabel supplied. Check if
                    // the shiftLabel is the uppercased normal label.
                    // If not, treat it as a distinct uppercase variant.
                    if (capsLabel != null) {
                        isDistinctUppercase = true;
                    } else if (upperLabel.equals(shiftLabel.toString())) {
                        isSimpleUppercase = true;
                    } else if (upperLabel.length() == 1) {
                        capsLabel = upperLabel;
                        isDistinctUppercase = true;
                    }
                }
            }
            if ((LatinIME.sKeyboardSettings.popupKeyboardFlags & POPUP_DISABLE) != 0) {
                popupCharacters = null;
                popupResId = 0;
            }
            if ((LatinIME.sKeyboardSettings.popupKeyboardFlags & POPUP_AUTOREPEAT) != 0) {
                // Assume POPUP_DISABLED is set too, otherwise things may get weird.
                repeatable = true;
            }
        }
        //Log.i(TAG, "added key definition: " + this);
        a.recycle();
    }

    public boolean isDistinctCaps() {
        return isDistinctUppercase && keyboard.isShiftCaps();
    }

    public boolean isShifted() {
        boolean shifted = keyboard.isShifted(isSimpleUppercase);
        //Log.i(TAG, "FIXME isShifted=" + shifted + " for " + this);
        return shifted;
    }

    public int getPrimaryCode(boolean isShiftCaps, boolean isShifted) {
        if (isDistinctUppercase && isShiftCaps) {
            return capsLabel.charAt(0);
        }
        //Log.i(TAG, "getPrimaryCode(), shifted=" + shifted);
        if (isShifted && shiftLabel != null) {
            if (shiftLabel.charAt(0) == DEAD_KEY_PLACEHOLDER && shiftLabel.length() >= 2) {
                return shiftLabel.charAt(1);
            } else {
                return shiftLabel.charAt(0);
            }
        } else {
            return codes[0];
        }
    }

    public int getPrimaryCode() {
        return getPrimaryCode(keyboard.isShiftCaps(), keyboard.isShifted(isSimpleUppercase));
    }

    public boolean isDeadKey() {
        if (codes == null || codes.length < 1) return false;
        return Character.getType(codes[0]) == Character.NON_SPACING_MARK;
    }

    public int[] getFromString(CharSequence str) {
        if (str.length() > 1) {
            if (str.charAt(0) == DEAD_KEY_PLACEHOLDER && str.length() >= 2) {
                return new int[] { str.charAt(1) }; // FIXME: >1 length?
            } else {
                text = str; // TODO: add space?
                return new int[] { 0 };
            }
        } else {
            char c = str.charAt(0);
            return new int[] { c };
        }
    }

    public String getCaseLabel() {
        if (isDistinctUppercase && keyboard.isShiftCaps()) {
            return capsLabel.toString();
        }
        boolean isShifted = keyboard.isShifted(isSimpleUppercase);
        if (isShifted && shiftLabel != null) {
            return shiftLabel.toString();
        } else {
            return label != null ? label.toString() : null;
        }
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

    public KeyboardConfig getPopupKeyboard(Context context, int padding) {
        if (popupCharacters == null) {
            if (popupResId != 0) {
                return new KeyboardConfig(context, keyboard.mDefaultHeight, popupResId);
            } else {
                if (modifier) return null; // Space, Return etc.
            }
        }

        if ((LatinIME.sKeyboardSettings.popupKeyboardFlags & POPUP_DISABLE) != 0) return null;

        String popup = getPopupKeyboardContent(keyboard.isShiftCaps(), keyboard.isShifted(isSimpleUppercase), true);
        //Log.i(TAG, "getPopupKeyboard: popup='" + popup + "' for " + this);
        if (popup.length() > 0) {
            int resId = popupResId;
            if (resId == 0) resId = R.xml.kbd_popup_template;
            return new KeyboardConfig(context, keyboard.mDefaultHeight, resId, popup, popupReversed, -1, padding);
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
                ((edgeFlags & KeyboardConfig.EDGE_LEFT) != 0 ? "L" : "-") +
                ((edgeFlags & KeyboardConfig.EDGE_RIGHT) != 0 ? "R" : "-") +
                ((edgeFlags & KeyboardConfig.EDGE_TOP) != 0 ? "T" : "-") +
                ((edgeFlags & KeyboardConfig.EDGE_BOTTOM) != 0 ? "B" : "-"));
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