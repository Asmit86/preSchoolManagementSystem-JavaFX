package com.preschool.model;

import java.util.List;

public class Admin extends User {

    public Admin(int userId, String username, String fullName) {
        super(userId, username, fullName, "ADMIN");
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }

    @Override
    public List<String> getPermissions() {
        // Admin has access to all modules including Fees
        return List.of("Teachers", "Students", "Attendance", "Fees", "Reports");
    }

    @Override
    public boolean canAccessModule(String moduleName) {
        return true; // Admin has full access
    }

    @Override
    public String getRoleLabel() {
        return "Admin";
    }
}
