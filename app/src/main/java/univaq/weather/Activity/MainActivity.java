package univaq.weather.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import univaq.weather.Database.RDatabase;
import univaq.weather.Database.WeatherDB;
import univaq.weather.Model.Weather;
import univaq.weather.R;
import univaq.weather.Utility.RequestService;
import univaq.weather.Utility.VolleyRequest;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterRecycler adapter;
    private List<Weather> dataw = new ArrayList<>();
    private Location lastLocation;
    private MyListener listener = new MyListener();
    private SwipeRefreshLayout swipeRefreshLayout;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    Integer i=0;
    final WeatherDB w= new WeatherDB();
    List<WeatherDB> wb= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkConnection()) {

            if(startGPS()){
                downloadData(lastLocation);
            }

        } else {
            Toast.makeText(MainActivity.this, R.string.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }

        adapter = new AdapterRecycler(dataw);
        RecyclerView list = findViewById(R.id.main_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.main_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        ImageView img = findViewById(R.id.imageView6);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "DEVELOP BY ME", Toast.LENGTH_SHORT).show();
            }
        });

        //carico i dati presenti nel db in wb
        new Thread(new Runnable() {
            @Override
            public void run() {
                wb = RDatabase.getInstance(getApplicationContext()).weatherDAO().getWeather();
            }
        }).start();

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        if(startGPS()){
            downloadData(lastLocation);
        }
    }

    @Override
    public void onRefresh() {

        if (checkConnection()) {

            dataw.clear();

            if(startGPS()){
                downloadData(lastLocation);
            }

        } else {
            Toast.makeText(MainActivity.this, R.string.NO_INTERNET, Toast.LENGTH_LONG).show();
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("ALARMTEMP", String.valueOf(i));

        SaveCityDB(dataw);

        if(i == 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 10);

            Intent intent = new Intent(this, RequestService.class);
            intent.putExtra("latitude", lastLocation.getLatitude());
            intent.putExtra("longitude", lastLocation.getLongitude());

            PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            //for 30 mint 60*60*1000
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    60 * 60 * 1000, pintent);
            startService(new Intent(getBaseContext(), RequestService.class));
            i++;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGPS();
            } else {
                downloadData(null);
            }
        }
    }

    /**
     * Check Internet connection
     */
    private boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    /**
     * Alert Message Gps
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.GRANT_GPS)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.NO_THX, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        downloadData(null);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Start Location Service by GPS and Network provider.
     */

    private boolean startGPS() {

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        int check = ContextCompat
                .checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (check == PackageManager.PERMISSION_GRANTED) {

            // getting GPS status
            boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {

                buildAlertMessageNoGps();

            } else if (manager != null) {

                manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
                manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);

                return true;
            }

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        return false;

    }

    /**
     * Stop Location service.
     */
    private void stopGPS() {

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager != null) manager.removeUpdates(listener);

    }

    /**
     * Download Data by Volley
     */
    private void downloadData(Location location) {

        Toast.makeText(MainActivity.this, R.string.DOWNLOAD, Toast.LENGTH_SHORT).show();

        this.lastLocation = location;

        VolleyRequest.getInstance(getApplicationContext())
                .downloadWheaterData(new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {
                            dataw.clear();

                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray weath = jsonObject.getJSONArray("list");


                            for (int i = 0; i < weath.length(); i++) {

                                JSONObject item = weath.getJSONObject(i);

                                //city
                                String city = item.getString("name");

                                //ID
                                String id = item.getString("id");

                                //temperature
                                JSONObject temp = item.getJSONObject("main");
                                double TP = temp.getDouble("temp") - 273.15;
                                String tempt = df2.format(TP);
                                try {
                                    TP= df2.parse(tempt).doubleValue();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //weatherNow
                                JSONArray weathern= item.getJSONArray("weather");
                                JSONObject temporaney=weathern.getJSONObject(0);
                                String nw = temporaney.getString("main");

                                //latitude
                                JSONObject coord = item.getJSONObject("coord");
                                double lat = coord.getDouble("lat");

                                //longitude
                                double lon = coord.getDouble("lon");

                                //wind
                                JSONObject windtp = item.getJSONObject("wind");
                                double wind = windtp.getDouble("speed");

                                //humidity
                                Integer pres = temp.getInt("pressure");

                                dataw.add(new Weather(id, lat, lon,city,TP, nw,null,wind,pres));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Refresh list because the adapter data are changed
                        if (adapter != null) adapter.notifyDataSetChanged();
                    }
                }, location);

    }

    public void download5dayData(Location location,Context context, Response.Listener listener) {

        this.lastLocation = location;

        VolleyRequest.getInstance(context)
                .downloadWheater5DaysData(listener, location);
    }

    /**
     * Location Listener
     */
    public class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

    public void SaveCityDB(List<Weather> dataw){
        //controllo se la città è gia presente nel db, se non è presente l'aggiungo al db

        //carico i dati presenti nel db in wb
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(Weather wa:dataw) {
                    List<WeatherDB> result = RDatabase.getInstance(getApplicationContext()).weatherDAO().check(wa.getId());

                    if (result.isEmpty()) {
                        w.setId(wa.getId());
                        w.setLatitudine(wa.getLat());
                        w.setLongitudine(wa.getLon());
                        w.setCity(wa.getCity());
                        w.setTemp(wa.getTemperature());
                        w.setWeath(wa.getNowweather());
                        w.setWind(wa.getWind());
                        w.setPres(wa.getPressure());

                        RDatabase.getInstance(getApplicationContext()).weatherDAO().save(w);
                    }
                }
            }

        }).start();

    }

}
