package com.example.finalproject.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.finalproject.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;



@Entity
public class User{
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String password;
    public String email;
    public String phone;
    public String avatar;
    public Long lastUpdated;
    public boolean isBarbershop;
    public boolean isAvailable;

    final static String ID = "id";
    final static String NAME = "name";
    final static String PASSWORD = "password";
    final static String EMAIL = "email";
    final static String PHONE = "phone";
    final static String AVATAR = "avatar";
    final static String LAST_UPDATED = "lastUpdated";
    final static String IS_BARBERSHOP = "isBarbershop";
    final static String IS_AVAILABLE = "isAvailable";


    //Setters:
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setBarbershop(boolean barbershop) {
        isBarbershop = barbershop;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    //Getters:
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isBarbershop() {
        return isBarbershop;
    }

    public boolean isAvailable() {
        return isAvailable;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(ID, id);
        json.put(NAME, name);
        json.put(PASSWORD, password);
        json.put(EMAIL, email);
        json.put(PHONE, phone);
        json.put(AVATAR, avatar);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(IS_BARBERSHOP,isBarbershop);
        json.put(IS_AVAILABLE,isAvailable);

        return json;
    }

    static public User create(Map<String,Object> json) {
        User user = new User();
        user.id = (String)json.get(ID);
        user.name = (String)json.get(NAME);
        user.password = (String)json.get(PASSWORD);
        user.email = (String)json.get(EMAIL);
        user.phone = (String)json.get(PHONE);
        user.avatar = (String)json.get(AVATAR);
        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);

        if(ts!=null)
            user.lastUpdated = new Long(ts.getSeconds());
        else
            user.lastUpdated = new Long(0);

        user.isBarbershop = (boolean)json.get(IS_BARBERSHOP);
        user.isAvailable = (boolean)json.get(IS_AVAILABLE);

        return user;
    }

    private static final String STUDENT_LAST_UPDATE = "StudentLastUpdate";

    static public void setLocalLastUpdateTime(Long ts){
        //Shared preference, saving the ts on the disk (like the db):
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(STUDENT_LAST_UPDATE,ts);
        editor.commit();
    }

    static public Long getLocalLastUpdateTime(){
        //Shared preference, saving the ts in app:
         return MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                 .getLong(STUDENT_LAST_UPDATE,0);
    }

}
