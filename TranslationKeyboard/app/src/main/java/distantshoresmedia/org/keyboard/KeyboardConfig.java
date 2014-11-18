/*
 * Copyright (C) 2008-2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package distantshoresmedia.org.keyboard;

import org.distantshoresmedia.translationkeyboard.R;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Loads an XML description of a keyboardConfig and stores the attributes of the keys. A keyboardConfig
 * consists of rows of keys.
 * <p>The layout file for a keyboardConfig contains XML that looks like the following snippet:</p>
 * <pre>
 * &lt;KeyboardConfig
 *         android:keyWidth="%10p"
 *         android:keyHeight="50px"
 *         android:horizontalGap="2px"
 *         android:verticalGap="2px" &gt;
 *     &lt;KeyboardRowConfig android:keyWidth="32px" &gt;
 *         &lt;KeyboardKeyConfig android:keyLabel="A" /&gt;
 *         ...
 *     &lt;/KeyboardRowConfig&gt;
 *     ...
 * &lt;/KeyboardConfig&gt;
 * </pre>
 * @attr ref android.R.styleable#Keyboard_keyWidth
 * @attr ref android.R.styleable#Keyboard_keyHeight
 * @attr ref android.R.styleable#Keyboard_horizontalGap
 * @attr ref android.R.styleable#Keyboard_verticalGap
 */
public class KeyboardConfig {

    private KeySizeOptions keyOptions;

    static final String TAG = "KeyboardConfig";

    // TODO This can't be a very good way of doing this.
    public final static char DEAD_KEY_PLACEHOLDER = 0x25cc; // dotted small circle
    public final static String DEAD_KEY_PLACEHOLDER_STRING = Character.toString(DEAD_KEY_PLACEHOLDER);

    // KeyboardConfig XML Tags
    private static final String TAG_KEYBOARD = "KeyboardConfig";
    private static final String TAG_ROW = "KeyboardRowConfig";
    private static final String TAG_KEY = "KeyboardKeyConfig";

    // TODO this needs to be made better.
    public static final int EDGE_LEFT = 0x01;
    public static final int EDGE_RIGHT = 0x02;
    public static final int EDGE_TOP = 0x04;
    public static final int EDGE_BOTTOM = 0x08;

    public static final int KEYCODE_SHIFT = -1;
    public static final int KEYCODE_MODE_CHANGE = -2;
    public static final int KEYCODE_CANCEL = -3;
    public static final int KEYCODE_DONE = -4;
    public static final int KEYCODE_DELETE = -5;
    public static final int KEYCODE_ALT_SYM = -6;
    // end TODO

    // TODO This needs to be gotten rid of.
    // Backwards compatible setting to avoid having to change all the kbd_qwerty files
    public static final int DEFAULT_LAYOUT_ROWS = 4;
    public static final int DEFAULT_LAYOUT_COLUMNS = 10;

    // Flag values for popup key contents. Keep in sync with strings.xml values.
    public static final int POPUP_ADD_SHIFT = 1; 
    public static final int POPUP_ADD_CASE = 2; 
    public static final int POPUP_ADD_SELF = 4; 
    public static final int POPUP_DISABLE = 256; 
    public static final int POPUP_AUTOREPEAT = 512; 

    private KeySizeOptions keyOptions;

    /** Total height of the keyboardConfig, including the padding and keys */
    private int height;

    /**
     * Total width of the keyboardConfig, including left side gaps and keys, but not any gaps on the
     * right side.
     */
    private int width;

    /** List of rows in this keyboardConfig */
    private List<KeyboardRowConfig> rows;

    // TODO I'm thinking this can be done better, but want to hold off.
    // Variables for pre-computing nearest keys.
    private int mCellWidth;
    private int mCellHeight;
    private int[][] mGridNeighbors;
    private int mProximityThreshold;
    /** Number of key widths from current touch point to search for nearest keys. */
    private static float SEARCH_DISTANCE = 1.8f;


    /**
     * Creates a keyboardConfig from the given xml key layout file.
     * @param context the application or service context
     * @param xmlLayoutResId the resource file that contains the keyboardConfig layout and keys.
     */
    public KeyboardConfig(Context context, int defaultHeight, int xmlLayoutResId) {
        this(context, defaultHeight, xmlLayoutResId, 0);
    }

    public KeyboardConfig(Context context, int defaultHeight, int xmlLayoutResId, int modeId) {
        this(context, defaultHeight, xmlLayoutResId, modeId, 0);
    }

    /**
     * Creates a keyboardConfig from the given xml key layout file. Weeds out rows
     * that have a keyboardConfig mode defined but don't match the specified mode.
     * @param context the application or service context
     * @param xmlLayoutResId the resource file that contains the keyboardConfig layout and keys.
     * @param modeId keyboardConfig mode identifier
     */
    public KeyboardConfig(Context context, int xmlLayoutResId, int modeId, KeySizeOptions keyOptions) {

        this.keyOptions = keyOptions;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
    }

    /**
     * <p>Creates a blank keyboardConfig from the given resource file and populates it with the specified
     * characters in left-to-right, top-to-bottom fashion, using the specified number of columns.
     * </p>
     * <p>If the specified number of columns is -1, then the keyboardConfig will fit as many keys as
     * possible in each row.</p>
     * @param context the application or service context
     * @param layoutTemplateResId the layout template file, containing no keys.
     * @param characters the list of characters to display on the keyboardConfig. One key will be created
     * for each character.
     * @param columns the number of columns of keys to display. If this number is greater than the
     * number of keys that can fit in a row, it will be ignored. If this number is -1, the
     * keyboardConfig will fit as many keys as possible in each row.
     */
    private KeyboardConfig(Context context, int defaultHeight, int layoutTemplateResId,
                           CharSequence characters, boolean reversed, int columns, int horizontalPadding) {
        this(context, defaultHeight, layoutTemplateResId);
        int x = 0;
        int y = 0;
        int column = 0;
        mTotalWidth = 0;

        Row row = new Row(this);
        row.defaultHeight = mDefaultHeight;
        row.defaultWidth = mDefaultWidth;
        row.defaultHorizontalGap = mDefaultHorizontalGap;
        row.verticalGap = mDefaultVerticalGap;
        final int maxColumns = columns == -1 ? Integer.MAX_VALUE : columns;
        mLayoutRows = 1;
        int start = reversed ? characters.length()-1 : 0;
        int end = reversed ? -1 : characters.length();
        int step = reversed ? -1 : 1;
        for (int i = start; i != end; i+=step) {
            char c = characters.charAt(i);
            if (column >= maxColumns
                    || x + mDefaultWidth + horizontalPadding > mDisplayWidth) {
                x = 0;
                y += mDefaultVerticalGap + mDefaultHeight;
                column = 0;
                ++mLayoutRows;
            }
            final Key key = new Key(row);
            key.x = x;
            key.realX = x;
            key.y = y;
            key.label = String.valueOf(c);
            key.codes = key.getFromString(key.label);
            column++;
            x += key.width + key.gap;
            mKeys.add(key);
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
        }
        mTotalHeight = y + mDefaultHeight;
        mLayoutColumns = columns == -1 ? column : maxColumns;
        setEdgeFlags();
    }

    private void fixAltChars(Locale locale) {
        if (locale == null) locale = Locale.getDefault();
        Set<Character> mainKeys = new HashSet<Character>();
        for (Key key : mKeys) {
            // Remember characters on the main keyboardConfig so that they can be removed from popups.
            // This makes it easy to share popup char maps between the normal and shifted
            // keyboards.
            if (key.label != null && !key.modifier && key.label.length() == 1) {
                char c = key.label.charAt(0);
                mainKeys.add(c);
            }
        }

    // TODO The shift state methods probably needs to stay, but will need to be changed to something more genaric.
    public boolean setShiftState(int shiftState, boolean updateKey) {
        //Log.i(TAG, "setShiftState " + mShiftState + " -> " + shiftState);
        if (updateKey && mShiftKey != null) {
            mShiftKey.on = (shiftState != SHIFT_OFF);
        }
        if (mShiftState != shiftState) {
            mShiftState = shiftState;
            return true;
        }
        return false;
    }

    public boolean setShiftState(int shiftState) {
        return setShiftState(shiftState, true);
    }


    // TODO This is one way to do it, but I'm thinking it may be better to have each key be aware of it's neighbors.
    private void computeNearestNeighbors() {
        // Round-up so we don't have any pixels outside the grid
        mCellWidth = (getMinWidth() + mLayoutColumns - 1) / mLayoutColumns;
        mCellHeight = (getHeight() + mLayoutRows - 1) / mLayoutRows;
        mGridNeighbors = new int[mLayoutColumns * mLayoutRows][];
        int[] indices = new int[mKeys.size()];
        final int gridWidth = mLayoutColumns * mCellWidth;
        final int gridHeight = mLayoutRows * mCellHeight;
        for (int x = 0; x < gridWidth; x += mCellWidth) {
            for (int y = 0; y < gridHeight; y += mCellHeight) {
                int count = 0;
                for (int i = 0; i < mKeys.size(); i++) {
                    final Key key = mKeys.get(i);
                    boolean isSpace = key.codes != null && key.codes.length > 0 &&
                    		key.codes[0] == LatinIME.ASCII_SPACE;
                    if (key.squaredDistanceFrom(x, y) < mProximityThreshold ||
                            key.squaredDistanceFrom(x + mCellWidth - 1, y) < mProximityThreshold ||
                            key.squaredDistanceFrom(x + mCellWidth - 1, y + mCellHeight - 1)
                                < mProximityThreshold ||
                            key.squaredDistanceFrom(x, y + mCellHeight - 1) < mProximityThreshold ||
                            isSpace && !(
                            		x + mCellWidth - 1 < key.x ||
                            		x > key.x + key.width ||
                            		y + mCellHeight - 1 < key.y ||
                            		y > key.y + key.height)) {
                    	//if (isSpace) Log.i(TAG, "space at grid" + x + "," + y);
                        indices[count++] = i;
                    }
                }
                int [] cell = new int[count];
                System.arraycopy(indices, 0, cell, 0, count);
                mGridNeighbors[(y / mCellHeight) * mLayoutColumns + (x / mCellWidth)] = cell;
            }
        }
    }

    /**
     * Returns the indices of the keys that are closest to the given point.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the array of integer indices for the nearest keys to the given point. If the given
     * point is out of range, then an array of size zero is returned.
     */
    public int[] getNearestKeys(int x, int y) {
        if (mGridNeighbors == null) computeNearestNeighbors();
        if (x >= 0 && x < getMinWidth() && y >= 0 && y < getHeight()) {
            int index = (y / mCellHeight) * mLayoutColumns + (x / mCellWidth);
            if (index < mLayoutRows * mLayoutColumns) {
                return mGridNeighbors[index];
            }
        }
        return new int[0];
    }

    private void loadKeyboard(Context context, XmlResourceParser parser) {
        boolean inKey = false;
        boolean inRow = false;
        float x = 0;
        int y = 0;
        Key key = null;
        Row currentRow = null;
        Resources res = context.getResources();
        boolean skipRow = false;
        mRowCount = 0;

        try {
            int event;
            Key prevKey = null;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_ROW.equals(tag)) {
                        inRow = true;
                        x = 0;
                        currentRow = createRowFromXml(res, parser);
                        skipRow = currentRow.mode != 0 && currentRow.mode != mKeyboardMode;
                        if (currentRow.extension) {
                            if (mUseExtension) {
                                ++mExtensionRowCount;
                            } else {
                                skipRow = true;
                            }
                        }
                        if (skipRow) {
                            skipToEndOfRow(parser);
                            inRow = false;
                        }
                   } else if (TAG_KEY.equals(tag)) {
                        inKey = true;
                        key = createKeyFromXml(res, currentRow, Math.round(x), y, parser);
                        key.realX = x;
                        if (key.codes == null) {
                          // skip this key, adding its width to the previous one
                          if (prevKey != null) {
                              prevKey.width += key.width;
                          }
                        } else {
                          mKeys.add(key);
                          prevKey = key;
                          if (key.codes[0] == KEYCODE_SHIFT) {
                              if (mShiftKeyIndex == -1) {
                                  mShiftKey = key;
                                  mShiftKeyIndex = mKeys.size()-1;
                              }
                              mModifierKeys.add(key);
                          } else if (key.codes[0] == KEYCODE_ALT_SYM) {
                              mModifierKeys.add(key);
                          } else if (key.codes[0] == LatinKeyboardView.KEYCODE_CTRL_LEFT) {
                              mCtrlKey = key;
                          } else if (key.codes[0] == LatinKeyboardView.KEYCODE_ALT_LEFT) {
                              mAltKey = key;
                          } else if (key.codes[0] == LatinKeyboardView.KEYCODE_META_LEFT) {
                              mMetaKey = key;
                          }
                        }
                    } else if (TAG_KEYBOARD.equals(tag)) {
                        parseKeyboardAttributes(res, parser);
                    }
                } else if (event == XmlResourceParser.END_TAG) {
                    if (inKey) {
                        inKey = false;
                        x += key.realGap + key.realWidth;
                        if (x > mTotalWidth) {
                            mTotalWidth = Math.round(x);
                        }
                    } else if (inRow) {
                        inRow = false;
                        y += currentRow.verticalGap;
                        y += currentRow.defaultHeight;
                        mRowCount++;
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





    // TODO this will need to be changed.
    @Override
    public String toString() {
        return "KeyboardConfig(" + mLayoutColumns + "x" + mLayoutRows +
            " keys=" + mKeys.size() +
            " rowCount=" + mRowCount +
            " mode=" + mKeyboardMode +
            " size=" + mTotalWidth + "x" + mTotalHeight +
            ")";

    }
}
