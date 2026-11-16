# Problem Analysis and Design
**University Course Registration and Academic Management System**
Web Technology and Internet — Individual Assignment

---

## 1. Identified Entities

| # | Entity | Why it is an entity |
|---|--------|---------------------|
| 1 | **Department** | The university is organised into departments. Each has its own data (name, code) and is linked to courses, lecturers and students. |
| 2 | **Lecturer** | Lecturers are employed by departments, teach courses, and one of them heads each department. They have their own identity and details. |
| 3 | **Student** | Students are admitted, assigned to a department and register for courses. They are the main users of the system. |
| 4 | **StudentProfile** | The problem says each student has *exactly one personal profile* holding personal details. It is modelled separately to keep personal data apart from academic data. |
| 5 | **Course** | Courses are offered by departments, taught by lecturers and registered for by students. |
| 6 | **Registration** | A student registering for a course has its own data (date, academic year, semester, status, grade). Those attributes belong to the *relationship* itself, so it becomes a **join entity**. |

**Not an entity:** the Lecturer–Course teaching assignment. The problem explicitly says it must be kept *without creating a separate entity*, and it has no extra attributes, so it is handled by a **join table** only.

**Enumerations (not entities, stored as values):**
- `Gender` — MALE, FEMALE, OTHER
- `Semester` — SEMESTER_ONE, SEMESTER_TWO, SEMESTER_THREE
- `RegistrationStatus` — PENDING, APPROVED, REJECTED

---

## 2. Attributes of Each Entity

### Department
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key, auto-generated |
| code | String | Unique, e.g. `CSC` |
| name | String | Unique, e.g. `Computer Science` |
| officeLocation | String | Optional |
| headOfDepartment | Lecturer | FK → lecturers (one-to-one) |

### Lecturer
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key |
| staffNumber | String | Unique |
| firstName | String | |
| lastName | String | |
| email | String | Unique |
| phoneNumber | String | |
| academicTitle | String | e.g. Dr., Prof., Mr. |
| hireDate | LocalDate | |
| department | Department | FK → departments (employing department) |

### Student
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key |
| registrationNumber | String | Unique |
| firstName | String | |
| lastName | String | |
| email | String | Unique |
| admissionDate | LocalDate | |
| department | Department | FK → departments |

### StudentProfile
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key |
| dateOfBirth | LocalDate | |
| gender | Gender (enum) | Stored as text |
| phoneNumber | String | |
| residentialAddress | String | |
| student | Student | FK → students, **unique** (one-to-one) |

### Course
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key |
| courseCode | String | Unique, e.g. `CSC301` |
| title | String | |
| description | String | Optional |
| credits | Integer | |
| department | Department | FK → departments |

### Registration (join entity)
| Attribute | Java type | Notes |
|-----------|-----------|-------|
| id | Long | Primary key |
| student | Student | FK → students |
| course | Course | FK → courses |
| registrationDate | LocalDate | |
| academicYear | String | e.g. `2026/2027` |
| semester | Semester (enum) | |
| status | RegistrationStatus (enum) | Defaults to PENDING |
| grade | String | Nullable — empty until results are released (e.g. `A`, `B+`) |

A unique constraint on (student, course, academicYear, semester) stops a student registering twice for the same course in the same semester.

---

## 3. Relationships and Cardinality

| # | Relationship | Cardinality | Explanation |
|---|-------------|-------------|-------------|
| R1 | Department **offers** Course | **1 : N** | A department offers many courses; each course belongs to one department. |
| R2 | Department **employs** Lecturer | **1 : N** | A department employs many lecturers; each lecturer works in one department. |
| R3 | Lecturer **heads** Department | **1 : 1** | Every department has one head; a lecturer can head at most one department. |
| R4 | Department **admits** Student | **1 : N** | A department has many students; each student is assigned to one department. |
| R5 | Student **has** StudentProfile | **1 : 1** | Each student has exactly one profile; each profile belongs to one student. |
| R6 | Lecturer **teaches** Course | **M : N** | A lecturer teaches many courses; a course can be taught by many lecturers. |
| R7 | Student **registers for** Course | **M : N** (with attributes) | A student takes many courses; a course has many students. Resolved through **Registration** into two 1 : N relationships: Student 1 : N Registration and Course 1 : N Registration. |

---

## 4. Relationships That Require a Foreign Key

| Relationship | Foreign key | Placed in table | Why there |
|--------------|-------------|-----------------|-----------|
| R1 Department–Course | `department_id` | `courses` | In 1 : N the FK always goes on the "many" side. |
| R2 Department–Lecturer (employs) | `department_id` | `lecturers` | "Many" side. |
| R3 Department–Lecturer (head) | `head_of_department_id` (UNIQUE) | `departments` | A department *has* one head, so the department points to its head. UNIQUE makes it one-to-one. |
| R4 Department–Student | `department_id` | `students` | "Many" side. |
| R5 Student–StudentProfile | `student_id` (UNIQUE) | `student_profiles` | The profile depends on the student and cannot exist without one, so the profile holds the FK. UNIQUE makes it one-to-one. |
| R7 Student–Registration | `student_id` | `registrations` | "Many" side. |
| R7 Course–Registration | `course_id` | `registrations` | "Many" side. |

## 5. Relationship That Requires a Join Table

**R6 — Lecturer teaches Course (M : N).**
A many-to-many relationship cannot be stored with one foreign key in either table. Since it has **no attributes of its own** and the problem forbids a separate entity, it is mapped with `@ManyToMany` + `@JoinTable`. Hibernate creates the table:

`course_lecturers (course_id FK, lecturer_id FK)` — composite primary key (course_id, lecturer_id).

## 6. Relationship That Requires a Join Entity

**R7 — Student registers for Course (M : N with attributes).**
This is also many-to-many, but the university must record registration date, academic year, semester, status and grade *for each registration*. A plain join table can only hold two foreign keys, so these attributes need a place to live. The relationship is therefore promoted to the **Registration** entity, which has its own primary key and two `@ManyToOne` links (to Student and to Course).

---

## 7. Relational Database Schema

Database: `university_academic_db`
(PK = primary key, FK = foreign key, UQ = unique)

```
departments (
    id                      BIGSERIAL     PK,
    code                    VARCHAR(10)   NOT NULL UQ,
    name                    VARCHAR(100)  NOT NULL UQ,
    office_location         VARCHAR(150),
    head_of_department_id   BIGINT        UQ  FK → lecturers(id)
)

lecturers (
    id                BIGSERIAL     PK,
    staff_number      VARCHAR(20)   NOT NULL UQ,
    first_name        VARCHAR(50)   NOT NULL,
    last_name         VARCHAR(50)   NOT NULL,
    email             VARCHAR(100)  NOT NULL UQ,
    phone_number      VARCHAR(20),
    academic_title    VARCHAR(30),
    hire_date         DATE,
    department_id     BIGINT        NOT NULL  FK → departments(id)
)

students (
    id                   BIGSERIAL     PK,
    registration_number  VARCHAR(20)   NOT NULL UQ,
    first_name           VARCHAR(50)   NOT NULL,
    last_name            VARCHAR(50)   NOT NULL,
    email                VARCHAR(100)  NOT NULL UQ,
    admission_date       DATE,
    department_id        BIGINT        NOT NULL  FK → departments(id)
)

student_profiles (
    id                   BIGSERIAL     PK,
    date_of_birth        DATE,
    gender               VARCHAR(10),
    phone_number         VARCHAR(20),
    residential_address  VARCHAR(255),
    student_id           BIGINT        NOT NULL UQ  FK → students(id)
)

courses (
    id              BIGSERIAL     PK,
    course_code     VARCHAR(15)   NOT NULL UQ,
    title           VARCHAR(150)  NOT NULL,
    description     VARCHAR(500),
    credits         INTEGER       NOT NULL,
    department_id   BIGINT        NOT NULL  FK → departments(id)
)

course_lecturers (                              -- join table (R6)
    course_id     BIGINT  NOT NULL  FK → courses(id),
    lecturer_id   BIGINT  NOT NULL  FK → lecturers(id),
    PK (course_id, lecturer_id)
)

registrations (                                 -- join entity (R7)
    id                  BIGSERIAL    PK,
    student_id          BIGINT       NOT NULL  FK → students(id),
    course_id           BIGINT       NOT NULL  FK → courses(id),
    registration_date   DATE         NOT NULL,
    academic_year       VARCHAR(9)   NOT NULL,
    semester            VARCHAR(20)  NOT NULL,
    status              VARCHAR(10)  NOT NULL,
    grade               VARCHAR(5),
    UQ (student_id, course_id, academic_year, semester)
)
```

**Note on the circular link:** `departments` points to `lecturers` (head) and `lecturers` points to `departments` (employer). For that reason `head_of_department_id` is nullable: a department is created first, its lecturers are added, and then one of them is set as head. The rule that the head must work in that same department is checked in the application, not by the database.

---

## 8. Summary

- **6 entities:** Department, Lecturer, Student, StudentProfile, Course, Registration
- **7 tables:** the 6 entity tables + `course_lecturers`
- **2 one-to-one:** Department–Head, Student–Profile
- **3 one-to-many:** Department→Course, Department→Lecturer, Department→Student
- **1 many-to-many via join table:** Lecturer–Course
- **1 many-to-many via join entity:** Student–Course → Registration
