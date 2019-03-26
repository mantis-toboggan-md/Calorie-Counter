package com.example.frontend.persistence;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.util.Calendar.*;

@Entity(tableName = "day_log")
public class DayLog {
    private Integer currentDay;
    private String foodName;
    private String ghg;
    private String land;
    private String water;
    private Double amtg;
    private Integer kCal;
    private Double p;
    private Double carbs;
    private Double fat;
    @PrimaryKey(autoGenerate=true)
    private Integer id;

    public DayLog(Integer currentDay, String foodName, String ghg, String land, String water, Double amtg, Integer kCal, Double p, Double carbs, Double fat){
        this.currentDay = currentDay;
        this.foodName = foodName;
        this.ghg = ghg;
        this.land = land;
        this.water = water;
        this.amtg = amtg;
        this.kCal = kCal;
        this.p = p;
        this.carbs = carbs;
        this.fat = fat;
    }

    public Integer getCurrentDay(){
        return this.currentDay;
    }

    public String getFoodName(){
        return this.foodName;
    }



    public void setId(Integer id){
        this.id=id;
    }

    public Integer getId(){
        return this.id;
    }

    public String getLand(){
        return this.land;
    }

    public String getGhg(){
        return this.ghg;
    }

    public String getWater(){
        return this.water;
    }
    public Double getAmtg() {

        return this.amtg;
    }

    public Integer getKCal() {
        return this.kCal;
    }

    public Double getP(){ return this.p;}

    public Double getCarbs(){return this.carbs;}

    public Double getFat(){return this.fat;}
}
