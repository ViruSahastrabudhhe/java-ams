ALTER TABLE properties DROP COLUMN property_details_id;
ALTER TABLE property_details ADD COLUMN property_id BIGINT;
ALTER TABLE property_details ADD CONSTRAINT property_details_properties_id_fkey
    FOREIGN KEY (property_id) REFERENCES properties(id);