package com.example.googlegeocode;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends Activity {

    TextView info;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.text_view_result);
        input = (EditText) findViewById(R.id.edit_text_coordinates);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    MyTask task;

    public void doSomething(View v){
        String str = input.getText().toString();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = connectivityManager.getActiveNetworkInfo();
        if (ninfo != null && ninfo.isConnected()){
            System.out.print("\n Что-то происходит \n");
        }
        else {
            System.out.print("\n Что-то не то происходит \n");
        }
        task = new MyTask();
        task.execute(StringPreparer.makeString(str));

    }


    class MyTask extends AsyncTask<String, Void, String> {
        public MyTask() {
            super();
        }



        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                InputStream is = connection.getInputStream();
                Scanner scanner = new Scanner(is);
                String str = "";
                str = scanner.nextLine();
                while(scanner.hasNext()) {
                    str = str.concat(scanner.nextLine());

                }
                System.out.print("\n" + str + "\n");
                JSONObject object = new JSONObject(str);
                JSONArray results = object.optJSONArray("results");
                JSONObject result = results.optJSONObject(0);
                str = "Адрес: ";
                str += result.optString("formatted_address");

                JSONObject location = result.optJSONObject("geometry").optJSONObject("location");
                double lat = location.optDouble("lat");
                double lng = location.optDouble("lng");
                str += "\nКоординаты: ";
                str += "\n" + lat + "\n" + lng;
                return str;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "notWorking";
        }



        @Override
        protected void onPostExecute(String str) {
            info.setText(str);

        }


    }

}
