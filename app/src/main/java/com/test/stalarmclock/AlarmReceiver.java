package com.test.stalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmManager;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {

        // This is the Intent to deliver to our service.
        Intent service = new Intent(context, AlarmWakeupService.class);

        // Start the service, keeping the device awake while it is launching.
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);

    }

    /**
     * Set alarm
     * @param context, hour, mimute
     */
    public Calendar setAlarm(Context context, Integer hour, Integer minute) {

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date now = new Date();
        if ( now.after(calendar.getTime())  ) {
            calendar.add(Calendar.DATE, 1);
        }

        Log.d("MyActivity", "Alarm  time: " + calendar.getTime().toString());

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        // Enable {@code AlarmBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return calendar;

    }


    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmManager!= null) {
            alarmManager.cancel(alarmIntent);
        }
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
