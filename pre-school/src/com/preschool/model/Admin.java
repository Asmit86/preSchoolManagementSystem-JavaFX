package com.preschool.model;

import java.util.List;

public class Admin extends User {

    private static final List<String> ADMIN_PERMISSIONS = List.of(
            "Students", "Teachers", "Attendance", "Fees", "Reports"
    );


    public Admin(int userId, String username, String fullName) {
        super(userId, username, fullName, "ADMIN");
    }



    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }


    @Override
    public List<String> getPermissions() {
        return ADMIN_PERMISSIONS;
    }


    @Override
    public boolean canAccessModule(String moduleName) {
        return true;   // Admin can access everything
    }


    @Override
    public String getRoleLabel() {
        return "Administrator";
    }
}
