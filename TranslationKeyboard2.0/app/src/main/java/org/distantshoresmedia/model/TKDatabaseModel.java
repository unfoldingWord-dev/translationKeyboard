package org.distantshoresmedia.model;

import org.distantshoresmedia.model.daoModels.DaoSession;
import org.json.JSONObject;

/**
 * Created by Fechner on 6/17/15.
 */
public abstract class TKDatabaseModel {

    abstract public long getUId();
    abstract public TKDatabaseModel setupModelFromJson(JSONObject json);
    abstract public TKDatabaseModel setupModelFromJson(JSONObject json, TKDatabaseModel parent);
    abstract public boolean updateWithModel(TKDatabaseModel newModel);
    abstract public void insertModel(DaoSession session);
}
