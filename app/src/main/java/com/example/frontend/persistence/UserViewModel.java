package com.example.frontend.persistence;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private LiveData<List<User>> mUsers;

    public UserViewModel(Application application){
        super(application);
        mRepository = new UserRepository(application);
        mUsers = mRepository.getUser();
    }

    public LiveData<List<User>> getUser(){
        return mUsers;
    }

    public void insert(User user){
        mRepository.insert(user);
    }

    public void update(long goal){
        mRepository.update(goal);
    }

    public User getClosestDate(Integer targetDate) {return mRepository.getClosestDate(targetDate);}

    public void deleteAll(){mRepository.deleteAll();}
}
