-- Source - https://stackoverflow.com/a/9556527
-- Posted by E.J. Brennan, modified by community. See post 'Timeline' for change history
-- Retrieved 2026-10-06, License - CC BY-SA 3.0

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ language 'plpgsql';

-- Source - https://stackoverflow.com/a/9556527
-- Posted by E.J. Brennan, modified by community. See post 'Timeline' for change history
-- Retrieved 2026-10-06, License - CC BY-SA 3.0

CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON USERS FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON TENANT_PROFILES FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON ADDRESSES FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON LANDLORD_PROFILES FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON INVOICES FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON INVOICE_LINES FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
CREATE TRIGGER update_updated_at_timestamp BEFORE UPDATE ON PAYMENTS FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();