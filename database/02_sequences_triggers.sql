/*
    Traffic System - Sequences and Triggers
    Source: DDL exported from DBeaver, simplified for submission/demo use.
    Target DBMS: Oracle Database

    Notes:
    - Run after: 01_schema_clean.sql
    - The provided DDL only showed the SYSTEM_LOG trigger. Therefore, this
      script creates SEQ_SYSTEM_LOG and TRG_SYSTEM_LOG_ID.
*/

-- =========================================================
-- 1. Sequence for SYSTEM_LOG.LOG_ID
-- =========================================================
CREATE SEQUENCE SEQ_SYSTEM_LOG
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- =========================================================
-- 2. Trigger: auto-generate LOG_ID as LOG001, LOG002, ...
-- =========================================================
CREATE OR REPLACE TRIGGER TRG_SYSTEM_LOG_ID
BEFORE INSERT ON SYSTEM_LOG
FOR EACH ROW
BEGIN
    IF :NEW.LOG_ID IS NULL THEN
        :NEW.LOG_ID := 'LOG' || LPAD(SEQ_SYSTEM_LOG.NEXTVAL, 3, '0');
    END IF;
END;
/

ALTER TRIGGER TRG_SYSTEM_LOG_ID ENABLE;
