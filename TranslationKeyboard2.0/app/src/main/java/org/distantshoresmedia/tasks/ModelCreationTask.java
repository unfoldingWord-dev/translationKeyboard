package org.distantshoresmedia.tasks;


import android.os.AsyncTask;

import org.distantshoresmedia.model.TKDatabaseModel;
import org.json.JSONObject;

/**
 * Created by Fechner on 6/17/15.
 */
public class ModelCreationTask extends AsyncTask<JSONObject, Void, TKDatabaseModel> {

    private static final String TAG = "ModelCreationTask";

    private ModelCreationTaskListener listener;
    private final TKDatabaseModel dbModel;
    private final TKDatabaseModel parentOrNull;

    public ModelCreationTask(TKDatabaseModel dbModel, TKDatabaseModel parentSlugOrNull, ModelCreationTaskListener listener) {

        this.listener = listener;

        this.dbModel = (dbModel != null)? dbModel : null;
        this.parentOrNull = (parentSlugOrNull != null)? parentSlugOrNull : null;
    }

    @Override
    protected TKDatabaseModel doInBackground(JSONObject... params) {

        if(parentOrNull != null){
            TKDatabaseModel finalModel = dbModel.setupModelFromJson(params[0], parentOrNull);
            return finalModel;
        }
        else {
            TKDatabaseModel finalModel = dbModel.setupModelFromJson(params[0]);
            return finalModel;
        }
    }

    @Override
    protected void onPostExecute(TKDatabaseModel TKDatabaseModel) {
        super.onPostExecute(TKDatabaseModel);
        listener.modelWasCreated(TKDatabaseModel);
    }

    interface ModelCreationTaskListener {
        void modelWasCreated(TKDatabaseModel model);
    }
}
