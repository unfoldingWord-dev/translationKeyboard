package org.distantshoresmedia.tasks;

import android.content.Context;
import android.util.Log;

import org.distantshoresmedia.model.TKDatabaseModel;
import org.distantshoresmedia.model.daoModels.AvailableKeyboard;
import org.distantshoresmedia.model.daoModels.DaoSession;
import org.distantshoresmedia.services.TKUpdater;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateAvailableKeyboardsRunnable implements Runnable{

    private static final String TAG = "UpdateProjectsRunnable";
    public static final String LANGUAGES_JSON_KEY = "langs";
    private JSONArray jsonModels;
    private TKUpdater updater;

    public UpdateAvailableKeyboardsRunnable(JSONArray jsonModels, TKUpdater updater) {
        this.jsonModels = jsonModels;
        this.updater = updater;

    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }

    private void parseModels(JSONArray models){

        for(int i = 0; i < models.length(); i++){

            try {
                updateModel(models.getJSONObject(i), (i == (models.length() - 1)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonObject, final boolean lastModel){
        new ModelCreationTask(new AvailableKeyboard(), null, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(TKDatabaseModel model) {

                if(model instanceof AvailableKeyboard) {

                    new AvailableKeyboardSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(TKDatabaseModel shouldContinueUpdate) {

                            Log.d(TAG, "project created");

                            if(shouldContinueUpdate != null){
                                updateBaseKeyboards(jsonObject, (AvailableKeyboard) shouldContinueUpdate);
                            }
                            if(lastModel){
                                updater.runnableFinished();
                            }
                        }
                    }
                    ).execute(model);
                }
            }
        }).execute(jsonObject);
    }

    private void updateBaseKeyboards(JSONObject project, AvailableKeyboard parentProject){

        try{
            JSONArray BaseKeyboards = project.getJSONArray(LANGUAGES_JSON_KEY);
            UpdateBaseKeyboardRunnable runnable = new UpdateBaseKeyboardRunnable(BaseKeyboards, updater, parentProject);
            updater.addRunnable(runnable);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    private class AvailableKeyboardSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public AvailableKeyboardSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected TKDatabaseModel getExistingModel(long uId, DaoSession session) {
            return AvailableKeyboard.getKeyboardFromId(uId, session);
        }
    }

}
