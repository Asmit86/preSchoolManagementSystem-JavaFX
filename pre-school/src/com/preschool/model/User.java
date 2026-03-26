package com.preschool.model;

import java.util.List;


/**
 * Abstract class representing a generic system user
 * This class acts as a base class for Admin and Teacher users
 * It defines common attributes and methods shared by all users
 */
public abstract class User {


    private final int userId;           // unique identifier for each user (primary key from database)
    private final String username;      // username used for login (cannot be changed after creation)
    private final String role;          // role of the user (ADMIN or TEACHER)
    private String fullName;
    private String password;

    /**
     * Constructor to initialize common user attributes
     */
    protected User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }


    public abstract String getDashboardTitle();     // Returns the title to be displayed on dashboard
    public abstract List<String> getPermissions();   // Returns list of permissions assigned to the user
    public abstract boolean canAccessModule(String moduleName);  // Checks whether the user can access a specific module
    public abstract String getRoleLabel();   // Returns a user-friendly label for the role


    //  ------------------------------  Getters  -------------------------------
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    //  ------------------------------  Setters  -------------------------------
    protected void setFullName(String fullName) { this.fullName = fullName; }
    protected void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return "ADMIN".equals(role); }       // Checks if the user is an Admin
    public boolean isTeacher() { return "TEACHER".equals(role); }   // Checks if the user is a Teacher

    @Override
    public String toString() {
        return fullName + " [" + getRoleLabel() + "]";    // Returns a string representation of the user
    }
}