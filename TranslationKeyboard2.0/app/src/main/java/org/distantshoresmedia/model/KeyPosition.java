/* Generated by JavaFromJSON */
/*http://javafromjson.dashingrocket.com*/

package org.distantshoresmedia.model;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class KeyPosition {

    private static final String TAG = "KeyPosition";

    static final private String kPercentWidthKey = "percent_width";
    static final private String kCharactersKey = "characters";

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    private double percentWidth;
    public void setPercentWidth(double percentWidth) {
        this.percentWidth = percentWidth;
    }
    public double getPercentWidth() {
        return percentWidth;
    }

	private KeyCharacter[] characters;
 	public void setCharacters(KeyCharacter[] characters) {
		this.characters = characters;
	}
	public KeyCharacter[] getCharacters() {
		return characters;
	}
    public KeyCharacter getCharacterAtIndex(int i){
        return characters[i];
    }

    private int row;
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }

    private int column;
    public void setColumn(int columns) {
        this.column = column;
    }
    public int getColumn() {
        return column;
    }

    public KeyPosition(double percentWidth, KeyCharacter[] characters, int row, int column){
        this.percentWidth = percentWidth;
        this.characters = characters;
        this.row = row;
        this.column = column;
    }


    static public KeyPosition getKeyboardFromJsonObject(JSONObject jsonObj, int row, int column){

//        Log.i(TAG, "Got to KeyPosition");

        try {
            double width = jsonObj.getDouble(kPercentWidthKey);

            JSONArray jsonChars = jsonObj.getJSONArray(kCharactersKey);

            KeyCharacter[] characters = new KeyCharacter[jsonChars.length()];

            for(int i = 0; i < jsonChars.length(); i++){

                JSONObject character = jsonChars.getJSONObject(i);
                characters[i] = KeyCharacter.getCharacterFromJsonObject(character);
            }

            KeyPosition position = new KeyPosition(width, characters, row, column);
            return position;
        }

        catch (JSONException e) {
            Log.e(TAG, "KeyPosition JSONException: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getAsJson() {

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(kPercentWidthKey, percentWidth);
            jsonObject.put(kCharactersKey, getCharactersJson());
            return jsonObject;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray getCharactersJson(){

        JSONArray jsonArray = new JSONArray();

        for(KeyCharacter character : getCharacters()){

            jsonArray.put(character.getAsJson());
        }

        return jsonArray;
    }

    @Override
    public String toString() {
        return "KeyPosition{" +
                "percentWidth=" + percentWidth +
                ", characters=" + Arrays.toString(characters) +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}