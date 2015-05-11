package org.distantshoresmedia.keyboard;

import android.view.inputmethod.EditorInfo;

/**
 * Created by Fechner on 12/22/14.
 */
public interface ComposeSequencing {
    public void onText(CharSequence text);
    public void updateShiftKeyState(EditorInfo attr);
    public EditorInfo getCurrentInputEditorInfo();
}