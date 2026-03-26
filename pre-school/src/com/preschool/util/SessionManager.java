package com.preschool.util;

import com.preschool.model.User;


/**
 * SessionManager is responsible for managing the current logged-in user session.
 * It follows the Singleton Design Pattern so that only one session exists throughout the application.
 */
public class SessionManager {

    private static SessionManager instance;         // Singleton instance of SessionManager
    private User currentUser;   // holds Admin OR TeacherUser — polymorphic

    private SessionManager() {}       // Private constructor to prevent external instantiation (Singleton pattern)



    /**
     * Returns the single instance of SessionManager.
     * If it doesn't exist, it creates one.
     */
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }


    /**
     * Logs in a user by storing the user object in session.
     * @param user the authenticated user object
     */
    public void login(User user)  { this.currentUser = user; }


    /**
     * Logs out the current user by clearing session data.
     */
    public void logout()          { this.currentUser = null; }


    /**
     * Returns the currently logged-in user.
     * @return current User object or null if no user is logged in
     */
    public User    getCurrentUser() { return currentUser; }


    /**
     * Checks whether any user is currently logged in.
     * @return true if user session exists, otherwise false
     */
    public boolean isLoggedIn()     { return currentUser != null; }


    /**
     * Checks whether the logged-in user is an Admin.
     * Uses method defined in User class (polymorphism/override).
     */
    public boolean isAdmin()   { return isLoggedIn() && currentUser.isAdmin(); }


    /**
     * Checks whether the logged-in user is a Teacher.
     * Uses method defined in User class (polymorphism/override).
     */
    public boolean isTeacher() { return isLoggedIn() && currentUser.isTeacher(); }
}
