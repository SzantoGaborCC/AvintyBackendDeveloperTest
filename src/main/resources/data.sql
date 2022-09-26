INSERT INTO employees (email, password, full_name,created_at, updated_at, is_active)
VALUES (
           'adminguy@gmail.com',
           '$2a$10$mI2hgZb4oP90n5dIPIVQi.Lx28CqCvLcCCo1w24HR5ef5KiX0WUKy',
           'John Doe',
           CURRENT_TIMESTAMP(),
           CURRENT_TIMESTAMP(),
           TRUE);
INSERT INTO employee_roles (employee_id, role)
VALUES(1, 'ADMIN');