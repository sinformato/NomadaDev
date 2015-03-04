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
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class TimeLineDayActivity extends Activity {

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

    static Vibrator vb;

    static View rootView;

    static TimeLine timeLine;
    int iDay = 0;

    List<TextView> dates = new ArrayList<TextView>();
    int iDates = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_day);

        iDay = getIntent().getExtras().getInt("iDay");
        String fileName = getIntent().getExtras().getString("fileName");
        Toast.makeText(this, "iDay " + iDay + " " + fileName, Toast.LENGTH_SHORT).show();
//        currentDay = readDay(fileName);
//        Toast.makeText(this,"nPlaces "+currentDay.nPlaces,Toast.LENGTH_SHORT).show();

        // Read timeLine
        timeLine = readTimeLine();

        // Vibrator
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mViewPager.setCurrentItem(iDay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line_day, menu);
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(TimeLineDayActivity.this, TimeLineDatesActivity.class);
        startActivity(intent);
        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = null;
            Bundle args = new Bundle();

            fragment = new showDayTimeLine();

            args.putInt("iDay",position);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return timeLine.nDays;
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

    public static class showDayTimeLine extends Fragment
    {
        public showDayTimeLine()
        {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            Day day = new Day();

            rootView = inflater.inflate(R.layout.timeline_day_layout, container, false);

            Bundle args = getArguments();

            int iDay = args.getInt("iDay");

            day = readDay(timeLine.item.get(iDay).fileName);
//            TimeLineDayDetailActivity.currentDay = new Day();
//            TimeLineDayDetailActivity.currentDay = day;

            addDay(day.dateString+" nItems "+day.nItems+" nPlaces"+day.nPlaces+" nPaths "+day.nPaths+" nUPaths "+day.nUnknownActivityPaths, Color.YELLOW,30,iDay);

            int iPlace = 0;
            int iPath = 0;
            int iUnknownPath = 0;
            int iItem = 0;

            for (iItem = 0; iItem < day.nItems; iItem++)
            {
                switch (day.typeOfItem.get(iItem))
                {
                    case 'P':
                        if (iPlace < day.nPlaces)
                        {
                            addPlace(day.places.get(iPlace),iItem, iDay);
                            iPlace++;
                        }
                        break;
                    case 'R':
                        if (iPath < day.nPaths)
                        {
                            addPath(day.paths.get(iPath),iItem, iDay);
                            iPath++;
                        }

                        break;
                    case 'U':
                        if (iUnknownPath < day.nUnknownActivityPaths)
                        {
                            addUnknownPath(day.unknownActivityPaths.get(iUnknownPath),iItem, iDay);
                            iUnknownPath++;
                        }

                        break;
                }
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
        }

        void addDay(String text, int textColor, int textSize, final int iDay)
        {
            LinearLayout currentLayout = (LinearLayout)rootView.findViewById(R.id.LinearLayoutActivityTimeLineDay);

            final TextView textView = new TextView(getActivity());
            // TextView data
            textView.setText(text);
            textView.setTextSize(textSize);
            textView.setTextColor(textColor);
            textView.setBackgroundColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            // TextView layout params
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,90);
            textView.setLayoutParams(textViewLayoutParams);
            // OnTouch Listener
            View.OnTouchListener onTouchDay = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        textView.setBackgroundColor(Color.GRAY);
                        vb.vibrate(35);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        textView.setBackgroundColor(Color.BLACK);
//                    Toast.makeText(TimeLineDatesActivity.this,"iDay "+iDay,Toast.LENGTH_SHORT).show()

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TimeLineDayActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("iDay",iDay);
                        bundle.putString("fileName",timeLine.item.get(iDay).fileName);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            };
            // Setting Listener
            textView.setOnTouchListener(onTouchDay);

//            dates.add(textView);

            // Add TextView to View
//            currentLayout.addView(dates.get(iDates));
            currentLayout.addView(textView);
            // Increasing iDates for holding new Dates
//            iDates++;
        }

        void addPlace(Day.Place place, final int iItem, final int iDay)
        {
            LinearLayout currentLayout = (LinearLayout)rootView.findViewById(R.id.LinearLayoutActivityTimeLineDay);

            final TextView textView = new TextView(getActivity());
            // TextView data
            textView.setText(place.timeArrivalString+"\n"+place.timeDurationString);
            textView.setTextSize(10);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(Color.YELLOW);
            textView.setGravity(Gravity.CENTER);
//            textView.setHeight(30);
//            textView.setWidth(70);

            // TextView layout params
            int width;
            int height = 60;
            width = 90+ (int)(place.timeDuration / 60000) * 4;

            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(width, height);
            textViewLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(textViewLayoutParams);



            // OnTouch Listener
            View.OnTouchListener onTouchPlace = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        textView.setBackgroundColor(Color.GRAY);
                        vb.vibrate(35);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        textView.setBackgroundColor(Color.YELLOW);
//                    Toast.makeText(TimeLineDatesActivity.this,"iDay "+iDay,Toast.LENGTH_SHORT).show()

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TimeLineDayDetailActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("iItem",iItem);
                        bundle.putInt("iDay",iDay);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            };
            // Setting Listener
            textView.setOnTouchListener(onTouchPlace);

//            dates.add(textView);

            // Add TextView to View
//            currentLayout.addView(dates.get(iDates));
            currentLayout.addView(textView);
            // Increasing iDates for holding new Dates
//            iDates++;
        }

        void addPath(final Day.Path path, final int iItem, final int iDay)
        {
            LinearLayout currentLayout = (LinearLayout)rootView.findViewById(R.id.LinearLayoutActivityTimeLineDay);

            final TextView textView = new TextView(getActivity());

            switch(path.activityType)
            {
                case WALKING:
                    textView.setBackgroundColor(Color.MAGENTA);
                    break;
                case RUNNING:
                    textView.setBackgroundColor(Color.GREEN);
                    break;
                case VEHICLE:
                    textView.setBackgroundColor(Color.CYAN);
                    break;
            }
            // TextView data
            textView.setText(path.timeStartString+"\n"+path.timeDurationString+"\n"+path.speedAverage);
            textView.setTextSize(10);
            textView.setTextColor(Color.BLACK);

            textView.setGravity(Gravity.CENTER);
//            textView.setHeight(30);
//            textView.setWidth(70);
            // TextView layout params
            int width = 70;
            int height = 0;
            height = 80 +(int)(path.timeDuration / 60000) * 4;

            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(width, height);
            textViewLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(textViewLayoutParams);

            // OnTouch Listener
            View.OnTouchListener onTouchPath = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        textView.setBackgroundColor(Color.GRAY);
                        vb.vibrate(35);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        switch(path.activityType)
                        {
                            case WALKING:
                                textView.setBackgroundColor(Color.MAGENTA);
                                break;
                            case RUNNING:
                                textView.setBackgroundColor(Color.GREEN);
                                break;
                            case VEHICLE:
                                textView.setBackgroundColor(Color.CYAN);
                                break;
                        }
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TimeLineDayDetailActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("iItem",iItem);
                        bundle.putInt("iDay",iDay);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            };
            // Setting Listener
            textView.setOnTouchListener(onTouchPath);

//            dates.add(textView);

            // Add TextView to View
//            currentLayout.addView(dates.get(iDates));
            currentLayout.addView(textView);
            // Increasing iDates for holding new Dates
//            iDates++;
        }

        void addUnknownPath(Day.UnknownActivityPath unknownPath, final int iItem, final int iDay)
        {
            LinearLayout currentLayout = (LinearLayout)rootView.findViewById(R.id.LinearLayoutActivityTimeLineDay);

            final TextView textView = new TextView(getActivity());
            // TextView data
//            textView.setText(text);
//            textView.setTextSize(textSize);
//            textView.setTextColor(textColor);
            textView.setBackgroundColor(Color.GRAY);
            textView.setGravity(Gravity.CENTER);
//            textView.setHeight(30);
//            textView.setWidth(70);
            // TextView layout params
            int width = 70;
            int height = 80;

            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(width, height);
            textViewLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;

            textView.setLayoutParams(textViewLayoutParams);
            // OnTouch Listener
            View.OnTouchListener onTouchUnknownPath = new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        textView.setBackgroundColor(Color.GREEN);
                        vb.vibrate(35);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        textView.setBackgroundColor(Color.GRAY);
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TimeLineDayDetailActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("iItem",iItem);
                        bundle.putInt("iDay",iDay);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            };
            // Setting Listener
            textView.setOnTouchListener(onTouchUnknownPath);

//            dates.add(textView);

            // Add TextView to View
//            currentLayout.addView(dates.get(iDates));
            currentLayout.addView(textView);
            // Increasing iDates for holding new Dates
//            iDates++;
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
