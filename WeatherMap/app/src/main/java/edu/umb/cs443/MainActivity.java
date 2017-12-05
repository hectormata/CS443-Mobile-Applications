package edu.umb.cs443;

import android.location.Address;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.content.Context;
import android.widget.EditText;
import android.support.v4.app.FragmentActivity;
import android.location.Geocoder;
import android.widget.ImageView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
/* import com.google.android.gms.maps.model.MarkerOptions; */

import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.net.URL;


/*
 * Hector Mata
 * Homework 3
 * 11/15/2017
 *
 *
 * For this Homework number 3 our goal was to practice using Google Map API and RESTful calls in Android
 *
 *  Requirements:
 *      Edit one activity (MAIN)
 *      Display Temperature and to obtain it we will use openweathermap.org
 *      Use API key (I Used professor's key
 *      AsyncTask enables proper and easy use of the UI thread. This class allows you to perform background operations
 *      and publish results on the UI thread without having to manipulate threads and/or handlers. (Android documentation)
 *
 * Limitations:
 *
 *      Code only works with city's name
 *      Does not work with zip codes
 *
 */

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // Unused
    // public final static String DEBUG_TAG = "edu.umb.cs443.MYMSG";
    // MarkerOptions markerOptions;
    GoogleMap googleMap;
    LatLng latLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new Button object
        Button button_find = (Button) findViewById(R.id.button);

        // Getting a reference to the map
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);


        // Button click event listener for the find button
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reference to EditText
                EditText etLocation = (EditText) findViewById(R.id.editText);

                // User input location
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    new GeocoderTask().execute(location);

                    new Fetch().execute(location);
                }
            }
        };

        // button click listener for the find button
        button_find.setOnClickListener(findClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Add item to action bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         *
         */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *  Example from developer android for AsyncTask subclassing
     *
     *   private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
     *       protected Long doInBackground(URL... urls) {
     *       int count = urls.length;
     *       long totalSize = 0;
     *       for (int i = 0; i < count; i++) {
     *           totalSize += Downloader.downloadFile(urls[i]);
     *           publishProgress((int) ((i / (float) count) * 100));
     *           // Escape early if cancel() is called
     *           if (isCancelled()) break;
     *       }
     *       return totalSize;
     *   }
     *
     *   protected void onProgressUpdate(Integer... progress) {
     *       setProgressPercent(progress[0]);
     *   }
     *
     *   protected void onPostExecute(Long result) {
     *       showDialog("Downloaded " + result + " bytes");
     *   }
     * }
     */

    // An AsyncTask class for accessing OPEN WEATHER
    private class Fetch extends AsyncTask<String, Void, String> {

        TextView textView = (TextView) findViewById(R.id.textView);
        String city;
        Bitmap map;

        /* private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric"; */

        @Override
        protected String doInBackground(String... input) {

            try {

                JSONObject json2 = RemoteFetch.getJSON(getBaseContext(), input[0]);
                JSONObject weather = new JSONObject(json2.getString("weather").replace("[",""));
                map = BitmapFactory.decodeStream((InputStream) new URL("http://openweathermap.org/img/w/"+ weather.getString("icon")+".png").getContent());

                return json2.getJSONObject("main").getDouble("temp") + "";

            } catch (Exception e) {
                Log.e("Simple Weather", "One or more fields not found in the JSON data");
            }
            return null;
        }


        @Override
        protected void onPostExecute(String temp) {

            ImageView i = (ImageView)findViewById(R.id.imageView);
            i.setImageBitmap(map);
            textView.setText(temp);

        }

        @Override
        protected void onPreExecute() {
            city = textView.getText().toString();
        }
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            // Creating an instance of Geocoder class
            Geocoder geocode = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocode.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPreExecute(){

            float zoom = (float) Math.random() * 10 + 5;
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clear the map
            googleMap.clear();

            // Adding Markers on Google Map
            for (int i = 0; i < addresses.size(); i++) {

                Address address = addresses.get(i);

                // Create an instance of GeoPoint to display in Google Map
                latLong = new LatLng(address.getLatitude(), address.getLongitude());

                // String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());
                if (i == 0) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLong));
                }


            }
        }
    }

    public void getWeatherInfo(View v) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
    }
}

class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial";

    public static JSONObject getJSON(Context context, String city) {

        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200)
                return null;

            return data;

        } catch (Exception e) {

            return null;
        }
    }
}