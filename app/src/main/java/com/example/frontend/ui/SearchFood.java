package com.example.frontend.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.R;

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

public class SearchFood extends AppCompatActivity {

    private static final String TAG = "SearchFood";
    private TextView mTextMessage;
    private TextView mSearchText;
    public Integer currentDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        mTextMessage = (TextView) findViewById(R.id.message);

        mSearchText = findViewById(R.id.input_food_search);

        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    new apiResults().execute();
                    return true;
                }
                return false;
            }
        });

        currentDay = getIntent().getIntExtra("currentDay", 0);

    }


    public void searchUSDA(View view) {
        new apiResults().execute();
    }

    public void viewFoodDetail(View view) {
        String ndbno = view.getTag().toString();
        Intent intent = new Intent(this, FoodDetail.class);
        Log.i("inspect", ndbno);
        intent.putExtra("ndbno", ndbno);
        intent.putExtra("currentDay", currentDay);
        startActivity(intent);
    }

    private class apiResults extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params){
            //combine search string with usda API
            String url = "https://api.nal.usda.gov/ndb/search/?format=json&sort=r&max=25&offset=0&ds=Standard%20Reference&api_key=yWoagqfZMOoxfTOs9h3CI5siSLhvyc4MF791ZE5u" + "&q=" + mSearchText.getText().toString();
            //initialize variables to null to run while loops
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String foodList = null;

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
                foodList = buffer.toString();
                return foodList;
            } catch (IOException e){
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            }

            finally {
                urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s)  {
            JSONObject foodObject;
            JSONArray foodRes = null;
            super.onPostExecute(s);
            try {
                foodObject = (JSONObject) new JSONTokener(s).nextValue();
                foodRes = foodObject.getJSONObject("list").getJSONArray("item");
            } catch (JSONException e){
                Log.i("json", e.toString() );
            }finally {
                //remove focus from editText to get rid of keyboard
                findViewById(R.id.scroll_food_res).requestFocus();
                // Parent layout
                LinearLayout parentLayout = (LinearLayout)findViewById(R.id.linear_food_res);

                //remove previous search results
                parentLayout.removeAllViews();

                //use layoutinflater to add views (indiv food results) to scroll view
                LayoutInflater layoutInflater = getLayoutInflater();
                View view;

                for(int i = 0; i < 25; i++){
                    try{
                        view = layoutInflater.inflate(R.layout.food_result, parentLayout, false );
                        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.food_res_layout);
                        view.findViewById(R.id.button_view_details).setTag(foodRes.getJSONObject(i).getString("ndbno")+foodRes.getJSONObject(i).getString("group").replaceAll(" ", "%20"));
                        TextView foodName = (TextView)view.findViewById(R.id.text_food_name);
                        TextView foodGroup = (TextView)view.findViewById(R.id.text_food_group);
                        foodName.setText(foodRes.getJSONObject(i).getString("name"));
                        foodGroup.setText(foodRes.getJSONObject(i).getString("group"));
                        parentLayout.addView(view);
                    } catch(JSONException e){
                        Log.e("json", e.toString());
                    }

                }

                Log.i("json", foodRes.toString());
            }

        }

    }
}
