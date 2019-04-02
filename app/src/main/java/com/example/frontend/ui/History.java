package com.example.frontend.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogViewModel;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserDao;
import com.example.frontend.persistence.UserViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;


    final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    final SimpleDateFormat smallDate = new SimpleDateFormat("MM/dd");
    private DayLogViewModel mDayLogViewModel;
    Date weekAgo;
    Date newStart;
    Date newEnd;
    View pView;
    Integer days = 7;
    String modeChosen = "kCal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);


        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(toggle);
        }
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.textColorPrimary));
        NavigationView navigationView = findViewById(R.id.nav_side);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }



        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        newStart = today;
        c.add(Calendar.DAY_OF_YEAR, -6);
        weekAgo = c.getTime();
        newEnd = c.getTime();

        RadioGroup radioGroup = findViewById(R.id.radio_date_range);
        radioGroup.check(R.id.button_select_week);

        getDailySnapShots(today, weekAgo, modeChosen);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch(menuItem.getItemId()){
//            case R.id.nav_search: {
//                Intent intent = new Intent(History.this, SearchFood.class);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//                intent.putExtra("currentDay",Integer.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())));
//                startActivity(intent);
//                return true;
//            }
            case R.id.nav_profile: {
                Intent intent = new Intent(History.this, Profile.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_history: {
                Intent intent = new Intent(History.this, History.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_summary: {
                Intent intent = new Intent(History.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default: return false;
        }
    }

    public void getDailySnapShots(Date start, Date end, String mode) {
        //connect to daily log db
        mDayLogViewModel = ViewModelProviders.of(this).get(DayLogViewModel.class);

        //use layoutinflater to add views (indiv day summaries) to scroll view
        final LayoutInflater layoutInflater = getLayoutInflater();

        // Parent layout
        final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.linear_day_snapshots);

        //remove previous snapshots
        parentLayout.removeAllViews();
        Integer currentDay = Integer.valueOf(df.format(start));
        Integer endDay = Integer.valueOf(df.format(end));
        if(mode.equals("kCal")){
            //add kCal averages layout
            FrameLayout avgsFrame = findViewById(R.id.frameLayout);
            avgsFrame.removeAllViews();
            layoutInflater.inflate(R.layout.history_avg_kcal, avgsFrame, true);
            Integer totalDiff = 0;
            final TextView totalDiffEl = findViewById(R.id.text_total_diff_value);
            final TextView avgDiffEl = findViewById(R.id.text_avg_diff_value);

            //initialize HashMap of dates and caloric differences (to graph)
            final LinkedHashMap<Date, Integer> weekData = new LinkedHashMap<Date, Integer>();

            while (currentDay >= endDay) {

                final Integer dayInLoop = currentDay;
                final Integer lastDay = endDay;
                //add day's snapshot to parent view
                final View view = layoutInflater.inflate(R.layout.day_snapshot, parentLayout, false);
                TextView dateEl = view.findViewById(R.id.text_date);
                try{
                    dateEl.setText(smallDate.format(df.parse(String.valueOf(dayInLoop))));
                }catch (ParseException e){
                    Log.e("parse", e.toString());
                }


                final TextView intakeEl = view.findViewById(R.id.text_intake_value);
                final TextView diffEl = view.findViewById(R.id.text_diff_value);

                //get most recent calorie goal using asynctask
                class getClosestAsyncTask extends AsyncTask<Integer, Void, User> {
                    UserViewModel mUserViewModel = ViewModelProviders.of(History.this).get(UserViewModel.class);

                    @Override
                    protected User doInBackground(final Integer... params) {
                        return mUserViewModel.getClosestDate(params[0]);
                    }

                    @Override
                    protected void onPostExecute(User mUser) {
                        long goal = 1600;
                        if (mUser != null) {
                            goal = mUser.getGoal();
                        }
                        TextView goalEl = view.findViewById(R.id.text_goal_value);
                        goalEl.setText(String.valueOf(goal));

                        double difference =  Double.valueOf(intakeEl.getText().toString()) - goal;
                        diffEl.setText(String.valueOf(difference));


                        //add difference and date to weekData
                        try{
                            weekData.put(df.parse(String.valueOf(dayInLoop)), (int)difference);
                            if(weekData.size()==days){
                                Log.i("weekData size", String.valueOf(weekData.size()));
                                //once each day in week has been added to HashMap, graph data
                                if(modeChosen.equals("kCal")){
                                    createGraph(weekData, df.parse(String.valueOf(lastDay)));
                                }

                            }
                        } catch (ParseException e){
                            Log.e("parse dayInLoop", e.toString());
                        }

                        if(modeChosen.equals("env")){
                            getEnvTotals();
                        } else {
                            getkCalTotals();
                        }
                    }
                }

                mDayLogViewModel.getOneDayLogs(currentDay).observe(this, new Observer<List<DayLog>>() {
                    @Override
                    public void onChanged(@Nullable List<DayLog> dayLogs) {

                        Double totalkCal = 0.0;
                        TextView totalkCalEl = view.findViewById(R.id.text_intake_value);


                        //add event listener to edit button, open edit day activity
                        ImageButton editButton = view.findViewById(R.id.button_edit_day);
                        editButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(History.this, EditDay.class);
                                intent.putExtra("currentDay", dayInLoop);
                                startActivity(intent);
                            }
                        });

                        //iterate over food logs to get totals
                        for (final DayLog log : dayLogs) {
                            //add foods' nutrients to totals
                            totalkCal += log.getKCal();

                        }

                        //set text in snapshot
                        totalkCalEl.setText(totalkCal.toString());
                        new getClosestAsyncTask().execute(dayInLoop);
                    }
                });
                parentLayout.addView(view);

                //increment day
                Date nextDay = null;
                try {
                    nextDay = df.parse(currentDay.toString());
                } catch (ParseException e) {
                    Log.e("parsing", e.toString());
                }

                Calendar nextCal = Calendar.getInstance();
                nextCal.setTime(nextDay);
                nextCal.add(Calendar.DAY_OF_YEAR, -1);
                currentDay = Integer.valueOf(df.format(nextCal.getTime()));
            }

        //inflate with env snapshots
        } else {
            //add env averages layout
            FrameLayout avgsFrame = findViewById(R.id.frameLayout);
            avgsFrame.removeAllViews();
            layoutInflater.inflate(R.layout.history_avg_env, avgsFrame, true);

            //initialize variables to store totals
            Double ghgAvg = 0.0;
            Double landAvg = 0.0;
            Double waterAvg = 0.0;
            final TextView avgGhgEl = findViewById(R.id.text_avg_ghg_value);
            final TextView avgLandEl = findViewById(R.id.text_avg_land_value);
            final TextView avgWaterEl = findViewById(R.id.text_avg_water_value);

            //initialize hashmaps to store data for each env category to graph
            final LinkedHashMap<Date, Double> ghgData = new LinkedHashMap<Date, Double>();
            final LinkedHashMap<Date, Double> landData = new LinkedHashMap<Date, Double>();
            final LinkedHashMap<Date, Double> waterData = new LinkedHashMap<Date, Double>();

            //iterate through days
            while (currentDay >= endDay) {

                final Integer dayInLoop = currentDay;
                final Integer lastDay = endDay;

                //add day's snapshot to parent view
                final View view = layoutInflater.inflate(R.layout.day_snapshot_env, parentLayout, false);
                final TextView totGhgDayEl = view.findViewById(R.id.text_ghg_tot_value);
                final TextView totLandDayEl = view.findViewById(R.id.text_land_tot_value);
                final TextView totWaterDayEl = view.findViewById(R.id.text_water_tot_value);


                //display date in snapshot
                TextView dateEl = view.findViewById(R.id.text_date);
                try {
                    dateEl.setText(smallDate.format(df.parse(String.valueOf(dayInLoop))));
                } catch (ParseException e) {
                    Log.e("parse", e.toString());
                }

                mDayLogViewModel.getOneDayLogs(dayInLoop).observe(this, new Observer<List<DayLog>>() {
                    @Override
                    public void onChanged(@Nullable List<DayLog> dayLogs) {

                         //initialize variables to store day's total for env categories
                        Double totGhgDay = 0.0;
                        Double totLandDay = 0.0;
                        Double totWaterDay = 0.0;

                        //add event listener to edit button, open edit day activity
                        ImageButton editButton = view.findViewById(R.id.button_edit_day);
                        editButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(History.this, EditDay.class);
                                intent.putExtra("currentDay", dayInLoop);
                                startActivity(intent);
                            }
                        });

                        //iterate over food logs to get totals
                        for (final DayLog log : dayLogs) {
                            //add foods' nutrients to totals
                            totGhgDay += log.getGhgVal();
                            totLandDay += log.getLandVal();
                            totWaterDay += log.getWaterVal();

                        }

                        //set text in snapshot
                        totGhgDayEl.setText(String.format("%.3f", totGhgDay));
                        totLandDayEl.setText(String.format("%.3f", totLandDay));
                        totWaterDayEl.setText(String.format("%.3f", totWaterDay));

                        //add day's info to hashmaps to graph
                        try{
                            ghgData.put(df.parse(String.valueOf(dayInLoop)), totGhgDay);
                            landData.put(df.parse(String.valueOf(dayInLoop)), totLandDay);
                            waterData.put(df.parse(String.valueOf(dayInLoop)), totWaterDay);
                        }catch(ParseException e){}

                        if(ghgData.size()==days){
                            try{
                                createGraphEnv(ghgData, landData, waterData, df.parse(String.valueOf(lastDay)));
                            } catch(ParseException e){
                                Log.e("parse", e.toString());
                            }

                        }


                        //sum env cats and display averages
                        if(modeChosen.equals("env")){
                            getEnvTotals();
                        } else {
                            getkCalTotals();
                        }

                    }
                });


                parentLayout.addView(view);

                //increment day
                Date nextDay = null;
                try {
                    nextDay = df.parse(currentDay.toString());
                } catch (ParseException e) {
                    Log.e("parsing", e.toString());
                }

                Calendar nextCal = Calendar.getInstance();
                nextCal.setTime(nextDay);
                nextCal.add(Calendar.DAY_OF_YEAR, -1);
                currentDay = Integer.valueOf(df.format(nextCal.getTime()));
            }
            if(ghgData.size()==days){
                createGraphEnv(ghgData, landData, waterData, newEnd);
            }


        }

    }


    public void goBack(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(newEnd);
        c.add(Calendar.DAY_OF_YEAR, -1);
        newStart = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, -(days-1));
        newEnd = c.getTime();
        getDailySnapShots(newStart, newEnd, modeChosen);
    }

    public void goForward(View view) {
        Calendar c = Calendar.getInstance();
        if(newStart == null){
            newStart = c.getTime();
        }
        c.setTime(newStart);
        c.add(Calendar.DAY_OF_YEAR, 1);
        newEnd = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, (days -1));
        newStart = c.getTime();
        getDailySnapShots(newStart, newEnd, modeChosen);

    }

    private void createGraph(LinkedHashMap<Date, Integer> weekData, Date newEnd){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        //create array of DataPoints where x is a Date and y is the difference between cals consumed and cal goal
        DataPoint[] dataPoints = new DataPoint[days];
        graph.getViewport().setMinX(newEnd.getTime());
        Date day = null;
        for(int i = 0; i <days; i ++){
            Calendar c = Calendar.getInstance();
            c.setTime(newEnd);
            c.add(Calendar.DAY_OF_YEAR, i);
            //convert day to and from formatting to get rid of exact time (and match Date in hashmap)
            try{
                day = df.parse(df.format((c.getTime())));
            } catch(ParseException e ){
                Log.e("parse", e.toString());
            }
            Log.i("inspect days in dataSet", weekData.keySet().toString());
            Log.i("inspect day added to dataSet", day.toString());
            Log.i("inspect dataSet size", String.valueOf(weekData.size()));
            Log.i("inspect day being added to dataset", String.valueOf(weekData.get(day)));
            if(weekData.get(day)!=null){
                dataPoints[i] = new DataPoint(day, weekData.get(day));
            }

        }
        graph.getViewport().setMaxX(day.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setColor(getResources().getColor(R.color.colorAccent));
        graph.addSeries(series);

       graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
       graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);


    }

    private void createGraphEnv(LinkedHashMap<Date, Double> ghgData, LinkedHashMap<Date, Double> landData, LinkedHashMap<Date, Double> waterData, Date newEnd){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        //create arrays for each set of datapoints to graph
        DataPoint[] ghgDataPoints = new DataPoint[days];
        DataPoint[] landDataPoints = new DataPoint[days];
        DataPoint[] waterDataPoints = new DataPoint[days];

       graph.getViewport().setMinX(newEnd.getTime());
        Date day = null;
        for(int i = 0; i < days; i++){
            Calendar c = Calendar.getInstance();
            c.setTime(newEnd);
            c.add(Calendar.DAY_OF_YEAR, i);
            //convert day to and from formatting to get rid of exact time (and match Date in hashmap)
            try{
                day = df.parse(df.format((c.getTime())));
            } catch(ParseException e ){
                Log.e("parse", e.toString());
            }
            ghgDataPoints[i] = new DataPoint(day, ghgData.get(day));
            landDataPoints[i] = new DataPoint(day, landData.get(day));
            waterDataPoints[i] = new DataPoint(day, waterData.get(day));
        }
        graph.getViewport().setMaxX(day.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        //make series for each category of data, color appropriately
        LineGraphSeries<DataPoint> ghgSeries = new LineGraphSeries<>(ghgDataPoints);
        LineGraphSeries<DataPoint> landSeries = new LineGraphSeries<>(landDataPoints);
        LineGraphSeries<DataPoint> waterSeries = new LineGraphSeries<>(waterDataPoints);

        ghgSeries.setColor(getResources().getColor(R.color.ghg));
        landSeries.setColor(getResources().getColor(R.color.land));
        waterSeries.setColor(getResources().getColor(R.color.water));
        graph.addSeries(ghgSeries);
        graph.addSeries(landSeries);
        graph.addSeries(waterSeries);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }

    public void getkCalTotals(){
        Double totalDiff = 0.0;
        TextView totalDiffEl = findViewById(R.id.text_total_diff_value);
        TextView avgDiffEl = findViewById(R.id.text_avg_diff_value);
        //get layout containing snapshots
        LinearLayout parentLayout = findViewById(R.id.linear_day_snapshots);

        //iterate through children of linear layout
        for(int i = 0 ; i < days; i++){
            View view = parentLayout.getChildAt(i);
            TextView diffEl = view.findViewById(R.id.text_diff_value);
                totalDiff += Math.round(Double.valueOf(diffEl.getText().toString()));


        }

        //get avg of total
        Double avgDiff = totalDiff/days;

        totalDiffEl.setText(totalDiff.toString());
        avgDiffEl.setText(String.valueOf(Math.round(avgDiff)));
    }

    public void getEnvTotals(){
        //initialize variables to store totals
        Double ghgAvg = 0.0;
        Double landAvg = 0.0;
        Double waterAvg = 0.0;
        final TextView avgGhgEl = findViewById(R.id.text_avg_ghg_value);
        final TextView avgLandEl = findViewById(R.id.text_avg_land_value);
        final TextView avgWaterEl = findViewById(R.id.text_avg_water_value);
        final TextView waterLabelEl = findViewById(R.id.text_water_avg_units);
        final TextView ghgLabelEl = findViewById(R.id.text_ghg_avg_units);
        final TextView landLabelEl = findViewById(R.id.text_land_avg_units);

        Spanned ghgText = Html.fromHtml(" g CO<sub><small>2</small></sub>",Html.FROM_HTML_MODE_LEGACY);
        Spanned waterText = Html.fromHtml(" m<sup><small>2</small></sup>/year", Html.FROM_HTML_MODE_LEGACY);
        Spanned landText = Html.fromHtml(" m<sup><small>3</small></sup>", Html.FROM_HTML_MODE_LEGACY);

        waterLabelEl.setText(waterText);
        ghgLabelEl.setText(ghgText);
        landLabelEl.setText(landText);

        //get layout containing snapshots
        LinearLayout parentLayout = findViewById(R.id.linear_day_snapshots);
        for(int i = 0; i < days; i++){
            View view = parentLayout.getChildAt(i);
            TextView totGhgDayEl = view.findViewById(R.id.text_ghg_tot_value);
            TextView totLandDayEl = view.findViewById(R.id.text_land_tot_value);
            TextView totWaterDayEl = view.findViewById(R.id.text_water_tot_value);
            ghgAvg += Double.valueOf(totGhgDayEl.getText().toString());
            landAvg += Double.valueOf(totLandDayEl.getText().toString());
            waterAvg += Double.valueOf(totWaterDayEl.getText().toString());
        }
        avgGhgEl.setText(String.format("%.3f", ghgAvg/days));
        avgLandEl.setText(String.format("%.3f", landAvg/days));
        avgWaterEl.setText(String.format("%.3f", waterAvg/days));
    }


    public void selectYear(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(newStart);
        days = 365;
        c.add(Calendar.DAY_OF_YEAR, -(days-1));

        newEnd = c.getTime();
        Log.i("inspect new end day", df.format(newEnd));
        Log.i("inspect new start day", df.format(newStart));
        getDailySnapShots(newStart, newEnd, modeChosen);
    }

    public void selectMonth(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(newStart);
        days = 31;
        c.add(Calendar.DAY_OF_YEAR, -(days-1));

        newEnd = c.getTime();
        Log.i("inspect new end day", df.format(newEnd));
        Log.i("inspect new start day", df.format(newStart));
        getDailySnapShots(newStart, newEnd, modeChosen);
    }

    public void selectWeek(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(newStart);
        days = 7;
        c.add(Calendar.DAY_OF_YEAR, -(days-1));

        newEnd = c.getTime();
        Log.i("inspect new end day", df.format(newEnd));
        Log.i("inspect new start day", df.format(newStart));
        getDailySnapShots(newStart, newEnd, modeChosen);
    }

    public void switchModes(View view) {
        if(modeChosen.equals("kCal")){
            modeChosen = "env";
        } else {
            modeChosen = "kCal";
        }
        getDailySnapShots(newStart, newEnd, modeChosen);
    }
}
