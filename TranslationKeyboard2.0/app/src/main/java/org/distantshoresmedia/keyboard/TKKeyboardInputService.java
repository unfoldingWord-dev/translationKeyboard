package org.distantshoresmedia.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import org.distantshoresmedia.keyboard.KeyboardInputService;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.model.KeyboardVariant;
import org.distantshoresmedia.translationkeyboard20.KeyboardDownloader;
import org.distantshoresmedia.translationkeyboard20.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fechner on 11/25/14.
 */
public class TKKeyboardInputService extends KeyboardInputService implements KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;

    private KeyboardVariant variant = null;

    private KeyboardVariant getVariant() {
        if(variant == null){
            BaseKeyboard desiredKeyboard = KeyboardDownloader.keyboards.get(0);
            variant = desiredKeyboard.getKeyboardVariants()[0];
        }
        return variant;
    }

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override public void onCreate() {
        super.onCreate();
    }

    @Override public View onCreateInputView() {

        createKeyboards();

        mInputView = (TKKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mQwertyKeyboard);
        return mInputView;

    }

    @Override public void onInitializeInterface() {

        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        createKeyboards();
    }

    private void createKeyboards(){

        mQwertyKeyboard = new Keyboard(this, R.xml.keyboard_template_5row, 0, getVariant(), 0);
        mSymbolsKeyboard = new Keyboard(this, R.xml.keyboard_template_5row, 0, getVariant(), 1);
    }
}


