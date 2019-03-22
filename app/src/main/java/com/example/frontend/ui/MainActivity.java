package com.example.frontend.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public static final int UPDATE_GOAL_CAL_REQ_CODE = 0;


    private DrawerLayout drawerLayout;
    private UserViewModel mUserViewModel;
    private DayLogViewModel mDayLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        final TextView mGoalCal = findViewById(R.id.text_goal_calories);
        mUserViewModel.getUser().observe(this, new Observer<User>(){
           @Override
           public void onChanged(@Nullable final User user){
               mGoalCal.setText(String.valueOf(user.getGoal()));
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

        getDailyLog();
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
            }
            default: return false;
        }
    }

    public void getDailyLog(){
        //connect to daily log db
        mDayLogViewModel = ViewModelProviders.of(this).get(DayLogViewModel.class);


        mDayLogViewModel.deleteAll();

        //get list of food logs for day, observe changes
        mDayLogViewModel.getDayLogs().observe(this, new Observer<List<DayLog>>() {
            @Override
            public void onChanged(@Nullable List<DayLog> dayLogs) {
                //iterate over list and add food_log_item layout

                //use layoutinflater to add views (indiv food results) to scroll view
                LayoutInflater layoutInflater = getLayoutInflater();
                View view;
                // Parent layout
                LinearLayout parentLayout = (LinearLayout)findViewById(R.id.linear_food_log);

                //remove previous food logs
                parentLayout.removeAllViews();

                for(DayLog log : dayLogs ){
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


                    foodNameEl.setText(log.getFoodName());
                    foodAmtEl.setText(log.getAmtg().toString());
                    foodkCalEl.setText(log.getKCal().toString());
                    foodPEl.setText(log.getP().toString());
                    foodCarbEl.setText(log.getCarbs().toString());
                    foodFatEl.setText(log.getFat().toString());
                    foodGhgEl.setText(log.getGhg());
                    foodWaterEl.setText(log.getWater());
                    foodLandEl.setText(log.getLand());

                    parentLayout.addView(view);
                }
            }
        });
    }
}
