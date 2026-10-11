ALTER TABLE leases DROP COLUMN IF EXISTS landlord_profile_id;
ALTER TABLE leases ADD COLUMN tenant_profile_id BIGINT;
ALTER TABLE leases ADD CONSTRAINT leases_tenant_profiles_id_fkey
    FOREIGN KEY (tenant_profile_id) REFERENCES TENANT_PROFILES(id);