package com.preschool.model;

import java.util.List;


/**
 * Admin class represents a system administrator user.
 * It extends the base User class and has full system access.
 */
public class Admin extends User {

    // Constructor initializes an Admin user with role "ADMIN"
    public Admin(int userId, String username, String fullName) {
        super(userId, username, fullName, "ADMIN");
    }

    @Override
    public String getDashboardTitle() { return "Admin Dashboard"; }  // Returns the dashboard title shown to Admin users after login

    /**
     * Defines all modules Admin can access in the system.
     * Admin has full access to all major features.
     */
    @Override
    public List<String> getPermissions() {
        return List.of("Teachers", "Students", "Attendance", "Fees", "Reports", "Classes");
    }

    @Override
    public boolean canAccessModule(String moduleName) { return true; }  // Always returns true regardless of module name

    @Override
    public String getRoleLabel() { return "Admin"; }   // Returns a readable role label for UI display
}
