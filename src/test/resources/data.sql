INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (10, 'admin@example.com', 'admin', '{noop}password', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (11, 'landlord@example.com', 'landlord', '{noop}word123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (12, 'tenant@example.com', 'tenant', '{noop}pass123', '2026-05-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (13, 'renter@example.com', 'renter', '{noop}pass456', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (14, 'notenantprofile@example.com', 'notenant', '{noop}pass123', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (15, 'nolandlordprofile@example.com', 'nolandlord', '{noop}lord123', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (16, 'lenderlaud@example.com', 'lender', '{noop}lend123', '2026-08-25T00:37:27.614');
INSERT INTO users (ID, EMAIL, USERNAME, PASSWORD, CREATED_AT)
VALUES (17, 'tenrent@example.com', 'tenrent', '{noop}pass123', '2026-05-25T00:37:27.614');

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

INSERT INTO user_roles (user_id, role_id) VALUES (10, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (11, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (12, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (13, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (14, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (15, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (16, 3);

INSERT INTO landlord_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (21, 'ALFRED', 'LIM', 'LANDLORD', 11);
INSERT INTO landlord_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (22, 'LENDER', 'MAGNA', 'LAUDE', 16);

INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (11, 'JOHN', 'MAN', 'TENANT', 12);
INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (12, 'JANE', 'WOMAN', 'RENTER', 13);
INSERT INTO tenant_profiles (id, first_name, middle_name, last_name, user_id)
VALUES (13, 'TEN', 'WOW', 'RENT', 17);

INSERT INTO properties (id, landlord_profile_id, name, property_type, property_value, size, description, created_at)
VALUES (60, 21, 'Santa Cruz Apartment', 'Apartments', 4500.00, 240.0, 'Apartment in Santa Cruz.', '2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, name, property_type, property_value, size, description, created_at)
VALUES (61, 21, 'Lynville Apartments', 'Apartments', 5500.00, 120.0, 'Lynville Diamond Apartment in Santa Cruz.', '2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, name, property_type, property_value, size, description, created_at)
VALUES (62, 21, 'TeraVille Apartments', 'Apartments', 6500.00, 120.0, 'TeraVille Coolio Apartment in Laguna.','2026-07-01T00:37:27.614');
INSERT INTO properties (id, landlord_profile_id, name, property_type, property_value, size, description, created_at)
VALUES (63, 22, 'S&U Apartments', 'Apartments', 8000.00, 500.0, 'Studio Apartment in Manila.',  '2026-07-01T00:37:27.614');

INSERT INTO units (id, property_id, unit_type, square_meters, base_rent, currency, status, created_at)
VALUES (70, 60, 'RESIDENTIAL', 80.0, 4500.00, 'Philippine Peso', 'OCCUPIED', '2026-07-01T00:37:27.614');
INSERT INTO units (id, property_id, unit_type, square_meters, base_rent, currency, status, created_at)
VALUES (71, 60, 'RESIDENTIAL', 80.0, 4500.00, 'Philippine Peso', 'OCCUPIED', '2026-07-01T00:37:27.614');
INSERT INTO units (id, property_id, unit_type, square_meters, base_rent, currency, status, created_at)
VALUES (72, 60, 'RESIDENTIAL', 80.0, 4500.00, 'Philippine Peso', 'OCCUPIED', '2026-07-01T00:37:27.614');
INSERT INTO units (id, property_id, unit_type, square_meters, base_rent, currency, status, created_at)
VALUES (73, 63, 'RESIDENTIAL', 100.0, 8000.00, 'Philippine Peso', 'OCCUPIED', '2026-07-01T00:37:27.614');

INSERT INTO residential_units (id, unit_id, unit_number, bedroom_quantity, bathroom_quantity, created_at)
VALUES (80, 70, '1A', 2, 1, '2026-07-01T00:37:27.614');
INSERT INTO residential_units (id, unit_id, unit_number, bedroom_quantity, bathroom_quantity, created_at)
VALUES (81, 71, '1B', 2, 1, '2026-07-01T00:37:27.614');
INSERT INTO residential_units (id, unit_id, unit_number, bedroom_quantity, bathroom_quantity, created_at)
VALUES (82, 72, '1C', 2, 1, '2026-07-01T00:37:27.614');
INSERT INTO residential_units (id, unit_id, unit_number, floor, bedroom_quantity, bathroom_quantity, created_at)
VALUES (83, 73, '4D', 4, 2, 1, '2026-07-01T00:37:27.614');

INSERT INTO invoices (id, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (110, 4500.00, 'PAID', '2026-06-01', 12, 11, '2026-06-01T00:37:27.614');
INSERT INTO invoices (id, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (111,  4500.00, 'PAID', '2026-07-01', 12, 11, '2026-07-01T00:37:27.614');
INSERT INTO invoices (id, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (112, 4500.00, 'PAID', '2026-08-01', 12, 11, '2026-08-01T00:37:27.614');
INSERT INTO invoices (id, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (113,  4500.00, 'PAID', '2026-09-01', 12, 11, '2026-09-01T00:37:27.614');
INSERT INTO invoices (id, invoice_total, invoice_status, due_date, user_id, tenant_profile_id, created_at)
VALUES (114, 5000.00, 'DRAFT', '2026-09-01', 13, 12, '2026-09-01T00:37:27.614');

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
VALUES (90, 100.00, 12, 'CASH', 110, 11, '2026-06-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (91, 5.00, 12, 'CASH',  111, 11, '2026-07-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (92, 250.00, 12, 'CASH',  112, 11, '2026-08-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (93, 110.10, 12, 'CASH',  113, 11, '2026-09-02T00:37:27.614');
INSERT INTO payments (ID, AMOUNT_PAID, USER_ID, PAYMENT_TYPE, INVOICE_ID, TENANT_PROFILE_ID, CREATED_AT)
VALUES (94, 99.99, 13, 'CASH',  114, 12, '2026-09-02T00:37:27.614');

INSERT INTO leases(ID, unit_id, tenant_profile_id, start_date, end_date, rent, deposit, billing_type, status, created_at)
VALUES (40, 70, 11, '2026-06-01', '2027-03-01', 4500.00, 4500.00, 'MONTHLY', 'FINALIZED', '2026-05-01T00:37:27.614');
INSERT INTO leases(ID, unit_id, tenant_profile_id, start_date, end_date, rent, deposit, billing_type, status, created_at)
VALUES (41, 71, 12, '2026-07-01', '2027-04-01', 4500.00, 4500.00, 'MONTHLY', 'FINALIZED', '2026-06-01T00:37:27.614');
INSERT INTO leases(ID, unit_id, tenant_profile_id, start_date, end_date, rent, deposit, billing_type, status, created_at)
VALUES (42, 73, 13, '2026-08-01', '2027-05-01', 8000.00, 8000.00, 'MONTHLY', 'DRAFT', '2026-07-01T00:37:27.614');
INSERT INTO leases(ID, unit_id, tenant_profile_id, start_date, end_date, rent, deposit, billing_type, status, created_at)
VALUES (44, 70, 11, '2027-03-01', '2027-05-01', 4500.00, 4500.00, 'MONTHLY', 'DRAFT', '2026-05-01T00:37:27.614');
