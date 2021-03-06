package univaq.weather.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import univaq.weather.Database.RDatabase;
import univaq.weather.Model.Weather;
import univaq.weather.R;


public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {

    private List<Weather> dataw;
    Context context;

    public AdapterRecycler(List<Weather> dataw, Context context){
        this.dataw = dataw ; this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Weather weather = dataw.get(i);
        viewHolder.title.setText(Double.toString(weather.getTemperature()));
        viewHolder.subtitle.setText(weather.getCity());
        viewHolder.weath.setText(weather.getNowweather());
        viewHolder.windv.setText(Double.toString(weather.getWind()));
        viewHolder.presval.setText(Integer.toString(weather.getPressure()));

        if(weather.getNowweather().equals("Rain")) {
            viewHolder.imageweath.setImageResource(R.drawable.rain_icon);
        }else if(weather.getNowweather().equals("Snow")) {
            viewHolder.imageweath.setImageResource(R.drawable.snow_icon);
        }else if(weather.getNowweather().equals("Clouds")) {
            viewHolder.imageweath.setImageResource(R.drawable.cloud_icon);
        }else if(weather.getNowweather().equals("Fog")) {
                viewHolder.imageweath.setImageResource(R.drawable.fog_icon);
        }else{
            viewHolder.imageweath.setImageResource(R.drawable.sun_icon);
        }

    }

    @Override
    public int getItemCount() {
        return dataw.size();
    }

    // Use ViewHolder Pattern
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        TextView weath;
        ImageView imageweath;
        TextView windv;
        TextView presval;
        Button button;

        ViewHolder(@NonNull View view) {
            super(view);

            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            weath = view.findViewById(R.id.weath);
            imageweath=view.findViewById(R.id.imageweath);
            windv=view.findViewById(R.id.windv);
            presval=view.findViewById(R.id.presval);
            button=view.findViewById(R.id.btnsave);

            // Define the click event on item
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Open another Activity and pass to it the right city

                    //TODO

                    Weather weather = dataw.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);

                    intent.putExtra("city",weather.getCity());
                    intent.putExtra("temperature",weather.getTemperature());
                    intent.putExtra("latitude", weather.getLat());
                    intent.putExtra("longitude", weather.getLon());
                    intent.putExtra("weath",weather.getNowweather());
                    intent.putExtra("wind",weather.getWind());
                    intent.putExtra("pressure",weather.getPressure());

                   // Log.d("ORA",weather.getLocaltime().toString());
                    v.getContext().startActivity(intent);

                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Weather weather = dataw.get(getAdapterPosition());
                    Toast.makeText(context, "Città aggiunta ai preferiti!", Toast.LENGTH_LONG).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RDatabase.getInstance(context).weatherDAO().setprefer(weather.getId(),1);
                        }
                    }).start();

                }
            });
        }
    }
}