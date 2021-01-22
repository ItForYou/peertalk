package co.kr.itforone.peertalk.Util;

import android.app.Activity;
import android.app.ActivityManager;

import java.util.ArrayList;

public class Dialog_manager {
    private static Dialog_manager activityMananger = null;
    private ArrayList<Activity> activityList = null;

    private Dialog_manager() {
        activityList = new ArrayList<Activity>();
    }

    public static Dialog_manager getInstance() {

        if( Dialog_manager.activityMananger == null ) {
            activityMananger = new Dialog_manager();
        }
        return activityMananger;
    }

    public ArrayList<Activity> getActivityList() {
        return activityList;
    }
    public Activity getActivityLast() {
        return activityList.get(activityList.size()-1);
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public boolean removeActivity(Activity activity) {
        return activityList.remove(activity);
    }

/*    public void clearManager(){

        activityList.clear();

    }*/

    public void finishAllActivity() {
        for (Activity activity : activityList) {

            activity.finish();

        }
    }
    public void reset() {
        activityList.clear();
    }
}
