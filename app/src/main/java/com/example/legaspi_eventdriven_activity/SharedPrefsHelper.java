package com.example.legaspi_eventdriven_activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.legaspi_eventdriven_activity.network.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefsHelper {

    private static final String PREF_NAME = "UsersPrefs";
    private static final String KEY_USERS_LIST = "users_list";

    public static void saveUserLocally(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        
        String json = prefs.getString(KEY_USERS_LIST, null);
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        List<User> userList = json == null ? new ArrayList<>() : gson.fromJson(json, type);
        
        userList.add(user);
        
        prefs.edit().putString(KEY_USERS_LIST, gson.toJson(userList)).apply();
    }

    public static List<User> getLocalUsers(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        
        String json = prefs.getString(KEY_USERS_LIST, null);
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        List<User> userList = json == null ? new ArrayList<>() : gson.fromJson(json, type);
        
        return userList;
    }
}
