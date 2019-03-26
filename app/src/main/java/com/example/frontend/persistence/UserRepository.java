package com.example.frontend.persistence;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.os.AsyncTask;

import java.util.List;

public class UserRepository  {

    private UserDao mUserDao;
    private LiveData<List<User>> mUsers;

    UserRepository(Application application){
        UserDatabase db = UserDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mUsers = mUserDao.getUser();
    }

    LiveData<List<User>> getUser(){
        return mUsers;
    }

    User getClosestDate(Integer targetDate){ return mUserDao.getclosestDate(targetDate);}
//
//    private static class getClosestAsyncTask extends AsyncTask<Integer, Void, User> {
//        private UserDao mAsyncTaskDao;
//
//        getClosestAsyncTask(UserDao dao){mAsyncTaskDao = dao;}
//
//        @Override
//        protected User doInBackground(final Integer... params){
//            return mAsyncTaskDao.getclosestDate(params[0]);
//        }
//
//        @Override
//        protected User onPostExecute(User mUser){
//            return mUser;
//        }
//    }



    public void insert (User user){
        new insertAsyncTask(mUserDao).execute(user);
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void deleteAll(){ new deleteAllUsersAsyncTask(mUserDao).execute();}

    private static class deleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao mAsyncTaskDao;

        deleteAllUsersAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    public void update(long goal) {new updateAsyncTask(mUserDao).execute(goal);}

    private static class updateAsyncTask extends AsyncTask<Long, Void,Void> {
        private UserDao mAsyncTaskDao;
        updateAsyncTask(UserDao dao) {mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.updateUser(params[0]);
            return null;
        }
    }
}
