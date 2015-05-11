package org.distantshoresmedia.database;

import java.util.Date;

/**
 * Created by Fechner on 12/31/14.
 */
public class TimeHelper {

    //region Helper Methods

    public static boolean isCurrent(double oldTime, double newTime){

        boolean current = isCurrent(new Date(Math.round(oldTime)), new Date(Math.round(newTime)));
        return current;
    }

    public static boolean isCurrent(Date oldTime, Date newTime){

        if(oldTime.before(newTime)){
            return false;
        }
        else{
            return true;
        }
    }
    //endregion

}
