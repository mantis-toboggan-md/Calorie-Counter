package com.example.frontend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
        usdaSearchRes = (TextView) findViewById(R.id.text_search_res);
    }


    public void searchUSDA(View view) {
        new apiResults().execute();
    }

    private class apiResults extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params){
            String url = "https://api.nal.usda.gov/ndb/search/?format=json&sort=n&max=25&offset=0&ds=Standard%20Reference&api_key=yWoagqfZMOoxfTOs9h3CI5siSLhvyc4MF791ZE5u" + "&q=" + mSearchText.getText().toString();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String foodList = null;

            try {
                URL urlHTTP = new URL(url);
                urlConnection = (HttpURLConnection) urlHTTP.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            usdaSearchRes.setText(s);
            Log.i("json", s);
        }

    }
}
