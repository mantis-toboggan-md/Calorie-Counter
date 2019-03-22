package com.example.frontend.persistence;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private LiveData<User> mUser;

    public UserViewModel(Application application){
        super(application);
        mRepository = new UserRepository(application);
        mUser = mRepository.getUser();
    }

    public LiveData<User> getUser(){
        return mUser;
    }

    public void insert(User user){
        mRepository.insert(user);
    }

    public void update(long goal){
        mRepository.update(goal);
    }
}
