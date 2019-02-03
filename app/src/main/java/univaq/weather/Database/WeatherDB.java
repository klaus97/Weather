package univaq.weather.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "weather")
public class WeatherDB {

    @NonNull
    @PrimaryKey
    private String id ;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "lat")
    private double latitudine;

    @ColumnInfo(name = "lon")
    private double longitudine;

    @ColumnInfo(name = "nowweather")
    private String weath;

    @ColumnInfo(name = "wind")
    private double wind;

    @ColumnInfo(name = "temperature")
    private double temp;

    @ColumnInfo(name = "pressure")
    private Integer pres;

    public WeatherDB(){}

    public WeatherDB(String id, double lat, double lon, String city, double TP,String nw,double wind,Integer pres){
        this.id=id;
        this.latitudine = lat;
        this.longitudine = lon;
        this.city = city;
        this.temp = TP;
        this.weath=nw;
        this.wind=wind;
        this.pres=pres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public String getWeath() {
        return weath;
    }

    public void setWeath(String weath) {
        this.weath = weath;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public Integer getPres() {
        return pres;
    }

    public void setPres(Integer pres) {
        this.pres = pres;
    }



}
