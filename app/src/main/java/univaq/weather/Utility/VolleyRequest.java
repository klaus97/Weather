package univaq.weather.Utility;

import android.content.Context;
import android.location.Location;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyRequest {

    private RequestQueue queue;
    private static VolleyRequest instance = null;

    public static VolleyRequest getInstance(Context context){
        return instance==null ? instance=new VolleyRequest(context) : instance;
    }

    private VolleyRequest(Context context){
        queue = Volley.newRequestQueue(context);
    }

    /* Scarico i dati dall'url */

    public void downloadWheaterData(Response.Listener<String> listner, Location location){

        String url="http://api.openweathermap.org/data/2.5/find";

        if(location!=null){
            url=url+"?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&cnt=50"+"&appid=3945afe904f7e54c4bef128e8c437b32";
        }

        StringRequest request = new StringRequest(StringRequest.Method.GET,url,listner,null);
        queue.add(request);
    }

    public void downloadWheater5DaysData(Response.Listener<String> listner, Location location){


        String url="http://api.openweathermap.org/data/2.5/forecast";

        if(location!=null){
            url=url+"?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=3945afe904f7e54c4bef128e8c437b32";
        }

        StringRequest request = new StringRequest(StringRequest.Method.GET,url,listner,null);
        queue.add(request);
    }
}
