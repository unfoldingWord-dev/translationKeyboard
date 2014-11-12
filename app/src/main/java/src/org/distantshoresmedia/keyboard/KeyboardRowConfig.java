package org.distantshoresmedia.keyboard;


import org.distantshoresmedia.keyboard.KeyboardConfig;

import org.distantshoresmedia.translationkeyboard.LatinIME;
import org.distantshoresmedia.translationkeyboard.R;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Xml;

public class KeyboardRowConfig {

    /** Default width of a key in this row. */
    public float defaultWidth;
    /** Default height of a key in this row. */
    public int defaultHeight;
    /** Default horizontal gap between keys in this row. */
    public float defaultHorizontalGap;
    /** Vertical gap following this row. */
    public int verticalGap;

    /** The keyboard mode for this row */
    public int mode;
    
    public boolean extension;

    private KeyboardConfig parent;

    public KeyboardRowConfig(KeyboardConfig parent) {
        this.parent = parent;
    }

    public KeyboardRowConfig(Resources res, KeyboardConfig parent, XmlResourceParser parser) {
        this.parent = parent;
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard);
        defaultWidth = getDimensionOrFraction(a,
                R.styleable.Keyboard_keyWidth,
                parent.mDisplayWidth, parent.mDefaultWidth);
        defaultHeight = Math.round(getDimensionOrFraction(a,
                R.styleable.Keyboard_keyHeight,
                parent.mDisplayHeight, parent.mDefaultHeight));
        defaultHorizontalGap = getDimensionOrFraction(a,
                R.styleable.Keyboard_horizontalGap,
                parent.mDisplayWidth, parent.mDefaultHorizontalGap);
        verticalGap = Math.round(getDimensionOrFraction(a,
                R.styleable.Keyboard_verticalGap,
                parent.mDisplayHeight, parent.mDefaultVerticalGap));
        a.recycle();
        a = res.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.Keyboard_Row);
        mode = a.getResourceId(R.styleable.Keyboard_Row_keyboardMode,
                0);
        extension = a.getBoolean(R.styleable.Keyboard_Row_extension, false);
        

        
        if (parent.mLayoutRows >= 5) {
            boolean isTop = (extension || parent.mRowCount - parent.mExtensionRowCount <= 0);
            float topScale = LatinIME.sKeyboardSettings.topRowScale;
            float scale = isTop ? topScale : 1.0f + (1.0f - topScale) / (parent.mLayoutRows - 1);
            defaultHeight = Math.round(defaultHeight * scale);
        }
        a.recycle();
    }
}