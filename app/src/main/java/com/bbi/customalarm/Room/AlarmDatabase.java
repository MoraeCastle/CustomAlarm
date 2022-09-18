package com.bbi.customalarm.Room;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.bbi.customalarm.Object.AlarmItem;

@Database(entities = {AlarmItem.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AlarmDatabase extends RoomDatabase {
    private static AlarmDatabase INSTANCE;
    public abstract AlarmDao alarmDao();

    public static AlarmDatabase getAppDatabase(Context context) {
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AlarmDatabase.class , "alarm-db")
                    .build();

        }
        return  INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
