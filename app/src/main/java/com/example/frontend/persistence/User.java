package com.example.frontend.persistence;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user_profile_table")
public class User {

    private long mGoal;
    @PrimaryKey(autoGenerate=true)
    private Integer id;

    public User(@NonNull long goal) {
        this.mGoal = goal;
    }
    public long getGoal(){
        return this.mGoal;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public Integer getId(){
        return this.id;
    }

}
