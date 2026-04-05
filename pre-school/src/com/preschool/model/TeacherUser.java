package com.preschool.model;

import java.util.List;


// TeacherUser class inherits from User (Inheritance)
public class TeacherUser extends User {

    // Static permissions list, same for all teachers (shared resource)
    private static final List<String> PERMISSIONS = List.of(
            "Students", "Attendance", "Classes"
    );

    private final int teacherId;             // Unique teacher ID (specific to teacher entity in DB)

    // Constructor, calls parent (User) constructor
    public TeacherUser(int userId, String username, String fullName, int teacherId) {
        super(userId, username, fullName, "TEACHER");
        this.teacherId = teacherId;
    }

    // Getter for teacherId
    public int getTeacherId() {
        return teacherId;
    }


    @Override
    public String getDashboardTitle() {          // Polymorphism, Teacher-specific dashboard title
        return "Teacher Dashboard";
    }

    // Returns allowed modules for Teacher
    @Override
    public List<String> getPermissions() {
        return PERMISSIONS;
    }

    // Access control, checks if teacher can access a module
    @Override
    public boolean canAccessModule(String moduleName) {
        return PERMISSIONS.contains(moduleName);
    }

    @Override
    public String getRoleLabel() {
        return "Teacher";
    }
}