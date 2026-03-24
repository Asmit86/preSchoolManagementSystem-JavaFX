package com.preschool.model;

import java.util.List;

public class TeacherUser extends User {

    private static final List<String> PERMISSIONS = List.of(
            "Students", "Attendance"
    );

    private final int teacherId;

    public TeacherUser(int userId, String username, String fullName, int teacherId) {
        super(userId, username, fullName, "TEACHER");
        this.teacherId = teacherId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    @Override
    public String getDashboardTitle() {
        return "Teacher Dashboard";
    }

    @Override
    public List<String> getPermissions() {
        return PERMISSIONS;
    }

    @Override
    public boolean canAccessModule(String moduleName) {
        return PERMISSIONS.contains(moduleName);
    }

    @Override
    public String getRoleLabel() {
        return "Teacher";
    }
}