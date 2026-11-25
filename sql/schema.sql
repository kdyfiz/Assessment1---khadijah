-- ============================================
-- Cards IT Assessment - MSSQL Schema
-- Database: TESTDB (Local Machine)
-- ============================================
-- Handover notes:
--   - Prefer `docker compose up -d mssql mssql-init` (runs init scripts for you).
--   - JPA `ddl-auto: update` can also create/alter this table from the Card entity.
--   - This script is the source of truth for DBA review / manual setup.
--   - card_type values used by API: DEBIT | CREDIT | PREPAID
--   - status values used by API: ACTIVE | INACTIVE | BLOCKED | EXPIRED
-- ============================================

-- Create database if not exists (run as sa / admin)
IF DB_ID('TESTDB') IS NULL
BEGIN
    CREATE DATABASE TESTDB;
END
GO

USE TESTDB;
GO

-- Maps to com.cardsit.assessment.entity.Card
IF OBJECT_ID('dbo.cards', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.cards (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        card_number     NVARCHAR(19)  NOT NULL UNIQUE,  -- full PAN stored; API masks on response
        cardholder_name NVARCHAR(100) NOT NULL,
        card_type       NVARCHAR(20)  NOT NULL,
        status          NVARCHAR(20)  NOT NULL,
        credit_limit    DECIMAL(15,2) NULL,
        currency        NVARCHAR(3)   NULL,            -- ISO 4217; app defaults to MYR
        created_at      DATETIME2     NOT NULL,
        updated_at      DATETIME2     NULL
    );
END
GO

-- Sample seed data (12 rows) so pagination (10/page) can be demonstrated immediately
IF NOT EXISTS (SELECT 1 FROM dbo.cards)
BEGIN
    INSERT INTO dbo.cards (card_number, cardholder_name, card_type, status, credit_limit, currency, created_at, updated_at)
    VALUES
        ('4111111111111111', 'Ahmad Bin Ali',    'CREDIT', 'ACTIVE',   10000.00, 'MYR', GETDATE(), GETDATE()),
        ('4222222222222222', 'Siti Nurhaliza',   'DEBIT',  'ACTIVE',   NULL,     'MYR', GETDATE(), GETDATE()),
        ('4333333333333333', 'Raj Kumar',        'CREDIT', 'ACTIVE',   25000.00, 'MYR', GETDATE(), GETDATE()),
        ('4444444444444444', 'Lim Wei Jie',      'PREPAID','ACTIVE',   500.00,   'MYR', GETDATE(), GETDATE()),
        ('4555555555555555', 'Fatimah Zahra',    'CREDIT', 'BLOCKED',  15000.00, 'MYR', GETDATE(), GETDATE()),
        ('4666666666666666', 'John Tan',         'DEBIT',  'ACTIVE',   NULL,     'MYR', GETDATE(), GETDATE()),
        ('4777777777777777', 'Nur Aisyah',       'CREDIT', 'ACTIVE',   8000.00,  'MYR', GETDATE(), GETDATE()),
        ('4888888888888888', 'David Lee',        'CREDIT', 'INACTIVE', 12000.00, 'MYR', GETDATE(), GETDATE()),
        ('4999999999999999', 'Priya Sharma',     'PREPAID','ACTIVE',   1000.00,  'MYR', GETDATE(), GETDATE()),
        ('4000000000000000', 'Mohd Hafiz',       'DEBIT',  'ACTIVE',   NULL,     'MYR', GETDATE(), GETDATE()),
        ('4011111111111111', 'Emily Wong',       'CREDIT', 'ACTIVE',   30000.00, 'MYR', GETDATE(), GETDATE()),
        ('4022222222222222', 'Hassan Ibrahim',   'CREDIT', 'ACTIVE',   5000.00,  'MYR', GETDATE(), GETDATE());
END
GO
