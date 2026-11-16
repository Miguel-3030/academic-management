package com.university.academic.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "office_location", length = 150)
    private String officeLocation;

    // 1:1 - each department has one head (a lecturer). FK lives in departments.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_of_department_id", unique = true)
    private Lecturer headOfDepartment;

    // 1:N - a department offers many courses
    @OneToMany(mappedBy = "department")
    private List<Course> courses = new ArrayList<>();

    // 1:N - a department employs many lecturers
    @OneToMany(mappedBy = "department")
    private List<Lecturer> lecturers = new ArrayList<>();

    // 1:N - a department has many students
    @OneToMany(mappedBy = "department")
    private List<Student> students = new ArrayList<>();

    public Department() {}

    public Department(String code, String name, String officeLocation) {
        this.code = code;
        this.name = name;
        this.officeLocation = officeLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public Lecturer getHeadOfDepartment() { return headOfDepartment; }
    public void setHeadOfDepartment(Lecturer headOfDepartment) { this.headOfDepartment = headOfDepartment; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    public List<Lecturer> getLecturers() { return lecturers; }
    public void setLecturers(List<Lecturer> lecturers) { this.lecturers = lecturers; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

}
