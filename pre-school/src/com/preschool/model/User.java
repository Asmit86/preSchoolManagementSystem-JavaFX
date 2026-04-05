package com.preschool.model;

import java.util.List;


// Abstract class, cannot be instantiated directly
// Acts as a base class for Admin and TeacherUser
public abstract class User {
    // 'final', cannot be changed once assigned (data integrity)
    private final int userId;
    private final String username;
    private final String role;


    private String fullName;
    private String password;

    // Constructor used by subclasses
    protected User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // Abstract methods, MUST be implemented by subclasses
    public abstract String getDashboardTitle();
    public abstract List<String> getPermissions();
    public abstract boolean canAccessModule(String moduleName);
    public abstract String getRoleLabel();

    // Getter methods (Encapsulation: controlled access)
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    // Protected setters, only subclasses can modify (not external classes)
    protected void setFullName(String fullName) { this.fullName = fullName; }
    protected void setPassword(String password) { this.password = password; }

    // Role checking (used in SessionManager / authorization)
    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isTeacher() { return "TEACHER".equals(role); }

    @Override
    public String toString() {
        return fullName + " [" + getRoleLabel() + "]";
    }
}