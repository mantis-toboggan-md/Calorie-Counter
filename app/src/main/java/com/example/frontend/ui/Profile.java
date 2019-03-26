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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

}
