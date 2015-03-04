package org.ALPHA.nomadadev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Geocoder;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MapActivity extends Activity
{

    static int section=R.id.main;

    private Handler TimerMapUI;
    private int UPDATE_TIME_MAP_UI = 2000; //ms
    Vibrator vb;

    private boolean MapFirstPosition=true;

    static boolean updateFileChanges=true;

    public GoogleMap mMap;


    // TimerMapUI
    private Runnable updateMapUI = new Runnable()
    {
        @Override
        public void run()
        {
          //  vb.vibrate(10);
            updateMapLayout();
            TimerMapUI.postDelayed(this, UPDATE_TIME_MAP_UI);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        // TimerMapUI
        TimerMapUI = new Handler();
        TimerMapUI.removeCallbacks(updateMapUI);
        TimerMapUI.postDelayed(updateMapUI, UPDATE_TIME_MAP_UI);

        // Vibrator
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Create map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
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
                intent.setClass(MapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.stats:
                intent.setClass(MapActivity.this, StatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.timeline:
                intent.setClass(MapActivity.this, TimelineActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
                intent.setClass(MapActivity.this, SettingsActivity.class);
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
        TimerMapUI.removeCallbacks(updateMapUI);
        updateFileChanges=true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        startActivity(intent);
        updateFileChanges=true;
        finish();
    }

    public void onClickMap (View view) {

    }

    void updateMapLayout()
    {
        int LineColor;

        int iPlace=0;
        int iPath=0;
        int iUnknownActivityPath = 0;
        int iPoint=0;
        int iItems=0;

        Day currentDay = new Day();

        if (NomadaService.On) // Servicio ejecutándose
        {
            updateFileChanges=true;

            currentDay=NomadaService.currentDay;
        }
        else // Consulta sin servicio en ejecución
        {
            if (updateFileChanges)
            {
                currentDay=readCurrentDay();
                NomadaService.v.serviceRequest.mapUpdate=true;
            }
        }

        if (NomadaService.v.serviceRequest.mapUpdate)
        {
            NomadaService.v.serviceRequest.mapUpdate = false;

            if (MapFirstPosition)
            {
                MapFirstPosition=false;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentDay.places.get(currentDay.nPlaces-1).latitude, currentDay.places.get(currentDay.nPlaces-1).longitude), 17));
                //mMap.moveCamera(CameraUpdateFactory.zoomBy(6));
            }

            mMap.clear();

            // Places
            if (currentDay.nPlaces > 0)
            {
                for (iPlace = 0; iPlace < currentDay.nPlaces; iPlace++)
                {
                    // Prueba de la posición actual
                    if (iPlace == currentDay.nPlaces - 1)
                    {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(currentDay.places.get(iPlace).latitude, currentDay.places.get(iPlace).longitude))
                                .title(currentDay.places.get(iPlace).address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
                    }
                    else
                    {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(currentDay.places.get(iPlace).latitude, currentDay.places.get(iPlace).longitude))
                                .title(currentDay.places.get(iPlace).address));
                    }
                }
            }

            // Paths
            if (currentDay.nPaths > 0)
            {
                for (iPath = 0; iPath < currentDay.nPaths; iPath++)
                {
                    switch (currentDay.paths.get(iPath).activityType)
                    {
                        case STILL:
                            LineColor = Color.YELLOW;
                            break;
                        case WALKING:
                            LineColor = Color.MAGENTA;
                            break;
                        case RUNNING:
                            LineColor = Color.GREEN;
                            break;
                        case VEHICLE:
                            LineColor = Color.CYAN;
                            break;
                        default:
                            LineColor = Color.BLACK;
                            break;
                    }
                    //Toast.makeText(this,"Plotting routing points",Toast.LENGTH_SHORT).show();
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(6)
                            .color(LineColor));
                    //route.setPoints(paths.get(k).routePoints);

                    // Conversión de Coordinates a LatLng
                    List<LatLng> routePoints = new ArrayList<LatLng>();

                    for (iPoint=0; iPoint<currentDay.paths.get(iPath).nRoutePoints; iPoint++)
                    {
                        routePoints.add(new LatLng(currentDay.paths.get(iPath).routePoints.get(iPoint).latitude,currentDay.paths.get(iPath).routePoints.get(iPoint).longitude));
                    }

                    route.setPoints(routePoints);
                }
            }
            else
            {
                Toast.makeText(this,"No Paths for Maps",Toast.LENGTH_SHORT).show();
            }

            // Unknown Activity Paths
            if (currentDay.nUnknownActivityPaths > 0)
            {
                for (iUnknownActivityPath = 0; iUnknownActivityPath < currentDay.nUnknownActivityPaths; iUnknownActivityPath++)
                {
                    //Toast.makeText(this,"Plotting routing points",Toast.LENGTH_SHORT).show();
                    Polyline route = mMap.addPolyline(new PolylineOptions()
                            .width(6)
                            .color(Color.GRAY));
                    //route.setPoints(paths.get(k).routePoints);

                    // Conversión de Coordinates a LatLng
                    List<LatLng> routePoints = new ArrayList<LatLng>();

                    routePoints.add(new LatLng(currentDay.unknownActivityPaths.get(iUnknownActivityPath).pointStart.latitude,currentDay.unknownActivityPaths.get(iUnknownActivityPath).pointStart.longitude));
                    routePoints.add(new LatLng(currentDay.unknownActivityPaths.get(iUnknownActivityPath).pointEnd.latitude,currentDay.unknownActivityPaths.get(iUnknownActivityPath).pointEnd.longitude));

                    route.setPoints(routePoints);
                }
            }
            else
            {
                Toast.makeText(this,"No Unknown Activity Paths for Maps",Toast.LENGTH_SHORT).show();
            }

            if (!NomadaService.On)
            {
                updateFileChanges = false;
            }
        }
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
//    void updateMapLayout()
//    {
//
//        int i;
//        int LineColor;
//
//        if (NomadaService.requestMapUpdate)
//        {
//            NomadaService.requestMapUpdate = false;
//            if (MapFirstPosition)
//            {
//                MapFirstPosition=false;
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(NomadaService.latitude, NomadaService.longitude), 17));
//                //mMap.moveCamera(CameraUpdateFactory.zoomBy(6));
//            }
//            mMap.clear();
//
//            // Places
//            // Places from Class
//            for (i=0; i<NomadaService.storyline.nPlaces; i++)
//            {
//                // Prueba de la posición actual
//                if (i == NomadaService.storyline.nPlaces-1)
//                {
//                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(NomadaService.places.get(i).latitude,NomadaService.places.get(i).longitude))
//                            .title(NomadaService.places.get(i).address)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
//                }
//                else
//                {
//                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(NomadaService.places.get(i).latitude,NomadaService.places.get(i).longitude))
//                            .title(NomadaService.places.get(i).address));
//                }
//
//            }
//
//            int k=0;
//            for (k=0; k<NomadaService.storyline.nPaths; k++)
//            {
//                switch(NomadaService.paths.get(k).activityType)
//                {
//                    case STILL:
//                        LineColor=Color.YELLOW;
//                        break;
//                    case WALKING:
//                        LineColor=Color.MAGENTA;
//                        break;
//                    case RUNNING:
//                        LineColor=Color.GREEN;
//                        break;
//                    case CAR:
//                        LineColor=Color.CYAN;
//                        break;
//                    default:
//                        LineColor=Color.BLACK;
//                        break;
//                }
//                Polyline route = mMap.addPolyline(new PolylineOptions()
//                        .width(6)
//                        .color(LineColor));
//                route.setPoints(NomadaService.paths.get(k).routePoints);
//            }
//        }
//    }
}
