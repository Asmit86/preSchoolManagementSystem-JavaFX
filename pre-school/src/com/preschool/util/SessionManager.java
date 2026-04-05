package com.preschool.util;

import com.preschool.model.User;



// This class manages the current logged-in user
public class SessionManager {

    // Static instance (used for Singleton pattern)
    private static SessionManager instance;


    // This variable stores the currently logged-in user
    // It can hold Admin OR TeacherUser (polymorphism)
    private User currentUser;   // holds Admin OR TeacherUser — polymorphic

    private SessionManager() {}

    // Method to get the single instance of SessionManager
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }


    public void login(User user)  { this.currentUser = user; }     // Stores the user object in currentUser

    public void logout()          { this.currentUser = null; }     // Removes the current user (sets to null)


    public User    getCurrentUser() { return currentUser; }        // Returns the currently logged-in user

    public boolean isLoggedIn()     { return currentUser != null; }   // Checks if a user is logged in


    // Checks if the logged-in user is an Admin
    public boolean isAdmin()   { return isLoggedIn() && currentUser.isAdmin(); }

    // Checks if the logged-in user is a Teacher
    public boolean isTeacher() { return isLoggedIn() && currentUser.isTeacher(); }
}
