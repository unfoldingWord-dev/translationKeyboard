package org.distantshoresmedia.database;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Fechner on 2/9/15.
 */
public class TKFontFinder {

    static final String TAG = "TKFontFinder";

    static public Typeface findTypefaceForLocal(Context context, String language){

//        Log.i(TAG, "Language: " + language);

        Typeface typeface;

        if(language.equalsIgnoreCase("my")){
//            Log.i(TAG, "selected bu");
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/bu.ttf");
        }
        else{
//            Log.i(TAG, "selected default");
            typeface = Typeface.DEFAULT;
        }

        return typeface;
    }
}
