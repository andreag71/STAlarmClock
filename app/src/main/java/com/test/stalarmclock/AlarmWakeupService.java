package com.test.stalarmclock;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by a_ghelfi on 04/06/2016.
 */
public class AlarmWakeupService extends IntentService {

    String baseUrl = "http://192.168.0.100:8090";

    public AlarmWakeupService() {
        super("AlarmWakeupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        MainActivity inst = MainActivity.instance();
        inst.setAlarmText("Wake Up! SoundTouch ON");
        inst.setAlarmOn(true);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(inst);
        String ip = sharedPref.getString("device_ip", "192.168.0.100");
        int volume = sharedPref.getInt("volume", 20);
        String presetNumber = sharedPref.getString("example_list", "1");

        baseUrl = "http://" + ip + ":8090";

        Log.d("AlarmWakeupService", "Set Volume to: " + volume + " and Press: " +  presetNumber);

        String keyPressXml = "<key state=\"press\" sender=\"Gabbo\">PRESET_" + presetNumber + "</key>";
        String keyReleaseXml = "<key state=\"release\" sender=\"Gabbo\">PRESET_" + presetNumber + "</key>";

        // Set volume

        String volumeXml = "<volume>" + volume + "</volume>";
        doRequest(baseUrl + "/volume", volumeXml);

        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
             e.printStackTrace();
        }


        // Press key
        doRequest(baseUrl + "/key", keyPressXml);
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doRequest(baseUrl + "/key", keyReleaseXml);

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private String doRequest(String postUrl, String xml) {


        HttpURLConnection conn = null;
        StringBuffer buffer = new StringBuffer();

        try {
            URL url = new URL(postUrl);

            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(5000 /*milliseconds*/);
            conn.setConnectTimeout(5000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(xml.getBytes().length);

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/keyXml;charset=utf-8");
            //open
            conn.connect();

            //setup send
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(xml.getBytes());
            //clean up
            os.flush();
            os.close();

            //do somehting with response
            InputStream inputStream = conn.getInputStream();
            //input stream

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                }
            }

            Log.d("PowerOnAsyncTask", "success " + buffer.toString());

        } catch (IOException e) {
            Log.d("PowerOnAsyncTask", "error " + e.getMessage());
        }
        finally {
            if ( conn != null ) {
                conn.disconnect();
            }
        }

        return buffer.toString();

    }
}
