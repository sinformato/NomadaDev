package org.ALPHA.nomadadev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MainActivity extends Activity
{
    static int section=R.id.main;

    private Handler TimerMainUI;

    public Constants c;

    Vibrator vb;

    // TimerMainUI
    private Runnable updateMainUI = new Runnable()
    {
        @Override
        public void run()
        {
            updateMainLayout();
            //TimerMainUI.postDelayed(this, c.updateTimer.Main);
            TimerMainUI.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Get constants
        getConstants();
        //c=new Constants();
        //setDefaultSettings();

        // TimerMainUI
        TimerMainUI = new Handler();
        TimerMainUI.removeCallbacks(updateMainUI);
        TimerMainUI.postDelayed(updateMainUI, 500);

        //TimerMainUI.postDelayed(updateMainUI, c.updateTimer.Main);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent();
        section = item.getItemId();
        switch (section)
        {
            case R.id.map:
                intent.setClass(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.stats:
                intent.setClass(MainActivity.this, StatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.timeline:
                intent.setClass(MainActivity.this, TimelineActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.dates:
                intent.setClass(MainActivity.this, TimeLineDatesActivity.class);
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
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy()
    {
        TimerMainUI.removeCallbacks(updateMainUI);
        super.onDestroy();
    }

    public void onClickService(View view)
    {
        vb.vibrate(100);
        serviceStarter();
    }

    public void onClickRecord(View view)
    {
        vb.vibrate(100);
        if (NomadaService.v.serviceStatus.logRecordOn)
        {
            NomadaService.v.serviceStatus.logRecordOn=false;
            NomadaService.v.serviceRequest.logFileClose=true;
            NomadaService.v.serviceRequest.newLogFile=false;
            ((Button) findViewById(R.id.buttonRecord)).setText("OFF");
            ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#FF4100"));
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        }
        else
        {
           // Toast.makeText(this,"File "+NomadaService.fileName+" closed",Toast.LENGTH_SHORT).show();
            NomadaService.v.serviceStatus.logRecordOn=true;
            NomadaService.v.serviceRequest.logFileClose=false;
            NomadaService.v.serviceRequest.newLogFile=true;
            ((Button) findViewById(R.id.buttonRecord)).setText("ON");
            ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#00FF00"));

        }
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

    void serviceStarter()
    {
        if (NomadaService.On)
        {
            stopService(new Intent(MainActivity.this, NomadaService.class));
            ((Button) findViewById(R.id.buttonService)).setText("OFF");
            ((Button) findViewById(R.id.buttonService)).setTextColor(Color.parseColor("#FF4100"));

            ((Button) findViewById(R.id.buttonRecord)).setText("--");
            ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#858585"));
            ((Button) findViewById(R.id.buttonRecord)).setEnabled(false);
            ((TextView) findViewById(R.id.txtRecordButton)).setTextColor(Color.parseColor("#858585"));
        }
        else
        {
            startService(new Intent(this, NomadaService.class));
            ((Button) findViewById(R.id.buttonService)).setText("ON");
            ((Button) findViewById(R.id.buttonService)).setTextColor(Color.parseColor("#00FF00"));

            ((Button) findViewById(R.id.buttonRecord)).setText("OFF");
            ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#FF4100"));
            ((Button) findViewById(R.id.buttonRecord)).setEnabled(true);
            ((TextView) findViewById(R.id.txtRecordButton)).setTextColor(Color.parseColor("#FFFFFF"));
            //NomadaService.v.serviceStatus.logRecordOn=false;
        }
    }

    void updateMainLayout()
    {
        if (NomadaService.On)
        {
            // Enable and Record
            ((Button) findViewById(R.id.buttonService)).setText("ON");
            ((Button) findViewById(R.id.buttonService)).setTextColor(Color.parseColor("#00FF00"));
            ((Button) findViewById(R.id.buttonRecord)).setEnabled(true);
            if (NomadaService.v.serviceStatus.logRecordOn)
            {
                ((Button) findViewById(R.id.buttonRecord)).setText("ON");
                ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#00FF00"));
                ((TextView) findViewById(R.id.txtRecordButton)).setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                ((Button) findViewById(R.id.buttonRecord)).setText("OFF");
                ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#FF4100"));
            }
            // Main Information
            ((TextView) findViewById(R.id.RunTimeData)).setText(msToTimeString(NomadaService.v.serviceData.runTime));
            switch(NomadaService.v.movStatus.mov)
            {
                case STILL:
                    ((TextView) findViewById(R.id.movStatusData)).setText("Still");
                    ((TextView) findViewById(R.id.movStatusData)).setTextColor(Color.YELLOW);
                    break;
                case WALKING:
                    ((TextView) findViewById(R.id.movStatusData)).setText("Walking");
                    ((TextView) findViewById(R.id.movStatusData)).setTextColor(Color.MAGENTA);
                    break;
                case RUNNING:
                    ((TextView) findViewById(R.id.movStatusData)).setText("Running");
                    ((TextView) findViewById(R.id.movStatusData)).setTextColor(Color.GREEN);
                    break;
                case VEHICLE:
                    ((TextView) findViewById(R.id.movStatusData)).setText("By car");
                    ((TextView) findViewById(R.id.movStatusData)).setTextColor(Color.CYAN);
                    break;
                case UNKNOWN:
                    ((TextView) findViewById(R.id.movStatusData)).setText("Unknown Status");
                    break;
                case INITIAL:
                    ((TextView) findViewById(R.id.movStatusData)).setText("Initial Status");
                    break;
            }
            switch(NomadaService.v.movStatus.movSub)
            {
                case STILL_SURE:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Still for sure");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.YELLOW);
                    break;

                case STILL_MOVING_SURE:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Still moving the phone for sure");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.YELLOW);
                    break;

                case STILL_MOVING_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably still moving the phone");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.YELLOW);
                    break;

                case WALKING_SURE:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Walking for sure");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.MAGENTA);
                    break;

                case WALKING_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably walking");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.MAGENTA);
                    break;

                case RUNNING_SURE:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Running for sure");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.GREEN);
                    break;

                case RUNNING_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably running");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.GREEN);
                    break;

                case VEHICLE_RUNNING_SURE:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("In car for sure");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.CYAN);
                    break;

                case VEHICLE_RUNNING_NO_GPS_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably in car. No GPS");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.CYAN);
                    break;

                case VEHICLE_STILL_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably still in car");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.CYAN);
                    break;

                case VEHICLE_PARKED_PROBABLY:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Probably have the car parked");
                    ((TextView) findViewById(R.id.movSubStatusData)).setTextColor(Color.CYAN);
                    break;

                case UNKNOWN:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Unknown SubStatus");
                    break;

                case INITIAL:
                    ((TextView) findViewById(R.id.movSubStatusData)).setText("Initial SubStatus");
                    break;
            }
            ((TextView) findViewById(R.id.timeStepData)).setText(NomadaService.v.serviceData.currentTimeStep+" ms");
            // Accelerometer Info
            ((TextView) findViewById(R.id.movQuantityData)).setText(NomadaService.v.sensorData.accelerometer.movQuantity+"");
            ((TextView) findViewById(R.id.movQuantityAbsData)).setText(NomadaService.v.sensorData.accelerometer.movQuantityAbs+"");
            ((TextView) findViewById(R.id.nStepsData)).setText(NomadaService.v.sensorData.pedometer.nSteps+" steps");
            // GPS Info
            ((TextView) findViewById(R.id.GPSTrackingONData)).setText(NomadaService.v.sensorData.gps.trackingOn+"");
            ((TextView) findViewById(R.id.TimeNoGPSData)).setText(NomadaService.v.sensorData.gps.timeNoTracking+" ms");
            ((TextView) findViewById(R.id.GPSTimeStepData)).setText(NomadaService.v.sensorData.gps.currentTimeStep+" ms");
            ((TextView) findViewById(R.id.LatitudeData)).setText(NomadaService.v.sensorData.gps.currentLocation.latitude+"");
            ((TextView) findViewById(R.id.LongitudeData)).setText(NomadaService.v.sensorData.gps.currentLocation.longitude+"");
            ((TextView) findViewById(R.id.AltitudeData)).setText(NomadaService.v.sensorData.gps.currentLocation.altitude+" m");
            ((TextView) findViewById(R.id.AccuracyData)).setText(NomadaService.v.sensorData.gps.currentLocation.accuracy+" m");
            ((TextView) findViewById(R.id.SpeedData)).setText(NomadaService.v.sensorData.gps.currentLocation.speedKm+" Km/h");
            ((TextView) findViewById(R.id.DistanceData)).setText(NomadaService.v.sensorData.gps.currentLocation.distanceBetweenLocations+" m");
            // Location and places
            ((TextView) findViewById(R.id.LocationData)).setText(NomadaService.v.sensorData.gps.nTotalLocations+" locations");
            ((TextView) findViewById(R.id.PlacesData)).setText(NomadaService.currentDay.nPlaces+" places");
            ((TextView) findViewById(R.id.PathsData)).setText(NomadaService.currentDay.nPaths+" paths");
            if (NomadaService.currentDay.nPlaces > 0)
            {
                ((TextView) findViewById(R.id.AddressData)).setText(NomadaService.currentDay.places.get(NomadaService.currentDay.nPlaces-1).address);
                ((TextView) findViewById(R.id.LatitudeSumData)).setText(NomadaService.v.detection.locations.place.computeCurrentPlace.latitudeSum+"");
                ((TextView) findViewById(R.id.LongitudeSumData)).setText(NomadaService.v.detection.locations.place.computeCurrentPlace.longitudeSum+"");
                ((TextView) findViewById(R.id.nPlaceLocationsData)).setText(NomadaService.v.detection.locations.place.computeCurrentPlace.nLocations+"");
                ((TextView) findViewById(R.id.CurrentPlaceLatitudeData)).setText(NomadaService.currentDay.places.get(NomadaService.currentDay.nPlaces-1).latitude+"");
                ((TextView) findViewById(R.id.CurrentPlaceLongitudeData)).setText(NomadaService.currentDay.places.get(NomadaService.currentDay.nPlaces-1).longitude+"");
            }
            // Other Sensors
            ((TextView) findViewById(R.id.ProximityData)).setText(NomadaService.v.sensorData.proximity.value+"");
            ((TextView) findViewById(R.id.LightData)).setText(NomadaService.v.sensorData.light.value+" lux");
            ((TextView) findViewById(R.id.RotationXData)).setText(NomadaService.v.sensorData.rotation.x+"");
            ((TextView) findViewById(R.id.RotationYData)).setText(NomadaService.v.sensorData.rotation.y+"");
            ((TextView) findViewById(R.id.RotationZData)).setText(NomadaService.v.sensorData.rotation.z+"");

            // Mov Detection Info
            ((TextView) findViewById(R.id.MovStatusChangedFromStillData)).setText(NomadaService.v.movStatus.changedFromStill+"");
            ((TextView) findViewById(R.id.nMovStatusChangesData)).setText(NomadaService.currentDay.stats.nDetections.Total+" changes");
            ((TextView) findViewById(R.id.TimeToStillThresholdData)).setText(NomadaService.c.detection.timeForChangingTo.Still+" ms");
            ((TextView) findViewById(R.id.TimeToStillCurrentData)).setText(NomadaService.v.detection.timeForChangingTo.Still+" ms");
            ((TextView) findViewById(R.id.ContinuousStillTimeData)).setText(NomadaService.v.detection.locations.place.timeForNewPlace+" ms");
            ((TextView) findViewById(R.id.NumberStillDetectionsData)).setText(NomadaService.currentDay.stats.nDetections.Still+"");
            ((TextView) findViewById(R.id.TimeToWalkingThresholdData)).setText(NomadaService.c.detection.timeForChangingTo.Walking+" ms");
            ((TextView) findViewById(R.id.TimeToWalkingCurrentData)).setText(NomadaService.v.detection.timeForChangingTo.Walking+" ms");
            //((TextView) findViewById(R.id.ContinuousWalkingTimeData)).setText(NomadaService.continuousTimeWalking+" ms");
            ((TextView) findViewById(R.id.NumberWalkingDetectionsData)).setText(NomadaService.currentDay.stats.nDetections.Walking+"");
            ((TextView) findViewById(R.id.TimeToRunningThresholdData)).setText(NomadaService.c.detection.timeForChangingTo.Running+" ms");
            ((TextView) findViewById(R.id.TimeToRunningCurrentData)).setText(NomadaService.v.detection.timeForChangingTo.Running+" ms");
            //((TextView) findViewById(R.id.ContinuousRunningTimeData)).setText(NomadaService.continuousTimeRunning+" ms");
            ((TextView) findViewById(R.id.NumberRunningDetectionsData)).setText(NomadaService.currentDay.stats.nDetections.Running+"");
            ((TextView) findViewById(R.id.TimeToInVehicleThresholdData)).setText(NomadaService.c.detection.timeForChangingTo.VehicleRunning+" ms");
            ((TextView) findViewById(R.id.TimeToInVehicleCurrentData)).setText(NomadaService.v.detection.timeForChangingTo.Vehicle+" ms");
            //((TextView) findViewById(R.id.ContinuousInVehicleTimeData)).setText(NomadaService.continuousTimeInVehicle+" ms");
            ((TextView) findViewById(R.id.NumberInVehicleDetectionsData)).setText(NomadaService.currentDay.stats.nDetections.Vehicle+"");
            ((TextView) findViewById(R.id.TimeToHaveCarParkedThresholdData)).setText(NomadaService.c.detection.timeForChangingTo.VehicleHaveParked+" ms");
            ((TextView) findViewById(R.id.TimeToHaveCarParkedCurrentData)).setText(NomadaService.v.detection.timeForChangingTo.VehicleParked+" ms");
            ((TextView) findViewById(R.id.NumberHavingCarParkedDetectionsData)).setText(NomadaService.currentDay.stats.nDetections.VehicleParked+"");
            ((TextView) findViewById(R.id.TimeToAddData)).setText(NomadaService.v.detection.times.timeWhileDetectingToAdd+" ms");
        }
        else
        {
            ((Button) findViewById(R.id.buttonService)).setText("OFF");
            ((Button) findViewById(R.id.buttonService)).setTextColor(Color.parseColor("#FF4100"));

            ((Button) findViewById(R.id.buttonRecord)).setText("--");
            ((Button) findViewById(R.id.buttonRecord)).setTextColor(Color.parseColor("#858585"));
            ((Button) findViewById(R.id.buttonRecord)).setEnabled(false);
            ((TextView) findViewById(R.id.txtRecordButton)).setTextColor(Color.parseColor("#858585"));
        }
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

    public void getConstants()
    {
        c = new Constants();
        if (SettingsExits())
        {
            c = readFileSettings();
        }
        else
        {
            Toast.makeText(this,"creando nuevas settings",Toast.LENGTH_SHORT).show();
            setDefaultSettings();
            writeFileSettings();
        }
    }

    public void setDefaultSettings()
    {
        /* Detection */
        // movQuantity Thresholds Near (m/s^2)
        c.detection = new Constants.Detection();
        c.detection.movQuantity = new Constants.Detection.MovQuantity();
        c.detection.movQuantity.near = new Constants.Detection.MovQuantity.Near();
        c.detection.movQuantity.near.max = new Constants.Detection.MovQuantity.Near.Max();
        c.detection.movQuantity.near.min = new Constants.Detection.MovQuantity.Near.Min();
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
        c.detection.movQuantity.near.max.VehicleHavingPark = 0.3;
        // MovQuantity Thresholds Far (m/s^2)
        c.detection.movQuantity.far = new Constants.Detection.MovQuantity.Far();
        c.detection.movQuantity.far.max = new Constants.Detection.MovQuantity.Far.Max();
        c.detection.movQuantity.far.min = new Constants.Detection.MovQuantity.Far.Min();
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
        c.detection.movQuantity.far.max.VehicleHavingPark = 0.3;
        // Time for changing to... (ms)
        c.detection.timeForChangingTo = new Constants.Detection.TimeForChangingTo();
        c.detection.timeForChangingTo.Still = 1000;
        c.detection.timeForChangingTo.Walking = 3000;
        c.detection.timeForChangingTo.Running = 5000;
        c.detection.timeForChangingTo.VehicleRunning = 20000;
        c.detection.timeForChangingTo.VehicleHaveParked = 2000;
        // Speeds Thresholds (km/h)
        c.detection.speed = new Constants.Detection.Speed();
        c.detection.speed.max = new Constants.Detection.Speed.Max();
        c.detection.speed.min = new Constants.Detection.Speed.Min();
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
        c.detection.accelerometer = new Constants.Detection.Accelerometer();
        c.detection.accelerometer.minAcceleration = 0.6;
        // Places Time Threshold (ms)
        c.detection.places = new Constants.Detection.Places();
        c.detection.places.minTimeStillForNewPlace = 5000;

        /* UpdateTimer (ms) */
        c.updateTimer = new Constants.UpdateTimer();
        c.updateTimer.Main = 500;
        c.updateTimer.Map = 2000;
        c.updateTimer.timeLine = 2000;
        c.updateTimer.Service = 500;
        c.updateTimer.Stats = 2000;

        /* GPS Settings (ms and m) */
        c.gps = new Constants.GPS();
        c.gps.maxTimeWithoutGpsFix = 4000;
        c.gps.updateTime = 1000;
        c.gps.minDistance = 0;

        /* Notification Service */
        c.notificationService = new Constants.NotificationService();
        c.notificationService.id = 17;
    }
}
