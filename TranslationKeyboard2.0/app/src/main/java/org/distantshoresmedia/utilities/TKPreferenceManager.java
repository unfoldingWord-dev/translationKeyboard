package org.distantshoresmedia.utilities;

import android.content.Context;
import android.preference.PreferenceManager;

import org.distantshoresmedia.translationkeyboard20.R;

/**
 * Created by Fechner on 3/25/15.
 */
public class TKPreferenceManager {

    private static final String LAST_UPDATED_ID = "last_updated_date";
    public static double getLastUpdatedDate(Context context){
        double updated =  Double.longBitsToDouble(PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_ID, Double.doubleToRawLongBits(0)));
        return updated;
    }
    public static void setLastUpdatedDate(Context context, double newValue){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATED_ID, Double.doubleToRawLongBits(newValue)).commit();
    }

    private static final String DATA_DOWNLOAD_URL_KEY = "base_url";
    public static String getDataDownloadUrl(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(DATA_DOWNLOAD_URL_KEY, context.getResources().getString(R.string.pref_default_base_url));
    }
    public static void setDataDownloadUrl(Context context, String newValue){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DATA_DOWNLOAD_URL_KEY, newValue).commit();
    }
}
