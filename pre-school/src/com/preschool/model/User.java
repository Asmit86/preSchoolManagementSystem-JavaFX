package com.preschool.model;

import java.util.List;


public abstract class User {


    private final int    userId;       // immutable, no setter
    private final String username;     // immutable, no setter
    private final String role;         // immutable, no setter
    private       String fullName;     // mutable only by subclass
    private       String password;     // write-only — no getter

    protected User(int userId, String username, String fullName, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
    }


    public abstract String getDashboardTitle();


    public abstract List<String> getPermissions();


    public abstract boolean canAccessModule(String moduleName);

    public abstract String getRoleLabel();


    public int    getUserId()   { return userId; }

    public String getUsername() { return username; }


    public String getFullName() { return fullName; }

    public String getRole() { return role; }

    protected void setFullName(String fullName) { this.fullName = fullName; }


    protected void setPassword(String password) { this.password = password; }

    public boolean isAdmin()   { return "ADMIN".equals(role); }
    public boolean isTeacher() { return "TEACHER".equals(role); }

    @Override
    public String toString() { return getFullName() + " [" + getRoleLabel() + "]"; }
}
