package univaq.weather.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import univaq.weather.Database.RDatabase;
import univaq.weather.Database.WeatherDB;
import univaq.weather.R;

public class FavouriteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterFavourite adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<WeatherDB> wb = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        adapter = new AdapterFavourite(wb,getApplicationContext());
        RecyclerView list = findViewById(R.id.fav_list);
        list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        list.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.fav_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                wb = RDatabase.getInstance(getApplicationContext()).weatherDAO().getFavourite();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDataw(wb);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onRefresh() {
        finish();
        startActivity(getIntent());
    }
}
