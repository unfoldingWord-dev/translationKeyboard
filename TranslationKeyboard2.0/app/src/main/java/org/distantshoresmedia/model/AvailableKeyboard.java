package org.distantshoresmedia.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Fechner on 12/18/14.
 */
public class AvailableKeyboard extends BaseDataClass{

    static final private String kKeyboardKey = "keyboards";
    static final private String kIdKey = "id";
    static final private String kIsoLanguageKey = "isoLanguage";
    static final private String kIsoRegionKey = "isoRegion";
    static final private String kLanguageNameKey = "language_name";
    static final private String kUpdatedKey = "updated_at";



    private String isoLanguage;

    public String getIsoLanguage() {
        return isoLanguage;
    }

    public void setIsoLanguage(String isoLanguage) {
        this.isoLanguage = isoLanguage;
    }

    private String isoRegion;

    public String getIsoRegion() {
        return isoRegion;
    }

    public void setIsoRegion(String isoRegion) {
        this.isoRegion = isoRegion;
    }

    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String language) {
        this.languageName = language;
    }


    public AvailableKeyboard(Date updated, int id, String isoLanguage, String isoRegion, String language) {
        super(id, updated);
        this.isoLanguage = isoLanguage;
        this.isoRegion = isoRegion;
        this.languageName = language;
    }

    public String getObjectAsJSONString(){

        String jsonString = "{\n" + kIdKey + ":" + this.id + ",\n"
                + kIsoLanguageKey + ":\"" + this.isoLanguage + "\",\n"
                + kIsoRegionKey + ":\"" + this.isoRegion + "\",\n"
                + kLanguageNameKey + ":\"" + this.languageName + "\",\n"
                + kUpdatedKey + ":" + this.updated + ",\n},";

        return jsonString;
    }
    @Override
    public String toString() {
        return "AvailableKeyboard{" +
                "id=" + id +
                ", isoLanguage='" + isoLanguage + '\'' +
                ", isoRegion='" + isoRegion + '\'' +
                ", language_name='" + languageName + '\'' +
                ", updated=" + updated +
                '}';


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


                AvailableKeyboard newKeyboard = new AvailableKeyboard(new Date(Math.round(updated)), id, isoLanguage, isoRegion, language);
                keyboardObjects[i] = newKeyboard;
            }

            return keyboardObjects;
        } catch (JSONException e) {
            System.out.println("KeyboardVariant JSONException: " + e.toString());
            return null;
        }
    }

}
