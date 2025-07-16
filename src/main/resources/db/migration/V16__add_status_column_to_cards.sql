-- V16: Add STATUS column to CARDS table

ALTER TABLE cards ADD status VARCHAR2(50);

-- Optional: Set existing rows to 'ACTIVE' by default
UPDATE cards SET status = 'ACTIVE' WHERE status IS NULL;

-- Commit if your environment requires it
COMMIT;
