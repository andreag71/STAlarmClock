package com.test.stalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by a_ghelfi on 04/06/2016.
 */
public class AlarmBootReceiver extends BroadcastReceiver {

    AlarmReceiver alarm = new AlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            int alarmHour = settings.getInt("AlarmHour", -1);
            int alarmMinute = settings.getInt("AlarmMinute", -1);

            if ( alarmHour != -1 ) {
                Log.d("AlarmBootReceiver", "restore alarm at " + alarmHour + ":" + alarmMinute);
                alarm.setAlarm(context, alarmHour, alarmMinute);
            }

            Log.d("AlarmBootReceiver", "boot complete");
        }
    }
}
