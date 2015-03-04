package org.ALPHA.nomadadev;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
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
import android.view.View;
import android.view.ViewGroup;
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


public class ShowPlaceActivity extends Activity {

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

    public static Day currentDay;
    public static TimeLine timeLine;

    public static int nItemToShow = 0;
    public static int nPlaceToShow = 0;
    public static int nPathToShow = 0;
    public static int nUnknownPathToShow = 0;

    static View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_place_activity);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setOffscreenPageLimit(currentDay.nItems-1);
        mViewPager.setOffscreenPageLimit(1);


        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        lastPosition = nItemToShow;
        mViewPager.setCurrentItem(3);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_place, menu);
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
//                if (nPlaceToShow > currentDay.nPlaces-1)
//                {
//                    nPlaceToShow = currentDay.nPlaces-1;
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
//                        itemType=currentDay.typeOfItem.get(position);
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

            itemType=currentDay.typeOfItem.get(position);

            Bundle args;

            switch (itemType)
            {
                case 'P':
//                    if ((position - lastPosition) > 0)
//                    {
//                        nPlaceToShow++;
//                        if (nPlaceToShow > currentDay.nPlaces-1)
//                        {
//                           nPlaceToShow = currentDay.nPlaces-1;
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
//                    nPlaceToShow = currentItemToCurrentActivity(currentDay,position,0);

                    fragment= new showPlace();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nPlace", nPlaceToShow);
                    fragment.setArguments(args);
                    nPlaceToShow++;
                    if (nPlaceToShow > currentDay.nPlaces-1)
                    {
                       nPlaceToShow = currentDay.nPlaces-1;
                    }
                    break;


                case 'R':
//                    if ((position - lastPosition) > 0)
//                    {
//                        nPathToShow++;
//                        if (nPathToShow > currentDay.nPaths-1)
//                        {
//                            nPathToShow = currentDay.nPaths-1;
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

//                    nPathToShow = currentItemToCurrentActivity(currentDay,position,1);
//
                    fragment= new showPath();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nPath", nPathToShow);
                    fragment.setArguments(args);
                    nPathToShow++;
                    if (nPathToShow > currentDay.nPaths-1)
                    {
                        nPathToShow = currentDay.nPaths-1;
                    }
                    break;


                case 'U':

//                    nUnknownPathToShow = currentItemToCurrentActivity(currentDay,position,2);

                    fragment= new showUnknownPath();
                    args = new Bundle();
                    // Our object is just an integer :-P
                    args.putInt("nItem", position);
                    args.putInt("nUnknownPath", nUnknownPathToShow);
                    fragment.setArguments(args);
                    nUnknownPathToShow++;
                    if (nUnknownPathToShow > currentDay.nUnknownActivityPaths-1)
                    {
                        nUnknownPathToShow = currentDay.nUnknownActivityPaths-1;
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
//            return currentDay.nItems;
            return currentDay.nItems;

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

//            nPlaceToShow = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),0);
            int nPlace = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),0);

            place = currentDay.places.get(nPlace);


//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+nPlaceToShow+"/"+currentDay.nPlaces);
//            ((TextView) rootView.findViewById(R.id.PlaceNItem)).setText(nItemToShow+"/"+(currentDay.nItems-1));
//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(nPlaceToShow+1)+"/"+currentDay.nPlaces);
//            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(args.getInt("nPlace")+1)+"/"+currentDay.nPlaces);
            ((TextView) rootView.findViewById(R.id.PlaceTitle)).setText("PLACE "+(nPlace+1)+"/"+currentDay.nPlaces);
            ((TextView) rootView.findViewById(R.id.PlaceNItem)).setText((args.getInt("nItem")+1)+"/"+(currentDay.nItems));
            ((TextView) rootView.findViewById(R.id.PlaceName)).setText(place.address);
            ((TextView) rootView.findViewById(R.id.PlaceLatitudeData)).setText(Double.toString(place.latitude));
            ((TextView) rootView.findViewById(R.id.PlaceLongitudeData)).setText(Double.toString(place.longitude));
            ((TextView) rootView.findViewById(R.id.PlaceAltitudeData)).setText(Double.toString(place.altitude)+" m");
            ((TextView) rootView.findViewById(R.id.PlaceAddressData)).setText(place.address);
            ((TextView) rootView.findViewById(R.id.PlaceFeatureNameData)).setText(place.placeName);
            ((TextView) rootView.findViewById(R.id.PlaceTimeArrivalData)).setText(place.timeArrivalString);
            ((TextView) rootView.findViewById(R.id.PlaceTimeDepartureData)).setText(place.timeDepartureString);
            ((TextView) rootView.findViewById(R.id.PlaceTimeDurationData)).setText(place.timeDurationString);


            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) rootView.findViewById(R.id.placeMap);
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

//            nPathToShow = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),1);

            int nPath = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),1);

            path = currentDay.paths.get(nPath);


//            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+nPathToShow+"/"+currentDay.nPaths);
//            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+(args.getInt("nPath")+1)+"/"+currentDay.nPaths);
            ((TextView) rootView.findViewById(R.id.PathTitle)).setText("PATH "+(nPath+1)+"/"+currentDay.nPaths);

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
//            ((TextView) rootView.findViewById(R.id.PathNItem)).setText(nItemToShow+"/"+(currentDay.nItems-1));
            ((TextView) rootView.findViewById(R.id.PathNItem)).setText((args.getInt("nItem")+1)+"/"+currentDay.nItems);

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

//            nUnknownPathToShow = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),2);

            int nUnknownPath = currentItemToCurrentActivity(currentDay,args.getInt("nItem"),2);

            unknownPath = currentDay.unknownActivityPaths.get(nUnknownPath);


//            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+nUnknownPathToShow+"/"+currentDay.nUnknownActivityPaths);
//            ((TextView) rootView.findViewById(R.id.UnknownPathNItem)).setText(nItemToShow+"/"+(currentDay.nItems-1));

            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+(nUnknownPath+1)+"/"+currentDay.nUnknownActivityPaths);

//            ((TextView) rootView.findViewById(R.id.UnknownPathTitle)).setText("UNKNOWN PATH "+(args.getInt("nUnknownPath")+1)+"/"+currentDay.nUnknownActivityPaths);
            ((TextView) rootView.findViewById(R.id.UnknownPathNItem)).setText((args.getInt("nItem")+1)+"/"+(currentDay.nItems-1));

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

    public static int currentItemToCurrentActivity (Day currentDay, int currentItem, int typeOfItem)
    {
        int currentPlace = -1;
        int currentPath = -1;
        int currentUnknownPath = -1;
        int iItem = 0;

        for (iItem = 0; iItem < (currentItem +1); iItem++)
        {
            switch (currentDay.typeOfItem.get(iItem))
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
}
