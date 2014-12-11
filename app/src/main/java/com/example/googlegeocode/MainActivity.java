package com.example.googlegeocode;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends Activity {

    TextView info;
    EditText input;
    private GoogleMapFragment googleMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
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

    private void initMap() {
        FragmentManager fm = getFragmentManager();
        googleMapFragment = (GoogleMapFragment) fm.findFragmentByTag(GoogleMapFragment.TAG);
        if (googleMapFragment == null) {
            googleMapFragment = new GoogleMapFragment();
            fm.beginTransaction().replace(R.id.map, googleMapFragment, GoogleMapFragment.TAG).commit();
        }
    }

    MyTask task;

    public void doSomething(View v){
        String str = input.getText().toString();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = connectivityManager.getActiveNetworkInfo();
        if (ninfo == null || !ninfo.isConnected()) {
            System.out.print("\n Нет соединения \n");
        }
        task = new MyTask();
        task.execute(StringPreparer.makeString(str));

    }


    class MyTask extends AsyncTask<String, Void, CoordinatesObject> {
        public MyTask() {
            super();
        }



        @Override
        protected CoordinatesObject doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                Scanner scanner = new Scanner(is);
                String str = scanner.nextLine();
                while(scanner.hasNext()) {
                    str = str.concat(scanner.nextLine());
                }
                //JSONObject object = new JSONObject(str);
                //JSONArray results = new JSONObject(str).optJSONArray("results");
                JSONObject result = new JSONObject(str).optJSONArray("results").optJSONObject(0);
                String address = result.optString("formatted_address");
                JSONObject location = result.optJSONObject("geometry").optJSONObject("location");
                double lat = location.optDouble("lat");
                double lng = location.optDouble("lng");
                return new CoordinatesObject(address, lat, lng);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new CoordinatesObject("not working", null, null);
        }



        @Override
        protected void onPostExecute(CoordinatesObject str) {
            info.setText(str.toString());

        }


    }

}
