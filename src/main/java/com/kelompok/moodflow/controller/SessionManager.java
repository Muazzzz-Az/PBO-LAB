package com.kelompok.moodflow.controller;

import com.kelompok.moodflow.model.User;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}