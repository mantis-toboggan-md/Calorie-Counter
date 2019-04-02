package com.example.frontend.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private UserViewModel mUserViewModel;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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





        final TextView mGoalCal = findViewById(R.id.edit_calorie_goal);
        Button button = findViewById(R.id.button_update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                Integer dateSet = Integer.valueOf(df.format(Calendar.getInstance().getTime()));
                long mGoal = Long.parseLong(mGoalCal.getText().toString());
                mUserViewModel.insert(new User(mGoal, dateSet));
                finish();

            }
        });

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mUserViewModel.getUser().observe(this, new Observer<List<User>>(){
            @Override
            public void onChanged(@Nullable final List<User> userList){
                mGoalCal.setText(String.valueOf(userList.get(userList.size()-1).getGoal()));
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch(menuItem.getItemId()){
//            case R.id.nav_search: {
//                Intent intent = new Intent(Profile.this, SearchFood.class);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//                intent.putExtra("currentDay",Integer.valueOf(simpleDateFormat.format(Calendar.getInstance().getTime())));
//                startActivity(intent);
//                return true;
//            }
            case R.id.nav_profile: {
                Intent intent = new Intent(Profile.this, Profile.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_history: {
                Intent intent = new Intent(Profile.this, History.class);
                startActivity(intent);
                return true;
            }
            case R.id.nav_summary: {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default: return false;
        }
    }
}
