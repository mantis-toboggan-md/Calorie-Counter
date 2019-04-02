package com.example.frontend.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.R;

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
import java.util.Iterator;

public class AddCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        new envCategories().execute();
    }

    private class envCategories extends AsyncTask<Void, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {

            //initialize variables to null to run while loops
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String categoriesString = null;
            JSONObject categories = null;

            String url = "https://capback.herokuapp.com/categories";

            try{
                URL urlHTTP = new URL(url);
                urlConnection =(HttpURLConnection)urlHTTP.openConnection();
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
                    categoriesString = buffer.toString();

            } catch(IOException e){
                Log.e("http", e.toString());
            } finally{
                urlConnection.disconnect();
            }

            try{
                categories = (JSONObject) new JSONTokener(categoriesString).nextValue();
            } catch(JSONException e){
                Log.e("json", e.toString());
            }
            return categories;
        }

        @Override
        protected void onPostExecute(JSONObject categories){
            Log.i("inspect categories", categories.toString());
            LayoutInflater layoutInflater = getLayoutInflater();
            LinearLayout parentLayout = findViewById(R.id.layout_categories);

            //add "none" category to list first
            View view = layoutInflater.inflate(R.layout.env_category, parentLayout, false);
            TextView catName = view.findViewById(R.id.text_cat_name);
            TextView catGHG = view.findViewById(R.id.text_cat_ghg_value);
            TextView catLand = view.findViewById(R.id.text_cat_land_value);
            TextView catWater = view.findViewById(R.id.text_cat_water_value);
            catName.setText("None/Not applicable");
            catGHG.setText("0");
            catLand.setText("0");
            catWater.setText("0");
//            final JSONObject none = new JSONObject();
//            try{
//                none.put("ghg", 0);
//                none.put("land", 0);
//                none.put("water", 0);
//            } catch(JSONException e){}

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("category", new String("none"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            parentLayout.addView(view);

            //iterate through categories returned by API call, add to view
            Iterator<String> keys = categories.keys();
            while(keys.hasNext()){
                view = null;
                String key = keys.next();
                final String category = key;
                try{
                    if(categories.get(key) instanceof JSONObject){
                        JSONObject eachCategory = categories.getJSONObject(key);
                        view = layoutInflater.inflate(R.layout.env_category, parentLayout, false);
                        catName = view.findViewById(R.id.text_cat_name);
                        catGHG = view.findViewById(R.id.text_cat_ghg_value);
                        catLand = view.findViewById(R.id.text_cat_land_value);
                        catWater = view.findViewById(R.id.text_cat_water_value);
                        catName.setText(key);
                        catGHG.setText(eachCategory.getString("ghg"));
                        catLand.setText(eachCategory.getString("land"));
                        catWater.setText(eachCategory.getString("water"));

                    }
                }catch(JSONException e ){
                    Log.e("json", e.toString());
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("category", category);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                parentLayout.addView(view);
            }

        }
    }
}
