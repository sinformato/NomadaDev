package org.ALPHA.nomadadev;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
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
import java.util.List;

public class NomadaService extends Service implements SensorEventListener, LocationListener
{

    static Day currentDay;
    static TimeLine timeLine;

    static Constants c;
    static Variables v;

    static boolean On = false;

    private Handler Timer;
    private Runnable updateData = new Runnable()
    {
        @Override
        public void run()
        {
            updateAll();
            // Set Update Time
            Timer.postDelayed(this, c.updateTimer.Service);
        }
    };

    private Notification notification;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;

    public NotificationManager mNotificationManager;
    public Notification.Builder mNotificationBuilder;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mProximity;
    private Sensor mLight;
    private Sensor mRotation;

    private LocationManager locationManager;

    public FileWriter DataWriter;

    Vibrator vb;

    // Activity ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate()
    {
        Toast.makeText(this, "NomadaService created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id)
    {
        initAll();
        //On=true;
        return START_REDELIVER_INTENT;
    }

    // Mejorar esto
    public void onDestroy()
    {
        Toast.makeText(this, "Service killed", Toast.LENGTH_SHORT).show();

        // Close File
        if (v.serviceStatus.logRecordOn)
        {
            try
            {
                DataWriter.close();
            }
            catch (IOException e)
            {
                Log.e("Acc", "Could not write to phone accelerometer log file: " + e.toString());
                //return false;
            }
        }

        // Unregister sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);

        // Unregister GPS
        locationManager.removeUpdates(this);
        Timer.removeCallbacks(updateData);

        // Close current tracking
        if (v.detection.locations.place.trackingOn)
        {
            endPlace();
        }
        // End last path
        if (v.detection.locations.path.trackingOn)
        {
            endPath();
        }

        // Reset Variables
        initVariables();

        // Write Day
        writeCurrentDay();
        writeTimeLine();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    // Initialization //////////////////////////////////////////////////////////////////////////////////
    public void initAll ()
    {
        // Init variables
        initVariables();

        // Read Settings
        c=readFileSettings();

        // Init detection thresholds variables
        initDetectionThresholdsVariables();

        // Open or create timeLine
        initTimeLine();

        // Notification init
        createNotification();

        // Accelerometer init
        initAcc();

        // Proximity Init
        initProximity();

        // Light Init
        initLight();

        // Vibrator init
        initVibrator();

        // Rotation init
        initRotation();

        // GPS init
        initGps(c.gps.updateTime,c.gps.minDistance);

        // Time init
        initServiceTime();

        // Flag service ON
        On = true;

        // Timer init
        initTimer();
    }

    public void initVariables ()
    {
        v = new Variables();
        /* SensorData */
        v.sensorData = new Variables.SensorData();
        // Accelerometer
        v.sensorData.accelerometer = new Variables.SensorData.Accelerometer();
        v.sensorData.accelerometer.movQuantity = 0;
        v.sensorData.accelerometer.movQuantityAbs = 0;
        v.sensorData.accelerometer.movQuantityAbsLast = 0;
        v.sensorData.accelerometer.current = new Variables.SensorData.Accelerometer.Current();
        v.sensorData.accelerometer.current.x = 0;
        v.sensorData.accelerometer.current.y = 0;
        v.sensorData.accelerometer.current.z = 0;
        v.sensorData.accelerometer.averagePerTimeStep = new Variables.SensorData.Accelerometer.AveragePerTimeStep();
        v.sensorData.accelerometer.averagePerTimeStep.signed = new Variables.SensorData.Accelerometer.AveragePerTimeStep.Signed();
        v.sensorData.accelerometer.averagePerTimeStep.signed.x = 0;
        v.sensorData.accelerometer.averagePerTimeStep.signed.y = 0;
        v.sensorData.accelerometer.averagePerTimeStep.signed.z = 0;
        v.sensorData.accelerometer.averagePerTimeStep.abs = new Variables.SensorData.Accelerometer.AveragePerTimeStep.Abs();
        v.sensorData.accelerometer.averagePerTimeStep.abs.x = 0;
        v.sensorData.accelerometer.averagePerTimeStep.abs.y = 0;
        v.sensorData.accelerometer.averagePerTimeStep.abs.z = 0;
        v.sensorData.accelerometer.compute = new Variables.SensorData.Accelerometer.Compute();
        v.sensorData.accelerometer.compute.nSamples = 0;
        v.sensorData.accelerometer.compute.Ts = 0;
        v.sensorData.accelerometer.compute.average = new Variables.SensorData.Accelerometer.Compute.Average();
        v.sensorData.accelerometer.compute.average.signed = new Variables.SensorData.Accelerometer.Compute.Average.Signed();
        v.sensorData.accelerometer.compute.average.signed.x = 0;
        v.sensorData.accelerometer.compute.average.signed.y = 0;
        v.sensorData.accelerometer.compute.average.signed.z = 0;
        v.sensorData.accelerometer.compute.average.abs = new Variables.SensorData.Accelerometer.Compute.Average.Abs();
        v.sensorData.accelerometer.compute.average.abs.x = 0;
        v.sensorData.accelerometer.compute.average.abs.y = 0;
        v.sensorData.accelerometer.compute.average.abs.z = 0;
        v.sensorData.accelerometer.speedPerTimeStep = new Variables.SensorData.Accelerometer.SpeedPerTimeStep();
        v.sensorData.accelerometer.speedPerTimeStep.x = 0;
        v.sensorData.accelerometer.speedPerTimeStep.y = 0;
        v.sensorData.accelerometer.speedPerTimeStep.z = 0;
        v.sensorData.accelerometer.speedPerTimeStep.speed = 0;
        v.sensorData.accelerometer.speedPerTimeStep.speedKm = 0;
        v.sensorData.accelerometer.distancePerTimeStep = new Variables.SensorData.Accelerometer.DistancePerTimeStep();
        v.sensorData.accelerometer.distancePerTimeStep.x = 0;
        v.sensorData.accelerometer.distancePerTimeStep.y = 0;
        v.sensorData.accelerometer.distancePerTimeStep.z = 0;
        v.sensorData.accelerometer.distancePerTimeStep.distance = 0;
        // Proximity
        v.sensorData.proximity = new Variables.SensorData.Proximity();
        v.sensorData.proximity.value = 0;
        // Light
        v.sensorData.light = new Variables.SensorData.Light();
        v.sensorData.light.value = 0;
        // GPS
        v.sensorData.gps = new Variables.SensorData.GPS();
        v.sensorData.gps.trackingOn = false;
        v.sensorData.gps.timeNoTracking = 0;
        v.sensorData.gps.currentTimeStep = 0;
        v.sensorData.gps.lastTime = 0;
        v.sensorData.gps.currentTime = 0;
        v.sensorData.gps.nTotalLocations = 0;
        v.sensorData.gps.Status = 0;
        v.sensorData.gps.firstTime = true;
        v.sensorData.gps.currentLocation = new Variables.SensorData.GPS.CurrentLocation();
        v.sensorData.gps.currentLocation.latitude = 0;
        v.sensorData.gps.currentLocation.longitude = 0;
        v.sensorData.gps.currentLocation.altitude = 0;
        v.sensorData.gps.currentLocation.accuracy = 0;
        v.sensorData.gps.currentLocation.speed = 0;
        v.sensorData.gps.currentLocation.speedKm = 0;
        v.sensorData.gps.currentLocation.distanceBetweenLocations = 0;
//        v.sensorData.gps.currentLocation.location = null;
//        v.sensorData.gps.currentLocation.locationLast = null;
        // Pedometer
        v.sensorData.pedometer = new Variables.SensorData.Pedometer();
        v.sensorData.pedometer.nSteps = 0;
        // Rotation vector
        v.sensorData.rotation = new Variables.SensorData.Rotation();
        v.sensorData.rotation.x = 0;
        v.sensorData.rotation.y = 0;
        v.sensorData.rotation.z = 0;

        /* MovStatus */
        v.movStatus = new Variables.MovStatus();
        v.movStatus.mov = Constants.MovStatus.INITIAL;
        v.movStatus.movLast = Constants.MovStatus.INITIAL;
        v.movStatus.movSub = Constants.MovSubStatus.INITIAL;
        v.movStatus.movSubLast = Constants.MovSubStatus.INITIAL;
        v.movStatus.Changed = false;
        v.movStatus.changedFromStill = false;

        /* ServiceStatus */
        v.serviceStatus = new Variables.ServiceStatus();
        On = false;
        v.serviceStatus.logRecordOn = false;

        /* ServiceData */
        v.serviceData = new Variables.ServiceData();
        v.serviceData.runTime = 0;
        v.serviceData.currentTimeStep = 0;
        v.serviceData.startTime = 0;
        v.serviceData.currentTime = 0;
        v.serviceData.lastTime = 0;

        /* ServiceRequest */
        v.serviceRequest = new Variables.ServiceRequest();
        v.serviceRequest.newLogFile = false;
        v.serviceRequest.logFileClose = false;
        v.serviceRequest.mapUpdate = false;
        v.serviceRequest.distanceUpdate = false;

        /* Detection */
        v.detection = new Variables.Detection();
        // MovQuantityThresholds
        v.detection.movQuantityThresholds = new Variables.Detection.MovQuantityThresholds();
        v.detection.movQuantityThresholds.min = new Variables.Detection.MovQuantityThresholds.Min();
        v.detection.movQuantityThresholds.min.Walking = 0;
        v.detection.movQuantityThresholds.min.Running = 0;
        v.detection.movQuantityThresholds.min.VehicleStill = 0;
        v.detection.movQuantityThresholds.min.VehicleRunning = 0;
        v.detection.movQuantityThresholds.max = new Variables.Detection.MovQuantityThresholds.Max();
        v.detection.movQuantityThresholds.max.StillAbsolutely = 0;
        v.detection.movQuantityThresholds.max.Still = 0;
        v.detection.movQuantityThresholds.max.Walking = 0;
        v.detection.movQuantityThresholds.max.Running = 0;
        v.detection.movQuantityThresholds.max.VehicleStill = 0;
        v.detection.movQuantityThresholds.max.VehicleRunning = 0;
        v.detection.movQuantityThresholds.max.VehicleHavingPark = 0;
        // TimeForChangingTo
        v.detection.timeForChangingTo = new Variables.Detection.TimeForChangingTo();
        v.detection.timeForChangingTo.Still = 0;
        v.detection.timeForChangingTo.Walking = 0;
        v.detection.timeForChangingTo.Running = 0;
        v.detection.timeForChangingTo.Vehicle = 0;
        v.detection.timeForChangingTo.VehicleParked = 0;
        // Locations
        v.detection.locations = new Variables.Detection.Locations();
        v.detection.locations.currentSession = false;
        v.detection.locations.needToSetUnknownActivityPathEnding=false;
        v.detection.locations.path = new Variables.Detection.Locations.Path();
        v.detection.locations.path.firstPath = true;
        v.detection.locations.path.trackingOn = false;
        v.detection.locations.place = new Variables.Detection.Locations.Place();
        v.detection.locations.place.firstPlace = true;
        v.detection.locations.place.timeForNewPlace = 0;
        v.detection.locations.place.trackingOn = false;
        // Place ComputeCurrentLocation
        v.detection.locations.place.computeCurrentPlace = new Variables.Detection.Locations.Place.ComputeCurrentPlace();
        v.detection.locations.place.computeCurrentPlace.latitudeSum = 0;
        v.detection.locations.place.computeCurrentPlace.longitudeSum = 0;
        v.detection.locations.place.computeCurrentPlace.altitudeSum = 0;
        v.detection.locations.place.computeCurrentPlace.nLocations = 0;
        // Path ComputeCurrentLocation
        v.detection.locations.path.computeCurrentSpeed = new Variables.Detection.Locations.Path.ComputeCurrentSpeed();
        v.detection.locations.path.computeCurrentSpeed.speedSum = 0;
        // Times
        v.detection.times = new Variables.Detection.Times();
        v.detection.times.timeWhileDetectingToAdd = 0;
        // Pedometer
        v.detection.pedometer = new Variables.Detection.Pedometer();
        v.detection.pedometer.detectionStatus = 0;
        v.detection.pedometer.currentStepsWhenPathStarted = 0;
    }

    public void initDetectionThresholdsVariables ()
    {
        v.detection.movQuantityThresholds.max.StillAbsolutely=c.detection.movQuantity.far.max.StillAbsolutely;
        v.detection.movQuantityThresholds.max.Still=c.detection.movQuantity.far.max.Still;

        v.detection.movQuantityThresholds.min.Walking=c.detection.movQuantity.far.min.Walking;
        v.detection.movQuantityThresholds.max.Walking=c.detection.movQuantity.far.max.Walking;

        v.detection.movQuantityThresholds.min.Running=c.detection.movQuantity.far.min.Running;
        v.detection.movQuantityThresholds.max.Running=c.detection.movQuantity.far.max.Running;

        v.detection.movQuantityThresholds.min.VehicleStill=c.detection.movQuantity.far.min.VehicleStill;
        v.detection.movQuantityThresholds.max.VehicleStill=c.detection.movQuantity.far.max.VehicleStill;

        v.detection.movQuantityThresholds.min.VehicleRunning=c.detection.movQuantity.far.min.VehicleRunning;
        v.detection.movQuantityThresholds.max.VehicleRunning=c.detection.movQuantity.far.max.VehicleRunning;

        v.detection.movQuantityThresholds.max.VehicleHavingPark=c.detection.movQuantity.far.max.VehicleHavingPark;
    }

    public void initTimeLine ()
    {
        if (timeLineExits())
        {
            timeLine=readTimeLine();
            if (currentDayExits())
            {
                currentDay=readDay(timeLine.item.get(timeLine.nDays-1).fileName);
                Toast.makeText(this,"current Day EXISTS",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"current Day does NOT exist",Toast.LENGTH_SHORT).show();
                currentDay = createCurrentDay();
                newCurrentDayToTimeLine();
            }
        }
        else
        {
            timeLine=createTimeLine();
            currentDay = createCurrentDay();
            Toast.makeText(this,"TimeLine created",Toast.LENGTH_SHORT).show();
        }
    }

    public Constants readFileSettings ()
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

    public void createNotification ()
    {
        int icon;
        String text;
        String ticker;
        String title;
        String ns;

        icon=R.drawable.ic_launcher;
        text="Waiting for data";
        ticker="Starting Service";
        title="Nomada";
        ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) this.getSystemService(ns);

        notificationIntent = new Intent(Intent.ACTION_MAIN);
        notificationIntent.setClass(getApplicationContext(), MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        mNotificationBuilder = new Notification.Builder(this);
        mNotificationBuilder
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .build();

        notification=mNotificationBuilder.build();

        startForeground(17, notification);
        mNotificationManager.notify(17, notification);
//        startForeground(c.notificationService.id, notification);
//        mNotificationManager.notify(c.notificationService.id, notification);
    }

    public void initAcc ()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void initProximity ()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initLight ()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initRotation ()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void initVibrator ()
    {
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void initGps (long UpdateTime, float PrecisionMeters)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                UpdateTime,
                PrecisionMeters, this);
        v.sensorData.gps.currentTime=System.currentTimeMillis();
        v.sensorData.gps.lastTime=v.sensorData.gps.currentTime;
    }

    public void initServiceTime ()
    {
        v.serviceData.startTime = System.currentTimeMillis();
        v.serviceData.currentTime=v.serviceData.startTime;
        v.serviceData.lastTime=v.serviceData.currentTime;
    }

    public void initTimer ()
    {
        Timer = new Handler();
        Timer.removeCallbacks(updateData);
        Timer.postDelayed(updateData, c.updateTimer.Service);
    }


    // Gps /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location)
    {
        //v.sensorData.gps.trackingOn=true;

        if (v.sensorData.gps.firstTime)
        {
            v.sensorData.gps.firstTime=false;
            v.sensorData.gps.currentLocation.locationLast= location;
        }

        // Current Time Step
        v.sensorData.gps.currentTime=System.currentTimeMillis();
        v.sensorData.gps.currentTimeStep=v.sensorData.gps.currentTime-v.sensorData.gps.lastTime;
        v.sensorData.gps.lastTime=v.sensorData.gps.currentTime;

        // Location
        v.sensorData.gps.currentLocation.location = location;

        // Position Data
        v.sensorData.gps.currentLocation.longitude=location.getLongitude();
        v.sensorData.gps.currentLocation.latitude=location.getLatitude();
        v.sensorData.gps.currentLocation.altitude=location.getAltitude();
        v.sensorData.gps.currentLocation.accuracy=location.getAccuracy();

        // Distance and speed
        v.sensorData.gps.currentLocation.speed=location.getSpeed();
        v.sensorData.gps.currentLocation.speedKm=v.sensorData.gps.currentLocation.speed*(float)3.6;
        v.sensorData.gps.currentLocation.distanceBetweenLocations=location.distanceTo(v.sensorData.gps.currentLocation.locationLast);

        // New Location
        if ((location != v.sensorData.gps.currentLocation.locationLast) && (v.sensorData.gps.currentLocation.accuracy < 51))
        {
            v.sensorData.gps.trackingOn=true;
            trackLocations();

            // Add location and update lastLocation
            v.sensorData.gps.currentLocation.locationLast=v.sensorData.gps.currentLocation.location;
            currentDay.stats.gps.nTotalLocationPoints++;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        v.sensorData.gps.Status = status;
        switch (v.sensorData.gps.Status)
        {
            case 0:
                Toast.makeText(this,"GPS Out of Service",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this,"GPS Temporarily Unavailable",Toast.LENGTH_SHORT).show();
                break;
            case 2:
//                Toast.makeText(this,"GPS Available",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s)
    {
        Toast.makeText(this,"Provider enable",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s)
    {

    }

    // Sensors
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType)
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                // Accelerometer Data
                v.sensorData.accelerometer.current.x = sensorEvent.values[0];
                v.sensorData.accelerometer.current.y = sensorEvent.values[1];
                v.sensorData.accelerometer.current.z = sensorEvent.values[2];

                // Filter Noise
                if (Math.abs(v.sensorData.accelerometer.current.x) < c.detection.accelerometer.minAcceleration)
                {
                    v.sensorData.accelerometer.current.x=0;
                }
                if (Math.abs(v.sensorData.accelerometer.current.y) < c.detection.accelerometer.minAcceleration)
                {
                    v.sensorData.accelerometer.current.y=0;
                }
                if (Math.abs(v.sensorData.accelerometer.current.z) < c.detection.accelerometer.minAcceleration)
                {
                    v.sensorData.accelerometer.current.z=0;
                }

                // Computing Average
                v.sensorData.accelerometer.compute.nSamples++;
                v.sensorData.accelerometer.compute.average.signed.x += v.sensorData.accelerometer.current.x;
                v.sensorData.accelerometer.compute.average.signed.y += v.sensorData.accelerometer.current.y;
                v.sensorData.accelerometer.compute.average.signed.z += v.sensorData.accelerometer.current.z;
                v.sensorData.accelerometer.compute.average.abs.x += Math.abs(v.sensorData.accelerometer.current.x);
                v.sensorData.accelerometer.compute.average.abs.y += Math.abs(v.sensorData.accelerometer.current.y);
                v.sensorData.accelerometer.compute.average.abs.z += Math.abs(v.sensorData.accelerometer.current.z);
                break;
            case Sensor.TYPE_PROXIMITY:
                v.sensorData.proximity.value=sensorEvent.values[0];
                updateMovQuantityThresholdForProximity();
                break;
            case Sensor.TYPE_LIGHT:
                v.sensorData.light.value=sensorEvent.values[0];
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                v.sensorData.rotation.x=sensorEvent.values[0];
                v.sensorData.rotation.y=sensorEvent.values[1];
                v.sensorData.rotation.z=sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }
    
    public void resetAccVariables ()
    {
        v.sensorData.accelerometer.compute.average.signed.x = 0;
        v.sensorData.accelerometer.compute.average.signed.y = 0;
        v.sensorData.accelerometer.compute.average.signed.z = 0;
        v.sensorData.accelerometer.compute.average.abs.x = 0;
        v.sensorData.accelerometer.compute.average.abs.y = 0;
        v.sensorData.accelerometer.compute.average.abs.z = 0;
        v.sensorData.accelerometer.compute.nSamples = 0;
    }

    public void stepCounter ()
    {
        if ((v.movStatus.mov == Constants.MovStatus.WALKING) || (v.movStatus.mov == Constants.MovStatus.RUNNING))
        {
            // Go up
            if ((v.sensorData.accelerometer.movQuantityAbs - v.sensorData.accelerometer.movQuantityAbsLast) > 0)
            {
                v.detection.pedometer.detectionStatus = 1;
            }
            else if (v.detection.pedometer.detectionStatus == 1)
            {
                // Waiting for Go down
                if ((v.sensorData.accelerometer.movQuantityAbs - v.sensorData.accelerometer.movQuantityAbsLast) < 0)
                {
                    v.detection.pedometer.detectionStatus = 2;
                }
            }
            if (v.detection.pedometer.detectionStatus == 2) {
                // Step Detected
                v.detection.pedometer.detectionStatus = 0;
                v.sensorData.pedometer.nSteps++;
            }
        }
        else
        {
            v.detection.pedometer.detectionStatus = 0;
        }
        v.sensorData.accelerometer.movQuantityAbsLast = v.sensorData.accelerometer.movQuantityAbs;
    }

    // Logging data into a file ////////////////////////////////////////////////////////////////////////
    public static class logFileToWrite
    {

        //Returns a FileWriter object for the specified file that other methods can use
        public static FileWriter createLogFileWriter(String logFileName){
            FileWriter fileWriter = null;

            File root = new File(Environment.getExternalStorageDirectory(), "SDR");
            if (!root.exists()) {   //Create the subfolder if it does not exist
                root.mkdirs();
            }
            File file = new File(root.getPath(), logFileName);    //Create the file object using the provided file name

            //Open the writer
            try{
                fileWriter = new FileWriter(file, true);
            }
            catch (IOException e){
                Log.v("LOG FILE", "Failed to open file for '" + logFileName + "'");
            }
            return fileWriter;
        }
    }

    private boolean logFileWrite(String Text)
    {
        try {
            DataWriter.append(Text);
            DataWriter.flush();
            return true;
        }
        catch (IOException e) {
            Log.e("Acc", "Could not write file: "+e.toString());
            return false;
        }
    }

    public void logFileInit ()
    {
        String fileHeader;
        String fileName;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        int ms_day_offset = ms + minute*60000 + hour*3600000;

        String sMonth;
        String sDay;
        String sHour;
        String sMinute;
        String sSecond;

        // Better calendar
        if (month < 10)
        {
            sMonth="0"+month;
        }
        else
        {
            sMonth=month+"";
        }
        if (day < 10)
        {
            sDay="0"+day;
        }
        else
        {
            sDay=day+"";
        }
        if (hour < 10)
        {
            sHour="0"+hour;
        }
        else
        {
            sHour=hour+"";
        }
        if (minute < 10)
        {
            sMinute="0"+minute;
        }
        else
        {
            sMinute=minute+"";
        }
        if (second < 10)
        {
            sSecond="0"+second;
        }
        else
        {
            sSecond=second+"";
        }

        // File name
        fileName = year+ "-" +sMonth+ "-" +sDay+ " " +sHour+ "." +sMinute+ "."+sSecond+".nmd";

        // File header
        fileHeader = "NOMADA\n"
                +sDay+ "/" +sMonth+ "/" +year+ " " +sHour+ ":" +sMinute+ ":" +sSecond+"\n"
                +"ms_day_offset"+" "+ms_day_offset+"\n"
                +"Time "
                +"MovStatus "
                +"MovSubStatus "
                +"nPlaces "
                +"nPaths "
                +"Latitude "
                +"Longitude "
                +"Altitude "
                +"speedKm "
                +"speedAccKm "
                +"Distance "
                +"distanceAcc "
                +"Accuracy "
                +"xmAcc "
                +"ymAcc "
                +"zmAcc "
                +"movQuantity "
                +"xmAbsAcc "
                +"ymAbsAcc "
                +"zmAbsAcc "
                +"movQuantityAbs "
                +"Light "
                +"Proximity "
                +"nSteps "
                +"distanceTotal "
                +"distanceTotalAcc"
                +"\n";
        // Create file
        DataWriter = logFileToWrite.createLogFileWriter(fileName);
        if(DataWriter==null)
        {
            Log.v("Acc", "Failed to open file");
        }

        // Write header
        try
        {
            DataWriter.append(fileHeader);
            DataWriter.flush();
            Toast.makeText(this,"File "+fileName+" created",Toast.LENGTH_SHORT).show();
            //return true;
        }
        catch (IOException e)
        {
            Log.e("Acc", "Could not write file: "+e.toString());
            //  return false;
        }
    }

    public void logFileClose ()
    {
        try {
            DataWriter.close();
            Toast.makeText(this,"Recording File closed",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Acc", "Could not write to phone accelerometer log file: " + e.toString());
        }
    }

    // Functions ///////////////////////////////////////////////////////////////////////////////////////
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

    public String CalendarToString ()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        int ms_day_offset = ms + minute*60000 + hour*3600000;

        String sMonth;
        String sDay;
        String sHour;
        String sMinute;
        String sSecond;

        String sCalendar;

        // Better calendar
        if (month < 10)
        {
            sMonth="0"+month;
        }
        else
        {
            sMonth=month+"";
        }
        if (day < 10)
        {
            sDay="0"+day;
        }
        else
        {
            sDay=day+"";
        }
        if (hour < 10)
        {
            sHour="0"+hour;
        }
        else
        {
            sHour=hour+"";
        }
        if (minute < 10)
        {
            sMinute="0"+minute;
        }
        else
        {
            sMinute=minute+"";
        }
        if (second < 10)
        {
            sSecond="0"+second;
        }
        else
        {
            sSecond=second+"";
        }

        sCalendar = year+"-"+sMonth+"-"+sDay+" "+sHour+":"+sMinute+":"+sSecond;
        return sCalendar;
    }

    public String currentTimeToString ()
    {
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String sHour;
        String sMinute;
        String sSecond;

        String sTime;

        if (hour < 10)
        {
            sHour="0"+hour;
        }
        else
        {
            sHour=hour+"";
        }
        if (minute < 10)
        {
            sMinute="0"+minute;
        }
        else
        {
            sMinute=minute+"";
        }
        if (second < 10)
        {
            sSecond="0"+second;
        }
        else
        {
            sSecond=second+"";
        }

        sTime = sHour+":"+sMinute+":"+sSecond;
        return sTime;
    }

    // Notifications ///////////////////////////////////////////////////////////////////////////////////

    // Mejorar esto
    public void updateNotification(Constants.NotificationType action, Constants.MovStatus status)
    {
        int icon;
        String text;
        String ticker;
        String title;

        switch(status)
        {
            case STILL:
                icon=R.drawable.yellow;
                text=getText(R.string.text_still)+" "+msToTimeString(currentDay.stats.times.still.Total);
                ticker=(String)getText(R.string.ticket_still);
                break;
            case WALKING:
                icon=R.drawable.pink;
                text=getText(R.string.text_walking)+" "+msToTimeString(currentDay.stats.times.walking.Total);
                ticker=(String)getText(R.string.ticket_walking);
                break;
            case RUNNING:
                icon=R.drawable.green;
                text=getText(R.string.text_running)+" "+msToTimeString(currentDay.stats.times.running.Total);
                ticker=(String)getText(R.string.ticket_running);
                break;
            case VEHICLE:
                text=getText(R.string.text_car)+" "+msToTimeString(currentDay.stats.times.vehicle.Total);
                ticker=(String)getText(R.string.ticket_car);
                icon=R.drawable.cyan;
                break;
            default:
                text="Default";
                ticker="Default ticket";
                icon=R.drawable.ic_launcher;
                break;
        }

        switch(action)
        {
            case NOTIFICATION_STATUS_CHANGED:
                mNotificationBuilder
                        .setContentText(text)
                        .setSmallIcon(icon);
                //  .setTicker(ticker);
                break;

            case NOTIFICATION_UPDATE:
                mNotificationBuilder.setContentText(text);
                break;

            default:
                break;
        }

        mNotificationManager.notify(
                c.notificationService.id,
                mNotificationBuilder.build());
    }
    
    // Updates /////////////////////////////////////////////////////////////////////////////////////////
    public void updateAll()
    {
        // Update service time
        updateServiceTime();

        // Update Gps status
        updateGpsStatus();

        // Update accelerometer data
        updateAccelerometerData();

        // Update log file
        updateLogFile();

        // Update motion activity
        updateMov();

        // Update locations
        //updateLocations();

        // Check change in movStatus
        updateNotifications();

        // Step Counter
        stepCounter();
    }

    public void updateServiceTime ()
    {
        v.serviceData.currentTime = System.currentTimeMillis();
        v.serviceData.runTime = v.serviceData.currentTime - v.serviceData.startTime;
        if (v.serviceData.currentTime > v.serviceData.lastTime)
        {
            v.serviceData.currentTimeStep = v.serviceData.currentTime - v.serviceData.lastTime;
        }
        else
        {
            v.serviceData.currentTimeStep = c.updateTimer.Service;
        }
        v.serviceData.lastTime = v.serviceData.currentTime;
    }

    public void updateGpsStatus ()
    {
        // Time without GPS
        v.sensorData.gps.timeNoTracking=v.serviceData.currentTime-v.sensorData.gps.lastTime;

        // Manage no GPS
        if (v.sensorData.gps.timeNoTracking > c.gps.maxTimeWithoutGpsFix)
        {
            v.sensorData.gps.trackingOn=false;
            v.sensorData.gps.currentLocation.speed=0;
            v.sensorData.gps.currentLocation.speedKm=0;
            v.sensorData.gps.currentLocation.distanceBetweenLocations=0;
            v.serviceRequest.distanceUpdate=false;
            v.serviceRequest.mapUpdate=false;
        }
    }

    public void updateAccelerometerData ()
    {
        // Ts
        if (v.serviceData.currentTimeStep > 0) {
            v.sensorData.accelerometer.compute.Ts = (float) v.serviceData.currentTimeStep / 1000;
        }

        // Absolute Average
        if (v.sensorData.accelerometer.compute.nSamples > 0)
        {
            v.sensorData.accelerometer.averagePerTimeStep.abs.x = v.sensorData.accelerometer.compute.average.abs.x / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.averagePerTimeStep.abs.y = v.sensorData.accelerometer.compute.average.abs.y / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.averagePerTimeStep.abs.z = v.sensorData.accelerometer.compute.average.abs.z / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.movQuantityAbs = v.sensorData.accelerometer.averagePerTimeStep.abs.x + v.sensorData.accelerometer.averagePerTimeStep.abs.y + v.sensorData.accelerometer.averagePerTimeStep.abs.z;
        }

        // Real Average
        if (v.sensorData.accelerometer.compute.nSamples > 0)
        {
            v.sensorData.accelerometer.averagePerTimeStep.signed.x = v.sensorData.accelerometer.compute.average.signed.x / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.averagePerTimeStep.signed.y = v.sensorData.accelerometer.compute.average.signed.y / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.averagePerTimeStep.signed.z = v.sensorData.accelerometer.compute.average.signed.z / v.sensorData.accelerometer.compute.nSamples;
            v.sensorData.accelerometer.movQuantity = v.sensorData.accelerometer.averagePerTimeStep.signed.x + v.sensorData.accelerometer.averagePerTimeStep.signed.y + v.sensorData.accelerometer.averagePerTimeStep.signed.z;
        }

        // Derivative from acceleration
        if (v.serviceData.currentTimeStep > 0)
        {
            // Speed from acceleration
            v.sensorData.accelerometer.speedPerTimeStep.x = v.sensorData.accelerometer.averagePerTimeStep.signed.x * v.sensorData.accelerometer.compute.Ts;
            v.sensorData.accelerometer.speedPerTimeStep.y = v.sensorData.accelerometer.averagePerTimeStep.signed.y * v.sensorData.accelerometer.compute.Ts;
            v.sensorData.accelerometer.speedPerTimeStep.z = v.sensorData.accelerometer.averagePerTimeStep.signed.z * v.sensorData.accelerometer.compute.Ts;
            v.sensorData.accelerometer.speedPerTimeStep.speed = Math.sqrt(Math.pow(v.sensorData.accelerometer.speedPerTimeStep.x,2)+Math.pow(v.sensorData.accelerometer.speedPerTimeStep.y,2)+Math.pow(v.sensorData.accelerometer.speedPerTimeStep.z,2));
            v.sensorData.accelerometer.speedPerTimeStep.speedKm = v.sensorData.accelerometer.speedPerTimeStep.speed * 3.6;
            // Distance from acceleration
            v.sensorData.accelerometer.distancePerTimeStep.x = (v.sensorData.accelerometer.speedPerTimeStep.x * v.sensorData.accelerometer.compute.Ts);
            v.sensorData.accelerometer.distancePerTimeStep.y = (v.sensorData.accelerometer.speedPerTimeStep.y * v.sensorData.accelerometer.compute.Ts);
            v.sensorData.accelerometer.distancePerTimeStep.z = (v.sensorData.accelerometer.speedPerTimeStep.z * v.sensorData.accelerometer.compute.Ts);
            v.sensorData.accelerometer.distancePerTimeStep.distance = Math.sqrt(Math.pow(v.sensorData.accelerometer.distancePerTimeStep.x,2)+Math.pow(v.sensorData.accelerometer.distancePerTimeStep.y,2)+Math.pow(v.sensorData.accelerometer.distancePerTimeStep.z,2));
        }
        // Reset acceleration variables
        resetAccVariables();
    }

    public void updateLogFile ()
    {
        // Log file close
        if (v.serviceRequest.logFileClose && !v.serviceStatus.logRecordOn)
        {
            v.serviceRequest.logFileClose = false;
            logFileClose();
        }
        // New log file
        else if (v.serviceRequest.newLogFile && v.serviceStatus.logRecordOn)
        {
            v.serviceRequest.newLogFile = false;
            logFileInit();
            String Text = v.serviceData.runTime+" "+
                    v.movStatus.mov+" "+
                    v.movStatus.movSub+" "+
                    currentDay.nPlaces+" "+
                    currentDay.nPaths+" "+
                    v.sensorData.gps.currentLocation.latitude+" "+
                    v.sensorData.gps.currentLocation.longitude+" "+
                    v.sensorData.gps.currentLocation.altitude+" "+
                    v.sensorData.gps.currentLocation.speedKm+" "+
                    v.sensorData.accelerometer.speedPerTimeStep.speedKm+" "+
                    v.sensorData.gps.currentLocation.distanceBetweenLocations+" "+
                    v.sensorData.accelerometer.distancePerTimeStep.distance+" "+
                    v.sensorData.gps.currentLocation.accuracy+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.x+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.y+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.z+" "+
                    v.sensorData.accelerometer.movQuantity+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.x+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.y+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.z+" "+
                    v.sensorData.accelerometer.movQuantityAbs+" "+
                    v.sensorData.light.value+" "+
                    v.sensorData.proximity+" "+
                    v.sensorData.pedometer.nSteps+" "+
                    currentDay.stats.distances.total+" "+
                    currentDay.stats.distances.totalDistanceByAcceleration+
                    "\n";
            logFileWrite(Text);
        }
        // Logging
        else if (v.serviceStatus.logRecordOn)
        {
            String Text = v.serviceData.runTime+" "+
                    v.movStatus.mov+" "+
                    v.movStatus.movSub+" "+
                    currentDay.nPlaces+" "+
                    currentDay.nPaths+" "+
                    v.sensorData.gps.currentLocation.latitude+" "+
                    v.sensorData.gps.currentLocation.longitude+" "+
                    v.sensorData.gps.currentLocation.altitude+" "+
                    v.sensorData.gps.currentLocation.speedKm+" "+
                    v.sensorData.accelerometer.speedPerTimeStep.speedKm+" "+
                    v.sensorData.gps.currentLocation.distanceBetweenLocations+" "+
                    v.sensorData.accelerometer.distancePerTimeStep.distance+" "+
                    v.sensorData.gps.currentLocation.accuracy+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.x+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.y+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.signed.z+" "+
                    v.sensorData.accelerometer.movQuantity+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.x+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.y+" "+
                    v.sensorData.accelerometer.averagePerTimeStep.abs.z+" "+
                    v.sensorData.accelerometer.movQuantityAbs+" "+
                    v.sensorData.light.value+" "+
                    v.sensorData.proximity+" "+
                    v.sensorData.pedometer.nSteps+" "+
                    currentDay.stats.distances.total+" "+
                    currentDay.stats.distances.totalDistanceByAcceleration+
                    "\n";
            logFileWrite(Text);
        }
    }

    public void updateMov()
    {
        // Update times for changing to another movStatus
        updateTimesForChangingToMovStatus();
        // Check change of movStatus
        updateMovStatus();
        // Check change of movSubStatus and update data
        updateMovSubStatusAndData();
    }

    public void updateTimesForChangingToMovStatus()
    {
        switch(v.movStatus.mov)
        {
            case INITIAL:
                // To Running
                if (v.sensorData.accelerometer.movQuantityAbs >= v.detection.movQuantityThresholds.min.Running)
                {
                    v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                }
                // To Walking
                else if (v.sensorData.accelerometer.movQuantityAbs >= v.detection.movQuantityThresholds.min.Walking)
                {
                    v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                }
                // To Still
                else
                {
                    v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                }
                break;
            case STILL:
                // To Vehicle
                if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.VehicleRunning) && (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.VehicleRunning))
                {
                    v.detection.timeForChangingTo.Vehicle += v.serviceData.currentTimeStep;
                }
                // To Running
                else if (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.Running)
                {
                    if (v.sensorData.gps.trackingOn) {
                        if (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Running)
                        {
                            v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                        }
                    }
                    else
                    {
                        v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                    }
                }
                // To Walking
                else if (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.Walking)
                {
                    if (v.sensorData.gps.trackingOn) {
                        if (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Walking)
                        {
                            v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                        }
                        else // Prueba
                        {
                            v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                        }
                    }
                    else
                    {
                        v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                    }
                }
                // To Still
                else
                {
                    v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                }
                break;
            case WALKING:
                // To Vehicle
                if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.VehicleRunning) && (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.VehicleRunning))
                {
                    v.detection.timeForChangingTo.Vehicle += v.serviceData.currentTimeStep;
                }
                // To Running
                else if (v.sensorData.accelerometer.movQuantityAbs >= v.detection.movQuantityThresholds.min.Running)
                {
                    if (v.sensorData.gps.trackingOn)
                    {
                        if (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Running)
                        {
                            v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                        }
                    }
                    else
                    {
                        v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                    }
                }
                // To Still
                else if (v.sensorData.accelerometer.movQuantityAbs <= v.detection.movQuantityThresholds.max.Still)
                {
                    if (v.sensorData.gps.trackingOn)
                    {
                        if (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.Still)
                        {
                            v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                        }
                    }
                    v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                }
                // To Walking
                else
                {
                    v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                }
                break;
            case RUNNING:
                // To Vehicle
                if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.VehicleRunning) && (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.VehicleRunning))
                {
                    v.detection.timeForChangingTo.Vehicle += v.serviceData.currentTimeStep;
                }
                // To Walking
                else if ((v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.Walking) && (v.sensorData.accelerometer.movQuantityAbs <= v.detection.movQuantityThresholds.max.Walking))
                {
                    if (v.sensorData.gps.trackingOn)
                    {
                        if (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Walking)
                        {
                            v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                        }
                    }
                    else
                    {
                        v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                    }
                }
                // To Still
                else if (v.sensorData.accelerometer.movQuantityAbs <= v.detection.movQuantityThresholds.max.Still)
                {
                    if (v.sensorData.gps.trackingOn)
                    {
                        if (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.Still)
                        {
                            v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                        }
                    }
                    else
                    {
                        v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                    }
                }
                // To Running
                else
                {
                    v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                }
                break;
            case VEHICLE:
                //
                if (v.movStatus.movSubLast == Constants.MovSubStatus.VEHICLE_PARKED_SURE)
                {
                    // To Running
                    if (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.Running)
                    {
                        v.detection.timeForChangingTo.Running += v.serviceData.currentTimeStep;
                    }
                    // To Walking
                    else if (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.Walking)
                    {
                        v.detection.timeForChangingTo.Walking += v.serviceData.currentTimeStep;
                    }
                    // To Still
                    else
                    {
                        v.detection.timeForChangingTo.Still += v.serviceData.currentTimeStep;
                    }
                }
                // To VehicleParked
                else if ((v.sensorData.accelerometer.movQuantityAbs <= v.detection.movQuantityThresholds.max.VehicleHavingPark) && v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.VehicleStill))
                {
                    v.detection.timeForChangingTo.VehicleParked += v.serviceData.currentTimeStep;
                }
                // To Vehicle
                else
                {
                    v.detection.timeForChangingTo.Vehicle += v.serviceData.currentTimeStep;
                }
                break;
        }
    }

    public void updateMovStatus ()
    {
        // To Still
        if (v.detection.timeForChangingTo.Still >= c.detection.timeForChangingTo.Still)
        {
            if (v.movStatus.mov != Constants.MovStatus.STILL)
            {
                v.detection.times.timeWhileDetectingToAdd += v.detection.timeForChangingTo.Still+v.detection.timeForChangingTo.Walking+v.detection.timeForChangingTo.Running+v.detection.timeForChangingTo.Vehicle+v.detection.timeForChangingTo.VehicleParked;
            }
            v.movStatus.mov=Constants.MovStatus.STILL;
            v.detection.timeForChangingTo.Still=0;
            v.detection.timeForChangingTo.Walking=0;
            v.detection.timeForChangingTo.Running=0;
            v.detection.timeForChangingTo.Vehicle=0;
            v.detection.timeForChangingTo.VehicleParked=0;
            currentDay.stats.nDetections.Still++;
            ////////////////////////////// PRUEBA
            if (!v.detection.locations.place.trackingOn)
            {
                newStill();
            }
            //////////////////////////////
        }
        // To Walking
        else if (v.detection.timeForChangingTo.Walking >= c.detection.timeForChangingTo.Walking)
        {
            if (v.movStatus.mov != Constants.MovStatus.WALKING)
            {
                v.detection.times.timeWhileDetectingToAdd += v.detection.timeForChangingTo.Still+v.detection.timeForChangingTo.Walking+v.detection.timeForChangingTo.Running+v.detection.timeForChangingTo.Vehicle+v.detection.timeForChangingTo.VehicleParked;
            }
            v.movStatus.mov=Constants.MovStatus.WALKING;
            v.detection.timeForChangingTo.Still=0;
            v.detection.timeForChangingTo.Walking=0;
            v.detection.timeForChangingTo.Running=0;
            v.detection.timeForChangingTo.Vehicle=0;
            v.detection.timeForChangingTo.VehicleParked=0;
            currentDay.stats.nDetections.Walking++;
            ////////////////////////////// PRUEBA
            if (v.movStatus.movLast != Constants.MovStatus.WALKING)
            {
                newMovement();
            }
            /////////////////////////////////

        }
        // To Running
        else if (v.detection.timeForChangingTo.Running >= c.detection.timeForChangingTo.Running)
        {
            if (v.movStatus.mov != Constants.MovStatus.RUNNING)
            {
                v.detection.times.timeWhileDetectingToAdd += v.detection.timeForChangingTo.Still+v.detection.timeForChangingTo.Walking+v.detection.timeForChangingTo.Running+v.detection.timeForChangingTo.Vehicle+v.detection.timeForChangingTo.VehicleParked;
            }
            v.movStatus.mov=Constants.MovStatus.RUNNING;
            v.detection.timeForChangingTo.Still=0;
            v.detection.timeForChangingTo.Walking=0;
            v.detection.timeForChangingTo.Running=0;
            v.detection.timeForChangingTo.Vehicle=0;
            v.detection.timeForChangingTo.VehicleParked=0;
            currentDay.stats.nDetections.Running++;
            ////////////////////////////// PRUEBA
            if (v.movStatus.movLast != Constants.MovStatus.RUNNING)
            {
                newMovement();
            }
            /////////////////////////////////
        }
        // To Vehicle
        else if (v.detection.timeForChangingTo.Vehicle >= c.detection.timeForChangingTo.VehicleRunning)
        {
            Toast.makeText(this,"To Vehicle",Toast.LENGTH_SHORT).show();
            if (v.movStatus.mov != Constants.MovStatus.VEHICLE)
            {
                v.detection.times.timeWhileDetectingToAdd += v.detection.timeForChangingTo.Still+v.detection.timeForChangingTo.Walking+v.detection.timeForChangingTo.Running+v.detection.timeForChangingTo.Vehicle+v.detection.timeForChangingTo.VehicleParked;
            }
            v.movStatus.mov=Constants.MovStatus.VEHICLE;
            v.detection.timeForChangingTo.Still=0;
            v.detection.timeForChangingTo.Walking=0;
            v.detection.timeForChangingTo.Running=0;
            v.detection.timeForChangingTo.Vehicle=0;
            v.detection.timeForChangingTo.VehicleParked=0;
            currentDay.stats.nDetections.Vehicle++;
            ////////////////////////////// PRUEBA
            if (v.movStatus.movLast != Constants.MovStatus.VEHICLE)
            {
                newMovement();
            }
            /////////////////////////////////
        }
        // To Vehicle Having Parked
        else if (v.detection.timeForChangingTo.VehicleParked >= c.detection.timeForChangingTo.VehicleHaveParked)
        {
            Toast.makeText(this,"To Park "+v.detection.timeForChangingTo.VehicleParked+"/"+c.detection.timeForChangingTo.VehicleHaveParked,Toast.LENGTH_SHORT).show();
            v.movStatus.movSub = Constants.MovSubStatus.VEHICLE_PARKED_SURE;
            v.detection.times.timeWhileDetectingToAdd=v.detection.timeForChangingTo.VehicleParked;
            v.detection.timeForChangingTo.Still = 0;
            v.detection.timeForChangingTo.Walking = 0;
            v.detection.timeForChangingTo.Running = 0;
            v.detection.timeForChangingTo.Vehicle = 0;
            v.detection.timeForChangingTo.VehicleParked = 0;
            currentDay.stats.nDetections.VehicleParked++;
        }

        // Check if there is movStatus changes
        updateMovStatusChanges();

    }

    public void updateMovStatusChanges ()
    {
        if (v.movStatus.mov != v.movStatus.movLast)
        {
            v.movStatus.Changed=true;
            currentDay.stats.nDetections.Total++;
            if (v.movStatus.movLast == Constants.MovStatus.STILL || v.movStatus.movLast == Constants.MovStatus.INITIAL)
            {
                v.movStatus.changedFromStill=true;
            }
        }
        else
        {
            v.movStatus.Changed=false;
        }
    }

    public void updateMovSubStatusAndData ()
    {
        switch(v.movStatus.mov)
        {
            case STILL:
                // Still absolutely
                if (v.sensorData.accelerometer.movQuantityAbs < v.detection.movQuantityThresholds.max.StillAbsolutely)
                {
                    v.movStatus.movSub = Constants.MovSubStatus.STILL_SURE;
                    currentDay.stats.times.still.Absolutely += v.serviceData.currentTimeStep;
                    if (v.movStatus.Changed)
                    {
                        currentDay.stats.times.still.Absolutely += v.detection.times.timeWhileDetectingToAdd;
                        v.detection.times.timeWhileDetectingToAdd = 0;
                    }
                }
                // Still moving
                else if (v.sensorData.accelerometer.movQuantityAbs < v.detection.movQuantityThresholds.max.Still)
                {
                    // Still moving sure
                    if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.Still))
                    {
                        v.movStatus.movSub = Constants.MovSubStatus.STILL_MOVING_SURE;
                        currentDay.stats.times.still.MovingSure += v.serviceData.currentTimeStep;
                        if (v.movStatus.Changed)
                        {
                            currentDay.stats.times.still.MovingSure += v.detection.times.timeWhileDetectingToAdd;
                            v.detection.times.timeWhileDetectingToAdd = 0;
                        }
                    }
                    // Still moving probably
                    else
                    {
                        v.movStatus.movSub = Constants.MovSubStatus.STILL_MOVING_PROBABLY;
                        currentDay.stats.times.still.MovingProbably += v.serviceData.currentTimeStep;
                        if (v.movStatus.Changed)
                        {
                            currentDay.stats.times.still.MovingProbably += v.detection.times.timeWhileDetectingToAdd;
                            v.detection.times.timeWhileDetectingToAdd = 0;
                        }
                    }
                }
                // Update total still time
                currentDay.stats.times.still.Total += v.serviceData.currentTimeStep;
                // Update time for new place
                v.detection.locations.place.timeForNewPlace += v.serviceData.currentTimeStep;
                break;
            case WALKING:
                // Walking sure
                if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Walking) && (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.Walking)) {
                    v.movStatus.movSub = Constants.MovSubStatus.WALKING_SURE;
                    currentDay.stats.times.walking.Sure += v.serviceData.currentTimeStep;
                    if (v.serviceRequest.distanceUpdate)
                    {
                        currentDay.stats.distances.walking += v.sensorData.gps.currentLocation.distanceBetweenLocations;
                        v.serviceRequest.distanceUpdate=false;
                    }

                    if (v.movStatus.Changed) {
                        currentDay.stats.times.walking.Sure += v.detection.times.timeWhileDetectingToAdd;
                        v.detection.times.timeWhileDetectingToAdd = 0;
                    }
                }
                // Walking probably
                else
                {
                    v.movStatus.movSub = Constants.MovSubStatus.WALKING_PROBABLY;
                    currentDay.stats.times.walking.Probably += v.serviceData.currentTimeStep;
                    if (v.movStatus.Changed) {
                        currentDay.stats.times.walking.Probably += v.detection.times.timeWhileDetectingToAdd;
                        v.detection.times.timeWhileDetectingToAdd = 0;
                    }
                }
                // Update total walking time
                currentDay.stats.times.walking.Total += v.serviceData.currentTimeStep;
                break;
            case RUNNING:
                // Running sure
                if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.Running) && (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.Running)) {
                    v.movStatus.movSub = Constants.MovSubStatus.RUNNING_SURE;
                    currentDay.stats.times.running.Sure += v.serviceData.currentTimeStep;
                    if (v.serviceRequest.distanceUpdate)
                    {
                        currentDay.stats.distances.running += v.sensorData.gps.currentLocation.distanceBetweenLocations;
                        v.serviceRequest.distanceUpdate=false;
                    }
                    if (v.movStatus.Changed) {
                        currentDay.stats.times.running.Sure += v.detection.times.timeWhileDetectingToAdd;
                        v.detection.times.timeWhileDetectingToAdd = 0;
                    }
                }
                // Running probably
                else
                {
                    v.movStatus.movSub = Constants.MovSubStatus.RUNNING_PROBABLY;
                    currentDay.stats.times.running.Probably += v.serviceData.currentTimeStep;
                    if (v.movStatus.Changed) {
                        currentDay.stats.times.running.Probably += v.detection.times.timeWhileDetectingToAdd;
                        v.detection.times.timeWhileDetectingToAdd = 0;
                    }
                }
                // Update total running time
                currentDay.stats.times.running.Total += v.serviceData.currentTimeStep;
                break;
            case VEHICLE:
                if (v.movStatus.movSub != Constants.MovSubStatus.VEHICLE_PARKED_SURE)
                {
                    // Vehicle running sure
                    if (v.sensorData.gps.trackingOn && (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.VehicleRunning) && (v.sensorData.gps.currentLocation.speedKm > c.detection.speed.min.VehicleRunning))
                    {
                        v.movStatus.movSub = Constants.MovSubStatus.VEHICLE_RUNNING_SURE;
                        currentDay.stats.times.vehicle.RunningSure += v.serviceData.currentTimeStep;
                        if (v.serviceRequest.distanceUpdate)
                        {
                            currentDay.stats.distances.vehicle += v.sensorData.gps.currentLocation.distanceBetweenLocations;
                            v.serviceRequest.distanceUpdate=false;
                        }

                        if (v.movStatus.Changed)
                        {
                            currentDay.stats.times.vehicle.RunningSure += v.detection.times.timeWhileDetectingToAdd;
                            v.detection.times.timeWhileDetectingToAdd = 0;
                        }
                    }
                    // Vehicle still sure
                    else if (v.sensorData.gps.trackingOn && (v.sensorData.gps.currentLocation.speedKm <= c.detection.speed.max.VehicleStill) && (v.sensorData.accelerometer.movQuantityAbs <= v.detection.movQuantityThresholds.max.VehicleStill))
                    {
                        v.movStatus.movSub = Constants.MovSubStatus.VEHICLE_STILL_SURE;
                        currentDay.stats.times.vehicle.StillSure += v.serviceData.currentTimeStep;
                        if (v.movStatus.Changed)
                        {
                            currentDay.stats.times.vehicle.StillSure += v.detection.times.timeWhileDetectingToAdd;
                            v.detection.times.timeWhileDetectingToAdd = 0;
                        }
                    }
                    // Vehicle no GPS
                    else if (!v.sensorData.gps.trackingOn && (v.sensorData.accelerometer.movQuantityAbs > v.detection.movQuantityThresholds.min.VehicleRunning))
                    {
                        v.movStatus.movSub = Constants.MovSubStatus.VEHICLE_RUNNING_NO_GPS_PROBABLY;
                        currentDay.stats.times.vehicle.NoGPS += v.serviceData.currentTimeStep;
                        if (v.movStatus.Changed) {
                            currentDay.stats.times.vehicle.NoGPS += v.detection.times.timeWhileDetectingToAdd;
                            v.detection.times.timeWhileDetectingToAdd = 0;
                        }
                    }
                }
                // Update total vehicle time
                currentDay.stats.times.vehicle.Total += v.serviceData.currentTimeStep;
                break;
        }

        // Update total time
        currentDay.stats.times.Total += v.serviceData.currentTimeStep;

        // If movStatus changed, reset timeForNewPlace
        if (v.movStatus.Changed)
        {
            v.detection.locations.place.timeForNewPlace = 0;
        }
    }

    public void updateNotifications ()
    {

        if (v.movStatus.Changed)
        {
            updateNotification(Constants.NotificationType.NOTIFICATION_STATUS_CHANGED,v.movStatus.mov);
        }
        else
        {
            updateNotification(Constants.NotificationType.NOTIFICATION_UPDATE,v.movStatus.mov);
        }
        v.movStatus.movLast = v.movStatus.mov;
        v.movStatus.movSubLast = v.movStatus.movSub;
    }

    void updateMovQuantityThresholdForProximity()
    {
        if (v.sensorData.proximity.value == 0) // Near
        {
            v.detection.movQuantityThresholds.max.StillAbsolutely=c.detection.movQuantity.near.max.StillAbsolutely;
            v.detection.movQuantityThresholds.max.Still=c.detection.movQuantity.near.max.Still;

            v.detection.movQuantityThresholds.min.Walking=c.detection.movQuantity.near.min.Walking;
            v.detection.movQuantityThresholds.max.Walking=c.detection.movQuantity.near.max.Walking;

            v.detection.movQuantityThresholds.min.Running=c.detection.movQuantity.near.min.Running;
            v.detection.movQuantityThresholds.max.Running=c.detection.movQuantity.near.max.Running;

            v.detection.movQuantityThresholds.min.VehicleStill=c.detection.movQuantity.near.min.VehicleStill;
            v.detection.movQuantityThresholds.max.VehicleStill=c.detection.movQuantity.near.max.VehicleStill;

            v.detection.movQuantityThresholds.min.VehicleRunning=c.detection.movQuantity.near.min.VehicleRunning;
            v.detection.movQuantityThresholds.max.VehicleRunning=c.detection.movQuantity.near.max.VehicleRunning;

            v.detection.movQuantityThresholds.max.VehicleHavingPark=c.detection.movQuantity.near.max.VehicleHavingPark;

        }
        else
        {
            v.detection.movQuantityThresholds.max.StillAbsolutely=c.detection.movQuantity.far.max.StillAbsolutely;
            v.detection.movQuantityThresholds.max.Still=c.detection.movQuantity.far.max.Still;

            v.detection.movQuantityThresholds.min.Walking=c.detection.movQuantity.far.min.Walking;
            v.detection.movQuantityThresholds.max.Walking=c.detection.movQuantity.far.max.Walking;

            v.detection.movQuantityThresholds.min.Running=c.detection.movQuantity.far.min.Running;
            v.detection.movQuantityThresholds.max.Running=c.detection.movQuantity.far.max.Running;

            v.detection.movQuantityThresholds.min.VehicleStill=c.detection.movQuantity.far.min.VehicleStill;
            v.detection.movQuantityThresholds.max.VehicleStill=c.detection.movQuantity.far.max.VehicleStill;

            v.detection.movQuantityThresholds.min.VehicleRunning=c.detection.movQuantity.far.min.VehicleRunning;
            v.detection.movQuantityThresholds.max.VehicleRunning=c.detection.movQuantity.far.max.VehicleRunning;

            v.detection.movQuantityThresholds.max.VehicleHavingPark=c.detection.movQuantity.far.max.VehicleHavingPark;
        }
    }

    // Locations ///////////////////////////////////////////////////////////////////////////////////////
    public void newMovement ()
    {
        // New UnknownActivityPath if to the last place follows other place
        if (currentDay.nItems > 0)
        {
            if (!v.detection.locations.currentSession)
            {
                v.detection.locations.currentSession = true;
                v.detection.locations.needToSetUnknownActivityPathEnding = true;
                newUnknownActivityPath();
            }
//            if (currentDay.typeOfItem.get(currentDay.nItems - 1) == 'P')
//            {
//                newUnknownActivityPath();
//            }
        }
        // End current place
        if (v.detection.locations.place.trackingOn)
        {
            endPlace();
        }
        // End last path
        if (v.detection.locations.path.trackingOn)
        {
            endPath();
        }
        // New path
        newPath();
    }

    public void newStill ()
    {
        // New UnknownActivityPath if to the last place follows other place
        if (currentDay.nItems > 0)
        {
            if (!v.detection.locations.currentSession)
            {
                v.detection.locations.currentSession = true;
                v.detection.locations.needToSetUnknownActivityPathEnding = true;
                newUnknownActivityPath();
            }
//            if (currentDay.typeOfItem.get(currentDay.nItems - 1) == 'P')
//            {
//                newUnknownActivityPath();
//            }
        }
        // End Path
        if (v.detection.locations.path.trackingOn)
        {
            endPath();
        }
        // New Place
        newPlace();
    }

//    public void updateLocations ()
//    {
//        // New Path
//        if (v.sensorData.gps.trackingOn && (v.movStatus.mov != Constants.MovStatus.STILL) && v.movStatus.Changed)
//        {
//            // End current place
//            if (v.detection.locations.place.trackingOn)
//            {
//                endPlace();
//            }
//            // End last path
//            if (v.detection.locations.path.trackingOn)
//            {
//                endPath();
//            }
//            // New path
//            newPath();
//        }
//        // New Place
//        else if ((v.movStatus.mov == Constants.MovStatus.STILL) && v.sensorData.gps.trackingOn && (v.detection.locations.place.timeForNewPlace >= c.detection.places.minTimeStillForNewPlace) && v.movStatus.changedFromStill)
//        {
//            // New UnknownActivityPath if to the last place follows other place
//            if (currentDay.nItems > 0)
//            {
//                if (currentDay.typeOfItem.get(currentDay.nItems - 1) == 'P')
//                {
//                    newUnknownActivityPath();
//                }
//            }
//            // End Path
//            if (v.detection.locations.path.trackingOn)
//            {
//                endPath();
//            }
//            // New Place
//            newPlace();
//        }
//        // No Tracking place or path
//        else if (!v.detection.locations.path.trackingOn && !v.detection.locations.place.trackingOn)
//        {
//            if (v.movStatus.mov == Constants.MovStatus.STILL)
//            {
//                newPlace();
//            }
//            else if (v.movStatus.mov != Constants.MovStatus.INITIAL)
//            {
//                newPath();
//            }
//        }
//    }

    public void newPath ()
    {
        Day.Coordinates pathPoint;
        Day.Coordinates linkLocation;
        Day.Path currentPath;

        // Activity distance update
        v.serviceRequest.distanceUpdate=true;



        // new Path
        currentPath=new Day.Path();

        // Link current path start routePoints with last place or path ending routePoints
        if (currentDay.nItems > 0)
        {
            switch (currentDay.typeOfItem.get(currentDay.nItems-1))
            {
                case 'P':
                    linkLocation=new Day.Coordinates();
                    linkLocation.latitude=currentDay.places.get(currentDay.nPlaces-1).latitude;
                    linkLocation.longitude=currentDay.places.get(currentDay.nPlaces-1).longitude;
                    linkLocation.altitude=currentDay.places.get(currentDay.nPlaces-1).altitude;
                    currentPath.routePoints.add(linkLocation);
                    currentPath.nRoutePoints++;
                    break;
                case 'R':
                    //linkLocation=currentDay.paths.get(currentDay.nPaths-1).routePoints.get(currentDay.paths.get(currentDay.nPaths-1).routePoints.size()-1);
                    linkLocation=currentDay.paths.get(currentDay.nPaths-1).routePoints.get(currentDay.paths.get(currentDay.nPaths-1).nRoutePoints-1);
                    currentPath.routePoints.add(linkLocation);
                    currentPath.nRoutePoints++;
                    break;
            }
        }

        // Set ActivityType
        currentPath.activityType = v.movStatus.mov;

        if (v.sensorData.gps.trackingOn)
        {
            // Set pathPoint coordinates
            pathPoint = new Day.Coordinates();
            pathPoint.latitude = v.sensorData.gps.currentLocation.latitude;
            pathPoint.longitude = v.sensorData.gps.currentLocation.longitude;
            pathPoint.altitude = v.sensorData.gps.currentLocation.altitude;

            // Add RoutePoints and update its variables
            currentPath.routePoints.add(pathPoint);
            currentPath.nRoutePoints++;

            // Set Initial Distance
            currentPath.distance = v.sensorData.gps.currentLocation.distanceBetweenLocations;

            // Set Initial Speed
            currentPath.speedAverage = v.sensorData.gps.currentLocation.speedKm;
            currentPath.speedMin = v.sensorData.gps.currentLocation.speedKm;
            currentPath.speedMax = v.sensorData.gps.currentLocation.speedKm;
            v.detection.locations.path.computeCurrentSpeed.speedSum = v.sensorData.gps.currentLocation.speedKm;

            // Update day activity distance
            switch (currentPath.activityType)
            {
                case WALKING:
                    currentDay.stats.distances.walking = currentPath.distance;
                    break;
                case RUNNING:
                    currentDay.stats.distances.running = currentPath.distance;
                    break;
                case VEHICLE:
                    currentDay.stats.distances.vehicle = currentPath.distance;
                    break;
            }

            // Update Total Distance
            currentDay.stats.distances.total += v.sensorData.gps.currentLocation.distanceBetweenLocations;
        }

        // Set TimeStart
        currentPath.timeStart=System.currentTimeMillis();
        currentPath.timeStartString = currentTimeToString();

        // Set TimeDuration
        currentPath.timeDuration = 0;
        currentPath.timeDurationString = msToTimeString(0);


        // Set Initial Pedometer data
        v.detection.pedometer.currentStepsWhenPathStarted = v.sensorData.pedometer.nSteps;
        currentPath.nSteps = v.sensorData.pedometer.nSteps - v.detection.pedometer.currentStepsWhenPathStarted;


        // PRUEBA ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        currentDay.stats.distances.totalDistanceByAcceleration += v.sensorData.accelerometer.distancePerTimeStep.distance;

        // Add Path and update its variables
        currentDay.paths.add(currentDay.nPaths, currentPath);
        currentDay.nPaths++;
        currentDay.nItems++;
        currentDay.typeOfItem.add('R');

        // Toast showing New Path
        Toast.makeText(this, "New Path "+currentDay.nPaths, Toast.LENGTH_SHORT).show();

        // Set path.trackingOn on
        v.detection.locations.path.trackingOn=true;
    }

//    public void newPath ()
//    {
//        Day.Coordinates pathPoint;
//        Day.Coordinates linkLocation;
//        Day.Path currentPath;
//
//        // Activity distance update
//        v.serviceRequest.distanceUpdate=true;
//
//        // Set pathPoint coordinates
//        pathPoint = new Day.Coordinates();
//        pathPoint.latitude=v.sensorData.gps.currentLocation.latitude;
//        pathPoint.longitude=v.sensorData.gps.currentLocation.longitude;
//        pathPoint.altitude=v.sensorData.gps.currentLocation.altitude;
//
//        // new Path
//        currentPath=new Day.Path();
//
//        // Link current path start routePoints with last place or path ending routePoints
//        if (currentDay.nItems > 0)
//        {
//            switch (currentDay.typeOfItem.get(currentDay.nItems-1))
//            {
//                case 'P':
//                    linkLocation=new Day.Coordinates();
//                    linkLocation.latitude=currentDay.places.get(currentDay.nPlaces-1).latitude;
//                    linkLocation.longitude=currentDay.places.get(currentDay.nPlaces-1).longitude;
//                    linkLocation.altitude=currentDay.places.get(currentDay.nPlaces-1).altitude;
//                    currentPath.routePoints.add(linkLocation);
//                    currentPath.nRoutePoints++;
//                    break;
//                case 'R':
//                    //linkLocation=currentDay.paths.get(currentDay.nPaths-1).routePoints.get(currentDay.paths.get(currentDay.nPaths-1).routePoints.size()-1);
//                    linkLocation=currentDay.paths.get(currentDay.nPaths-1).routePoints.get(currentDay.paths.get(currentDay.nPaths-1).nRoutePoints-1);
//                    currentPath.routePoints.add(linkLocation);
//                    currentPath.nRoutePoints++;
//                    break;
//            }
//        }
//
//        // Add RoutePoints and update its variables
//        currentPath.routePoints.add(pathPoint);
//        currentPath.nRoutePoints++;
//
//        // Set ActivityType
//        currentPath.activityType=v.movStatus.mov;
//
//        // Set Initial Distance
//        currentPath.distance=v.sensorData.gps.currentLocation.distanceBetweenLocations;
//
//        // Update day activity distance
//        switch (currentPath.activityType)
//        {
//            case WALKING:
//                currentDay.stats.distances.walking = currentPath.distance;
//                break;
//            case RUNNING:
//                currentDay.stats.distances.running = currentPath.distance;
//                break;
//            case VEHICLE:
//                currentDay.stats.distances.vehicle = currentPath.distance;
//                break;
//        }
//
//        // Set TimeStart
//        currentPath.timeStart=System.currentTimeMillis();
//        currentPath.timeStartString = currentTimeToString();
//
//        // Set TimeDuration
//        currentPath.timeDuration = 0;
//        currentPath.timeDurationString = msToTimeString(0);
//
//        // Set Initial Speed
//        currentPath.speedAverage = v.sensorData.gps.currentLocation.speedKm;
//        currentPath.speedMin = v.sensorData.gps.currentLocation.speedKm;
//        currentPath.speedMax = v.sensorData.gps.currentLocation.speedKm;
//        v.detection.locations.path.computeCurrentSpeed.speedSum = v.sensorData.gps.currentLocation.speedKm;
//
//        // Set Initial Pedometer data
//        v.detection.pedometer.currentStepsWhenPathStarted = v.sensorData.pedometer.nSteps;
//        currentPath.nSteps = v.sensorData.pedometer.nSteps - v.detection.pedometer.currentStepsWhenPathStarted;
//
//        // Update Total Distance
//        currentDay.stats.distances.total += v.sensorData.gps.currentLocation.distanceBetweenLocations;
//        // PRUEBA ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        currentDay.stats.distances.totalDistanceByAcceleration += v.sensorData.accelerometer.distancePerTimeStep.distance;
//
//        // Add Path and update its variables
//        currentDay.paths.add(currentDay.nPaths, currentPath);
//        currentDay.nPaths++;
//        currentDay.nItems++;
//        currentDay.typeOfItem.add('R');
//
//        // Toast showing New Path
//        Toast.makeText(this, "New Path "+currentDay.nPaths, Toast.LENGTH_SHORT).show();
//
//        // Set path.trackingOn on
//        v.detection.locations.path.trackingOn=true;
//    }

    public void endPath ()
    {
        Day.Path currentPath;

        // Set path.trackingOn off
        v.detection.locations.path.trackingOn=false;

        // Get last path
        currentPath=currentDay.paths.get(currentDay.nPaths-1);

        // Set timeEnd
        currentPath.timeEnd = System.currentTimeMillis();
        currentPath.timeEndString = currentTimeToString();

        // Set timeDuration
        currentPath.timeDuration = currentPath.timeEnd-currentPath.timeStart;
        currentPath.timeDurationString = msToTimeString(currentPath.timeDuration);

        // Set nSteps
        currentPath.nSteps=v.sensorData.pedometer.nSteps-v.detection.pedometer.currentStepsWhenPathStarted;
        v.detection.pedometer.currentStepsWhenPathStarted = 0;

        // Set Path
        currentDay.paths.set(currentDay.nPaths-1,currentPath);

        // Init speed average variable
        v.detection.locations.path.computeCurrentSpeed.speedSum = 0;
    }

    public void newPlace ()
    {
        Day.Place currentPlace;

        // Reset movStatus.changedFromStill
        v.movStatus.changedFromStill = false;

        // New Place
        currentPlace = new Day.Place();

        if (v.sensorData.gps.trackingOn)
        {
            // Initial Coordinates
            currentPlace.latitude = v.sensorData.gps.currentLocation.latitude;
            currentPlace.longitude = v.sensorData.gps.currentLocation.longitude;
            currentPlace.altitude = v.sensorData.gps.currentLocation.altitude;

            // Compute current Place coordinates average
            v.detection.locations.place.computeCurrentPlace.latitudeSum += v.sensorData.gps.currentLocation.latitude;
            v.detection.locations.place.computeCurrentPlace.longitudeSum += v.sensorData.gps.currentLocation.longitude;
            v.detection.locations.place.computeCurrentPlace.altitudeSum += v.sensorData.gps.currentLocation.altitude;
            v.detection.locations.place.computeCurrentPlace.nLocations++;
        }

        // Arrival Time
        currentPlace.timeArrival = System.currentTimeMillis();
        currentPlace.timeArrivalString = currentTimeToString();

        // Time duration
        currentPlace.timeDuration = 0;
        currentPlace.timeDurationString = msToTimeString(0);

        // Add current Place
        currentDay.places.add(currentDay.nPlaces, currentPlace);

        // Update variables
        currentDay.nPlaces++;
        currentDay.nItems++;
        currentDay.typeOfItem.add('P');

        // Request map update
        v.serviceRequest.mapUpdate = true;

        // Set place.trackingON on
        v.detection.locations.place.trackingOn = true;

        Toast.makeText(this, "New Place " + currentDay.nPlaces, Toast.LENGTH_SHORT).show();
        vb.vibrate(50);
    }

//    public void newPlace ()
//    {
//        Day.Place currentPlace;
//
//        // Reset movStatus.changedFromStill
//        v.movStatus.changedFromStill=false;
//
//        // New Place
//        currentPlace = new Day.Place();
//
//        // Initial Coordinates
//        currentPlace.latitude=v.sensorData.gps.currentLocation.latitude;
//        currentPlace.longitude=v.sensorData.gps.currentLocation.longitude;
//        currentPlace.altitude=v.sensorData.gps.currentLocation.altitude;
//
//        // Compute current Place coordinates average
//        v.detection.locations.place.computeCurrentPlace.latitudeSum += v.sensorData.gps.currentLocation.latitude;
//        v.detection.locations.place.computeCurrentPlace.longitudeSum += v.sensorData.gps.currentLocation.longitude;
//        v.detection.locations.place.computeCurrentPlace.altitudeSum += v.sensorData.gps.currentLocation.altitude;
//        v.detection.locations.place.computeCurrentPlace.nLocations++;
//
//        // Arrival Time
//        currentPlace.timeArrival=System.currentTimeMillis();
//        currentPlace.timeArrivalString=currentTimeToString();
//
//        // Time duration
//        currentPlace.timeDuration=0;
//        currentPlace.timeDurationString = msToTimeString(0);
//
//        // Add current Place
//        currentDay.places.add(currentDay.nPlaces, currentPlace);
//
//        // Update variables
//        currentDay.nPlaces++;
//        currentDay.nItems++;
//        currentDay.typeOfItem.add('P');
//
//        // Request map update
//        v.serviceRequest.mapUpdate=true;
//
//        // Set place.trackingON on
//        v.detection.locations.place.trackingOn=true;
//
//        Toast.makeText(this,"New Place "+currentDay.nPlaces,Toast.LENGTH_SHORT).show();
//        vb.vibrate(50);
//    }

    public void endPlace ()
    {
        Day.Place currentPlace;

        // Set place.trackingOn Off
        v.detection.locations.place.trackingOn=false;

        // Get current Place
        currentPlace=currentDay.places.get(currentDay.nPlaces-1);

        // Compute current Place average coordinates and set them
        currentPlace.latitude = v.detection.locations.place.computeCurrentPlace.latitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;
        currentPlace.longitude = v.detection.locations.place.computeCurrentPlace.longitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;
        currentPlace.altitude = v.detection.locations.place.computeCurrentPlace.altitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;

        // Address and place Name
        currentPlace.address = getReverseGeocodedAddress(this,currentPlace.latitude,currentPlace.longitude);
        currentPlace.placeName = getReverseGeocodedFeatureName(this, currentPlace.latitude,currentPlace.longitude);

        // Time Departure
        currentPlace.timeDeparture = System.currentTimeMillis();
        currentPlace.timeDepartureString = currentTimeToString();

        // Time Duration
        currentPlace.timeDuration =  currentPlace.timeDeparture - currentPlace.timeArrival;
        currentPlace.timeDurationString = msToTimeString(currentPlace.timeDuration);

        // Set current Place
        currentDay.places.set(currentDay.nPlaces-1, currentPlace);

        // Link last path ending routePoints with current place coordinates
        if (currentDay.nPaths > 0)
        {
            Day.Path tempPath = currentDay.paths.get(currentDay.nPaths-1);
            Day.Coordinates tempLatLng = new Day.Coordinates();
            tempLatLng.latitude=currentPlace.latitude;
            tempLatLng.longitude=currentPlace.longitude;
            tempLatLng.altitude=currentPlace.altitude;
            tempPath.routePoints.add(tempLatLng);
            tempPath.nRoutePoints++;
            currentDay.paths.set(currentDay.nPaths-1,tempPath);
        }

        // Set Map request
        v.serviceRequest.mapUpdate=true;

        // Init average location coordinates variables
        v.detection.locations.place.computeCurrentPlace.latitudeSum=0;
        v.detection.locations.place.computeCurrentPlace.longitudeSum=0;
        v.detection.locations.place.computeCurrentPlace.altitudeSum=0;
        v.detection.locations.place.computeCurrentPlace.nLocations=0;
    }

    public void newUnknownActivityPath()
    {
        Day.UnknownActivityPath currentUnknownActivityPath;

        currentUnknownActivityPath= new Day.UnknownActivityPath();
        currentUnknownActivityPath.pointStart.latitude=currentDay.places.get(currentDay.nPlaces-1).latitude;
        currentUnknownActivityPath.pointStart.longitude=currentDay.places.get(currentDay.nPlaces-1).longitude;
        currentUnknownActivityPath.pointStart.altitude=currentDay.places.get(currentDay.nPlaces-1).altitude;

        currentUnknownActivityPath.pointEnd.latitude=v.sensorData.gps.currentLocation.latitude;
        currentUnknownActivityPath.pointEnd.longitude=v.sensorData.gps.currentLocation.longitude;
        currentUnknownActivityPath.pointEnd.altitude=v.sensorData.gps.currentLocation.altitude;

        currentDay.unknownActivityPaths.add(currentUnknownActivityPath);
        currentDay.typeOfItem.add('U');
        currentDay.nItems++;
        currentDay.nUnknownActivityPaths++;
    }

    public void trackLocations ()
    {
        if (v.detection.locations.path.trackingOn)
        {
            trackPath();
        }
        else if (v.detection.locations.place.trackingOn)
        {
            trackPlace();
        }
    }

    public void trackPath ()
    {
        Day.Coordinates pathPoint;
        Day.Path currentPath;

        // Get current Path
        currentPath = currentDay.paths.get(currentDay.nPaths-1);

        // Set pathPoint coordinates
        pathPoint = new Day.Coordinates();
        pathPoint.latitude=v.sensorData.gps.currentLocation.latitude;
        pathPoint.longitude=v.sensorData.gps.currentLocation.longitude;
        pathPoint.altitude=v.sensorData.gps.currentLocation.altitude;

        // Add RoutePoints and update its variables
        currentPath.routePoints.add(pathPoint);
        currentPath.nRoutePoints++;

        // Set TimeDuration
        currentPath.timeDuration = System.currentTimeMillis() - currentPath.timeStart;
        currentPath.timeDurationString = msToTimeString(currentPath.timeDuration);

        // Update Distance
        currentPath.distance += v.sensorData.gps.currentLocation.distanceBetweenLocations;

        // Update day activity distance
        switch (currentPath.activityType)
        {
            case WALKING:
                currentDay.stats.distances.walking += currentPath.distance;
                break;
            case RUNNING:
                currentDay.stats.distances.running += currentPath.distance;
                break;
            case VEHICLE:
                currentDay.stats.distances.vehicle += currentPath.distance;
                break;
        }

        // Update Total Distance
        currentDay.stats.distances.total += v.sensorData.gps.currentLocation.distanceBetweenLocations;
        // PRUEBA ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        currentDay.stats.distances.totalDistanceByAcceleration += v.sensorData.accelerometer.distancePerTimeStep.distance;

        // Update Speeds
        if (v.sensorData.gps.currentLocation.speedKm > 0)
        {
            v.detection.locations.path.computeCurrentSpeed.speedSum += v.sensorData.gps.currentLocation.speedKm;
            currentPath.speedAverage = v.detection.locations.path.computeCurrentSpeed.speedSum / currentPath.nRoutePoints;
            if (v.sensorData.gps.currentLocation.speedKm > currentPath.speedMax)
            {
                currentPath.speedMax = v.sensorData.gps.currentLocation.speedKm;
            }
            if (v.sensorData.gps.currentLocation.speedKm < currentPath.speedMin)
            {
                currentPath.speedMin = v.sensorData.gps.currentLocation.speedKm;
            }
        }

        // Update Path
        currentDay.paths.set(currentDay.nPaths-1,currentPath);

        if (v.detection.locations.needToSetUnknownActivityPathEnding)
        {
            v.detection.locations.needToSetUnknownActivityPathEnding = false;
            Day.UnknownActivityPath currentUnknownActivityPath;

            currentUnknownActivityPath=currentDay.unknownActivityPaths.get(currentDay.nUnknownActivityPaths-1);

            currentUnknownActivityPath.pointEnd.latitude=v.sensorData.gps.currentLocation.latitude;
            currentUnknownActivityPath.pointEnd.longitude=v.sensorData.gps.currentLocation.longitude;
            currentUnknownActivityPath.pointEnd.altitude=v.sensorData.gps.currentLocation.altitude;

            currentDay.unknownActivityPaths.set(currentDay.nUnknownActivityPaths-1,currentUnknownActivityPath);
        }

        // Request Distance Update
        v.serviceRequest.distanceUpdate=true;

        // Request Map Update
        v.serviceRequest.mapUpdate = true;
    }

    public void trackPlace ()
    {
        Day.Place currentPlace;

        // Get currentPlace
        currentPlace=currentDay.places.get(currentDay.nPlaces-1);

        // Compute current place average coordinates
        v.detection.locations.place.computeCurrentPlace.latitudeSum += v.sensorData.gps.currentLocation.latitude;
        v.detection.locations.place.computeCurrentPlace.longitudeSum += v.sensorData.gps.currentLocation.longitude;
        v.detection.locations.place.computeCurrentPlace.altitudeSum += v.sensorData.gps.currentLocation.altitude;
        v.detection.locations.place.computeCurrentPlace.nLocations++;
        currentPlace.latitude = v.detection.locations.place.computeCurrentPlace.latitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;
        currentPlace.longitude = v.detection.locations.place.computeCurrentPlace.longitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;
        currentPlace.altitude = v.detection.locations.place.computeCurrentPlace.altitudeSum / v.detection.locations.place.computeCurrentPlace.nLocations;

        // Time Duration
        currentPlace.timeDuration =  System.currentTimeMillis() - currentPlace.timeArrival;
        currentPlace.timeDurationString = msToTimeString(currentPlace.timeDuration);

        currentDay.places.set(currentDay.nPlaces-1, currentPlace);

        if (v.detection.locations.needToSetUnknownActivityPathEnding)
        {
            v.detection.locations.needToSetUnknownActivityPathEnding = false;
            Day.UnknownActivityPath currentUnknownActivityPath;

            currentUnknownActivityPath=currentDay.unknownActivityPaths.get(currentDay.nUnknownActivityPaths-1);

            currentUnknownActivityPath.pointEnd.latitude=v.sensorData.gps.currentLocation.latitude;
            currentUnknownActivityPath.pointEnd.longitude=v.sensorData.gps.currentLocation.longitude;
            currentUnknownActivityPath.pointEnd.altitude=v.sensorData.gps.currentLocation.altitude;

            currentDay.unknownActivityPaths.set(currentDay.nUnknownActivityPaths-1,currentUnknownActivityPath);
        }
        v.serviceRequest.mapUpdate=true;
    }

    // Reverse geocoded ////////////////////////////////////////////////////////////////////////////////
    public static String getReverseGeocodedAddress(Context context, double latitude, double longitude) {
        String addrStr = "";

        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude , longitude, 1);
            StringBuilder addressStrBuilder = new StringBuilder();
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex() && i < 1; i++) {
                    addressStrBuilder.append(address.getAddressLine(i) + " ");
                }
                addrStr = addressStrBuilder.toString();
            }
        } catch (IOException e) {
            //Log.w(TAG, "Unable to retrieve reverse geocoded address.", e);
        }
        return addrStr;
    }

    public static String getReverseGeocodedFeatureName(Context context, double latitude, double longitude) {
        String addrStr = "";

        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude , longitude, 1);
            StringBuilder addressStrBuilder = new StringBuilder();
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                addressStrBuilder.append(address.getFeatureName() + " ");

                addrStr = addressStrBuilder.toString();
            }
        } catch (IOException e) {
            // Log.w(TAG,"Unable to retrieve reverse geocoded address.", e);
        }
        if (addrStr != null) {
            return addrStr;
        }
        else {
            return "No featured name";
        }
    }

    // TimeLine ////////////////////////////////////////////////////////////////////////////////////////
    public boolean timeLineExits ()
    {
        String fullPath;
        String fileName;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "timeLine.ini";


        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(fullPath+fileName);
            return true;
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"timeLine.ini not found",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    public TimeLine createTimeLine ()
    {
        TimeLine timeline;
        TimeLine.Item currentItem;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        currentItem = new TimeLine.Item();
        timeline = new TimeLine();

        // timeline information and creating
        timeline.nDays=1;
        currentItem.year = year;
        currentItem.month = month;
        currentItem.day = day;
        currentItem.fileName = year+"-"+month+"-"+day+".nmd";
        timeline.item.add(currentItem);

        return timeline;
    }

    public TimeLine readTimeLine ()
    {
        String fullPath;
        String fileName;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "timeLine.ini";

        TimeLine timeline = new TimeLine();

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error reading timeline.ini",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(fis);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating TimeLine ObjectInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            timeline = (TimeLine)ois.readObject();
            Toast.makeText(this,"TimeLine read",Toast.LENGTH_SHORT).show();
        }
        catch (ClassNotFoundException e)
        {
            Toast.makeText(this,"TimeLine ClassNotFoundException",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error getting TimeLine",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            ois.close();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Closing TimeLine file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return timeline;
    }

    public void newCurrentDayToTimeLine()
    {
        TimeLine.Item currentItem;
        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        currentItem = new TimeLine.Item();

        // TimeLine information and creating
        timeLine.nDays++;
        currentItem.year = year;
        currentItem.month = month;
        currentItem.day = day;
        currentItem.fileName = year+"-"+month+"-"+day+".nmd";
        timeLine.item.add(currentItem);

    }

    public void writeTimeLine ()
    {
        String fullPath;
        String fileName;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = "timeline.ini";


        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error creating timeline.ini file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(fos);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating TimeLine ObjectOutputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.writeObject(timeLine);

            Toast.makeText(this,"TimeLine Write successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error writing TimeLine object",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.flush();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Write TimeLine Flush",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.close();
            Toast.makeText(this,"TimeLine Close successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"TimeLine Error closing",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Day /////////////////////////////////////////////////////////////////////////////////////////////
    public boolean currentDayExits ()
    {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        int lastTimeLineYear = timeLine.item.get(timeLine.nDays-1).year;
        int lastTimeLineMonth = timeLine.item.get(timeLine.nDays-1).month;
        int lastTimeLineDay = timeLine.item.get(timeLine.nDays-1).day;

        if ((year == lastTimeLineYear) && (month == lastTimeLineMonth) && (day == lastTimeLineDay))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public Day createCurrentDay ()
    {
        String fileName;
        Day currentday = new Day();

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        fileName = year+"-"+month+"-"+day+".nmd";

        // Updating Day information
        currentday.year = year;
        currentday.month = month;
        currentday.day = day;
        currentday.dateString = year+"-"+month+"-"+day;
        currentday.fileName = fileName;

        // Init stats
        currentday.stats = new Day.Stats();
        
        // Distances
        currentday.stats.distances = new Day.Stats.Distances();
        currentday.stats.distances.total = 0;
        currentday.stats.distances.totalDistanceByAcceleration = 0;
        currentday.stats.distances.walking = 0;
        currentday.stats.distances.running = 0;
        currentday.stats.distances.vehicle = 0;
        
        // Times
        currentday.stats.times = new Day.Stats.Times();
        currentday.stats.times.Total = 0;
        currentday.stats.times.TimeAverageInEveryPlace = 0;
        currentday.stats.times.still = new Day.Stats.Times.Still();
        currentday.stats.times.still.Total = 0;
        currentday.stats.times.still.Absolutely = 0;
        currentday.stats.times.still.MovingSure = 0;
        currentday.stats.times.still.MovingProbably = 0;
        currentday.stats.times.walking = new Day.Stats.Times.Walking();
        currentday.stats.times.walking.Total = 0;
        currentday.stats.times.walking.Sure = 0;
        currentday.stats.times.walking.Probably = 0;
        currentday.stats.times.running = new Day.Stats.Times.Running();
        currentday.stats.times.running.Total = 0;
        currentday.stats.times.running.Sure = 0;
        currentday.stats.times.running.Probably = 0;
        currentday.stats.times.vehicle = new Day.Stats.Times.Vehicle();
        currentday.stats.times.vehicle.Total = 0;
        currentday.stats.times.vehicle.RunningSure = 0;
        currentday.stats.times.vehicle.StillSure = 0;
        currentday.stats.times.vehicle.NoGPS = 0;
        // Speeds
        currentday.stats.speeds = new Day.Stats.Speeds();
        currentday.stats.speeds.min = new Day.Stats.Speeds.Min();
        currentday.stats.speeds.min.Walking = 0;
        currentday.stats.speeds.min.Running = 0;
        currentday.stats.speeds.min.Vehicle = 0;
        currentday.stats.speeds.max = new Day.Stats.Speeds.Max();
        currentday.stats.speeds.max.Walking = 0;
        currentday.stats.speeds.max.Running = 0;
        currentday.stats.speeds.max.Vehicle = 0;
        currentday.stats.speeds.average = new Day.Stats.Speeds.Average();
        currentday.stats.speeds.average.Walking = 0;
        currentday.stats.speeds.average.Running = 0;
        currentday.stats.speeds.average.Vehicle = 0;
        // nDetections
        currentday.stats.nDetections = new Day.Stats.NDetections();
        currentday.stats.nDetections.Total = 0;
        currentday.stats.nDetections.Still = 0;
        currentday.stats.nDetections.Walking = 0;
        currentday.stats.nDetections.Running = 0;
        currentday.stats.nDetections.Vehicle = 0;
        currentday.stats.nDetections.VehicleParked = 0;

        // Pedometer
        currentday.stats.pedometer = new Day.Stats.Pedometer();
        currentday.stats.pedometer.nStepsTotal = 0;
        currentday.stats.pedometer.nStepsWalking = 0;
        currentday.stats.pedometer.nStepsRunning = 0;
        // GPS
        currentday.stats.gps = new Day.Stats.GPS();
        currentday.stats.gps.nTotalLocationPoints = 0;

        return currentday;
    }

    public Day readDay (String fileName)
    {
        String fullPath;

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;

        Day currentday = new Day();

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error creating FileInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(fis);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating ObjectInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            currentday = (Day)ois.readObject();
            //Toast.makeText(this,"Places got",Toast.LENGTH_SHORT).show();
        }
        catch (ClassNotFoundException e)
        {
            Toast.makeText(this,"Places ClassNotFoundException",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Places got",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            ois.close();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Closing file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return currentday;
    }

    public void writeCurrentDay ()
    {
        String fullPath;
        String fileName;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = year+"-"+month+"-"+day+".nmd";

        // Updating Day information
        currentDay.year = year;
        currentDay.month = month;
        currentDay.day = day;
        currentDay.dateString = year+"-"+month+"-"+day;
        currentDay.fileName = fileName;

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(fullPath+fileName);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this,"Error creating Day file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(fos);
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error creating Day ObjectOutputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.writeObject(currentDay);

            Toast.makeText(this,"Day Write successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error writing Day object",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.flush();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Day Write Flush",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try
        {
            oos.close();
            Toast.makeText(this,"Day Close successful",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,"Error Day closing",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
