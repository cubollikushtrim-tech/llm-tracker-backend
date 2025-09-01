-- Allow null values for customer_id in users table to support SUPERADMIN users
ALTER TABLE users ALTER COLUMN customer_id DROP NOT NULL;
