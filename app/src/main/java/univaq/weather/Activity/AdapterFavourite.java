package univaq.weather.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import univaq.weather.Database.RDatabase;
import univaq.weather.Database.WeatherDB;
import univaq.weather.R;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.ViewHolder> {

    private List<WeatherDB> dataw;
    Context context;

    public AdapterFavourite(List<WeatherDB> dataw, Context context){
        this.dataw = dataw;
        this.context=context;
    }

    public void setDataw(List<WeatherDB> dataw) {
        this.dataw = dataw;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_favourite, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WeatherDB weather = dataw.get(i);

            Log.d("SIZE", String.valueOf(dataw.size()));

            viewHolder.city.setText(weather.getCity());
            viewHolder.lat.setText(Double.toString(weather.getLatitudine()));
            viewHolder.lon.setText(Double.toString(weather.getLongitudine()));

    }

    @Override
    public int getItemCount() {
        return dataw.size();
    }

    // Use ViewHolder Pattern
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView city;
        TextView lon;
        TextView lat;
        Button but;

        ViewHolder(@NonNull View view) {
            super(view);

            city = view.findViewById(R.id.city);
            lon = view.findViewById(R.id.lon);
            lat = view.findViewById(R.id.lat);
            but = view.findViewById(R.id.btnpref);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherDB weatherdb = dataw.get(getAdapterPosition());
                    Toast.makeText(context, "Rimosso dai preferiti", Toast.LENGTH_LONG).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            RDatabase.getInstance(context).weatherDAO().setprefer(weatherdb.getId(),0);
                        }
                    }).start();
                }
            });
        }
    }
}