package univaq.weather.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class Weather {

    private String id;
    private double lat,lon;
    private String city;
    private double temperature;
    private String nowweather;
    private LocalDate date;
    private double wind;
    private Integer pressure;

    public Weather(){}

    public Weather(String id){
        this.id=id;
    }

    public Weather(String id, double lat, double lon, String city, double TP,String nw,LocalDate date,double wind,Integer pres){
        this.id=id;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.temperature = TP;
        this.nowweather=nw;
        this.date=date;
        this.wind=wind;
        this.pressure=pres;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getNowweather() {
        return nowweather;
    }

    public void setNowweather(String nowweather) {
        this.nowweather = nowweather;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }
}
