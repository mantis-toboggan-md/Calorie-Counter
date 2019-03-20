package com.example.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodDetail extends AppCompatActivity {
    String ndbno = null;
    String foodGroup = null;


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
          String foodName = null;
          String foodServing = null;
          String kCal = null;
          String protein = null;
          String carbs = null;
          String fat = null;

            JSONObject foodObject = null;
            super.onPostExecute(s);
            try {
                foodObject = (JSONObject) new JSONTokener(s).nextValue();
                foodName = foodObject.getString("name");
                foodServing = foodObject.getString("measure") + " (" + foodObject.getString("weight") + "g)";
                kCal = foodObject.getJSONArray("nutrients").getJSONObject(0).getString("value");
                protein = foodObject.getJSONArray("nutrients").getJSONObject(1).getString("value") + "g";
                carbs = foodObject.getJSONArray("nutrients").getJSONObject(2).getString("value") + "g";
                fat = foodObject.getJSONArray("nutrients").getJSONObject(3).getString("value") + "g";

            } catch (JSONException e){
                Log.i("json", e.toString() );
            }finally {
                foodNameEl.setText(foodName);
                foodServingEl.setText(foodServing);
                kCalEl.setText(kCal);
                proteinEl.setText(protein);
                carbsEl.setText(carbs);
                fatEl.setText(fat);
            }
        }
    }
}
