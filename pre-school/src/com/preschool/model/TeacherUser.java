package com.preschool.model;

import java.util.List;


public class TeacherUser extends User {

    private static final List<String> TEACHER_PERMISSIONS = List.of(
            "Students", "Attendance"
    );


    public TeacherUser(int userId, String username, String fullName) {
        super(userId, username, fullName, "TEACHER");
    }




    @Override
    public String getDashboardTitle() {
        return "Teacher Dashboard";
    }


    @Override
    public List<String> getPermissions() {
        return TEACHER_PERMISSIONS;
    }


    @Override
    public boolean canAccessModule(String moduleName) {
        return TEACHER_PERMISSIONS.contains(moduleName);
    }


    @Override
    public String getRoleLabel() {
        return "Teacher";
    }
}
