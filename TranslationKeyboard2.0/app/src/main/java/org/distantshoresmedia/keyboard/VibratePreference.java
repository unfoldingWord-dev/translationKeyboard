package org.distantshoresmedia.keyboard;

import android.content.Context;
import android.util.AttributeSet;

public class VibratePreference extends SeekBarPreferenceString {
    public VibratePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void onChange(float val) {
        TKIME ime = TKIME.sInstance;
        if (ime != null) ime.vibrate((int) val);
    }
}