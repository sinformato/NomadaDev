package org.ALPHA.nomadadev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StatsActivity extends Activity {

    Vibrator vb;

    static int section=R.id.main;

    private Handler TimerStatsUI;
    private int UPDATE_TIME_STATS_UI = 500;

    // TimerStatsUI
    private Runnable updateStatsUI = new Runnable()
    {
        @Override
        public void run()
        {
            updateStatsLayout();
            TimerStatsUI.postDelayed(this, UPDATE_TIME_STATS_UI);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        // TimerStatsUI
        TimerStatsUI = new Handler();
        TimerStatsUI.removeCallbacks(updateStatsUI);
        TimerStatsUI.postDelayed(updateStatsUI, UPDATE_TIME_STATS_UI);

        // Vibrator
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent();
        section = item.getItemId();
        switch (section)
        {
            case R.id.main:
                intent.setClass(StatsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.map:
                intent.setClass(StatsActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.timeline:
                intent.setClass(StatsActivity.this, TimelineActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
                intent.setClass(StatsActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy()
    {
        TimerStatsUI.removeCallbacks(updateStatsUI);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    String msToTimeString(long msTotal)
    {
        int days;
        int hours;
        int minutes;
        int seconds;
        int secondsTotal;

        secondsTotal=(int)msTotal/1000;
        String timeString ="";

        days=secondsTotal/86400;
        hours=(secondsTotal-(days*86400))/3600;
        minutes=(secondsTotal-(days*86400)-(hours*3600))/60;
        seconds=secondsTotal-(days*86400)-(hours*3600)-(minutes*60);

        if (days > 0)
        {
            timeString=days+" d "+hours+" h "+minutes+" min "+seconds+" s";
        }
        else
        {
            if (hours > 0)
            {
                timeString=timeString+hours+" h "+minutes+" min "+seconds+" s";
            }
            else
            {
                if (minutes > 0)
                {
                    timeString=timeString+minutes+" min "+seconds+" s";
                }
                else
                {
                    timeString=timeString+seconds+" s";
                }
            }
        }
        return timeString;
    }

    void updateStatsLayout()
    {
        if (NomadaService.On)
        {
            ((TextView) findViewById(R.id.RunTimeData2)).setText(msToTimeString(NomadaService.v.serviceData.runTime));
            ((TextView) findViewById(R.id.totalDistanceData)).setText(NomadaService.currentDay.stats.distances.total+" m");
            //((TextView) findViewById(R.id.TimeLostData)).setText(msToTimeString(NomadaService.timeLost));

            ((TextView) findViewById(R.id.TimeStillData)).setText(msToTimeString(NomadaService.currentDay.stats.times.still.Total));
            ((TextView) findViewById(R.id.TimeStillForSureData)).setText(msToTimeString(NomadaService.currentDay.stats.times.still.Absolutely));
            ((TextView) findViewById(R.id.TimeProbablyStillMovingPhoneData)).setText(msToTimeString(NomadaService.currentDay.stats.times.still.MovingProbably));
            ((TextView) findViewById(R.id.TimeStillMovingPhoneForSureData)).setText(msToTimeString(NomadaService.currentDay.stats.times.still.MovingSure));

            ((TextView) findViewById(R.id.TimeWalkingData)).setText(msToTimeString(NomadaService.currentDay.stats.times.walking.Total));
            ((TextView) findViewById(R.id.DistanceWalkingData)).setText(NomadaService.currentDay.stats.distances.walking+" m");
            ((TextView) findViewById(R.id.TimeWalkingForSureData)).setText(msToTimeString(NomadaService.currentDay.stats.times.walking.Sure));
            ((TextView) findViewById(R.id.TimeProbablyWalkingData)).setText(msToTimeString(NomadaService.currentDay.stats.times.walking.Probably));

            ((TextView) findViewById(R.id.TimeRunningData)).setText(msToTimeString(NomadaService.currentDay.stats.times.running.Total));
            ((TextView) findViewById(R.id.DistanceRunningData)).setText(NomadaService.currentDay.stats.distances.running+" m");
            ((TextView) findViewById(R.id.TimeRunningForSureData)).setText(msToTimeString(NomadaService.currentDay.stats.times.running.Sure));
            ((TextView) findViewById(R.id.TimeProbablyRunningData)).setText(msToTimeString(NomadaService.currentDay.stats.times.running.Probably));

            ((TextView) findViewById(R.id.TimeCarData)).setText(msToTimeString(NomadaService.currentDay.stats.times.vehicle.Total));
            ((TextView) findViewById(R.id.DistanceInVehicleData)).setText(NomadaService.currentDay.stats.distances.vehicle+" m");
            ((TextView) findViewById(R.id.TimeInVehicleForSureData)).setText(msToTimeString(NomadaService.currentDay.stats.times.vehicle.RunningSure));
            ((TextView) findViewById(R.id.TimeStillInVehicleData)).setText(msToTimeString(NomadaService.currentDay.stats.times.vehicle.StillSure));
            ((TextView) findViewById(R.id.TimeProbablyInVehicleData)).setText(msToTimeString(NomadaService.currentDay.stats.times.vehicle.NoGPS));
        }
    }
    
}
