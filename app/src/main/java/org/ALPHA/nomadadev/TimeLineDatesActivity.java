package org.ALPHA.nomadadev;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class TimeLineDatesActivity extends Activity {

    Vibrator vb;

    int i=0;


    List<TextView> dates = new ArrayList<TextView>();
    int iDates = 0;

    TimeLine timeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_dates);

        // Vibrator
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        timeLine=readTimeLine();

        updateLayout();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line_dates, menu);
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
    public void onBackPressed() {

        Intent intent = new Intent(TimeLineDatesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateLayout ()
    {
        int iDay = 0;

        // Add line
        addLine();

        // Add Days
        for (iDay = (timeLine.nDays-1); iDay > -1; iDay--)
        {
            addDay(iDay);
        }
    }

    public void addDay (int iDay)
    {
        String dayDate;
        TimeLine.Item item;

        item = timeLine.item.get(iDay);

        // Day Date Text
        dayDate = item.day + "/" + item.month + "/" + item.year;
        addText(dayDate,Color.WHITE,40,iDay);

        // Add line
        addLine();
    }

    public void addLine ()
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutActivityTimeLineDates);
        View v = new View(TimeLineDatesActivity.this);
        Space s1 = new Space(TimeLineDatesActivity.this);
        Space s2 = new Space(TimeLineDatesActivity.this);
        v.setBackgroundColor(Color.parseColor("#ff4aff00"));
        v.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1));
        s1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2));
        s2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2));
        currentLayout.addView(s1);
        currentLayout.addView(v);
        currentLayout.addView(s2);
    }

    public void addText (String text, int textColor, int textSize, final int iDay)
    {
        LinearLayout currentLayout = (LinearLayout)findViewById(R.id.LinearLayoutActivityTimeLineDates);

        final TextView textView = new TextView(this);
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
                    intent.setClass(TimeLineDatesActivity.this, TimeLineDayActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putInt("iDay",iDay);
                    bundle.putString("fileName",timeLine.item.get(iDay).fileName);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();
                }
                return true;
            }
        };
        // Setting Listener
        textView.setOnTouchListener(onTouchDay);

        dates.add(textView);

        // Add TextView to View
        currentLayout.addView(dates.get(iDates));
        // Increasing iDates for holding new Dates
        iDates++;
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
            Toast.makeText(this, "Error reading timeline.ini", Toast.LENGTH_SHORT).show();
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
