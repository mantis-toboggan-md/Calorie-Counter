package com.example.frontend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextMessage;
    private TextView mSearchText;
    private TextView usdaSearchRes;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSearchText = findViewById(R.id.input_food_search);
        //usdaSearchRes = (TextView) findViewById(R.id.text_search_res);

    }


    public void searchUSDA(View view) {
        new apiResults().execute();
    }

    private class apiResults extends AsyncTask<Void, Void, String>{

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
                //usdaSearchRes.setText(foodRes.toString());


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
