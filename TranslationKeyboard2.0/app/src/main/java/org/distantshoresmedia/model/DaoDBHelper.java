package org.distantshoresmedia.model;

import android.content.Context;

import org.distantshoresmedia.model.daoModels.DaoMaster;
import org.distantshoresmedia.model.daoModels.DaoSession;
import org.distantshoresmedia.translationkeyboard20.R;


/**
 * Created by Fechner on 4/28/15.
 */
public class DaoDBHelper {

    static private DaoMaster daoMaster;

    static public DaoSession getDaoSession(Context context){

        if(daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    context.getResources().getString(R.string.database_name), null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        DaoSession session = daoMaster.newSession();
        return session;
    }
}
