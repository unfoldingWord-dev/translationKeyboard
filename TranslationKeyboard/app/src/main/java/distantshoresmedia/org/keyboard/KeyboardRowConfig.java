package distantshoresmedia.org.keyboard;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Xml;

import distantshoresmedia.org.model.KeyboardVariant;

/**
 * Container for keys in the keyboardConfig. All keys in a row are at the same Y-coordinate.
 * Some of the key size defaults can be overridden per row from what the {@link distantshoresmedia.org.keyboard.KeyboardConfig}
 * defines.
 * @attr ref android.R.styleable#Keyboard_keyWidth
 * @attr ref android.R.styleable#Keyboard_keyHeight
 * @attr ref android.R.styleable#Keyboard_horizontalGap
 * @attr ref android.R.styleable#Keyboard_verticalGap
 * @attr ref android.R.styleable#Keyboard_Row_keyboardMode
 */
public class KeyboardRowConfig {

    private KeySizeOptions keyOptions;

    private KeyboardVariant keysVariant;

    /** The keyboardConfig mode for this row */
    public int mode;

    public boolean extension;


    public KeyboardRowConfig(Resources res, XmlResourceParser parser, KeySizeOptions keyOptions, KeyboardVariant keysVariant) {

        this.keyOptions = keyOptions;
        this.keysVariant = keysVariant;

        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard);

        a.recycle();
        a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard_Row);
        mode = a.getResourceId(R.styleable.Keyboard_Row_keyboardMode,
                0);
        extension = a.getBoolean(R.styleable.Keyboard_Row_extension, false);

        // possibly un-neaded stuff
        /*
        if (parent.mLayoutRows >= 5) {
            boolean isTop = (extension || parent.mRowCount - parent.mExtensionRowCount <= 0);
            float topScale = LatinIME.sKeyboardSettings.topRowScale;
            float scale = isTop ? topScale : 1.0f + (1.0f - topScale) / (parent.mLayoutRows - 1);
            defaultHeight = Math.round(defaultHeight * scale);
        }*/
        a.recycle();
    }
}
