DROP TABLE IF EXISTS property_details;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS name VARCHAR;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS property_type VARCHAR NOT NULL default '';
ALTER TABLE properties ADD COLUMN IF NOT EXISTS property_value NUMERIC DEFAULT 0.0;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS size DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS description TEXT NOT NULL default '';

CREATE TYPE unitType AS ENUM ('RESIDENTIAL', 'COMMERCIAL');
CREATE TYPE unitStatus AS ENUM ('VACANT', 'OCCUPIED', 'UNAVAILABLE', 'MAINTENANCE');

CREATE TABLE IF NOT EXISTS units (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    property_id         BIGINT references properties(id),
    unit_type           unitType NOT NULL,
    square_meters       NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
    base_rent           NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
    currency            VARCHAR(50) NOT NULL DEFAULT 'Philippine Peso',
    status              unitStatus NOT NULL
);

CREATE TABLE IF NOT EXISTS residential_units (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    unit_id             BIGINT references units(id),
    unit_number         VARCHAR(50) NOT NULL,
    floor               INT NOT NULL,
    bedroom_quantity    INT NOT NULL,
    bathroom_quantity   INT NOT NULL
);

ALTER TABLE IF EXISTS tenant_profiles ADD COLUMN phone_number VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE IF EXISTS landlord_profiles ADD COLUMN phone_number VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE IF EXISTS invoices
    ALTER COLUMN INVOICE_TOTAL TYPE NUMERIC(12, 2);
ALTER TABLE IF EXISTS invoice_lines
    ALTER COLUMN unit_price TYPE NUMERIC(12, 2);
ALTER TABLE IF EXISTS invoice_lines
    ALTER COLUMN total_price TYPE NUMERIC(12, 2);
ALTER TABLE IF EXISTS payments
    ALTER COLUMN amount_paid TYPE NUMERIC(12, 2);
ALTER TABLE IF EXISTS payments
    ALTER COLUMN amount_change TYPE NUMERIC(12, 2);
