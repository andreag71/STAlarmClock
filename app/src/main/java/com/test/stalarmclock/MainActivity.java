package com.test.stalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmTextView;
    private ToggleButton alarmToggle;
    private DateFormat df = DateFormat.getTimeInstance();
    //private boolean isAlarmSet = false;

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
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        DateFormat.getTimeInstance();
    }

    public void onToggleClicked(View view) {

        if ( alarmToggle.isChecked() ) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            calendar.set(Calendar.SECOND, 0);

            Date now = new Date();
            if ( now.after(calendar.getTime())  ) {
                calendar.add(Calendar.DATE, 1);
            }

            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            Log.d("MyActivity", "Alarm  time: " + calendar.getTime().toString());
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmTextView.setText("Alarm On  " + df.format(calendar.getTime()));
        } else {
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager.cancel(pendingIntent);
            setAlarmText("Alarm Off");
            PowerOffAsyncTask task = new PowerOffAsyncTask();
            task.execute("POWER");
            Log.d("MyActivity", "Alarm Off");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save Alarm status
        outState.putBoolean("AlarmSet", alarmToggle.isChecked());
        outState.putString("AlarmMsg", alarmTextView.getText().toString());
        Log.d("MyActivity", "onSaveInstanceState");

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            alarmToggle.setChecked(savedInstanceState.getBoolean("AlarmSet"));
            setAlarmText(savedInstanceState.getString("AlarmMsg"));
            Log.d("MyActivity", "onRestoreInstanceState");
        }

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
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }

}
