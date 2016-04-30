package com.test.stalarmclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        MainActivity inst = MainActivity.instance();
        inst.setAlarmText("Alarm! Wake up! Wake up!");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(inst);
        String ip = sharedPref.getString("device_ip", "192.168.0.100");
        int volume = sharedPref.getInt("volume", 20);
        String presetNumber = sharedPref.getString("example_list", "1");

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        /*
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        */

        String baseUrl = "http://" + ip + ":8090";

        Log.d("AlarmReceiver", "Alarm config: url=" + baseUrl + " volume=" + volume + " preset=" + presetNumber);

        PowerOnAsyncTask task = new PowerOnAsyncTask(baseUrl);
        task.execute(Integer.toString(volume), "PRESET_" + presetNumber);

    }
}
