INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (1, 'admin@example.com', 'admin', '{noop}password', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (2, 'landlord@example.com', 'landlord', '{noop}word123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (3, 'tenant@example.com', 'tenant', '{noop}pass123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (4, 'renter@example.com', 'renter', '{noop}pass456', '2026-08-25T00:37:27.614');

INSERT INTO roles (NAME) VALUES ('ROLE_ADMIN');
INSERT INTO roles (NAME) VALUES ('ROLE_TENANT');
INSERT INTO roles (NAME) VALUES ('ROLE_LANDLORD');

INSERT INTO permissions (id, NAME) VALUES (1, 'CREATE_PAYMENTS');
INSERT INTO permissions (id, NAME) VALUES (2, 'UPDATE_PAYMENTS');
INSERT INTO permissions (id, NAME) VALUES (3, 'GET_PAYMENTS');
INSERT INTO permissions (id, NAME) VALUES (4, 'DELETE_PAYMENTS');

INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 2);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 3);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 4);

INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 2);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 3);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 4);

INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 3);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);

INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (11, 'JOHN', 'MAN', 'TENANT', 3);
INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (12, 'JANE', 'WOMAN', 'RENTER', 4);

INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (110, 23, 4500.00, 'PAID', '2026-06-01', 3, 11, '2026-06-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (111, 24, 4500.00, 'PAID', '2026-07-01', 3, 11, '2026-07-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (112, 25, 4500.00, 'PAID', '2026-08-01', 3, 11, '2026-08-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (113, 26, 4500.00, 'PAID', '2026-09-01', 3, 11, '2026-09-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (114, 27, 5000.00, 'CREATED', '2026-09-01', 4, 12, '2026-09-01T00:37:27.614');

/* it honestly gets to a point where i cant even read shit properly anymore */
INSERT INTO invoice_lines (id, invoice_id, item_name, description, quantity, unit_price, total_price, created_at)
VALUES (70, 110, 'RENT', 'PAYMENT FOR RENT.', 1, 4500.00, 4500.00, '2026-06-01T00:37:27.614');
INSERT INTO invoice_lines (id, invoice_id, item_name, description, quantity, unit_price, total_price, created_at)
VALUES (71, 111, 'RENT', 'PAYMENT FOR RENT.', 1, 4500.00, 4500.00, '2026-07-01T00:37:27.614');
INSERT INTO invoice_lines (id, invoice_id, item_name, description, quantity, unit_price, total_price, created_at)
VALUES (72, 112, 'RENT', 'PAYMENT FOR RENT.', 1, 4500.00, 4500.00, '2026-08-01T00:37:27.614');
INSERT INTO invoice_lines (id, invoice_id, item_name, description, quantity, unit_price, total_price, created_at)
VALUES (73, 113, 'RENT', 'PAYMENT FOR RENT.', 1, 4500.00, 4500.00, '2026-09-01T00:37:27.614');
INSERT INTO invoice_lines (id, invoice_id, item_name, description, quantity, unit_price, total_price, created_at)
VALUES (74, 114, 'RENT', 'PAYMENT FOR RENT.', 1, 4500.00, 4500.00, '2026-09-01T00:37:27.614');

INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (90, 100.00, 3, 'CASH', 110, 11, '2026-06-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (91, 5.00, 3, 'CASH',  111, 11, '2026-07-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (92, 250.00, 3, 'CASH',  112, 11, '2026-08-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (93, 110.10, 3, 'CASH',  113, 11, '2026-09-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (94, 99.99, 4, 'CASH',  114, 12, '2026-09-02T00:37:27.614');
