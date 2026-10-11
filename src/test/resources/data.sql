INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (1, 'admin@example.com', 'admin', '{noop}password', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (2, 'landlord@example.com', 'landlord', '{noop}word123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (3, 'tenant@example.com', 'tenant', '{noop}pass123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (4, 'renter@example.com', 'renter', '{noop}pass456', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (5, 'notenantprofile@example.com', 'notenant', '{noop}pass123', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (6, 'nolandlordprofile@example.com', 'nolandlord', '{noop}lord123', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (7, 'lenderlaud@example.com', 'lender', '{noop}lend123', '2026-08-25T00:37:27.614');

INSERT INTO roles (NAME) VALUES ('ROLE_ADMIN');
INSERT INTO roles (NAME) VALUES ('ROLE_TENANT');
INSERT INTO roles (NAME) VALUES ('ROLE_LANDLORD');

INSERT INTO permissions (id, NAME) VALUES (1, 'payments:create');
INSERT INTO permissions (id, NAME) VALUES (2, 'payments:update');
INSERT INTO permissions (id, NAME) VALUES (3, 'payments:read');
INSERT INTO permissions (id, NAME) VALUES (4, 'payments:delete');
INSERT INTO permissions (id, NAME) VALUES (5, 'properties:create');
INSERT INTO permissions (id, NAME) VALUES (6, 'properties:update');
INSERT INTO permissions (id, NAME) VALUES (7, 'properties:read');
INSERT INTO permissions (id, NAME) VALUES (8, 'properties:delete');

INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 2);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 3);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 4);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 5);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 6);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 7);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 8);

INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 3);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 5);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 6);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 7);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (3, 8);

INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 1);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 3);
INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 7);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (7, 3);

INSERT INTO landlord_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (21, 'ALFRED', 'LIM', 'LANDLORD', 2);
INSERT INTO landlord_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (22, 'LENDER', 'MAGNA', 'LAUDE', 7);

INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (11, 'JOHN', 'MAN', 'TENANT', 3);
INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (12, 'JANE', 'WOMAN', 'RENTER', 4);

INSERT INTO properties (id, landlord_profile_id, created_at)
VALUES (60, 21, '2026-07-01T00:37:27.614');
INSERT INTO property_details (id, property_id, name, property_type, property_value, bedroom_quantity, bathroom_quantity, size, description, created_at)
VALUES (70, 60, 'Santa Cruz Apartment', 'Apartments', 4500.00, 2, 1, '80', 'Apartment in Santa Cruz.', '2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, created_at)
VALUES (61, 21, '2026-07-01T00:37:27.614');
INSERT INTO property_details (id, property_id, name, property_type, property_value, bedroom_quantity, bathroom_quantity, size, description, created_at)
VALUES (71, 61, 'Lynville Apartment Unit No. 1', 'Apartments', 5500.00, 3, 1, '120', 'Lynville Diamond Apartment in Santa Cruz.', '2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, created_at)
VALUES (62, 21, '2026-07-01T00:37:27.614');
INSERT INTO property_details (id, property_id, name, property_type, property_value, bedroom_quantity, bathroom_quantity, size, description, created_at)
VALUES (72, 62, 'TeraVille Apartment Unit No. 64', 'Apartments', 6500.00, 3, 2, '160', 'TeraVille Coolio Apartment in Laguna.', '2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, created_at)
VALUES (63, 22, '2026-07-01T00:37:27.614');
INSERT INTO property_details (id, property_id, name, property_type, property_value, bedroom_quantity, bathroom_quantity, size, description, created_at)
VALUES (73, 63, 'S&U Apartment Unit', 'Apartments', 8000.00, 3, 2, '160', 'Studio Apartment in Manila.', '2026-07-01T00:37:27.614');

INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (110, 23, 4500.00, 'PAID', '2026-06-01', 3, 11, '2026-06-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (111, 24, 4500.00, 'PAID', '2026-07-01', 3, 11, '2026-07-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (112, 25, 4500.00, 'PAID', '2026-08-01', 3, 11, '2026-08-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (113, 26, 4500.00, 'PAID', '2026-09-01', 3, 11, '2026-09-01T00:37:27.614');
INSERT INTO invoices (id, invoice_number, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (114, 27, 5000.00, 'DRAFT', '2026-09-01', 4, 12, '2026-09-01T00:37:27.614');

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
