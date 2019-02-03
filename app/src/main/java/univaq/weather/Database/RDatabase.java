package univaq.weather.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {WeatherDB.class},version = 1)
public abstract class RDatabase extends RoomDatabase {

    public abstract WeatherDAO weatherDAO();

    private static RDatabase instance = null;

    public RDatabase(){}

    public static RDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context,
                    RDatabase.class,
                    "weatherDB").build();
        }
        return instance;
    }

}
