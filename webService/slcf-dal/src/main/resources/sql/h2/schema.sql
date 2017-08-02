
DROP TABLE IF EXISTS BAO_T_CUST_INFO CASCADE CONSTRAINTS
;

DROP TABLE IF EXISTS BAO_T_BANK_CARD_INFO CASCADE CONSTRAINTS
;

DROP TABLE IF EXISTS BAO_T_ALLOT_INFO CASCADE CONSTRAINTS
;

DROP TABLE IF EXISTS BAO_T_PRODUCT_TYPE_INFO CASCADE CONSTRAINTS
;

DROP TABLE IF EXISTS BAO_T_ACCOUNT_FLOW_INFO CASCADE CONSTRAINTS
;

DROP TABLE IF EXISTS BAO_T_ACCOUNT_FLOW_DETAIL CASCADE CONSTRAINTS
;

create table BAO_T_CUST_INFO 
(
   ID                   VARCHAR2(50)                   not null,
   LOGIN_NAME           VARCHAR2(150)                  null,
   LOGIN_PASSWORD       VARCHAR2(50)                   null,
   TRADE_PASSWORD       VARCHAR2(50)                   null,
   CREDENTIALS_TYPE     VARCHAR2(50)                   null,
   CREDENTIALS_CODE     VARCHAR2(50)                   null,
   CUST_NAME            VARCHAR2(150)                  null,
   CUST_CODE            VARCHAR2(50)                   null,
   BIRTHDAY             VARCHAR2(8)                    null,
   CUST_LEVEL           VARCHAR2(50)                   null,
   SAFE_LEVEL           VARCHAR2(50)                   null,
   CUST_GENDER          VARCHAR2(8)                    null,
   CUST_SOURCE          VARCHAR(150)                   null,
   CUST_TYPE            VARCHAR(150)                   null,
   NATVICE_PLACE_PROVINCE VARCHAR(150)                   null,
   NATVICE_PLACE_CITY   VARCHAR(150)                   null,
   NATVICE_PLACE_COUNTY VARCHAR(150)                   null,
   NATVICE_PLACE_AREA   VARCHAR(150)                   null,
   COMMUN_ADDRESS       VARCHAR(2000)                  null,
   MOBILE               VARCHAR2(50)                   null,
   EMAIL                VARCHAR2(50)                   null,
   PORTRAIT_PATH        VARCHAR2(255)                  null,
   REAL_NAME_AUTH_COUNT INTEGER                        null,
   IS_LUMPER            VARCHAR(50)                    null,
   MSG_ON_OFF           VARCHAR2(50)                   null,
   ENABLE_STATUS        VARCHAR2(50)                   null,
   RECORD_STATUS        VARCHAR2(50)                   null,
   CREATE_USER          VARCHAR2(50)                   null,
   CREATE_DATE          DATE                           not null,
   LAST_UPDATE_USER     VARCHAR2(50)                   null,
   LAST_UPDATE_DATE     DATE                           null,
   VERSION              INTEGER                        null,
   MEMO                 VARCHAR2(300)                  null,
   constraint PK_BAO_T_CUST_INFO primary key (ID)
);

create table BAO_T_BANK_CARD_INFO 
(
   ID                   VARCHAR2(50)                   not null,
   CUST_ID              VARCHAR2(50)                   null,
   BANK_NAME            VARCHAR2(150)                  null,
   CARD_NO              VARCHAR2(150)                  null,
   OPEN_PROVINCE        VARCHAR2(150)                  null,
   OPEN_CITY            VARCHAR2(150)                  null,
   SUB_BRANCH_NAME      VARCHAR2(150)                  null,
   IS_DEFAULT           VARCHAR2(50)                   null,
   RECORD_STATUS        VARCHAR2(50)                   null,
   CREATE_USER          VARCHAR2(50)                   null,
   CREATE_DATE          DATE                           not null,
   LAST_UPDATE_USER     VARCHAR2(50)                   null,
   LAST_UPDATE_DATE     DATE                           null,
   VERSION              INTEGER                        null,
   MEMO                 VARCHAR2(300)                  null,
   constraint PK_BAO_T_BANK_CARD_INFO primary key (ID)
);

create table BAO_T_ALLOT_INFO 
(
   ID                   VARCHAR(50)                    not null,
   RELATE_TYPE          VARCHAR2(200)                  null,
   RELATE_PRIMARY       VARCHAR2(50)                   null,
   ALLOT_CODE           VARCHAR(50)                    null,
   ALLOT_DATE           DATE                           null,
   ALLOT_AMOUNT         NUMBER(22,8)                   null,
   ALLOT_STATUS         VARCHAR(50)                    null,
   RECORD_STATUS        VARCHAR2(50)                   null,
   CREATE_USER          VARCHAR2(50)                   null,
   CREATE_DATE          DATE                           not null,
   LAST_UPDATE_USER     VARCHAR2(50)                   null,
   LAST_UPDATE_DATE     DATE                           null,
   VERSION              INTEGER                        null,
   MEMO                 VARCHAR2(300)                  null,
   constraint PK_BAO_T_ALLOT_INFO primary key (ID)
);

create table BAO_T_PRODUCT_TYPE_INFO 
(
   ID                   VARCHAR2(50)                   not null,
   TYPE_NAME            VARCHAR2(50)                   not null,
   TYPE_STATUS          VARCHAR2(50)                   null,
   INCOME_TYPE          VARCHAR2(100)                  null,
   INCOME_HANDLE_METHOD VARCHAR2(50)                   null,
   ALREADY_PRE_VALUE    NUMBER(22,8)                   null,
   EXPECT_PRE_VALUE     NUMBER(22,8)                   null,
   ENABLE_STATUS        VARCHAR2(50)                   null,
   RECORD_STATUS        VARCHAR2(50)                   null,
   CREATE_USER          VARCHAR2(50)                   null,
   CREATE_DATE          DATE                           not null,
   LAST_UPDATE_USER     VARCHAR2(50)                   null,
   LAST_UPDATE_DATE     DATE                           null,
   VERSION              INTEGER                        null,
   MEMO                 VARCHAR2(300)                  null,
   constraint PK_BAO_T_PRODUCT_TYPE_INFO primary key (ID)
);

create table BAO_T_ACCOUNT_FLOW_INFO
(
  ID                      VARCHAR2(50) not null,
  CUST_ID                 VARCHAR2(50),
  ACCOUNT_ID              VARCHAR2(50),
  ACCOUNT_TYPE            VARCHAR2(50),
  TRADE_TYPE              VARCHAR2(20),
  REQUEST_NO              VARCHAR2(50),
  OLD_TRADE_NO            VARCHAR2(50),
  TRADE_NO                VARCHAR2(50),
  BANKROLL_FLOW_DIRECTION VARCHAR2(200),
  TRADE_AMOUNT            NUMBER(22,8),
  TRADE_DATE              DATE not null,
  ACCOUNT_TOTAL_AMOUNT    NUMBER(22,8),
  ACCOUNT_FREEZE_AMOUNT   NUMBER(22,8),
  ACCOUNT_AVAILABLE       NUMBER(22,8),
  RECORD_STATUS           VARCHAR2(50),
  CREATE_USER             VARCHAR2(50),
  CREATE_DATE             DATE not null,
  LAST_UPDATE_USER        VARCHAR2(50),
  LAST_UPDATE_DATE        DATE,
  VERSION                 INTEGER,
  MEMO                    VARCHAR2(300),
  constraint PK_BAO_T_ACCOUNT_FLOW_INFO primary key (ID)
);

create table BAO_T_ACCOUNT_FLOW_DETAIL
(
  ID                VARCHAR2(50) not null,
  ACCOUNT_FLOW_ID   VARCHAR2(50),
  SUBJECT_TYPE      VARCHAR2(50),
  SUBJECT_DIRECTION VARCHAR2(50),
  TRADE_AMOUNT      NUMBER(22,8),
  TRADE_DESC        VARCHAR2(2000),
  RECORD_STATUS     VARCHAR2(50),
  CREATE_USER       VARCHAR2(50),
  CREATE_DATE       DATE not null,
  LAST_UPDATE_USER  VARCHAR2(50),
  LAST_UPDATE_DATE  DATE,
  VERSION           INTEGER,
  MEMO              VARCHAR2(300),
  TARGET_ACCOUNT    VARCHAR2(50),
  constraint PK_BAO_T_ACCOUNT_FLOW_DETAIL primary key (ID)
);