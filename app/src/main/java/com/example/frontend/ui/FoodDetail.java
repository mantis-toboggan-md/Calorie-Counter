package com.example.frontend.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.AsyncTask;

import com.example.frontend.R;
import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogViewModel;
import com.example.frontend.persistence.UserViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

public class FoodDetail extends AppCompatActivity {
    String ndbno = null;
    String foodGroup = null;
    String foodName = null;
    String foodServing = null;
    String kCal = null;
    String protein = null;
    String carbs = null;
    String fat = null;
    Double ghg = null;
    Double land = null;
    Double water = null;
    String ghgRating = null;
    String landRating = null;
    String waterRating = null;
    Double amtg;
    Integer kCalAdded = null;
    JSONObject foodObjectAll = null;
    Double pAdded;
    Double fatAdded;
    Double carbsAdded;

    private DayLogViewModel mDayLogViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //get USDA food ID from intent
        Intent intent = getIntent();
        ndbno = intent.getStringExtra("ndbno");
        Log.i("inspect", ndbno);

        //query my api to get nutrient info and env info
        new myAPI().execute();


       TextView foodNo = findViewById(R.id.text_dbno);
       foodNo.setText(ndbno);
    }

    private class myAPI extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void ...params){
            String url = "http://10.0.2.2:8082/details/" + ndbno;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String foodDetails = null;

            try {
                //turn url string into URL type
                URL urlHTTP = new URL(url);
                //attempt to open connection with usda api
                urlConnection = (HttpURLConnection) urlHTTP.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //StringBuffer is a modifiable string: use to store response
                StringBuffer buffer = new StringBuffer();

                if(in == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    buffer.append(line + "\n");
                }
                if(buffer.length()==0){
                    return null;
                }
                foodDetails = buffer.toString();
                return foodDetails;
            } catch (IOException e){
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            }

            finally {
                urlConnection.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String s){
          TextView foodNameEl = findViewById(R.id.text_food_name);
          TextView foodServingEl = findViewById(R.id.text_common_measure);
          TextView kCalEl = findViewById(R.id.text_kcal_value);
          TextView proteinEl = findViewById(R.id.text_p_value);
          TextView carbsEl = findViewById(R.id.text_carbs_value);
          TextView fatEl = findViewById(R.id.text_fat_value);
          TextView ghgEl = findViewById(R.id.text_ghg_value);
          TextView landEl = findViewById(R.id.text_land_value);
          TextView waterEl = findViewById(R.id.text_water_value);
          TextView ghgRatingEl = findViewById(R.id.text_ghg_rating);
          TextView landRatingEl = findViewById(R.id.text_land_rating);
          TextView waterRatingEl = findViewById(R.id.text_water_rating);
            super.onPostExecute(s);
            try {
                foodObjectAll = (JSONObject) new JSONTokener(s).nextValue();
                JSONObject foodObject = foodObjectAll.getJSONObject("food");
                JSONObject envObject = foodObjectAll.getJSONObject("envkCal");
                JSONObject envRatingObject = foodObjectAll.getJSONObject("envRating");
                foodName = foodObject.getString("name");
                foodServing = foodObject.getString("measure") + " (" + foodObject.getString("weight") + "g)";
                kCal = foodObject.getJSONArray("nutrients").getJSONObject(0).getString("value");
                protein = foodObject.getJSONArray("nutrients").getJSONObject(1).getString("value");
                carbs = foodObject.getJSONArray("nutrients").getJSONObject(2).getString("value");
                fat = foodObject.getJSONArray("nutrients").getJSONObject(3).getString("value");

                if(envObject.getString("land")!=null){
                    ghg = envObject.getDouble("ghg");
                    land = envObject.getDouble("land");
                    water = envObject.getDouble("water");
                    ghgRating = envRatingObject.getString("ghg");
                    landRating = envRatingObject.getString("land");
                    waterRating = envRatingObject.getString("water");
                }
            } catch (JSONException e){
                Log.i("json", e.toString() );
            }finally {


                foodNameEl.setText(foodName);
                foodServingEl.setText(foodServing);
                kCalEl.setText(kCal);
                proteinEl.setText(protein + "g");
                carbsEl.setText(carbs + "g");
                fatEl.setText(fat + "g");
                if(ghg != null){
                    Spanned ghgText = Html.fromHtml(String.format("%.3f", ghg)+" grams CO<sub>2</sub>",Html.FROM_HTML_MODE_LEGACY);
                    ghgEl.setText(ghgText);
                    ghgRatingEl.setText(ghgRating );
                    if(ghgRating.equals("poor")){
                        ghgRatingEl.setTextAppearance(FoodDetail.this, R.style.badText);
                    } else if(ghgRating.equals("fair")){
                        ghgRatingEl.setTextAppearance(FoodDetail.this, R.style.fairText);
                    } else {
                        ghgRatingEl.setTextAppearance(FoodDetail.this, R.style.goodText);
                    }
                }
                if(land != null){
                    Spanned mText = Html.fromHtml(String.format("%.3f", land)+" m<sup>2</sup> /year",Html.FROM_HTML_MODE_LEGACY);
                    landEl.setText(mText);
                    landRatingEl.setText(landRating);
                    if(landRating.equals("poor")){
                        landRatingEl.setTextAppearance(FoodDetail.this, R.style.badText);
                    } else if(landRating.equals("fair")){
                        landRatingEl.setTextAppearance(FoodDetail.this, R.style.fairText);
                    } else {
                        landRatingEl.setTextAppearance(FoodDetail.this, R.style.goodText);
                    }
                }
                if(water != null){
                    waterEl.setText(String.format("%.3f", water)  + " cubic meters");
                    waterRatingEl.setText(waterRating);
                    if(waterRating.equals("poor")){
                        waterRatingEl.setTextAppearance(FoodDetail.this, R.style.badText);
                    } else if(waterRating.equals("fair")){
                        waterRatingEl.setTextAppearance(FoodDetail.this, R.style.fairText);
                    } else {
                        waterRatingEl.setTextAppearance(FoodDetail.this, R.style.goodText);
                    }
                }

            }
        }
    }

    public void logFood(View view){
        Double caloriePerGram =  null;
        Integer gramsInServing = null;
        //get calories in a gram, grams in a serving
        try {
            gramsInServing = foodObjectAll.getJSONObject("food").getInt("weight");
            caloriePerGram = Double.valueOf(foodObjectAll.getJSONObject("food").getJSONArray("nutrients").getJSONObject(0).getDouble("value")/gramsInServing);
            Log.i("serv grams in serving", gramsInServing.toString());
            Log.i("serv calories per gram", caloriePerGram.toString());
        } catch (JSONException e) {
            Log.e("jsonError", e.toString());
        }
        //get amount entered
        EditText amountInput = findViewById(R.id.edit_amount_log);
        Double amount = Double.valueOf(amountInput.getText().toString());

        //get units selected
        Spinner spinner = findViewById(R.id.spinner_units);
        String units = spinner.getSelectedItem().toString();

        //convert units entered to grams and save in amtg variable; get nutrients for amount logged
        Log.i("units", units);
        switch(units){
            case "Servings": {
                amtg = Double.valueOf(amount.intValue() * gramsInServing);
                pAdded = Double.valueOf(protein) * Double.valueOf(amount);
                carbsAdded = Double.valueOf(carbs) * Double.valueOf(amount);
                fatAdded = Double.valueOf(fat) * Double.valueOf(amount);
                break;
            }
            case "Grams": {
                amtg = amount;
                pAdded = Double.valueOf(protein) * Double.valueOf(amtg);
                carbsAdded = Double.valueOf(carbs) * Double.valueOf(amtg);
                fatAdded = Double.valueOf(fat) * Double.valueOf(amtg);
                break;
            }

            case "Ounces": {
                Double amt = (amount.intValue() * 28.34952);
                amtg = amt;
                pAdded = Double.valueOf(protein) * Double.valueOf(amtg);
                carbsAdded = Double.valueOf(carbs) * Double.valueOf(amtg);
                fatAdded = Double.valueOf(fat) * Double.valueOf(amtg);
                break;
            }
            default: {
                amtg = Double.valueOf(amount.intValue() * gramsInServing);
                break;
            }
        }

        //calculate nutrients of amount to log
        kCalAdded = (int)Math.round(amtg * caloriePerGram);

        //get current day in integer format yyyymmdd
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Integer currentDay = Integer.valueOf(df.format(c.getTime()));

        //get db access
        mDayLogViewModel = ViewModelProviders.of(this).get(DayLogViewModel.class);



        //add food to db
        mDayLogViewModel.insert(new DayLog(currentDay, foodName, String.valueOf(ghg), String.valueOf(land), String.valueOf(water), amtg, kCalAdded, pAdded, carbsAdded, fatAdded));

        //close this screen, return to search
        finish();
    }
}
