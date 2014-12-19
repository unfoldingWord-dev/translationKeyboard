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

    protected Date updated;
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public BaseDataClass(long id, Date updated) {
        this.id = id;
        this.updated = updated;
    }

    static public double getUpdatedTimeFromJSONString(String json){

        try {
            JSONObject jObject = new JSONObject(json);
            double name = jObject.getDouble(kUpdatedKey);

            return name;
        }
        catch (JSONException e){
            System.out.println("getKeyboardNameFromJSONString JSONException: " + e.toString());
        }

        return -1;
    }
}
