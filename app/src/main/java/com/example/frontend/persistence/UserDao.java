package com.example.frontend.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao{
    @Insert
    void insert(User user);

    @Query("DELETE  FROM user_profile_table")
    void deleteAll();

    @Query("SELECT * FROM user_profile_table")
    LiveData<List<User>> getUser();

    @Query("UPDATE user_profile_table SET mGoal = :goal")
    void updateUser(long goal);

    @Query("SELECT * FROM user_profile_table WHERE dateSet<=:targetDate ORDER BY dateSet DESC, mGoal DESC LIMIT 1 ")
    User getclosestDate(Integer targetDate);
}
