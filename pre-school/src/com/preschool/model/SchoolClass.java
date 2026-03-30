package com.preschool.model;

public class SchoolClass {
    private int classId;
    private String className;
    private String section;
    private Integer teacherId;
    private String teacherName;

    public SchoolClass() {}

    public SchoolClass(int classId, String className, String section, Integer teacherId, String teacherName) {
        this.classId = classId;
        this.className = className;
        this.section = section;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getDisplayName() { return className + " - " + section; }

    @Override
    public String toString() { return getDisplayName(); }
}
