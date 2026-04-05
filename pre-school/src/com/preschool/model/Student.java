package com.preschool.model;

import java.time.LocalDate;

public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String address;
    private LocalDate enrollmentDate;
    private Integer classId;
    private String section;
    private String status;
    private String behaviour;

    public Student() {}

    public Student(int studentId, String firstName, String lastName, LocalDate dateOfBirth,
                   String gender, String guardianName, String guardianPhone, String guardianEmail,
                   String address, LocalDate enrollmentDate, Integer classId, String section,
                   String status, String behaviour) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.guardianEmail = guardianEmail;
        this.address = address;
        this.enrollmentDate = enrollmentDate;
        this.classId = classId;
        this.section = section;
        this.status = status;
        this.behaviour = behaviour;
    }

    // Getters and Setters

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public String getGuardianEmail() { return guardianEmail; }
    public void setGuardianEmail(String guardianEmail) { this.guardianEmail = guardianEmail; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBehaviour() { return behaviour; }
    public void setBehaviour(String behaviour) { this.behaviour = behaviour; }
}
