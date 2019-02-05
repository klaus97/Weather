package univaq.weather.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import univaq.weather.Model.Weather;

@Dao
public interface WeatherDAO {

    @Insert
    public void save(WeatherDB weatherdb);

    @Update
    public void update(WeatherDB weatherdb);

    @Query("SELECT * FROM weather order by city ")
    public List<WeatherDB> getWeather();

    @Query("DELETE FROM weather")
    public void delete();

    @Query("SELECT * FROM weather where id=:id")
    public List<WeatherDB> check(String id);

    @Query("UPDATE weather SET favourite=:f where id=:id")
    public void setprefer(String id,Integer f);

    @Query("SELECT * FROM weather WHERE favourite=1")
    public List<WeatherDB> getFavourite();

    @Query("SELECT * FROM weather ")
    public List<WeatherDB> getCity();
}
