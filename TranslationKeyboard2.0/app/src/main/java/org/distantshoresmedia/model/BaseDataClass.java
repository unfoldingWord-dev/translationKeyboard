package org.distantshoresmedia.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/**
 * Created by Fechner on 12/19/14.
 */
public class BaseDataClass {

    static final private String kUpdatedKey = "updated_at";

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
            System.out.println("getUpdatedTimeFromJSONString JSONException: " + e.toString() + " json: " + json);
            return 140000000.3473752;
        }
    }
}
