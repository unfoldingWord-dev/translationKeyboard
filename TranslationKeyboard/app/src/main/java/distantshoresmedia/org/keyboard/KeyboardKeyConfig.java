package distantshoresmedia.org.keyboard;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;

import java.util.StringTokenizer;

import distantshoresmedia.org.model.KeyPosition;
import distantshoresmedia.org.translationkeyboard.R;

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

    /** Options for keeping track of screen orientation*/
    private KeySizeOptions keyOptions;

    /**
     * All the key codes (unicode or custom code) that this key could generate,
     * organized in the same way as the below lists
     */
    public int[] codes;

    /** Label text to display */
    public CharSequence[][] labelCharacterList;

    /** Actual text to write when key is pressed **/
    public CharSequence[][] textCharacterList;

    /** Icon to display instead of a label. Icon takes precedence over a label */
    public Drawable icon;
    /** Preview version of the icon, for the preview popup */
    public Drawable iconPreview;

    /** The current state of this key */
    public KeyState state;

    /** The Type of key */
    public KeyType keyType;

    /**
     * If this key pops up a mini keyboardConfig, this is the resource id for the XML layout for that
     * keyboardConfig.
     */
    public int popupResId;
    /** Whether this key repeats itself when held down */
    public boolean repeatable;
    /** Is the shifted character the uppercase equivalent of the unshifted one? */


    /** Create an empty key with no attributes. */
    public KeyboardKeyConfig(KeySizeOptions options) {

        this.keyOptions = options;
    }

    /** Create a key with the given options Data and options
     * @param res resources associated with the caller's context
     */
    public KeyboardKeyConfig(Resources res, KeySizeOptions options, KeyPosition key, XmlResourceParser parser) {
        this(options);
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


    private String getKeyStringForState(KeyState state) {
        int stateNumber = KeyState.getNumberForKeyState(state);

        if(stateNumber < 1){
            stateNumber = 0;
        }
        String labelString = labelCharacterList[stateNumber].toString();
        return labelString;
    }

    public String getHintLabel(boolean wantAscii, boolean wantAll) {
        return getKeyStringForState(KeyState.SHIFT);
    }

    public String getAltHintLabel(boolean wantAscii, boolean wantAll) {
        String hint = getKeyStringForState(KeyState.LONG_PRESSED);
        return (hint.length() > 0)? hint.substring(0, 1) : "";
    }



    /**
     * Informs the key that it has been pressed, in case it needs to change its appearance or
     * state.
     * @see #onReleased(boolean)
     */
    public void onPressed() {
       this.state = KeyState.PRESSED;
    }

    /**
     * Changes the pressed state of the key. Sticky key indicators are handled explicitly elsewhere.
     * @param inside whether the finger was released inside the key
     * @see #onPressed()
     */
    public void onReleased(boolean inside) {
        this.state = KeyState.UNPRESSED;
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
                Log.e(this.getClass().toString(), "Error parsing keycodes " + value);
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

        return this.keyOptions.isInside(x, y);
    }

    /**
     * Returns the square of the distance between the center of the key and the given point.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the square of the distance of the point from the center of the key
     */
    public int squaredDistanceFrom(int x, int y) {
        int xDist = Math.round(this.keyOptions.x + this.keyOptions.centerX());
        int yDist = Math.round(this.keyOptions.y + this.keyOptions.centerY());
        return xDist * xDist + yDist * yDist;
    }

    /**
     * Returns the drawable state for the key, based on the current state and type of the key.
     * @return the drawable state of the key.
     * @see android.graphics.drawable.StateListDrawable#setState(int[])
     */
    public int[] getCurrentDrawableState() {
        return KeyboardHelper.getButtonStateForKeyState(this.state, this.keyType);
    }

    public String toString() {

        String characters = "";
        String codeString = "[";

        for (CharSequence[] sequence : this.textCharacterList) {
            characters = characters + " [" +sequence.toString() + "]";
        }

        for(int i : this.codes){
            codeString = codeString + " " + i;
        }
        codeString = codeString + "]";

        return this.getClass().toString() + "keyOptions: " + this.keyOptions.toString() + " characters: " + characters + " Codes: " + codeString;

    }
}