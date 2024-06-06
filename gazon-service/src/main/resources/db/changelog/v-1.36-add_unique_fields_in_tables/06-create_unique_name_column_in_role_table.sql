ALTER TABLE roles
    ADD CONSTRAINT unique_role_name UNIQUE (name);