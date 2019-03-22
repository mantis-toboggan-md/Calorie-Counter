package com.example.frontend.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserViewModel;

public class Profile extends AppCompatActivity {

    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView mGoalCal = findViewById(R.id.edit_calorie_goal);
        Button button = findViewById(R.id.button_update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserViewModel.update(Long.parseLong(mGoalCal.getText().toString()));
                finish();

            }
        });

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mUserViewModel.getUser().observe(this, new Observer<User>(){
            @Override
            public void onChanged(@Nullable final User user){
                mGoalCal.setText(String.valueOf(user.getGoal()));
            }
        });

    }

}
