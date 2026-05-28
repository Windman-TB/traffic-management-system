/*
    Traffic System - Clean Database Schema
    Source: DDL exported from DBeaver, simplified for submission/demo use.
    Target DBMS: Oracle Database

    Notes:
    - This script keeps the core schema only: tables, columns, defaults,
      primary keys, unique constraints, foreign keys, and check constraints.
    - Storage, tablespace, segment, buffer pool, and DBeaver-specific export
      options were removed to make the script easier to read and rerun.
    - Run this file before: 02_sequences_triggers.sql
*/

-- =========================================================
-- 1. EMPLOYEE
-- =========================================================
CREATE TABLE EMPLOYEE (
    EMPLOYEE_ID     VARCHAR2(20),
    FULLNAME        NVARCHAR2(20) NOT NULL,
    PHONENUMBER     VARCHAR2(100) NOT NULL,
    EMAIL           VARCHAR2(30) NOT NULL,
    DATEOFBIRTH     DATE,
    GENDER          NVARCHAR2(10),
    ADDRESS         NVARCHAR2(45),
    SALARY          NUMBER(10,2),
    STATUS          VARCHAR2(30),
    IS_DELETED      NUMBER(1,0) DEFAULT 0,

    CONSTRAINT PK_EMPLOYEE PRIMARY KEY (EMPLOYEE_ID),
    CONSTRAINT CHK_EMPLOYEE_IS_DELETED CHECK (IS_DELETED IN (0, 1))
);

-- =========================================================
-- 2. ACCOUNT
-- =========================================================
CREATE TABLE ACCOUNT (
    ACCOUNT_ID      VARCHAR2(20),
    EMPLOYEE_ID     VARCHAR2(20) NOT NULL,
    USERNAME        VARCHAR2(50) NOT NULL,
    PASSWORD        VARCHAR2(200) NOT NULL,
    STATUS          NVARCHAR2(20) NOT NULL,
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,
    UPDATED_AT      DATE DEFAULT SYSDATE + 7/24,
    IS_DELETED      NUMBER(1,0) DEFAULT 0,

    CONSTRAINT PK_ACCOUNT PRIMARY KEY (ACCOUNT_ID),
    CONSTRAINT UQ_ACCOUNT_USERNAME UNIQUE (USERNAME),
    CONSTRAINT CHK_ACCOUNT_STATUS CHECK (STATUS IN ('ACTIVE', 'LOCKED', 'INACTIVE')),
    CONSTRAINT CHK_ACCOUNT_IS_DELETED CHECK (IS_DELETED IN (0, 1)),
    CONSTRAINT FK_ACCOUNT_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID)
        REFERENCES EMPLOYEE (EMPLOYEE_ID)
);

-- =========================================================
-- 3. ACCOUNT_ROLE
-- =========================================================
CREATE TABLE ACCOUNT_ROLE (
    ACCOUNT_ROLE_ID VARCHAR2(20),
    ACCOUNT_ID      VARCHAR2(20) NOT NULL,
    ROLE_NAME       NVARCHAR2(100) NOT NULL,
    ASSIGNED_AT     DATE DEFAULT SYSDATE + 7/24 NOT NULL,

    CONSTRAINT PK_ACCOUNT_ROLE PRIMARY KEY (ACCOUNT_ROLE_ID),
    CONSTRAINT FK_ACCOUNT_ROLE_ACCOUNT FOREIGN KEY (ACCOUNT_ID)
        REFERENCES ACCOUNT (ACCOUNT_ID)
);

-- =========================================================
-- 4. AREA
-- =========================================================
CREATE TABLE AREA (
    AREA_ID         VARCHAR2(20),
    AREA_NAME       NVARCHAR2(150) NOT NULL,
    AREA_TYPE       NVARCHAR2(30) NOT NULL,
    OLD_PROVINCE    NVARCHAR2(50) NOT NULL,
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,
    IS_DELETED      NUMBER(1,0) DEFAULT 0,
    UPDATED_AT      DATE DEFAULT SYSDATE + 7/24,

    CONSTRAINT PK_AREA PRIMARY KEY (AREA_ID),
    CONSTRAINT CHK_AREA_TYPE CHECK (AREA_TYPE IN (N'Xã', N'Phường', N'Đặc khu')),
    CONSTRAINT CHK_AREA_IS_DELETED CHECK (IS_DELETED IN (0, 1))
);

-- =========================================================
-- 5. AREA_BOUNDARY
-- =========================================================
CREATE TABLE AREA_BOUNDARY (
    AREA_ID         VARCHAR2(20),
    BOUNDARY_WKT    CLOB NOT NULL,

    CONSTRAINT PK_AREA_BOUNDARY PRIMARY KEY (AREA_ID),
    CONSTRAINT FK_AREA_BOUNDARY_AREA FOREIGN KEY (AREA_ID)
        REFERENCES AREA (AREA_ID)
);

-- =========================================================
-- 6. STREET
-- =========================================================
CREATE TABLE STREET (
    STREET_ID       VARCHAR2(20),
    STREET_NAME     NVARCHAR2(150),
    STREET_TYPE     NVARCHAR2(30) NOT NULL,
    ROAD_LEVEL      NUMBER NOT NULL,
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,
    IS_DELETED      NUMBER(1,0) DEFAULT 0,
    UPDATED_AT      DATE DEFAULT SYSDATE + 7/24,

    CONSTRAINT PK_STREET PRIMARY KEY (STREET_ID),
    CONSTRAINT CHK_STREET_LEVEL CHECK (ROAD_LEVEL IN (1, 2, 3, 4)),
    CONSTRAINT CHK_STREET_IS_DELETED CHECK (IS_DELETED IN (0, 1))
);

-- =========================================================
-- 7. NODE
-- =========================================================
CREATE TABLE NODE (
    NODE_ID         VARCHAR2(20),
    LATITUDE        NUMBER(10,7) NOT NULL,
    LONGITUDE       NUMBER(10,7) NOT NULL,
    IS_DELETED      NUMBER(1,0) DEFAULT 0,
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24,
    UPDATED_AT      DATE DEFAULT SYSDATE + 7/24,

    CONSTRAINT PK_NODE PRIMARY KEY (NODE_ID),
    CONSTRAINT CHK_NODE_IS_DELETED CHECK (IS_DELETED IN (0, 1))
);

-- =========================================================
-- 8. SEGMENT
-- =========================================================
CREATE TABLE SEGMENT (
    SEGMENT_ID      VARCHAR2(20),
    STREET_ID       VARCHAR2(20) NOT NULL,
    AREA_ID         VARCHAR2(20),
    START_NODE_ID   VARCHAR2(20) NOT NULL,
    END_NODE_ID     VARCHAR2(20) NOT NULL,
    SEGMENT_LENGTH  NUMBER(10,2),
    MAX_VELOCITY    NUMBER(3,0),
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,
    UPDATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,
    IS_DELETED      NUMBER(1,0) DEFAULT 0,

    CONSTRAINT PK_SEGMENT PRIMARY KEY (SEGMENT_ID),
    CONSTRAINT CHK_SEGMENT_LENGTH CHECK (SEGMENT_LENGTH >= 0),
    CONSTRAINT CHK_MAX_VELOCITY CHECK (MAX_VELOCITY >= 0),
    CONSTRAINT CHK_SEGMENT_IS_DELETED CHECK (IS_DELETED IN (0, 1)),
    CONSTRAINT FK_SEGMENT_STREET FOREIGN KEY (STREET_ID)
        REFERENCES STREET (STREET_ID),
    CONSTRAINT FK_SEGMENT_AREA FOREIGN KEY (AREA_ID)
        REFERENCES AREA (AREA_ID),
    CONSTRAINT FK_SEGMENT_START_NODE FOREIGN KEY (START_NODE_ID)
        REFERENCES NODE (NODE_ID),
    CONSTRAINT FK_SEGMENT_END_NODE FOREIGN KEY (END_NODE_ID)
        REFERENCES NODE (NODE_ID)
);

-- =========================================================
-- 9. TRAFFIC
-- =========================================================
CREATE TABLE TRAFFIC (
    STATUS_ID       VARCHAR2(20),
    SEGMENT_ID      VARCHAR2(20) NOT NULL,
    VELOCITY        NUMBER(5,2) NOT NULL,
    UPDATED_AT      DATE DEFAULT SYSDATE NOT NULL,

    CONSTRAINT PK_TRAFFIC PRIMARY KEY (STATUS_ID),
    CONSTRAINT CHK_TRAFFIC_VELOCITY CHECK (VELOCITY >= 0),
    CONSTRAINT FK_TRAFFIC_SEGMENT FOREIGN KEY (SEGMENT_ID)
        REFERENCES SEGMENT (SEGMENT_ID)
);
-- =========================================================
-- 10. TRAFFIC_SOURCE
-- =========================================================
CREATE TABLE TRAFFIC_SOURCE (
    STATUS_ID    VARCHAR2(20) PRIMARY KEY,
    SEGMENT_ID   VARCHAR2(20) NOT NULL,
    VELOCITY     NUMBER(5,2) NOT NULL,
    RECORDED_AT  DATE NOT NULL
);
-- =========================================================
-- 11. PASSWORD_RESET
-- =========================================================
CREATE TABLE PASSWORD_RESET (
    RESET_ID        VARCHAR2(20),
    ACCOUNT_ID      VARCHAR2(20) NOT NULL,
    OTP_CODE        VARCHAR2(255) NOT NULL,
    CHANNEL         VARCHAR2(10) NOT NULL,
    DESTINATION     VARCHAR2(100) NOT NULL,
    CREATED_AT      DATE DEFAULT (SYSDATE + 7/24) NOT NULL,
    EXPIRED_AT      DATE DEFAULT (SYSDATE + 7/24 + (45/86400)) NOT NULL,
    STATUS          VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,

    CONSTRAINT PK_PASSWORD_RESET PRIMARY KEY (RESET_ID),
    CONSTRAINT CK_PASSWORD_RESET_CHANNEL CHECK (CHANNEL IN ('EMAIL', 'PHONE')),
    CONSTRAINT CK_PASSWORD_RESET_STATUS CHECK (STATUS IN ('ACTIVE', 'USED', 'EXPIRED')),
    CONSTRAINT FK_PASSWORD_RESET_ACCOUNT FOREIGN KEY (ACCOUNT_ID)
        REFERENCES ACCOUNT (ACCOUNT_ID)
);

-- =========================================================
-- 12. SYSTEM_LOG
-- =========================================================
CREATE TABLE SYSTEM_LOG (
    LOG_ID          VARCHAR2(20),
    ACCOUNT_ID      VARCHAR2(20) NOT NULL,
    BEHAVIOUR       NVARCHAR2(100) NOT NULL,
    TARGET_TABLE    VARCHAR2(50) NOT NULL,
    TARGET_ID       VARCHAR2(20) NOT NULL,
    OLD_VALUE       NVARCHAR2(250),
    NEW_VALUE       NVARCHAR2(250),
    LOG_STATUS      VARCHAR2(20) DEFAULT 'SUCCESS',
    CREATED_AT      DATE DEFAULT SYSDATE + 7/24 NOT NULL,

    CONSTRAINT PK_SYSTEM_LOG PRIMARY KEY (LOG_ID),
    CONSTRAINT CHK_SYSTEM_LOG_STATUS CHECK (LOG_STATUS IN ('SUCCESS', 'FAILED'))
);
