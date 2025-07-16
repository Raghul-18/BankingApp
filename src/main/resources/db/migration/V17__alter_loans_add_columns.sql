-- V17: Add columns to LOANS table for interest, payment, balance, etc.
ALTER TABLE loans ADD interest_rate NUMBER(5,2);
ALTER TABLE loans ADD monthly_payment NUMBER(15,2);
ALTER TABLE loans ADD remaining_balance NUMBER(15,2);
ALTER TABLE loans ADD paid_percentage NUMBER(3);

-- Optional: Set default values if needed
UPDATE loans SET interest_rate = 6.5 WHERE interest_rate IS NULL;
UPDATE loans SET monthly_payment = 15000 WHERE monthly_payment IS NULL;
UPDATE loans SET remaining_balance = loan_amount WHERE remaining_balance IS NULL;
UPDATE loans SET paid_percentage = 0 WHERE paid_percentage IS NULL;

COMMIT;
