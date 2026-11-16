package com.university.academic.config;

import com.university.academic.model.*;
import com.university.academic.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Inserts a small set of sample data on startup (only when the database is empty)
 * to test that every entity mapping and relationship works.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepo;
    private final LecturerRepository lecturerRepo;
    private final StudentRepository studentRepo;
    private final StudentProfileRepository profileRepo;
    private final CourseRepository courseRepo;
    private final RegistrationRepository registrationRepo;

    public DataSeeder(DepartmentRepository departmentRepo, LecturerRepository lecturerRepo,
                      StudentRepository studentRepo, StudentProfileRepository profileRepo,
                      CourseRepository courseRepo, RegistrationRepository registrationRepo) {
        this.departmentRepo = departmentRepo;
        this.lecturerRepo = lecturerRepo;
        this.studentRepo = studentRepo;
        this.profileRepo = profileRepo;
        this.courseRepo = courseRepo;
        this.registrationRepo = registrationRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepo.count() > 0) return;

        // Departments (head assigned after lecturers exist)
        Department cs = departmentRepo.save(new Department("CSC", "Computer Science", "Block A, Room 101"));
        Department bus = departmentRepo.save(new Department("BUS", "Business Administration", "Block C, Room 204"));

        // Lecturers (N:1 Department)
        Lecturer l1 = lecturerRepo.save(new Lecturer("STF001", "Jean", "Mugisha", "j.mugisha@uni.ac.rw",
                "+250788000001", "Dr.", LocalDate.of(2015, 9, 1), cs));
        Lecturer l2 = lecturerRepo.save(new Lecturer("STF002", "Aline", "Uwase", "a.uwase@uni.ac.rw",
                "+250788000002", "Mr.", LocalDate.of(2019, 1, 15), cs));
        Lecturer l3 = lecturerRepo.save(new Lecturer("STF003", "Eric", "Habimana", "e.habimana@uni.ac.rw",
                "+250788000003", "Prof.", LocalDate.of(2012, 3, 10), bus));

        // Heads of department (1:1)
        cs.setHeadOfDepartment(l1);
        bus.setHeadOfDepartment(l3);
        departmentRepo.save(cs);
        departmentRepo.save(bus);

        // Courses (N:1 Department) + lecturers (M:N join table)
        Course web = new Course("CSC301", "Web Technology and Internet", "Spring Boot, Hibernate, PostgreSQL", 4, cs);
        web.getLecturers().add(l1);
        web.getLecturers().add(l2);
        Course db = new Course("CSC205", "Database Systems", "Relational modelling and SQL", 3, cs);
        db.getLecturers().add(l2);
        Course mgt = new Course("BUS101", "Principles of Management", "Introduction to management", 3, bus);
        mgt.getLecturers().add(l3);
        courseRepo.save(web);
        courseRepo.save(db);
        courseRepo.save(mgt);

        // Students (N:1 Department) + profiles (1:1)
        Student s1 = studentRepo.save(new Student("REG2024001", "Alice", "Ingabire", "alice@student.uni.ac.rw",
                LocalDate.of(2024, 9, 2), cs));
        Student s2 = studentRepo.save(new Student("REG2024002", "Patrick", "Niyonzima", "patrick@student.uni.ac.rw",
                LocalDate.of(2024, 9, 2), bus));
        profileRepo.save(new StudentProfile(LocalDate.of(2004, 5, 12), Gender.FEMALE, "+250788111111", "Kigali, Gasabo", s1));
        profileRepo.save(new StudentProfile(LocalDate.of(2003, 11, 3), Gender.MALE, "+250788222222", "Kigali, Kicukiro", s2));

        // Registrations (join entity Student - Course)
        registrationRepo.save(new Registration(s1, web, LocalDate.of(2026, 9, 5), "2026/2027",
                Semester.SEMESTER_ONE, RegistrationStatus.APPROVED, "A"));
        registrationRepo.save(new Registration(s1, db, LocalDate.of(2026, 9, 5), "2026/2027",
                Semester.SEMESTER_ONE, RegistrationStatus.PENDING, null));
        registrationRepo.save(new Registration(s2, mgt, LocalDate.of(2026, 9, 6), "2026/2027",
                Semester.SEMESTER_ONE, RegistrationStatus.APPROVED, "B+"));
        registrationRepo.save(new Registration(s2, web, LocalDate.of(2026, 9, 6), "2026/2027",
                Semester.SEMESTER_ONE, RegistrationStatus.REJECTED, null));
    }
}
