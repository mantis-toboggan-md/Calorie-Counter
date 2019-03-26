package com.example.frontend.ui;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import java.util.*;
import java.text.*;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogViewModel;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserViewModel;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private TextView todaysDate;
    private DrawerLayout drawerLayout;
    private UserViewModel mUserViewModel;
    private DayLogViewModel mDayLogViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todaysDate = findViewById(R.id.text_today_date);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat ft =
                new SimpleDateFormat ("EEEE, MMMM dd");
        todaysDate.setText(ft.format(c.getTime()));
        //connect to user DB to get calorie goal
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        final TextView mGoalCal = findViewById(R.id.text_goal_calories);
//        long mGoal = 1600;
//        mUserViewModel.insert(new User(mGoal, new Integer(20190325)));
       // mUserViewModel.deleteAll();
        mUserViewModel.getUser().observe(this, new Observer<List<User>>(){
           @Override
           public void onChanged(@Nullable final List<User> userList){
               if(userList.size() == 0){
                   Log.i("inspect", "list of users is empty");
                   long mGoal = 1600;
                   mUserViewModel.insert(new User(mGoal, new Integer(20190325)));
               } else {
                   mGoalCal.setText(String.valueOf(userList.get(userList.size()-1).getGoal()));
               }

           }
        });


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);


        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(toggle);
        }
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_side);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        //get today's date in integer format
        SimpleDateFormat intFormat = new SimpleDateFormat("yyyyMMdd");
        Integer currentDay = Integer.valueOf(intFormat.format(Calendar.getInstance().getTime()));
        //get daily log for today's date
        getDailyLog(currentDay);

    }


    public void goToSearch(View view) {
        Intent intent = new Intent(this, SearchFood.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch(menuItem.getItemId()){
            case R.id.nav_search: {
                Intent intent = new Intent(MainActivity.this, SearchFood.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_profile: {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_history: {
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
                return true;
            }
            default: return false;
        }
    }

    public void getDailyLog(Integer intDate){
        //connect to daily log db
        mDayLogViewModel = ViewModelProviders.of(this).get(DayLogViewModel.class);


       // mDayLogViewModel.deleteAll();



        //get list of food logs for day, observe changes
        mDayLogViewModel.getOneDayLogs(intDate).observe(this, new Observer<List<DayLog>>() {
            @Override
            public void onChanged(@Nullable List<DayLog> dayLogs) {
                //initialize variables to total nutrients
                Double totalkCal = 0.0;
                Double totalP = 0.0;
                Double totalCarbs = 0.0;
                Double totalFat = 0.0;

                //use layoutinflater to add views (indiv food results) to scroll view
                LayoutInflater layoutInflater = getLayoutInflater();
                View view;
                // Parent layout
                LinearLayout parentLayout = (LinearLayout)findViewById(R.id.linear_food_log);

                //remove previous food logs
                parentLayout.removeAllViews();

                //iterate over list and add food_log_item layout
                for(final DayLog log : dayLogs ){
                    //add a food log view to the linear layout inside scroll view
                    view = layoutInflater.inflate(R.layout.food_log_item, parentLayout, false );
                    LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linear_food_log);
                    //add log's info to view
                    TextView foodNameEl = view.findViewById(R.id.text_food_name);
                    TextView foodAmtEl = view.findViewById(R.id.text_food_amt);
                    TextView foodkCalEl = view.findViewById(R.id.text_food_kCal);
                    TextView foodPEl = view.findViewById(R.id.text_food_p);
                    TextView foodCarbEl = view.findViewById(R.id.text_food_carbs);
                    TextView foodFatEl = view.findViewById(R.id.text_food_fat);
                    TextView foodGhgEl = view.findViewById(R.id.text_food_ghg);
                    TextView foodLandEl = view.findViewById(R.id.text_food_land);
                    TextView foodWaterEl = view.findViewById(R.id.text_food_water);

                    TextView totalkCalEl = findViewById(R.id.text_total_kcal);
                    TextView totalPEl = findViewById(R.id.text_total_p);
                    TextView totalCarbsEl = findViewById(R.id.text_total_carbs);
                    TextView totalFatEl = findViewById(R.id.text_total_fat);


                    foodNameEl.setText(log.getFoodName());
                    foodAmtEl.setText(String.valueOf(Math.round(log.getAmtg()))+"g");
                    foodkCalEl.setText(String.valueOf(Math.round(log.getKCal()))+" cal");
                    foodPEl.setText(String.valueOf(Math.round(log.getP()))+"g");
                    foodCarbEl.setText(String.valueOf(Math.round(log.getCarbs()))+"g");
                    foodFatEl.setText(String.valueOf(Math.round(log.getFat()))+"g");
                    foodGhgEl.setText(log.getGhg());
                    foodWaterEl.setText(log.getWater());
                    foodLandEl.setText(log.getLand());

                    //add foods' nutrients to totals
                    totalkCal += log.getKCal();
                    totalP += log.getP();
                    totalCarbs += log.getCarbs();
                    totalFat += log.getFat();

                    //display totals
                    totalkCalEl.setText(String.valueOf(Math.round(totalkCal) +" cal"));
                    totalPEl.setText(String.valueOf(Math.round(totalP) +"g p"));
                    totalCarbsEl.setText(String.valueOf(Math.round(totalCarbs) +"g carbs"));
                    totalFatEl.setText(String.valueOf(Math.round(totalFat) +"g fat"));

                    //add event listener to fab
                    FloatingActionButton fab = view.findViewById(R.id.fab_food_remove);
                    fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDayLogViewModel.deleteOne(log.getId());
                        }
                    });

                    parentLayout.addView(view);
                }
            }
        });
    }
}
