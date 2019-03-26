package com.example.frontend.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogViewModel;
import com.example.frontend.persistence.User;
import com.example.frontend.persistence.UserDao;
import com.example.frontend.persistence.UserViewModel;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History extends AppCompatActivity {

    private DayLogViewModel mDayLogViewModel;
    Date weekAgo;
    Date newStart;
    Date newEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, -7);
        weekAgo = c.getTime();
        newEnd = c.getTime();

        getDailySnapShots(today, weekAgo);
    }

    public void getDailySnapShots(Date start, Date end) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
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
        while (currentDay >= endDay) {
            final Integer dayInLoop = currentDay;
            //add day's snapshot to parent view
            final View view = layoutInflater.inflate(R.layout.day_snapshot, parentLayout, false);
            TextView dateEl = view.findViewById(R.id.text_date);
            dateEl.setText(currentDay.toString());

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
                    TextView intakeEl = view.findViewById(R.id.text_intake_value);
                    TextView diffEl = view.findViewById(R.id.text_diff_value);
                    double difference = goal - Double.valueOf(intakeEl.getText().toString());
                    diffEl.setText(String.valueOf(difference));

                }
            }

            mDayLogViewModel.getOneDayLogs(currentDay).observe(this, new Observer<List<DayLog>>() {
                @Override
                public void onChanged(@Nullable List<DayLog> dayLogs) {

                    Double totalkCal = 0.0;
                    TextView totalkCalEl = view.findViewById(R.id.text_intake_value);


                    //add event listener to button
                    Button editButton = view.findViewById(R.id.button_edit_day);
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
                        Log.i("inspect each food log", log.getFoodName());
                        //add foods' nutrients to totals
                        totalkCal += log.getKCal();

                    }

                    //set text in snapshot
                    totalkCalEl.setText(totalkCal.toString());
                    Log.i("inspect setting of intake", totalkCal.toString());
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

    }


    public void goBack(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(newEnd);
        c.add(Calendar.DAY_OF_YEAR, -1);
        newStart = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, -7);
        newEnd = c.getTime();
        getDailySnapShots(newStart, newEnd);
    }

    public void goForward(View view) {
        Calendar c = Calendar.getInstance();
        if(newStart == null){
            newStart = c.getTime();
        }
        c.setTime(newStart);
        c.add(Calendar.DAY_OF_YEAR, 1);
        newEnd = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 7);
        newStart = c.getTime();
        getDailySnapShots(newStart, newEnd);

    }
}
