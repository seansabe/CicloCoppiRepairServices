package com.example.repairservicesapp.app;

import com.example.repairservicesapp.model.User;

public class AppManager {

    public static AppManager instance = new AppManager();
    public User user = new User();

    private AppManager() { }

    public void setUser(User user) { this.user = user; }
}
