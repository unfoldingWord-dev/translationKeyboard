package org.distantshoresmedia.tasks;


import android.content.Context;
import android.os.AsyncTask;

import org.distantshoresmedia.model.DaoDBHelper;
import org.distantshoresmedia.model.TKDatabaseModel;
import org.distantshoresmedia.model.daoModels.DaoSession;

/**
 * Created by Fechner on 6/17/15.
 */
public abstract class ModelSaveOrUpdateTask extends AsyncTask<TKDatabaseModel, Void, TKDatabaseModel> {

    private static final String TAG = "ModelSaveOrUpdateTask";

    private ModelCreationTaskListener listener;
    private final Context context;
    protected abstract TKDatabaseModel getExistingModel(long uId, DaoSession session);

    public ModelSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected TKDatabaseModel doInBackground(TKDatabaseModel... params) {

        DaoSession session = DaoDBHelper.getDaoSession(context);
        TKDatabaseModel newModel = params[0];
        TKDatabaseModel existingModel = getExistingModel(newModel.getUId(), session);

        if(existingModel != null){
            if(existingModel.updateWithModel(newModel)){
//                Log.d(TAG, "Model updated and will update");
                return existingModel;
            }
            else{
//                Log.d(TAG, "Model updated and won't update");
                return null;
            }
        }
        else{
            newModel.insertModel(session);
//            Log.d(TAG, "Model created");
            return newModel;
        }
    }

    @Override
    protected void onPostExecute(TKDatabaseModel shouldContinueUpdate) {
        super.onPostExecute(shouldContinueUpdate);
        listener.modelWasUpdated(shouldContinueUpdate);
    }

    interface ModelCreationTaskListener {
        void modelWasUpdated(TKDatabaseModel shouldContinueUpdate);
    }
}
