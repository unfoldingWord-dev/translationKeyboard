package org.distantshoresmedia.tasks;

import android.content.Context;
import android.util.Log;

import org.distantshoresmedia.model.TKDatabaseModel;
import org.distantshoresmedia.model.daoModels.BaseKeyboard;
import org.distantshoresmedia.model.daoModels.DaoSession;
import org.distantshoresmedia.model.daoModels.KeyboardVariant;
import org.distantshoresmedia.services.TKUpdater;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateKeyboardVariantRunnable implements Runnable{

    private static final String TAG = "UpdateLanguagesRunnable";
    public static final String VERSIONS_JSON_KEY = "vers";
    private JSONArray jsonModels;
    private TKUpdater updater;
    private BaseKeyboard parent;

    public UpdateKeyboardVariantRunnable(JSONArray jsonModels, TKUpdater updater, BaseKeyboard parent) {
        this.jsonModels = jsonModels;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        for(int i = 0; i < models.length(); i++){

            try {
                updateModel(models.getJSONObject(i), i == (models.length() - 1));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonModel, final boolean isLast){

        new ModelCreationTask(new KeyboardVariant(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(TKDatabaseModel model) {

                if(model instanceof KeyboardVariant) {

                    new KeyboardVariantSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener() {
                        @Override
                        public void modelWasUpdated(TKDatabaseModel shouldContinueUpdate) {

                            Log.d(TAG, "language created");

                            if(shouldContinueUpdate != null){
                                updateKeyboardVariant(jsonModel, (KeyboardVariant) shouldContinueUpdate);
                            }
                            if(isLast){
                                updater.runnableFinished();
                            }
                        }
                    }).execute(model);
                }
            }
        }).execute(jsonModel);
    }

    private void updateKeyboardVariant(JSONObject language, KeyboardVariant parent){

        try{
            JSONArray keyPositions = language.getJSONArray(VERSIONS_JSON_KEY);
            UpdateKeyPositionRunnable runnable = new UpdateKeyPositionRunnable(keyPositions, updater, parent);
            updater.addRunnable(runnable);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class KeyboardVariantSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public KeyboardVariantSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected TKDatabaseModel getExistingModel(long uId, DaoSession session) {
            return KeyboardVariant.getModelForId(uId, session);
        }
    }

}
