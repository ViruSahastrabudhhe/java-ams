ALTER TABLE leases DROP COLUMN IF EXISTS property_id;
ALTER TABLE leases ADD COLUMN unit_id BIGINT;
ALTER TABLE leases ADD CONSTRAINT leases_units_id_fkey
    FOREIGN KEY (unit_id) REFERENCES units(id);