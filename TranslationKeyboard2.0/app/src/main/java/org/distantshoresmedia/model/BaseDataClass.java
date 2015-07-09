package org.distantshoresmedia.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by Fechner on 12/19/14.
 */
public class BaseDataClass implements Serializable{

    static final private String kUpdatedKey = "updated_at";
    private static final String TAG = "org.distantshoresmedia.model.translationkeyboard20";

    protected long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    protected double updated = -1;
    public double getUpdated() {
        return updated;
    }
    public void setUpdated(double updated) {
        this.updated = updated;
    }

    public BaseDataClass(long id, double updated) {
        this.id = id;
        this.updated = updated;
    }

    static public double getUpdatedTimeFromJSONString(String json){

        try {
            JSONObject jObject = new JSONObject(json);
            double time = jObject.getDouble(kUpdatedKey);

            return time;
        }
        catch (JSONException e){
            Log.e(TAG, "getUpdatedTimeFromJSONString JSONException: " + e.toString() + " json: " + json);
            return 140000000.3473752;
        }
    }
}
