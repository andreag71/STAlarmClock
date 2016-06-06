package com.test.stalarmclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    AlarmManager alarmManager;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmTextView;
    private ToggleButton alarmToggle;
    private DateFormat df = DateFormat.getTimeInstance();
    private AlertDialog infoDialog;
    public static final String PREFS_NAME = "MyPrefsFile";
    private AlarmReceiver alarm = new AlarmReceiver();
    private int alarmHour = -1;
    private int alarmMinute = -1;
    private boolean alarmOn = false;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "onCreate " + savedInstanceState);

        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        DateFormat.getTimeInstance();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Check whether we're recreating a previously destroyed instance
        Log.d("MainActivity", "restore saved instance state");
        alarmToggle.setChecked(settings.getBoolean("AlarmSet", false));
        setAlarmText(settings.getString("AlarmMsg", ""));
        setAlarmOn(settings.getBoolean("AlarmOn", false));

    }

    public void onToggleClicked(View view) {

        if ( alarmToggle.isChecked() ) {

            Log.d("MainActivity", "Alarm On");
            alarmHour = alarmTimePicker.getCurrentHour();
            alarmMinute = alarmTimePicker.getCurrentMinute();
            Calendar calendar = alarm.setAlarm(this, alarmHour, alarmMinute);

            alarmTextView.setText("Alarm On at: " + df.format(calendar.getTime()));

        } else {
            setAlarmText("Alarm Off");
            alarmHour = -1;
            alarmMinute = -1;
            alarm.cancelAlarm(this);
            if ( alarmOn ) {
                PowerOffAsyncTask task = new PowerOffAsyncTask();
                task.execute("POWER");
            }
            Log.d("MainActivity", "Alarm Off");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save Alarm status
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("AlarmSet", alarmToggle.isChecked());
        editor.putString("AlarmMsg", alarmTextView.getText().toString());
        editor.putInt("AlarmHour", alarmHour);
        editor.putInt("AlarmMinute", alarmMinute);
        editor.putBoolean("AlarmOn", alarmOn);
        // Commit the edits!
        editor.commit();

        Log.d("MainActivity", "onStop save alarm status");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_info:
                showInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showInfoDialog() {

        if(infoDialog != null && infoDialog.isShowing() ){
            //do nothing if already showing
        }else {
            infoDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.info_details)
                    .setCancelable(true)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            infoDialog.show();
        }
    }


    public void setAlarmText(final String alarmText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alarmTextView.setText(alarmText);
            }
        });

    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }
}
