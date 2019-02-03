package univaq.weather.Activity;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import univaq.weather.Model.Weather;
import univaq.weather.R;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location location = new Location("");

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        String city = getIntent().getStringExtra("city");

        Log.d("LATITUDINE", String.valueOf(latitude));
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        Context context= getApplicationContext();
        new MainActivity().download5dayData(location, context, new Response.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Object response) {
                List<Weather> dataw = new ArrayList<>();
                DateTimeFormatter formatterinput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter formatteroutput = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                try {

                    LocalDate today = LocalDate.now();
                    String stoday = formatteroutput.format(today);
                    int j=0;

                    JSONObject jsonObject = new JSONObject(String.valueOf(response));

                    JSONArray weath = jsonObject.getJSONArray("list");

                    for (int i = 0; i < weath.length(); i++) {

                        JSONObject item = weath.getJSONObject(i);

                        String datee = item.getString("dt_txt");
                        LocalDate tempdate=LocalDate.parse(datee,formatterinput);
                        String fdatee=formatteroutput.format(tempdate);

                        //controllo se ho preso i dati di 5 giorni se si esco dal ciclo
                        if(j==5){
                            break;
                        }

                        if(!stoday.equals(fdatee)){

                            //weatherDay
                            JSONArray weatday= item.getJSONArray("weather");
                            JSONObject temporaney=weatday.getJSONObject(0);
                            String wd = temporaney.getString("main");

                            LocalDate date= LocalDate.parse(fdatee);

                            dataw.add(new Weather("", 0, 0,"",0, wd,date,0,0));

                            Log.d("ARRAY:", String.valueOf(dataw.get(0).getDate()));
                            stoday=fdatee;
                            j++;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SIZE", String.valueOf(dataw.size()));
                setData(dataw);
            }
        });

        if(city == null) return;

        mMap.setMapType(MAP_TYPE_HYBRID);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScrollGesturesEnabled(false);

        LatLng position = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(position).title(String.format("%s", city)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(List<Weather>dataw)
    {
        double temperature = getIntent().getDoubleExtra("temperature", 0);
        String city = getIntent().getStringExtra("city");
        String weather = getIntent().getStringExtra("weath");

        TextView textCity = findViewById(R.id.city);
        TextView textTemperature = findViewById(R.id.temp);
        TextView textWeather = findViewById(R.id.weath);
        TextView date1 = findViewById(R.id.date1);
        TextView date2 = findViewById(R.id.date2);
        TextView date3 = findViewById(R.id.date3);
        TextView date4 = findViewById(R.id.date4);
        TextView date5 = findViewById(R.id.date5);
        TextView wdate1 = findViewById(R.id.wdate1);
        TextView wdate2 = findViewById(R.id.wdate2);
        TextView wdate3 = findViewById(R.id.wdate3);
        TextView wdate4 = findViewById(R.id.wdate4);
        TextView wdate5 = findViewById(R.id.wdate5);
        ImageView imgdate1 = findViewById(R.id.imgdate1);
        ImageView imgdate2 = findViewById(R.id.imgdate2);
        ImageView imgdate3 = findViewById(R.id.imgdate3);
        ImageView imgdate4 = findViewById(R.id.imgdate4);
        ImageView imgdate5 = findViewById(R.id.imgdate5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        textCity.setText(city);
        textTemperature.setText(Double.toString(temperature));
        textWeather.setText(weather);
        date1.setText(dataw.get(0).getDate().format(formatter));
        wdate1.setText(dataw.get(0).getNowweather());

        if(dataw.get(0).getNowweather().equals("Rain")) {
            imgdate1.setImageResource(R.drawable.rain_icon);
        }else if(dataw.get(0).getNowweather().equals("Snow")) {
            imgdate1.setImageResource(R.drawable.snow_icon);
        }else if(dataw.get(0).getNowweather().equals("Clouds")){
            imgdate1.setImageResource(R.drawable.cloud_icon);
        }else{
            imgdate1.setImageResource(R.drawable.sun_icon);
        }

        date2.setText(dataw.get(1).getDate().format(formatter));
        wdate2.setText(dataw.get(1).getNowweather());

        if(dataw.get(1).getNowweather().equals("Rain")) {
            imgdate2.setImageResource(R.drawable.rain_icon);
        }else if(dataw.get(1).getNowweather().equals("Snow")) {
            imgdate2.setImageResource(R.drawable.snow_icon);
        }else if(dataw.get(1).getNowweather().equals("Clouds")){
            imgdate2.setImageResource(R.drawable.cloud_icon);
        }else{
            imgdate2.setImageResource(R.drawable.sun_icon);
        }

        date3.setText(dataw.get(2).getDate().format(formatter));
        wdate3.setText(dataw.get(2).getNowweather());

        if(dataw.get(2).getNowweather().equals("Rain")) {
            imgdate3.setImageResource(R.drawable.rain_icon);
        }else if(dataw.get(2).getNowweather().equals("Snow")) {
            imgdate3.setImageResource(R.drawable.snow_icon);
        }else if(dataw.get(2).getNowweather().equals("Clouds")){
            imgdate3.setImageResource(R.drawable.cloud_icon);
        }else{
            imgdate3.setImageResource(R.drawable.sun_icon);
        }

        date4.setText(dataw.get(3).getDate().format(formatter));
        wdate4.setText(dataw.get(3).getNowweather());

        if(dataw.get(3).getNowweather().equals("Rain")) {
            imgdate4.setImageResource(R.drawable.rain_icon);
        }else if(dataw.get(3).getNowweather().equals("Snow")) {
            imgdate4.setImageResource(R.drawable.snow_icon);
        }else if(dataw.get(3).getNowweather().equals("Clouds")){
            imgdate4.setImageResource(R.drawable.cloud_icon);
        }else{
            imgdate4.setImageResource(R.drawable.sun_icon);
        }

        date5.setText(dataw.get(4).getDate().format(formatter));
        wdate5.setText(dataw.get(4).getNowweather());

        if(dataw.get(4).getNowweather().equals("Rain")) {
            imgdate5.setImageResource(R.drawable.rain_icon);
        }else if(dataw.get(4).getNowweather().equals("Snow")) {
            imgdate5.setImageResource(R.drawable.snow_icon);
        }else if(dataw.get(4).getNowweather().equals("Clouds")){
            imgdate5.setImageResource(R.drawable.cloud_icon);
        }else{
            imgdate5.setImageResource(R.drawable.sun_icon);
        }
    }
}
