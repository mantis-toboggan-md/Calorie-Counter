package com.example.frontend.persistence;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(tableName = "user_profile_table")
public class User {

    private long mGoal;
    private Integer dateSet;
    @PrimaryKey(autoGenerate=true)
    private Integer id;

    public User(@NonNull long goal, @NonNull Integer dateSet) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        this.mGoal = goal;
        this.dateSet = dateSet;
        Log.i("dateSet added to db: ", this.dateSet.toString());
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

    public Integer getDateSet(){return this.dateSet;}
}
