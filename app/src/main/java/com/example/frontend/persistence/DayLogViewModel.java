package com.example.frontend.persistence;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class DayLogViewModel extends AndroidViewModel {
    private DayLogRepository mRepository;
    private LiveData<List<DayLog>> mDayLogs;

    public DayLogViewModel(Application application){
        super(application);
        mRepository = new DayLogRepository(application);
       mDayLogs = mRepository.getDayLogs();
    }

    public LiveData<List<DayLog>> getDayLogs(){
        return mDayLogs;
    }

    public LiveData<List<DayLog>> getOneDayLogs(Integer dateInt){return mRepository.getOneDayLogs(dateInt);}

    public void insert(DayLog dayLog){
        mRepository.insert(dayLog);
    }

    public void deleteAll(){mRepository.deleteAll();}

    public void deleteOne(Integer id){mRepository.deleteOne(id);}

}