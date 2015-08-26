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

package org.distantshoresmedia.keyboard;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.translationkeyboard20.R;

public class InputLanguageSelection extends PreferenceActivity {
    private static final String TAG = "InputLanguageSelection";
    private List<AvailableKeyboard> mAvailableKeyboards = new ArrayList<AvailableKeyboard>();
    private static final String[] BLACKLIST_LANGUAGES = {

    };

    // Languages for which auto-caps should be disabled
    public static final Set<String> NOCAPS_LANGUAGES = new HashSet<String>();
    static {

    }

    // Languages which should not use dead key logic. The modifier is entered after the base character.
    public static final Set<String> NODEADKEY_LANGUAGES = new HashSet<String>();
    static {

    }

    // Languages which should not auto-add space after completions
    public static final Set<String> NOAUTOSPACE_LANGUAGES = new HashSet<String>();
    static {

    }

    // Run the GetLanguages.sh script to update the following lists based on
    // the available keyboard resources and dictionaries.
    private static final String[] KBD_LOCALIZATIONS = {
        "en"
    };

    private static final String[] KBD_5_ROW = {
        "en"
    };

    private static final String[] KBD_4_ROW = {
        "en"
    };

    private static String getLocaleName(Locale l) {
        String lang = l.getLanguage();
        String country = l.getCountry();

        return LanguageSwitcher.toTitleCase(l.getDisplayName(l));

    }
    
    private static class Loc implements Comparable<Object> {
        static Collator sCollator = Collator.getInstance();

        String label;
        Locale locale;

        public Loc(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        @Override
        public String toString() {
            return this.label;
        }

        public int compareTo(Object o) {
            return sCollator.compare(this.label, ((Loc) o).label);
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.language_prefs);
        // Get the settings preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLanguagePref = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, "");
        Log.i(TAG, "selected languages: " + selectedLanguagePref);
        String[] languageList = selectedLanguagePref.split(",");
        
        mAvailableKeyboards = getUniqueLocales();

        // Compatibility hack for v1.22 and older - if a selected language 5-code isn't
        // found in the current list of available languages, try adding the 2-letter
        // language code. For example, "en_US" is no longer listed, so use "en" instead.
        Set<String> availableLanguages = new HashSet<String>();
        for (int i = 0; i < mAvailableKeyboards.size(); i++) {
            Locale locale = getLocFromKeyboard(mAvailableKeyboards.get(i)).locale;
            availableLanguages.add(getCode(locale));
        }

        AvailableKeyboard[] keyboards = KeyboardDatabaseHandler.getInstalledKeyboards();


        Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

        for(AvailableKeyboard keyboard : keyboards){
            keyboardsDictionary.put( getCode(keyboard.getKeyboardAsLocale()), keyboard);
        }

        PreferenceGroup parent = getPreferenceScreen();
        for (int i = 0; i < mAvailableKeyboards.size(); i++) {
            CheckBoxPreference pref = new CheckBoxPreference(this);
            Locale locale = getLocFromKeyboard(mAvailableKeyboards.get(i)).locale;
            AvailableKeyboard availKey = mAvailableKeyboards.get(i);
            BaseKeyboard keyboard = KeyboardDatabaseHandler.getKeyboardWithID(Integer.toString(availKey.id));
            CharSequence name = keyboard.getKeyboardVariants()[0].getName();
            pref.setTitle(name + " [" + locale.toString() + "]");
            String fiveCode = getCode(locale);
//            String language = locale.getLanguage();
//            boolean checked = languageSelections.contains(fivecode);
            boolean downloaded = KeyboardDatabaseHandler.hasDownloadedKeyboard(availKey);
            boolean checked = KeyboardDatabaseHandler.hasInstalledKeyboard(availKey);
            pref.setChecked(checked);
//            boolean has4Row = arrayContains(KBD_4_ROW, fivecode) || arrayContains(KBD_4_ROW, language);
//            boolean has5Row = arrayContains(KBD_5_ROW, fivecode) || arrayContains(KBD_5_ROW, language);
            List<String> summaries = new ArrayList<String>(3);
//            if (has5Row) summaries.add("5-row");
//            if (has4Row) summaries.add("4-row");
//            if (hasDictionary(locale)) {
//            	summaries.add(getResources().getString(R.string.has_dictionary));
//            }
            if (!summaries.isEmpty()) {
            	StringBuilder summary = new StringBuilder();
            	for (int j = 0; j < summaries.size(); ++j) {
            		if (j > 0) summary.append(", ");
            		summary.append(summaries.get(j));
            	}
            	pref.setSummary(summary.toString());
            }
            parent.addPreference(pref);
        }
    }

    private boolean hasDictionary(Locale locale) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale saveLocale = conf.locale;
        boolean haveDictionary = false;
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());

        int[] dictionaries = LatinIME.getDictionary(res);
        BinaryDictionary bd = new BinaryDictionary(this, dictionaries, Suggest.DIC_MAIN);

        // Is the dictionary larger than a placeholder? Arbitrarily chose a lower limit of
        // 4000-5000 words, whereas the LARGE_DICTIONARY is about 20000+ words.
        if (bd.getSize() > Suggest.LARGE_DICTIONARY_THRESHOLD / 4) {
            haveDictionary = true;
        } else {
            BinaryDictionary plug = PluginManager.getDictionary(getApplicationContext(), locale.getLanguage());
            if (plug != null) {
                bd.close();
                bd = plug;
                haveDictionary = true;
            }
        }

        bd.close();
        conf.locale = saveLocale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
        return haveDictionary;
    }

    private String getCode(Locale locale) {

        String codeString = locale.getLanguage();

        String country = locale.getCountry();
        if(! TextUtils.isEmpty(country)){
            codeString = codeString + "_" + country;
        }
        String variant = locale.getVariant();
        if(! TextUtils.isEmpty(variant)){
            codeString = codeString + "_" + variant;
        }

        return codeString;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AvailableKeyboard[] keyboards = KeyboardDatabaseHandler.getInstalledKeyboards();

        Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

        for(AvailableKeyboard keyboard : keyboards){
            Locale keyboardLocal = keyboard.getKeyboardAsLocale();
            String keyboardKey = keyboardLocal.getDisplayName();
            keyboardsDictionary.put(keyboardKey, keyboard);
        }

        // Save the selected languages
        String checkedLanguages = "";
        PreferenceGroup parent = getPreferenceScreen();
        int count = parent.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            CheckBoxPreference pref = (CheckBoxPreference) parent.getPreference(i);

            Locale locale = getLocFromKeyboard(mAvailableKeyboards.get(i)).locale;
            if (pref.isChecked()) {
                checkedLanguages += getCode(locale) + ",";
            }

            String desiredKey = locale.getDisplayName();
            AvailableKeyboard localKeyboard = mAvailableKeyboards.get(i);
            KeyboardDatabaseHandler.installedKeyboardHasState(localKeyboard, pref.isChecked());
        }
        if (checkedLanguages.length() < 1) checkedLanguages = null; // Save null
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(LatinIME.PREF_SELECTED_LANGUAGES, checkedLanguages);
        SharedPreferencesCompat.apply(editor);
    }

    private Loc getLocFromKeyboard(AvailableKeyboard keyboard){

        return new Loc(keyboard.getLanguageName(), keyboard.getKeyboardAsLocale());
    }

    private static String asString(Set<String> set) {
    	StringBuilder out = new StringBuilder();
    	out.append("set(");
    	String[] parts = new String[set.size()];
    	parts = set.toArray(parts);
        Arrays.sort(parts);
        for (int i = 0; i < parts.length; ++i) {
    		if (i > 0) out.append(", ");
    		out.append(parts[i]);
    	}
    	out.append(")");
    	return out.toString();
    }



    private List<AvailableKeyboard> getUniqueLocales() {
//        Set<String> localeSet = new HashSet<String>();
//        Set<String> langSet = new HashSet<String>();
//
//        ArrayList<Loc> uniqueLocales = new ArrayList<Loc>();

        AvailableKeyboard[] keyboards = KeyboardDatabaseHandler.getInstalledKeyboards();

//        for(AvailableKeyboard keyboard : keyboards){
//
//            Loc newLoc = new Loc(keyboard.getLanguageName(), keyboard.getKeyboardAsLocale());
//            uniqueLocales.add(newLoc);
//        }

        return Arrays.asList(keyboards);
    }

    private boolean arrayContains(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
