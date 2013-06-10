DROP TABLE IF EXISTS SI_ACTIVITY_LOG;

CREATE TABLE SI_ACTIVITY_LOG (
  LOG_ID			DECIMAL(38)		NOT NULL PRIMARY KEY,
  TRACKING_ID		VARCHAR(36)		NOT NULL,
  WORKER_TYPE_ID	DECIMAL(3)		NOT NULL,
  ACCESS_ID			VARCHAR(36),
  TMNID				VARCHAR(22),
  LOGIN_ID			VARCHAR(100),
  WORKER_NAME		VARCHAR(50)		NOT NULL,
  ACTIVITY_NAME		VARCHAR(50)		NOT NULL,
  HTTP_STATUS		DECIMAL(3),
  RESULT_CODE		VARCHAR(20),
  RESULT_NAMESPACE	VARCHAR(20),
  TRANS_ID			VARCHAR(36),
  PROCESS_STATE		VARCHAR(20),
  REF_TRANS_ID		VARCHAR(36),
  CREATED_DATE		TIMESTAMP		NOT NULL,
  RESPONSE_DATE		TIMESTAMP		NOT NULL,
  DURATION_TIME		DECIMAL(10),
  DETAIL_INPUT		VARCHAR(4000),
  DETAIL_OUTPUT		VARCHAR(4000)
);

CREATE SEQUENCE SI_ACTIVITY_LOG_SEQ;