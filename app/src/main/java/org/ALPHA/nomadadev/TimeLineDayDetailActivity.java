package org.ALPHA.nomadadev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class TimeLineDayDetailActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    int lastPosition = 0;

    public static Day day;
    public static TimeLine timeLine;

    public static int nItemToShow = 0;
    public static int nPlaceToShow = 0;
    public static int nPathToShow = 0;
    public static int nUnknownPathToShow = 0;

    int iItem = 0;
    int iDay = 0;

    static View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_day_detail);


        iItem = getIntent().getExtras().getInt("iItem");
        iDay = getIntent().getExtras().getInt("iDay");

        timeLine = readTimeLine();

        day = readDay(timeLine.item.get(iDay).fileName);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setOffscreenPageLimit(day.nItems-1);
        mViewPager.setOffscreenPageLimit(1);


        mViewPager.setAdapter(mSectionsPagerAdapter);

        this.setTitle(day.dateString);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        lastPosition = nItemToShow;
        mViewPager.setCurrentItem(iItem);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line_day_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = null;

            Character itemType;

            nItemToShow = position;

            // FUNCIONA
/////////////////////////////////////////////////////////////////
//            nPlaceToShow = position;
//
//            if ((position - lastPosition) > 0)
//            {
//                nPlaceToShow++;
//                if (nPlaceToShow > day.nPlaces-1)
//                {
//                    nPlaceToShow = day.nPlaces-1;
//                }
//            }
//            else if ((position - lastPosition) < 0)
//            {
//                nPlaceToShow--;
//                if (nPlaceToShow < 0)
//                {
//                    nPlaceToShow = 0;
//                }
//            }
//                        itemType=day.typeOfItem.get(position);
//
//            Toast.makeText(ShowPlaceActivity.this,"itemType "+itemType,Toast.LENGTH_SHORT).show();
//
//
//            fragment= new showPlace();
//            args.putInt(showPlace.ARG_PLACE, position + 1);
//            fragment.setArguments(args);
///////////////////////////////////////////////////////////////

            //nPlaceToShow = position;
//            Toast.makeText(ShowPlaceActivity.this, "position" +position,Toast.LENGTH_SHORT).show();

            itemType=day.typeOfItem.get(position);

            Bundle args;

            switch (itemType)
            {
                case 'P':
//                    if ((position - lastPosition) > 0)
//                    {
//                        nPlaceToShow++;
//                        if (nPlaceToShow > day.nPlaces-1)
//                        {
//                           nPlaceToShow = day.nPlaces-1;
//                        }
//                    }
//                    else if ((position - lastPosition) < 0)
//                    {
//                        nPlaceToShow--;
//                        if (nPlaceToShow < 0)
//                        {
//                            nPlaceToShow = 0;
//                        }
//                    }
//                    nPlaceToShow = currentItemToCurrentActivity(day,position,0);

                    fragment= new showPlace();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nPlace", nPlaceToShow);
                    fragment.setArguments(args);
                    nPlaceToShow++;
                    if (nPlaceToShow > day.nPlaces-1)
                    {
                        nPlaceToShow = day.nPlaces-1;
                    }
                    break;


                case 'R':
//                    if ((position - lastPosition) > 0)
//                    {
//                        nPathToShow++;
//                        if (nPathToShow > day.nPaths-1)
//                        {
//                            nPathToShow = day.nPaths-1;
//                        }
//                    }
//                    else if ((position - lastPosition) < 0)
//                    {
//                        nPathToShow--;
//                        if (nPathToShow < 0)
//                        {
//                            nPathToShow = 0;
//                        }
//                    }

//                    nPathToShow = currentItemToCurrentActivity(day,position,1);
//
                    fragment= new showPath();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nPath", nPathToShow);
                    fragment.setArguments(args);
                    nPathToShow++;
                    if (nPathToShow > day.nPaths-1)
                    {
                        nPathToShow = day.nPaths-1;
                    }
                    break;


                case 'U':

//                    nUnknownPathToShow = currentItemToCurrentActivity(day,position,2);

                    fragment= new showUnknownPath();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nUnknownPath", nUnknownPathToShow);
                    fragment.setArguments(args);
                    nUnknownPathToShow++;
                    if (nUnknownPathToShow > day.nUnknownActivityPaths-1)
                    {
                        nUnknownPathToShow = day.nUnknownActivityPaths-1;
                    }
                    break;
            }

            lastPosition = position;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return day.nItems;
            return day.nItems;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(TimeLineDayDetailActivity.this, TimeLineDayActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("iDay",iDay);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    public static class showPlace extends Fragment
    {
//        public static final String ARG_PLACE = "place";

        Day.Place place = new Day.Place();
        MapView mapView;
        GoogleMap placeMap;

        public showPlace()
        {
//            place = CurrentData.day.places.get(CurrentData.nPlaceToShow);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {

            rootView = inflater.inflate(R.layout.place_layout, container, false);

            Bundle args = getArguments();

//            nPlaceToShow = currentItemToCurrentActivity(day,args.getInt("nItem"),0);
            int nPlace = currentItemToCurrentActivity(day,args.getInt("nItem"),0);

            place = day.places.get(nPlace);


//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+nPlaceToShow+"/"+day.nPlaces);
//            ((TextView) rootView.findViewById(R.id.PlaceNItem)).setText(nItemToShow+"/"+(day.nItems-1));
//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(nPlaceToShow+1)+"/"+day.nPlaces);
//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(args.getInt("nPlace")+1)+"/"+day.nPlaces);
            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(nPlace+1)+"/"+day.nPlaces);
            ((TextView) rootView.findViewById(R.id.PlaceNItem)).setText((args.getInt("nItem")+1)+"/"+(day.nItems));
            ((TextView) rootView.findViewById(R.id.PlaceName)).setText(place.address);
            ((TextView) rootView.findViewById(R.id.PlaceLatitudeData)).setText(Double.toString(place.latitude));
            ((TextView) rootView.findViewById(R.id.PlaceLongitudeData)).setText(Double.toString(place.longitude));
            ((TextView) rootView.findViewById(R.id.PlaceAltitudeData)).setText(Double.toString(place.altitude)+" m");
            ((TextView) rootView.findViewById(R.id.PlaceAddressData)).setText(place.address);
            ((TextView) rootView.findViewById(R.id.PlaceFeatureNameData)).setText(place.placeName);
            ((TextView) rootView.findViewById(R.id.PlaceTimeArrivalData)).setText(place.timeArrivalString);
            ((TextView) rootView.findViewById(R.id.PlaceTimeDepartureData)).setText(place.timeDepartureString);
            ((TextView) rootView.findViewById(R.id.PlaceTimeDurationData)).setText(place.timeDurationString);


            // OnTouch Map listener
            View.OnTouchListener onTouchMap = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        LinearLayout.LayoutParams mapLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                        mapView.setLayoutParams(mapLayoutParams);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {

                    }
                    return true;
                }
            };

            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) rootView.findViewById(R.id.placeMap);
            mapView.setOnTouchListener(onTouchMap);
            mapView.onCreate(savedInstanceState);



            // Gets to GoogleMap from the MapView and does initialization stuff
            placeMap = mapView.getMap();
            placeMap.getUiSettings().setMyLocationButtonEnabled(true);
            placeMap.setMyLocationEnabled(true);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());

            placeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.latitude, place.longitude), 17));
            placeMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.latitude, place.longitude))
                    .title(place.address));

            return rootView;
        }

        @Override
        public void onResume() {
            mapView.onResume();
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }
    }

    public static class showPath extends Fragment
    {


        Day.Path path = new Day.Path();
        MapView mapView;
        GoogleMap pathMap;

        public showPath()
        {
//            place = CurrentData.day.places.get(CurrentData.nPlaceToShow);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            int iPoint = 0;
            int LineColor = 0;


            View rootView = inflater.inflate(R.layout.path_layout, container, false);

            Bundle args = getArguments();

//            nPathToShow = currentItemToCurrentActivity(day,args.getInt("nItem"),1);

            int nPath = currentItemToCurrentActivity(day,args.getInt("nItem"),1);

            path = day.paths.get(nPath);


//            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+nPathToShow+"/"+day.nPaths);
//            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+(args.getInt("nPath")+1)+"/"+day.nPaths);
            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+(nPath+1)+"/"+day.nPaths);

            switch(path.activityType)
            {
                case WALKING:
                    ((TextView) rootView.findViewById(R.id.PathTitle)).setTextColor(Color.MAGENTA);
                    break;
                case RUNNING:
                    ((TextView) rootView.findViewById(R.id.PathTitle)).setTextColor(Color.GREEN);
                    break;
                case VEHICLE:
                    ((TextView) rootView.findViewById(R.id.PathTitle)).setTextColor(Color.CYAN);
                    break;
            }
//            ((TextView) rootView.findViewById(R.id.PathNItem)).setText(nItemToShow+"/"+(day.nItems-1));
            ((TextView) rootView.findViewById(R.id.PathNItem)).setText((args.getInt("nItem")+1)+"/"+day.nItems);

            ((TextView) rootView.findViewById(R.id.PathActivityData)).setText(""+path.activityType);
            ((TextView) rootView.findViewById(R.id.PathTimeStartData)).setText(path.timeStartString);
            ((TextView) rootView.findViewById(R.id.PathTimeStopData)).setText(path.timeEndString);
            ((TextView) rootView.findViewById(R.id.PathDurationData)).setText(path.timeDurationString);
            ((TextView) rootView.findViewById(R.id.PathDistanceData)).setText(path.distance+" m");
            ((TextView) rootView.findViewById(R.id.PathSpeedAverageData)).setText(path.speedAverage+" Km/h");
            ((TextView) rootView.findViewById(R.id.PathSpeedMinData)).setText(path.speedMin+" Km/h");
            ((TextView) rootView.findViewById(R.id.PathSpeedMaxData)).setText(path.speedMax+" Km/h");
            ((TextView) rootView.findViewById(R.id.PathStepsData)).setText(path.nSteps+"");
            ((TextView) rootView.findViewById(R.id.PathNPointsData)).setText(path.nRoutePoints+"");

            // Get proper map zoom
            int mapZoom = 0;
            mapZoom = properMapZoom(path.distance);

            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) rootView.findViewById(R.id.pathMap);
            mapView.onCreate(savedInstanceState);

            // Gets to GoogleMap from the MapView and does initialization stuff
            pathMap = mapView.getMap();
            pathMap.getUiSettings().setMyLocationButtonEnabled(true);
            pathMap.setMyLocationEnabled(true);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());

            int middlePoint = path.nRoutePoints/2;
            pathMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(path.routePoints.get(middlePoint).latitude, path.routePoints.get(middlePoint).longitude),mapZoom));

            switch (path.activityType)
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
            Polyline route = pathMap.addPolyline(new PolylineOptions()
                    .width(6)
                    .color(LineColor));

            // From Day.Coordinates to LatLng
            List<LatLng> routePoints = new ArrayList<LatLng>();

            for (iPoint=0; iPoint<path.nRoutePoints; iPoint++)
            {
                routePoints.add(new LatLng(path.routePoints.get(iPoint).latitude,path.routePoints.get(iPoint).longitude));
            }

            route.setPoints(routePoints);

            return rootView;
        }

        @Override
        public void onResume() {
            mapView.onResume();
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }
    }

    static public int properMapZoom(float distance)
    {
        int mapZoom = 0;

        if (distance > 512000)
        {
            mapZoom = 6;
        }
        else if (distance > 256000)
        {
            mapZoom = 7;
        }
        else if (distance > 128000)
        {
            mapZoom = 8;
        }
        else if (distance > 64000)
        {
            mapZoom = 9;
        }
        else if (distance > 32000)
        {
            mapZoom = 10;
        }
        else if (distance > 16000)
        {
            mapZoom = 11;
        }
        else if (distance > 8000)
        {
            mapZoom = 12;
        }
        else if (distance > 4000)
        {
            mapZoom = 13;
        }
        else if (distance > 2000)
        {
            mapZoom = 14;
        }
        else if (distance > 1000)
        {
            mapZoom = 15;
        }
        else if (distance > 500)
        {
            mapZoom = 16;
        }
        else if (distance > 250)
        {
            mapZoom = 17;
        }
        else if (distance <= 250)
        {
            mapZoom = 18;
        }

        return mapZoom;
    }

    public static class showUnknownPath extends Fragment
    {
        Day.UnknownActivityPath unknownPath = new Day.UnknownActivityPath();
        MapView mapView;
        GoogleMap unknownPathMap;

        public showUnknownPath()
        {
//            place = CurrentData.day.places.get(CurrentData.nPlaceToShow);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {


            View rootView = inflater.inflate(R.layout.unknown_path_layout, container, false);

            Bundle args = getArguments();

//            nUnknownPathToShow = currentItemToCurrentActivity(day,args.getInt("nItem"),2);

            int nUnknownPath = currentItemToCurrentActivity(day,args.getInt("nItem"),2);

            unknownPath = day.unknownActivityPaths.get(nUnknownPath);


//            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+nUnknownPathToShow+"/"+day.nUnknownActivityPaths);
//            ((TextView) rootView.findViewById(R.id.UnknownPathNItem)).setText(nItemToShow+"/"+(day.nItems-1));

            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+(nUnknownPath+1)+"/"+day.nUnknownActivityPaths);

//            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+(args.getInt("nUnknownPath")+1)+"/"+day.nUnknownActivityPaths);
            ((TextView) rootView.findViewById(R.id.UnknownPathNItem)).setText((args.getInt("nItem")+1)+"/"+(day.nItems-1));

            ((TextView) rootView.findViewById(R.id.UnknownPathLatitudeInitialData)).setText(unknownPath.pointStart.latitude+"");
            ((TextView) rootView.findViewById(R.id.UnknownPathLongitudeInitialData)).setText(unknownPath.pointStart.longitude+"");
            ((TextView) rootView.findViewById(R.id.UnknownPathLatitudeFinalData)).setText(unknownPath.pointEnd.latitude+"");
            ((TextView) rootView.findViewById(R.id.UnknownPathLongitudeFinalData)).setText(unknownPath.pointEnd.longitude+"");
//            ((TextView) rootView.findViewById(R.id.UnknownPathTimeStartData)).setText(unknownPath.timeStartString);
//            ((TextView) rootView.findViewById(R.id.UnknownPathTimeStopData)).setText(unknownPath.timeEndString);
//            ((TextView) rootView.findViewById(R.id.UnknownPathDurationData)).setText(unknownPath.timeDurationString);
//            ((TextView) rootView.findViewById(R.id.UnknownPathDistanceData)).setText(unknownPath.distance+" m");


            // Get proper map zoom
            int mapZoom = 0;
            mapZoom = properMapZoom(unknownPath.distance);

            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) rootView.findViewById(R.id.unknownPathMap);
            mapView.onCreate(savedInstanceState);

            // Gets to GoogleMap from the MapView and does initialization stuff
            unknownPathMap = mapView.getMap();
            unknownPathMap.getUiSettings().setMyLocationButtonEnabled(true);
            unknownPathMap.setMyLocationEnabled(true);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());

            // Get middle point to center the map
            double middleLatitudePoint;
            double middleLongitudePoint;
            middleLatitudePoint = (unknownPath.pointStart.latitude + unknownPath.pointEnd.latitude) / 2;
            middleLongitudePoint = (unknownPath.pointStart.longitude + unknownPath.pointEnd.longitude) / 2;

            unknownPathMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(middleLatitudePoint,middleLongitudePoint),mapZoom));

            // Create line for plotting the route
            Polyline route = unknownPathMap.addPolyline(new PolylineOptions()
                    .width(6)
                    .color(Color.GRAY));

            // From Day.Coordinates to LatLng
            List<LatLng> routePoints = new ArrayList<LatLng>();

            routePoints.add(new LatLng(unknownPath.pointStart.latitude,unknownPath.pointStart.longitude));
            routePoints.add(new LatLng(unknownPath.pointEnd.latitude,unknownPath.pointEnd.longitude));

            route.setPoints(routePoints);

            return rootView;
        }

        @Override
        public void onResume() {
            mapView.onResume();
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }
    }

    public static int currentItemToCurrentActivity (Day day, int currentItem, int typeOfItem)
    {
        int currentPlace = -1;
        int currentPath = -1;
        int currentUnknownPath = -1;
        int iItem = 0;

        for (iItem = 0; iItem < (currentItem +1); iItem++)
        {
            switch (day.typeOfItem.get(iItem))
            {
                case 'P':
                    currentPlace++;
                    break;

                case 'R':
                    currentPath++;
                    break;

                case 'U':
                    currentUnknownPath++;
                    break;
            }
        }

        if (typeOfItem == 0)
        {
            return currentPlace;
        }
        else if (typeOfItem == 1)
        {
            return currentPath;
        }
        else if (typeOfItem == 2)
        {
            return currentUnknownPath;
        }
        else
        {
            return -1;
        }

    }

    public static Day readDay (String fileName)
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
//            Toast.makeText(TimeLineDayActivity.this,"Error creating FileInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(fis);
        }
        catch (IOException e)
        {
//            Toast.makeText(this,"Error creating ObjectInputStream",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            currentday = (Day)ois.readObject();
            //Toast.makeText(this,"Places got",Toast.LENGTH_SHORT).show();
        }
        catch (ClassNotFoundException e)
        {
//            Toast.makeText(this,"Places ClassNotFoundException",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
//            Toast.makeText(this,"Error Places got",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        try
        {
            ois.close();
        }
        catch (IOException e)
        {
//            Toast.makeText(this,"Error Closing file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return currentday;
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
}
