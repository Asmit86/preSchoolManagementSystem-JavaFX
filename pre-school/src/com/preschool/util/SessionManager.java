package com.preschool.util;

import com.preschool.model.User;


public class SessionManager {

    private static SessionManager instance;
    private User currentUser;   // holds Admin OR TeacherUser — polymorphic

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }


    public void login(User user)  { this.currentUser = user; }

    public void logout()          { this.currentUser = null; }


    public User    getCurrentUser() { return currentUser; }

    public boolean isLoggedIn()     { return currentUser != null; }


    public boolean isAdmin()   { return isLoggedIn() && currentUser.isAdmin(); }
    public boolean isTeacher() { return isLoggedIn() && currentUser.isTeacher(); }
}
