package com.example.frontend.ui;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserViewModel;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private UserViewModel mUserViewModel;

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
            default: return false;
        }
    }
}
