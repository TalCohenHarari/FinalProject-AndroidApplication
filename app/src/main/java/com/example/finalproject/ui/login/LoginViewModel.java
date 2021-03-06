package com.example.finalproject.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.model.Model;
import com.example.finalproject.model.User;

import java.util.List;

public class LoginViewModel extends ViewModel {

    private static LiveData<List<User>> usersList;

    public LoginViewModel() {
        usersList = Model.instance.getAllUsers();
    }

    public LiveData<List<User>> getData() {
        return usersList;
    }

    public static boolean isUserExist(String userName, String password){

        if(usersList.getValue()!=null)
            for (User user : usersList.getValue())
                if (user.name.equals(userName) && user.password.equals(password) && user.isAvailable())
                    return true;

        return false;
    }

}