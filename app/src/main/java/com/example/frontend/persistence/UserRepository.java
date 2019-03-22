package com.example.frontend.persistence;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.os.AsyncTask;

public class UserRepository  {

    private UserDao mUserDao;
    private LiveData<User> mUser;

    UserRepository(Application application){
        UserDatabase db = UserDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mUser = mUserDao.getUser();
    }

    LiveData<User> getUser(){
        return mUser;
    }

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
