package org.distantshoresmedia.sideloading;

import android.content.Context;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Fechner on 7/9/15.
 */
public class SideLoadingDataPreparer {

    public static JSONObject getSideLoadingJson(Context context, List<AvailableKeyboard> keyboards){

        JSONArray jsonArray = new JSONArray();

        for(AvailableKeyboard keyboard : keyboards) {

            BaseKeyboard keyboardModel = KeyboardDatabaseHandler.getKeyboardWithID(Integer.toString(keyboard.id));

            if (keyboardModel != null) {

                jsonArray.put(getModelsAsJson(keyboard, keyboardModel));
            }
        }
        JSONObject finalObject = new JSONObject();

        try {
            finalObject.put("keyboards", jsonArray);
            return finalObject;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }


    }

    private static JSONObject getModelsAsJson(AvailableKeyboard parentKeyboard, BaseKeyboard baseKeyboard){

        try{
            JSONObject keyboard = parentKeyboard.getAsJson();
            keyboard.put("keyboards", baseKeyboard.getAsJson());
            return keyboard;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
