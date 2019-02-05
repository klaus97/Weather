package univaq.weather.Utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import univaq.weather.Activity.MainActivity;
import univaq.weather.Activity.MapsActivity;
import univaq.weather.Database.RDatabase;
import univaq.weather.Database.WeatherDB;
import univaq.weather.R;

public class RequestService extends Service {

    private final int notification_id = 1;
    public static Location location= new Location("current");
    private static DecimalFormat df2 = new DecimalFormat(".##");
    List<WeatherDB> w= new ArrayList<>();
    List<WeatherDB> wb=new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notifyMessage("Update Data run on background");

        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);
        Log.d("LATITUDINE", String.valueOf(latitude));
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        new Thread(new Runnable() {
            @Override
            public void run() {
                wb=RDatabase.getInstance(getApplicationContext()).weatherDAO().getCity();
                DownloadData(location);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyMessage(String mex) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myChannel", "Il Mio Canale", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(Color.argb(255, 255, 0, 0));
            if(notificationManager != null) notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), "myChannel");
        builder.setContentTitle(getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.weatherlogo);
        builder.setContentText(mex);
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, 0);

        builder.setContentIntent(pendingIntent);

        Notification notify = builder.build();
        if(notificationManager != null) notificationManager.notify(notification_id, notify);
    }

    private void DownloadData(Location location){

        Log.d("LOCATION", String.valueOf(location.getLatitude()));

        VolleyRequest.getInstance(getApplicationContext())
                .downloadWheaterData(new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {

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
                                    TP = df2.parse(tempt).doubleValue();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //latitude
                                JSONObject coord = item.getJSONObject("coord");
                                double lat = coord.getDouble("lat");

                                //longitude
                                double lon = coord.getDouble("lon");

                                //weatherNow
                                JSONArray weathern = item.getJSONArray("weather");
                                JSONObject temporaney = weathern.getJSONObject(0);
                                String nw = temporaney.getString("main");

                                //wind
                                JSONObject windtp = item.getJSONObject("wind");
                                double wind = windtp.getDouble("speed");

                                //humidity
                                Integer pres = temp.getInt("pressure");

                                final WeatherDB w= new WeatherDB();

                                 w.setId(id);
                                 w.setLatitudine(lat);
                                 w.setLongitudine(lon);
                                 w.setCity(city);
                                 w.setTemp(TP);
                                 w.setWeath(nw);
                                 w.setWind(wind);
                                 w.setPres(pres);
                                 w.setPref(wb.get(i).getPref());

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RDatabase.getInstance(getApplicationContext()).weatherDAO().update(w);
                                    }
                                }).start();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, location);
    }
}

