package org.ALPHA.nomadadev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TimelineActivity extends Activity {

    static int section = R.id.main;

    private static final String TAG = "ADDRESS";

    private Handler TimerPlacesUI;
    private int UPDATE_TIME_PLACES_UI = 500; //ms
    Vibrator vb;

    static boolean updateFileChanges=true;
    static boolean showLastDay=true;
    int iDayShow=0;

    Button buttonBefore;
    Button buttonNext;

    TimeLine timeLine;

    Day currentDay = new Day();

    boolean onlyOne = true;


    // TimerPlacesUI
    private Runnable updatePlacesUI = new Runnable() {
        @Override
        public void run() {
            //  vb.vibrate(10);
            updateTimeLineLayout();
            TimerPlacesUI.postDelayed(this, UPDATE_TIME_PLACES_UI);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_layout);

        // TimerPlacesUI
        TimerPlacesUI = new Handler();
        TimerPlacesUI.removeCallbacks(updatePlacesUI);
        TimerPlacesUI.postDelayed(updatePlacesUI, UPDATE_TIME_PLACES_UI);

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
        Intent intent = new Intent();
        section = item.getItemId();
        switch (section) {
            case R.id.main:
                intent.setClass(TimelineActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.stats:
                intent.setClass(TimelineActivity.this, StatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.map:
                intent.setClass(TimelineActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
//                intent.setClass(TimelineActivity.this, SettingsActivity.class);
//                CurrentData.nItemToShow=CurrentData.day.nItems-1;
//                CurrentData.nPlaceToShow=0;
                ShowPlaceActivity.nItemToShow = 0;
                ShowPlaceActivity.nPlaceToShow = 0;
                ShowPlaceActivity.nPathToShow = 0;
                ShowPlaceActivity.nUnknownPathToShow = 0;
                intent.setClass(TimelineActivity.this, ShowPlaceActivity.class);
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
    public void onDestroy() {
        TimerPlacesUI.removeCallbacks(updatePlacesUI);
        updateFileChanges=true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(TimelineActivity.this, MainActivity.class);
        startActivity(intent);
        updateFileChanges=true;
        finish();
    }

    public void setPlaceText(String text, int textColor, int textSize, final Day.Place place)
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutPlaces);
        TextView tv = new TextView(TimelineActivity.this);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        View.OnClickListener onClickPlace = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showPlace(place);
            }
        };
        tv.setOnClickListener(onClickPlace);
        currentLayout.addView(tv);
    }

    public void setPathText(String text, int textColor, int textSize, final Day.Path path)
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutPlaces);
        TextView tv = new TextView(TimelineActivity.this);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        View.OnClickListener onClickPath = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showPath(path);
            }
        };
        tv.setOnClickListener(onClickPath);
        currentLayout.addView(tv);
    }

    public void setText(String text, int textColor, int textSize)
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutPlaces);
        TextView tv = new TextView(TimelineActivity.this);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        currentLayout.addView(tv);
    }

    public void setLine ()
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutPlaces);
        View v = new View(TimelineActivity.this);
        Space s = new Space(TimelineActivity.this);
        v.setBackgroundColor(Color.parseColor("#ff4aff00"));
        v.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1));
        s.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                4));
        currentLayout.addView(v);
        currentLayout.addView(s);

    }

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
            Log.w(TAG, "Unable to retrieve reverse geocoded address.", e);
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
            Log.w(TAG,"Unable to retrieve reverse geocoded address.", e);
        }
        if (addrStr != null) {
            return addrStr;
        }
        else {
            return "No featured name";
        }
    }


    public void updateTimeLineLayout ()
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutPlaces);


//        Day currentDay = new Day();
        Day.Place place = new Day.Place();
        Day.Path path = new Day.Path();
        Day.UnknownActivityPath unknownActivityPath = new Day.UnknownActivityPath();

        int iPlace=0;
        int iPath=0;
        int iUnknownActivityPath=0;
        int iItems=0;

// FUNCIONA
//        if (NomadaService.service_on) // Servicio ejecutándose
//        {
//            updateFileChanges=true;
//            currentDay=NomadaService.currentDay;
//        }
//        else // Consulta sin servicio en ejecución
//        {
//            if (updateFileChanges)
//            {
//                currentDay=readCurrentDay();
//            }
//        }
        // CÓDIGO NUEVO PARA MOVERSE ENTRE DÍAS
///////////////////////

        if (showLastDay)
        {
            if (NomadaService.On) // Servicio ejecutándose
            {
                //updateFileChanges=true;
                timeLine=NomadaService.timeLine;
                currentDay=NomadaService.currentDay;
                iDayShow=timeLine.nDays-1;
            }
            else // Consulta sin servicio en ejecución
            {
                if (updateFileChanges)
                {
                    if (timeLineExits())
                    {
                        timeLine=readTimeLine();
                        currentDay=readDay(timeLine.item.get(timeLine.nDays-1).fileName);
                        iDayShow=timeLine.nDays-1;
                    }
                    else
                    {
                        updateFileChanges=false;
                    }
                }
            }


        }
        else
        {
            if (updateFileChanges)
            {
                timeLine = readTimeLine();
                currentDay = readDay(timeLine.item.get(iDayShow).fileName);
            }
        }
        if (onlyOne)
        {
            onlyOne=false;
            ShowPlaceActivity.currentDay = new Day();
            ShowPlaceActivity.currentDay = currentDay;
            Toast.makeText(this,"Current Day NPlaces TimeLineActivity "+ShowPlaceActivity.currentDay.nPlaces,Toast.LENGTH_SHORT).show();
        }

//        CurrentData.day = new Day();

        if (updateFileChanges)
        {
            currentLayout.removeAllViewsInLayout();
            // Create Before Button
            buttonBefore = new Button(this);
            buttonBefore.setText("Day before");
            buttonBefore.setTextColor(Color.BLUE);
            if (iDayShow == 0)
            {
                buttonBefore.setEnabled(false);
            }


            // Create Before Button on click listener
            View.OnClickListener onClickButtonBefore = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLastDay = false;
                    iDayShow--;
                    if (iDayShow < 0)
                    {
                        iDayShow = 0;
                    }
                    if (iDayShow == 0)
                    {
                        buttonBefore.setEnabled(false);
                    }
                    if (iDayShow < (timeLine.nDays-1))
                    {
                        buttonNext.setEnabled(true);
                    }
                    updateFileChanges = true;
                }
            };
            buttonBefore.setOnClickListener(onClickButtonBefore);

            // Create Next Button
            buttonNext = new Button(this);
            buttonNext.setText("Day next");
            buttonNext.setTextColor(Color.BLUE);

            if (iDayShow == timeLine.nDays-1)
            {
                buttonNext.setEnabled(false);
            }



            // create Next Button on click listener
            View.OnClickListener onClickButtonNext = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLastDay = false;
                    iDayShow++;
                    if (iDayShow > (timeLine.nDays-1))
                    {
                        iDayShow = timeLine.nDays-1;
                    }
                    if (iDayShow == timeLine.nDays-1)
                    {
                        buttonNext.setEnabled(false);
                    }
                    if (iDayShow > 0)
                    {
                        buttonBefore.setEnabled(true);
                    }
                    updateFileChanges = true;
                }
            };
            buttonNext.setOnClickListener(onClickButtonNext);
            currentLayout.addView(buttonBefore);
            currentLayout.addView(buttonNext);
        }





//////////////////////
        if (updateFileChanges)
        {
           // currentLayout.removeAllViewsInLayout();
            setText(currentDay.dateString, Color.YELLOW,30);
            setLine();
            setText("Total distance: "+mToDistanceString(currentDay.stats.distances.total),Color.RED, 14);
            setText("Walking distance: "+mToDistanceString(currentDay.stats.distances.walking),Color.RED, 14);
            setText("Running distance: "+mToDistanceString(currentDay.stats.distances.running),Color.RED, 14);
            setText("Vehicle distance: "+mToDistanceString(currentDay.stats.distances.vehicle),Color.RED, 14);
            setText("Total Acc distance: "+mToDistanceString(currentDay.stats.distances.totalDistanceByAcceleration),Color.RED, 14);
            setText("Total time: "+msToTimeString(currentDay.stats.times.Total),Color.RED, 14);
            setText("Total Still time: "+msToTimeString(currentDay.stats.times.still.Total),Color.RED, 14);
            setText("Absolutely Still time: "+msToTimeString(currentDay.stats.times.still.Absolutely),Color.RED, 14);
            setText("Moving Sure Still time: "+msToTimeString(currentDay.stats.times.still.MovingSure),Color.RED, 14);
            setText("Moving Probably Still time: "+msToTimeString(currentDay.stats.times.still.MovingProbably),Color.RED, 14);
            setText("Total Walking time: "+msToTimeString(currentDay.stats.times.walking.Total),Color.RED, 14);
            setText("Walking Sure time: "+msToTimeString(currentDay.stats.times.walking.Sure),Color.RED, 14);
            setText("Walking Probably time: "+msToTimeString(currentDay.stats.times.walking.Probably),Color.RED, 14);
            setText("Total Running time: "+msToTimeString(currentDay.stats.times.running.Total),Color.RED, 14);
            setText("Running Sure time: "+msToTimeString(currentDay.stats.times.running.Sure),Color.RED, 14);
            setText("Running Probably time: "+msToTimeString(currentDay.stats.times.running.Probably),Color.RED, 14);
            setText("Total Vehicle time: "+msToTimeString(currentDay.stats.times.vehicle.Total),Color.RED, 14);
            setText("Vehicle Still Sure time: "+msToTimeString(currentDay.stats.times.vehicle.StillSure),Color.RED, 14);
            setText("Vehicle Running Sure time: "+msToTimeString(currentDay.stats.times.vehicle.RunningSure),Color.RED, 14);
            setText("Vehicle No GPS time: "+msToTimeString(currentDay.stats.times.vehicle.NoGPS),Color.RED, 14);
            setText("Total nDetections: "+currentDay.stats.nDetections.Total,Color.RED, 14);
            setText("Still nDetections: "+currentDay.stats.nDetections.Still,Color.RED, 14);
            setText("Walking nDetections: "+currentDay.stats.nDetections.Walking,Color.RED, 14);
            setText("Running nDetections: "+currentDay.stats.nDetections.Running,Color.RED, 14);
            setText("Vehicle nDetections: "+currentDay.stats.nDetections.Vehicle,Color.RED, 14);
            setText("Vehicle Parked nDetections: "+currentDay.stats.nDetections.VehicleParked,Color.RED, 14);
            setText("Total Steps: "+currentDay.stats.pedometer.nStepsTotal,Color.RED, 14);
            setText("Total Walking Steps: "+currentDay.stats.pedometer.nStepsWalking,Color.RED, 14);
            setText("Total Running Steps: "+currentDay.stats.pedometer.nStepsWalking,Color.RED, 14);

            if (currentDay.nItems > 0)
            {
                for (iItems = 0; iItems < currentDay.nItems; iItems++)
                {
                    switch (currentDay.typeOfItem.get(iItems))
                    {
                        case 'P':
                            place = currentDay.places.get(iPlace);

                            setPlaceText("Place number " + iPlace, Color.GREEN, 18, place);
                            setLine();
                            setPlaceText("Latitude: " + place.latitude, Color.RED, 22, place);
                            setPlaceText("Longitude: " + place.longitude, Color.RED, 22, place);
                            setPlaceText("Altitude: " + place.altitude + " m", Color.RED, 22, place);
                            setPlaceText("Address", Color.RED, 20, place);
                            setPlaceText(place.address + "", Color.RED, 16, place);
                            setPlaceText("Feature Name",Color.RED,20, place);
                            setPlaceText(place.placeName+"",Color.RED,16, place);
                            setPlaceText("DurationString: " + place.timeDurationString, Color.RED, 22, place);
                            setPlaceText("Duration: "+place.timeDuration, Color.RED,22, place);
                            setPlaceText("Arrival Time", Color.RED, 20, place);
                            setPlaceText(place.timeArrivalString + "", Color.RED, 16, place);
                            setPlaceText("Departure Time", Color.RED, 20, place);
                            setPlaceText(place.timeDepartureString + "", Color.RED, 16, place);
                            iPlace++;
                            break;

                        case 'R':
                            path = currentDay.paths.get(iPath);
                            setPathText("Path number " + iPath, Color.CYAN, 18, path);
                            setLine();
                            setPathText("Activity type: " + path.activityType, Color.RED, 22, path);
                            setPathText("DurationString: " + path.timeDurationString, Color.RED, 22, path);
                            setPathText("Duration: "+path.timeDuration, Color.RED,22, path);
                            setPathText("Time Start: " + path.timeStartString, Color.RED, 22, path);
                            setPathText("Time End: " + path.timeEndString, Color.RED, 22, path);
                            setPathText("Distance: " + mToDistanceString(path.distance), Color.RED, 22, path);
                            setPathText("Steps: " + path.nSteps + "steps", Color.RED, 22, path);
                            setPathText("Speed Min: " + path.speedMin + " Km/h", Color.RED, 22, path);
                            setPathText("Speed Average: " + path.speedAverage + " Km/h", Color.RED, 22, path);
                            setPathText("Speed Max: " + path.speedMax + " Km/h", Color.RED, 22, path);
                            setPathText("RoutePoints: " + path.nRoutePoints, Color.RED, 22, path);
                            iPath++;
                            break;

                        case 'U':
                            unknownActivityPath = currentDay.unknownActivityPaths.get(iUnknownActivityPath);
                            setText("Unknown Activity Path numer " + iUnknownActivityPath, Color.GRAY, 18);
                            setLine();
                            setText("Start Latitude: " + unknownActivityPath.pointStart.latitude, Color.RED, 22);
                            setText("Start Longitude: " + unknownActivityPath.pointStart.longitude, Color.RED, 22);
                            setText("End Latitude: " + unknownActivityPath.pointEnd.latitude, Color.RED, 22);
                            setText("End Longitude: " + unknownActivityPath.pointEnd.longitude, Color.RED, 22);
                            iUnknownActivityPath++;
                            break;

                    }
                }
            }
            else
            {
                setText("Nothing to show ", Color.RED, 42);
            }
            if (!NomadaService.On)
            {
                updateFileChanges = false;
            }
            // PRUEBA
            else
            {
                updateFileChanges = false;
            }
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

    String mToDistanceString (double m)
    {
        int Km;
        int metersLeft;
        String distanceString="";

        Km = (int)m / 1000;
        metersLeft= (int)m - Km*1000;

        if (m > 1000)
        {
            if (metersLeft == 0)
            {
                distanceString=Km+" Km";
            }
            else
            {
                distanceString=Km+" Km and "+metersLeft+" m";
            }

        }
        else
        {
            distanceString=m+" m";
        }
        return distanceString;
    }

    public Day readCurrentDay ()
    {
        String fullPath;
        String fileName;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);

        fullPath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "SDR" + File.separator;
        fileName = year+"-"+month+"-"+day+".nmd";

        Day currentDay = new Day();

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
            currentDay = (Day)ois.readObject();
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

        return currentDay;
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

    public void showPlace(Day.Place place)
    {
        vb.vibrate(50);
        setContentView(R.layout.place_layout);
//        ((TextView) findViewById(R.id.DayDateData)).setText(place.address);
//        ((TextView) findViewById(R.id.LatitudeTimeLineData)).setText(Double.toString(place.latitude));
//        ((TextView) findViewById(R.id.LongitudeTimeLineData)).setText(Double.toString(place.longitude));
//        ((TextView) findViewById(R.id.AltitudeTimeLineData)).setText(Double.toString(place.altitude)+" m");
//        ((TextView) findViewById(R.id.AddressTimeLineData)).setText(place.address);
//        ((TextView) findViewById(R.id.FeatureNameTimeLineData)).setText(place.placeName);
//        ((TextView) findViewById(R.id.TimeArrivalTimeLineData)).setText(place.timeArrivalString);
//        ((TextView) findViewById(R.id.TimeDepartureTimeLineData)).setText(place.timeDepartureString);
//        ((TextView) findViewById(R.id.TimeDurationTimeLineData)).setText(place.timeDurationString);
//
//        // Create map
//        GoogleMap mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapTimeLine)).getMap();
//        mMap.setMyLocationEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.latitude, place.longitude),17));
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(place.latitude, place.longitude))
//                .title(place.address));
    }

    public void onClickPlaceBack (View view)
    {
        setContentView(R.layout.places_layout);
        updateTimeLineLayout();
        updateFileChanges=true;
    }

    public void showPath(Day.Path path)
    {
        int LineColor;
        int iPoint;
        
        vb.vibrate(50);
        setContentView(R.layout.path_layout);
//        ((TextView) findViewById(R.id.ActivityTimeLineData)).setText(""+path.activityType);
//        ((TextView) findViewById(R.id.TimeStartTimeLineData)).setText(path.timeStartString);
//        ((TextView) findViewById(R.id.TimeStopTimeLineData)).setText(path.timeEndString);
//        ((TextView) findViewById(R.id.PathDurationTimeLineData)).setText(path.timeDurationString);
//        ((TextView) findViewById(R.id.DistanceTimeLineData)).setText(path.distance+" m");
//        ((TextView) findViewById(R.id.SpeedAverageTimeLineData)).setText(path.speedAverage+" Km/h");
//        ((TextView) findViewById(R.id.MinSpeedTimeLineData)).setText(path.speedMin+" Km/h");
//        ((TextView) findViewById(R.id.MaxSpeedTimeLineData)).setText(path.speedMax+" Km/h");
//        ((TextView) findViewById(R.id.StepsTimeLineData)).setText(path.nSteps+"");
//        ((TextView) findViewById(R.id.NPointsTimeLineData)).setText(path.nRoutePoints+"");

        // Create map
        int Zoom = 0;
        if (path.distance > 512000)
        {
            Zoom = 6;
        }
        else if (path.distance > 256000)
        {
            Zoom = 7;
        }
        else if (path.distance > 128000)
        {
            Zoom = 8;
        }
        else if (path.distance > 64000)
        {
            Zoom = 9;
        }
        else if (path.distance > 32000)
        {
            Zoom = 10;
        }
        else if (path.distance > 16000)
        {
            Zoom = 11;
        }
        else if (path.distance > 8000)
        {
            Zoom = 12;
        }
        else if (path.distance > 4000)
        {
            Zoom = 13;
        }
        else if (path.distance > 2000)
        {
            Zoom = 14;
        }
        else if (path.distance > 1000)
        {
            Zoom = 15;
        }
        else if (path.distance > 500)
        {
            Zoom = 16;
        }
        else if (path.distance > 250)
        {
            Zoom = 17;
        }
        else if (path.distance <= 250)
        {
            Zoom = 18;
        }
//        GoogleMap mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapPathTimeLine)).getMap();
//        mMap.setMyLocationEnabled(true);
//        int middlePoint = path.nRoutePoints/2;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(path.routePoints.get(middlePoint).latitude, path.routePoints.get(middlePoint).longitude),Zoom));
//        switch (path.activityType)
//        {
//            case STILL:
//                LineColor = Color.YELLOW;
//                break;
//            case WALKING:
//                LineColor = Color.MAGENTA;
//                break;
//            case RUNNING:
//                LineColor = Color.GREEN;
//                break;
//            case VEHICLE:
//                LineColor = Color.CYAN;
//                break;
//            default:
//                LineColor = Color.BLACK;
//                break;
//        }
//        //Toast.makeText(this,"Plotting routing points",Toast.LENGTH_SHORT).show();
//        Polyline route = mMap.addPolyline(new PolylineOptions()
//                .width(6)
//                .color(LineColor));
//        //route.setPoints(paths.get(k).routePoints);
//
//        // Conversión de Coordinates a LatLng
//        List<LatLng> routePoints = new ArrayList<LatLng>();
//
//        for (iPoint=0; iPoint<path.nRoutePoints; iPoint++)
//        {
//            routePoints.add(new LatLng(path.routePoints.get(iPoint).latitude,path.routePoints.get(iPoint).longitude));
//        }
//
//        route.setPoints(routePoints);
    }
}