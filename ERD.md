# Entity Relationship Diagram

![ERD](ERD.png)

The same diagram as Mermaid (GitHub renders it automatically):

```mermaid
erDiagram
    DEPARTMENT ||--o{ COURSE : "offers"
    DEPARTMENT ||--o{ LECTURER : "employs"
    LECTURER |o--o| DEPARTMENT : "heads"
    DEPARTMENT ||--o{ STUDENT : "admits"
    STUDENT ||--|| STUDENT_PROFILE : "has"
    LECTURER }o--o{ COURSE : "teaches (course_lecturers)"
    STUDENT ||--o{ REGISTRATION : "makes"
    COURSE ||--o{ REGISTRATION : "receives"

    DEPARTMENT {
        BIGINT id PK
        VARCHAR code UK
        VARCHAR name UK
        VARCHAR office_location
        BIGINT head_of_department_id FK,UK "nullable"
    }
    LECTURER {
        BIGINT id PK
        VARCHAR staff_number UK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR email UK
        VARCHAR phone_number
        VARCHAR academic_title
        DATE hire_date
        BIGINT department_id FK
    }
    STUDENT {
        BIGINT id PK
        VARCHAR registration_number UK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR email UK
        DATE admission_date
        BIGINT department_id FK
    }
    STUDENT_PROFILE {
        BIGINT id PK
        DATE date_of_birth
        VARCHAR gender "MALE / FEMALE / OTHER"
        VARCHAR phone_number
        VARCHAR residential_address
        BIGINT student_id FK,UK
    }
    COURSE {
        BIGINT id PK
        VARCHAR course_code UK
        VARCHAR title
        VARCHAR description
        INTEGER credits
        BIGINT department_id FK
    }
    REGISTRATION {
        BIGINT id PK
        BIGINT student_id FK
        BIGINT course_id FK
        DATE registration_date
        VARCHAR academic_year
        VARCHAR semester
        VARCHAR status "PENDING / APPROVED / REJECTED"
        VARCHAR grade "nullable"
    }
```

**Legend (crow's foot):** `||` exactly one · `o|` zero or one · `o{` zero or many · `}o--o{` many-to-many.

- `course_lecturers` is the join table for Lecturer–Course (no entity).
- `REGISTRATION` is the join entity that resolves Student–Course many-to-many.
