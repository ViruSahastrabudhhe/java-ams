CREATE TYPE leaseStatus AS ENUM ('DRAFT', 'TENANTS_SIGNING', 'READY_TO_SIGN', 'FINALIZED', 'EXPIRED');

ALTER TABLE leases
    ALTER COLUMN status DROP DEFAULT,
    ALTER COLUMN status TYPE leaseStatus USING status::leaseStatus,
    ALTER COLUMN billing_type SET DEFAULT 'DRAFT';

CREATE TYPE billingType AS ENUM ('DAILY', 'MONTHLY', 'YEARLY');

ALTER TABLE property_details ADD COLUMN IF NOT EXISTS billing_type billingType DEFAULT 'MONTHLY';
ALTER TABLE leases
    ALTER COLUMN billing_type DROP DEFAULT,
    ALTER COLUMN billing_type TYPE billingType USING billing_type::billingType,
    ALTER COLUMN billing_type SET DEFAULT 'MONTHLY';