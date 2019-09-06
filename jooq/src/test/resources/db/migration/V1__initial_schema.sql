CREATE TABLE test_user (
  user_id bigint NOT NULL,
  user_name varchar(32)  NOT NULL,
  payload jsonb,
  PRIMARY KEY (user_id)
);

CREATE TABLE test_group (
  group_id bigint NOT NULL,
  group_name varchar(32)  NOT NULL,
  payload jsonb,
  PRIMARY KEY (group_id)
);

CREATE TABLE test_group2 (
  group_id bigint NOT NULL,
  group_name varchar(32)  NOT NULL,
  payload jsonb,
  PRIMARY KEY (group_id)
);

CREATE TABLE test_user_group (
  user_id bigint NOT NULL,
  group_id bigint NOT NULL,
  memo text,
  PRIMARY KEY (user_id, group_id),

  FOREIGN KEY (user_id) REFERENCES test_user(user_id)
    ON DELETE CASCADE,
  FOREIGN KEY (group_id) REFERENCES test_group(group_id)
    ON DELETE CASCADE
);

CREATE VIEW user_to_users AS
  SELECT test_user.user_id, test_user.user_name, test_user.payload as user_payload,
         test_group.group_id, test_group.group_name, test_group.payload as group_payload
  FROM test_user_group
    INNER JOIN test_user ON test_user_group.user_id = test_user.user_id
    INNER JOIN test_group ON test_user_group.group_id = test_group.group_id;
