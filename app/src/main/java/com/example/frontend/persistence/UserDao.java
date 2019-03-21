package com.example.frontend.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface UserDao{
    @Insert
    void insert(User user);

    @Query("DELETE  FROM user_profile_table")
    void deleteAll();

    @Query("SELECT * FROM user_profile_table LIMIT 1")
    LiveData<User> getUser();
}
