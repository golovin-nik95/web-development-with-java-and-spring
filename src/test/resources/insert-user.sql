MERGE INTO users (id, email, encrypted_password, name) KEY (id) VALUES
(1, 'test@griddynamics.com', '$2a$10$h2Iy3hJVWOFCcIXdguGcgOhYOdFA3PXhN/AN8hlhIyZAiW7e3KUjC', 'Ivan Ivanov');