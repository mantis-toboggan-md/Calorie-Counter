package com.example.frontend.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {DayLog.class}, version = 5, exportSchema = false)
public abstract class DayLogDatabase extends RoomDatabase {

    public abstract DayLogDao dayLogDao();

    private static DayLogDatabase INSTANCE;

    public static DayLogDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DayLogDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DayLogDatabase.class, "day_log_database")
                            //.addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}
