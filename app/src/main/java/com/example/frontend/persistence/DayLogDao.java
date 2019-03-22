package com.example.frontend.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DayLogDao {
    @Insert
    void insert(DayLog dayLog);

    @Query("DELETE  FROM day_log")
    void deleteAll();

    @Query("SELECT * FROM day_log")
    LiveData<List<DayLog>> getDayLogs();

}
