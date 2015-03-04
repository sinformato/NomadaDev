package org.ALPHA.nomadadev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * Created by ALPHA on 06/10/2014.
 */
public class SettingsActivity extends Activity
{
    static int section=R.id.settings;
    public Constants c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        c = new Constants();
        if (SettingsExits())
        {

            c = readFileSettings();
        }
        else
        {
            setDefaultSettings();
            writeFileSettings();
        }
        updateLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent();
        section = item.getItemId();
        switch (section)
        {
            case R.id.map:
                intent.setClass(SettingsActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.stats:
                intent.setClass(SettingsActivity.this, StatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.timeline:
                intent.setClass(SettingsActivity.this, TimelineActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void onClickDefault(View view)
    {
        setDefaultSettings();
        writeFileSettings();
        updateLayout();
    }

    public void onClickSave(View view)
    {
        getSettings();
        writeFileSettings();
    }

    public boolean SettingsExits ()
    {
        String fullPath;
        String fileName;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "settings.ini";


        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(fullPath+fileName);
            return true;
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"settings.ini not found",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    public Constants readFileSettings()
    {
        String fullPath;
        String fileName;
        Constants constants;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "settings.ini";
        constants = new Constants();

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error creating settings.ini",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(fis);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating Settings ObjectInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        try
        {
            constants = (Constants)ois.readObject();
            Toast.makeText(this,"Constants got",Toast.LENGTH_SHORT).show();
        }
        catch (ClassNotFoundException e)
        {
            Toast.makeText(this,"Constants ClassNotFoundException",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error getting Constants",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        try
        {
            ois.close();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error closing settings.ini",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return constants;
    }

    public void writeFileSettings ()
    {
        String fullPath;
        String fileName;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "settings.ini";


        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error creating settings.ini file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(fos);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating Constants ObjectOutputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.writeObject(c);

            Toast.makeText(this,"Settings wrote successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error writing Constants object",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.flush();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Write Constants Flush",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.close();
            Toast.makeText(this,"settings.ini closed successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error closing settings.ini",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void setDefaultSettings()
    {
        /* Detection */
        // movQuantity Thresholds Near (m/s^2)
        c.detection.movQuantity.near.max.StillAbsolutely = 0.1;
        c.detection.movQuantity.near.max.Still = 1.5;
        c.detection.movQuantity.near.min.Walking = 9;
        c.detection.movQuantity.near.max.Walking = 21;
        c.detection.movQuantity.near.min.Running = 16;
        c.detection.movQuantity.near.max.Running = 21;
        c.detection.movQuantity.near.min.VehicleStill = 0.5;
        c.detection.movQuantity.near.max.VehicleStill = 1.6;
        c.detection.movQuantity.near.min.VehicleRunning = 0.5;
        c.detection.movQuantity.near.max.VehicleRunning = 8;
        c.detection.movQuantity.near.max.VehicleHavingPark = 0.2;
        // MovQuantity Thresholds Far (m/s^2)
        c.detection.movQuantity.far.max.StillAbsolutely = 0.1;
        c.detection.movQuantity.far.max.Still = 1.4;
        c.detection.movQuantity.far.min.Walking = 1.4;
        c.detection.movQuantity.far.max.Walking = 4;
        c.detection.movQuantity.far.min.Running = 16;
        c.detection.movQuantity.far.max.Running = 21;
        c.detection.movQuantity.far.min.VehicleStill = 0.5;
        c.detection.movQuantity.far.max.VehicleStill = 1.6;
        c.detection.movQuantity.far.min.VehicleRunning = 0.5;
        c.detection.movQuantity.far.max.VehicleRunning = 8;
        c.detection.movQuantity.far.max.VehicleHavingPark = 0.2;
        // Time for changing to... (ms)
        c.detection.timeForChangingTo.Still = 5000;
        c.detection.timeForChangingTo.Walking = 5000;
        c.detection.timeForChangingTo.Running = 7000;
        c.detection.timeForChangingTo.VehicleRunning = 8000;
        c.detection.timeForChangingTo.VehicleHaveParked = 15000;
        // Speeds Thresholds (km/h)
        c.detection.speed.max.Still = 2;
        c.detection.speed.min.Walking = 1;
        c.detection.speed.max.Walking = 8;
        c.detection.speed.min.Running = 5;
        c.detection.speed.max.Running = 20;
        c.detection.speed.max.VehicleStill = 2;
        c.detection.speed.min.VehicleRunning = 10;
        c.detection.speed.max.VehicleRunning = 190;
        c.detection.speed.max.Absolute = 220;
        // Acceleration Threshold (m/s^2)
        c.detection.accelerometer.minAcceleration = 0.6;
        // Places Time Threshold (ms)
        c.detection.places.minTimeStillForNewPlace = 5000;

        /* UpdateTimer (ms) */
        c.updateTimer.Main = 500;
        c.updateTimer.Map = 2000;
        c.updateTimer.timeLine = 2000;
        c.updateTimer.Service = 500;
        c.updateTimer.Stats = 2000;
           
        /* GPS Settings (ms and m) */
        c.gps.maxTimeWithoutGpsFix = 4000;
        c.gps.updateTime = 1000;
        c.gps.minDistance = 0;
    }

    public void updateLayout ()
    {
        /* Detection */
        // MovQuantity Thresholds Near
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityAbsolutelyStillNearData)).setText(c.detection.movQuantity.near.max.StillAbsolutely+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityStillNearData)).setText(c.detection.movQuantity.near.max.Still+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityWalkingNearData)).setText(c.detection.movQuantity.near.min.Walking+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityWalkingNearData)).setText(c.detection.movQuantity.near.max.Walking+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityRunningNearData)).setText(c.detection.movQuantity.near.min.Running+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningNearData)).setText(c.detection.movQuantity.near.max.Running+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityStillVehicleNearData)).setText(c.detection.movQuantity.near.min.VehicleStill+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityStillVehicleNearData)).setText(c.detection.movQuantity.near.max.VehicleStill+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityRunningVehicleNearData)).setText(c.detection.movQuantity.near.min.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningVehicleNearData)).setText(c.detection.movQuantity.near.max.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityHaveVehicleParkedNearData)).setText(c.detection.movQuantity.near.max.VehicleHavingPark+"");
        // MovQuantity Thresholds Far
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityAbsolutelyStillFarData)).setText(c.detection.movQuantity.far.max.StillAbsolutely+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityStillFarData)).setText(c.detection.movQuantity.far.max.Still+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityWalkingFarData)).setText(c.detection.movQuantity.far.min.Walking+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityWalkingFarData)).setText(c.detection.movQuantity.far.max.Walking+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityRunningFarData)).setText(c.detection.movQuantity.far.min.Running+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningFarData)).setText(c.detection.movQuantity.far.max.Running+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityStillVehicleFarData)).setText(c.detection.movQuantity.far.min.VehicleStill+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityStillVehicleFarData)).setText(c.detection.movQuantity.far.max.VehicleStill+"");
        ((EditText) findViewById(R.id.SettingsMinMovQuantityRunningVehicleFarData)).setText(c.detection.movQuantity.far.min.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningVehicleFarData)).setText(c.detection.movQuantity.far.max.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsMaxMovQuantityHaveVehicleParkedFarData)).setText(c.detection.movQuantity.far.max.VehicleHavingPark+"");
        // Time for changing to...
        ((EditText) findViewById(R.id.SettingsTimeToStillData)).setText(c.detection.timeForChangingTo.Still+"");
        ((EditText) findViewById(R.id.SettingsTimeToWalkingData)).setText(c.detection.timeForChangingTo.Walking+"");
        ((EditText) findViewById(R.id.SettingsTimeToRunningData)).setText(c.detection.timeForChangingTo.Running+"");
        ((EditText) findViewById(R.id.SettingsTimeToInVehicleData)).setText(c.detection.timeForChangingTo.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsTimeToInVehicleParkedData)).setText(c.detection.timeForChangingTo.VehicleHaveParked+"");
        // Speeds Thresholds
        ((EditText) findViewById(R.id.SettingsStillMaxSpeedData)).setText(c.detection.speed.max.Still+"");
        ((EditText) findViewById(R.id.SettingsWalkingMinSpeedData)).setText(c.detection.speed.min.Walking+"");
        ((EditText) findViewById(R.id.SettingsWalkingMaxSpeedData)).setText(c.detection.speed.max.Walking+"");
        ((EditText) findViewById(R.id.SettingsRunningMinSpeedData)).setText(c.detection.speed.min.Running+"");
        ((EditText) findViewById(R.id.SettingsRunningMaxSpeedData)).setText(c.detection.speed.max.Running+"");
        ((EditText) findViewById(R.id.SettingsVehicleStillMaxSpeedData)).setText(c.detection.speed.max.VehicleStill+"");
        ((EditText) findViewById(R.id.SettingsVehicleRunningMinSpeedData)).setText(c.detection.speed.min.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsVehicleRunningMaxSpeedData)).setText(c.detection.speed.max.VehicleRunning+"");
        ((EditText) findViewById(R.id.SettingsAbsoluteMaxSpeedData)).setText(c.detection.speed.max.Absolute+"");
        // Accelerometer Thresholds
        ((EditText) findViewById(R.id.SettingsAccelerometerMinData)).setText(c.detection.accelerometer.minAcceleration+"");
        // New Place Thresholds
        ((EditText) findViewById(R.id.SettingsMinTimeForNewPlaceData)).setText(c.detection.places.minTimeStillForNewPlace+"");

        /* Update Time UI */
        ((EditText) findViewById(R.id.SettingsUpdateTimeUIMainData)).setText(c.updateTimer.Main+"");
        ((EditText) findViewById(R.id.SettingsUpdateTimeUIMapData)).setText(c.updateTimer.Map+"");
        ((EditText) findViewById(R.id.SettingsUpdateTimeUITimeLineData)).setText(c.updateTimer.timeLine+"");
        ((EditText) findViewById(R.id.SettingsUpdateTimeUIServiceData)).setText(c.updateTimer.Service+"");
        ((EditText) findViewById(R.id.SettingsUpdateTimeUIStatsData)).setText(c.updateTimer.Stats+"");

        /* GPS Thresholds */
        ((EditText) findViewById(R.id.SettingsMaxTimeWithoutGPSFixData)).setText(c.gps.maxTimeWithoutGpsFix+"");
        ((EditText) findViewById(R.id.SettingsGPSUpdateTimeData)).setText(c.gps.updateTime+"");
        ((EditText) findViewById(R.id.SettingsNewLocationMinDistanceData)).setText(c.gps.minDistance+"");
    }

    public void getSettings ()
    {
        /* Detection */
        // MovQuantity Thresholds Near
        c.detection.movQuantity.near.max.StillAbsolutely=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityAbsolutelyStillNearData)).getText().toString());
        c.detection.movQuantity.near.max.Still=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityStillNearData)).getText().toString());
        c.detection.movQuantity.near.min.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityWalkingNearData)).getText().toString());
        c.detection.movQuantity.near.max.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityWalkingNearData)).getText().toString());
        c.detection.movQuantity.near.min.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityRunningNearData)).getText().toString());
        c.detection.movQuantity.near.max.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningNearData)).getText().toString());
        c.detection.movQuantity.near.min.VehicleStill=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityStillVehicleNearData)).getText().toString());
        c.detection.movQuantity.near.max.VehicleStill=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityStillVehicleNearData)).getText().toString());
        c.detection.movQuantity.near.min.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityRunningVehicleNearData)).getText().toString());
        c.detection.movQuantity.near.max.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningVehicleNearData)).getText().toString());
        c.detection.movQuantity.near.max.VehicleHavingPark=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityHaveVehicleParkedNearData)).getText().toString());
        // MovQuantity Thresholds Far
        c.detection.movQuantity.far.max.StillAbsolutely=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityAbsolutelyStillFarData)).getText().toString());
        c.detection.movQuantity.far.max.Still=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityStillFarData)).getText().toString());
        c.detection.movQuantity.far.min.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityWalkingFarData)).getText().toString());
        c.detection.movQuantity.far.max.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityWalkingFarData)).getText().toString());
        c.detection.movQuantity.far.min.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityRunningFarData)).getText().toString());
        c.detection.movQuantity.far.max.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningFarData)).getText().toString());
        c.detection.movQuantity.far.min.VehicleStill=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityStillVehicleFarData)).getText().toString());
        c.detection.movQuantity.far.max.VehicleStill=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityStillVehicleFarData)).getText().toString());
        c.detection.movQuantity.far.min.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsMinMovQuantityRunningVehicleFarData)).getText().toString());
        c.detection.movQuantity.far.max.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityRunningVehicleFarData)).getText().toString());
        c.detection.movQuantity.far.max.VehicleHavingPark=Double.valueOf(((EditText) findViewById(R.id.SettingsMaxMovQuantityHaveVehicleParkedFarData)).getText().toString());
        // Time for changing to... (ms)
        c.detection.timeForChangingTo.Still=Long.valueOf(((EditText) findViewById(R.id.SettingsTimeToStillData)).getText().toString());
        c.detection.timeForChangingTo.Walking=Long.valueOf(((EditText) findViewById(R.id.SettingsTimeToWalkingData)).getText().toString());
        c.detection.timeForChangingTo.Running=Long.valueOf(((EditText) findViewById(R.id.SettingsTimeToRunningData)).getText().toString());
        c.detection.timeForChangingTo.VehicleRunning=Long.valueOf(((EditText) findViewById(R.id.SettingsTimeToInVehicleData)).getText().toString());
        c.detection.timeForChangingTo.VehicleHaveParked=Long.valueOf(((EditText) findViewById(R.id.SettingsTimeToInVehicleParkedData)).getText().toString());
        // Speeds Thresholds
        c.detection.speed.max.Still=Double.valueOf(((EditText) findViewById(R.id.SettingsStillMaxSpeedData)).getText().toString());
        c.detection.speed.min.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsWalkingMinSpeedData)).getText().toString());
        c.detection.speed.max.Walking=Double.valueOf(((EditText) findViewById(R.id.SettingsWalkingMaxSpeedData)).getText().toString());
        c.detection.speed.min.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsRunningMinSpeedData)).getText().toString());
        c.detection.speed.max.Running=Double.valueOf(((EditText) findViewById(R.id.SettingsRunningMaxSpeedData)).getText().toString());
        c.detection.speed.max.VehicleStill=Double.valueOf(((EditText) findViewById(R.id.SettingsVehicleStillMaxSpeedData)).getText().toString());
        c.detection.speed.min.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsVehicleRunningMinSpeedData)).getText().toString());
        c.detection.speed.max.VehicleRunning=Double.valueOf(((EditText) findViewById(R.id.SettingsVehicleRunningMaxSpeedData)).getText().toString());
        c.detection.speed.max.Absolute=Double.valueOf(((EditText) findViewById(R.id.SettingsAbsoluteMaxSpeedData)).getText().toString());
        // Accelerometer Thresholds
        c.detection.accelerometer.minAcceleration=Double.valueOf(((EditText) findViewById(R.id.SettingsAccelerometerMinData)).getText().toString());
        // New Place Thresholds
        c.detection.places.minTimeStillForNewPlace=Long.valueOf(((EditText) findViewById(R.id.SettingsNewLocationMinDistanceData)).getText().toString());

        /* UpdateTimer */
        c.updateTimer.Main= Integer.valueOf(((EditText) findViewById(R.id.SettingsUpdateTimeUIMainData)).getText().toString());
        c.updateTimer.Map= Integer.valueOf(((EditText) findViewById(R.id.SettingsUpdateTimeUIMapData)).getText().toString());
        c.updateTimer.timeLine= Integer.valueOf(((EditText) findViewById(R.id.SettingsUpdateTimeUITimeLineData)).getText().toString());
        c.updateTimer.Service= Integer.valueOf(((EditText) findViewById(R.id.SettingsUpdateTimeUIServiceData)).getText().toString());
        c.updateTimer.Stats= Integer.valueOf(((EditText) findViewById(R.id.SettingsUpdateTimeUIStatsData)).getText().toString());

        /* GPS Thresholds */
        c.gps.maxTimeWithoutGpsFix=Integer.valueOf(((EditText) findViewById(R.id.SettingsMaxTimeWithoutGPSFixData)).getText().toString());
        c.gps.updateTime=Integer.valueOf(((EditText) findViewById(R.id.SettingsGPSUpdateTimeData)).getText().toString());
        c.gps.minDistance=Integer.valueOf(((EditText) findViewById(R.id.SettingsNewLocationMinDistanceData)).getText().toString());
    }
}
