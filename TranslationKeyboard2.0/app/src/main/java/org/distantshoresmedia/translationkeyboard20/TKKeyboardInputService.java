package org.distantshoresmedia.translationkeyboard20;

import android.inputmethodservice.Keyboard;

import org.distantshoresmedia.basickeyboard.KeyboardInputService;

/**
 * Created by Fechner on 11/25/14.
 */
public class TKKeyboardInputService extends KeyboardInputService {

    private TKKeyboard mSymbolsKeyboard;
    private TKKeyboard mSymbolsShiftedKeyboard;
    private TKKeyboard mQwertyKeyboard;

    private TKKeyboard mCurKeyboard;
    private TKKeyboardView mInputView;

    private boolean mCapsLock;
    private long mLastShiftTime;

    private void handleShift() {

        System.out.println("Handle Shift");
        if (mInputView == null) {
            return;
        }

        TKKeyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            mInputView.setKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            mInputView.setKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 500 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }
}
