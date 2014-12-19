package org.distantshoresmedia.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Fechner on 12/18/14.
 */
public class AvailableKeyboard extends BaseDataClass{

    static final private String kKeyboardKey = "keyboards";
    static final private String kIdKey = "id";
    static final private String kIsoLanguageKey = "iso_language";
    static final private String kIsoRegionKey = "iso_region";
    static final private String kLanguageNameKey = "language_name";
    static final private String kUpdatedKey = "updated_at";



    private String iso_language;

    public String getIso_language() {
        return iso_language;
    }

    public void setIso_language(String iso_language) {
        this.iso_language = iso_language;
    }

    private String iso_region;

    public String getIso_region() {
        return iso_region;
    }

    public void setIso_region(String iso_region) {
        this.iso_region = iso_region;
    }

    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String language) {
        this.languageName = language;
    }


    public AvailableKeyboard(double updated, int id, String iso_language, String iso_region, String language) {
        super(id, updated);
        this.iso_language = iso_language;
        this.iso_region = iso_region;
        this.languageName = language;
    }

    static public String getKeyboardNameFromJSONString(String json){

        try {
            JSONObject jObject = new JSONObject(json);
            String name = jObject.getString(kLanguageNameKey);

            return name;
        }
        catch (JSONException e){
            System.out.println("getKeyboardNameFromJSONString JSONException: " + e.toString());
        }

        return null;
    }


    static public AvailableKeyboard[] getKeyboardsFromJsonObject(JSONObject jsonObj) {

        System.out.println("Got to KeyboardVariant");

        try {

            // Get an arraylist of keypositions based on the JSON
            JSONArray keyboards = jsonObj.getJSONArray(kKeyboardKey);
            AvailableKeyboard[] keyboardObjects = new AvailableKeyboard[keyboards.length()];

            int total = 0;

            for (int i = 0; i < keyboards.length(); i++) {
                JSONObject rowObj = keyboards.getJSONObject(i);

                int id = jsonObj.getInt(kIdKey);
                String isoLanguage = jsonObj.getString(kIsoLanguageKey);
                String isoRegion = jsonObj.getString(kIsoRegionKey);
                String language = jsonObj.getString(kLanguageNameKey);
                double updated = jsonObj.getDouble(kUpdatedKey);

                AvailableKeyboard newKeyboard = new AvailableKeyboard(updated, id, isoLanguage, isoRegion, language);
                keyboardObjects[i] = newKeyboard;
            }

            return keyboardObjects;
        } catch (JSONException e) {
            System.out.println("KeyboardVariant JSONException: " + e.toString());
            return null;
        }
    }

    @Override
    public String toString() {
        return "AvailableKeyboard{" +
                "id=" + id +
                ", iso_language='" + iso_language + '\'' +
                ", iso_region='" + iso_region + '\'' +
                ", language_name='" + languageName + '\'' +
                ", updated=" + updated +
                '}';


    }

}
