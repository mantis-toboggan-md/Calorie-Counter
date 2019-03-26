package com.example.frontend.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.R;
import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditDay extends AppCompatActivity {

    DayLogViewModel mDayLogViewModel;
    Integer currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_day);
        //get layout inside scrollview to store food log items
        LinearLayout thisParentLayout = findViewById(R.id.linear_food_log);
        //remove previous food logs
        thisParentLayout.removeAllViews();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Integer today = Integer.valueOf(df.format(Calendar.getInstance().getTime()));
        currentDay = getIntent().getIntExtra("currentDay", today );

        getDailyLog(currentDay, thisParentLayout);
    }

    public void getDailyLog(Integer intDate, final LinearLayout parentLayout){
        TextView dayTitle = findViewById(R.id.text_today_date);
        dayTitle.setText(intDate.toString());

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


    public void goToSearch(View view) {
        Log.i("where", "go to search button pressed");
        Intent intent = new Intent(this, SearchFood.class);
        Log.i("where intent in main", currentDay.toString());
        intent.putExtra("currentDay", currentDay);
        startActivity(intent);
    }
}
