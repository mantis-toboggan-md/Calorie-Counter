package com.example.frontend.persistence;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.os.AsyncTask;
import android.util.Log;

import com.example.frontend.persistence.DayLog;
import com.example.frontend.persistence.DayLogDao;
import com.example.frontend.persistence.DayLogDatabase;


import java.util.List;

public class DayLogRepository  {

    private DayLogDao mDayLogDao;
    private LiveData<List<DayLog>> mDayLogs;

    DayLogRepository(Application application){
        DayLogDatabase db = DayLogDatabase.getDatabase(application);
        mDayLogDao = db.dayLogDao();
        mDayLogs = mDayLogDao.getDayLogs();
    }

    LiveData<List<DayLog>> getDayLogs(){
        return mDayLogs;
    }

    LiveData<List<DayLog>> getOneDayLogs(Integer dateInt){return mDayLogDao.getOneDayLogs(dateInt);}

    public void insert (DayLog dayLog){
        new insertAsyncTask(mDayLogDao).execute(dayLog);
    }

    private static class deleteAllLogsAsyncTask extends AsyncTask<Void, Void, Void> {
        private DayLogDao mAsyncTaskDao;

        deleteAllLogsAsyncTask(DayLogDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteOneAsyncTask extends AsyncTask<Integer, Void, Void>{
        private DayLogDao mAsyncTaskDao;

        deleteOneAsyncTask(DayLogDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(Integer... params){
            mAsyncTaskDao.deleteOne(params[0]);
            return null;
        }
    }

    public void deleteAll(){
        new deleteAllLogsAsyncTask(mDayLogDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<DayLog, Void, Void> {

        private DayLogDao mAsyncTaskDao;

        insertAsyncTask(DayLogDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DayLog... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void deleteOne(Integer id){
        new deleteOneAsyncTask(mDayLogDao).execute(id);
    }

}
