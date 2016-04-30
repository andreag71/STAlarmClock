package com.test.stalarmclock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by a_ghelfi on 02/04/2016.
 */
public class PowerOnAsyncTask extends AsyncTask<String, Integer, Void> {

    String baseUrl = "http://192.168.0.100:8090";

    public PowerOnAsyncTask(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    protected Void doInBackground(String... params) {

        Log.d("PowerOnAsyncTask", "Set Volume to: " + params[0] + " and Press: " +  params[1]);

        String keyPressXml = "<key state=\"press\" sender=\"Gabbo\">" + params[1] + "</key>";
        String keyReleaseXml = "<key state=\"release\" sender=\"Gabbo\">" + params[1] + "</key>";

        // Set volume
        if ( params[0] != null ) {
            String volumeXml = "<volume>" + params[0] + "</volume>";
            doRequest(baseUrl + "/volume", volumeXml);

            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Press key
        doRequest(baseUrl + "/key", keyPressXml);
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doRequest(baseUrl + "/key", keyReleaseXml);

        return null;

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
