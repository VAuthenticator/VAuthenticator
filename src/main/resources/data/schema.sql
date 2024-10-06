CREATE TABLE ROLE
(
    name        varchar(64)  not null PRIMARY KEY,
    description varchar(255) not null DEFAULT ''
);


CREATE TABLE ACCOUNT
(
    account_non_expired     boolean      not null default false,
    account_non_locked      boolean      not null default false,
    credentials_non_expired boolean      not null default false,
    enabled                 boolean      not null default false,

    username                varchar(255) not null primary key,
    password                varchar(255) not null,

    email                   varchar(255) not null unique,
    email_verified          boolean      not null default false,

    first_name              varchar(255) not null default '',
    last_name               varchar(255) not null default '',

    birth_date              date,
    phone                   varchar(30)           default '',
    locale                  varchar(10)           default 'en',
    mandatory_action        varchar(100) not null default 'NO_ACTION'
);

CREATE TABLE ACCOUNT_ROLE
(
    account_username varchar(255) not null,
    role_name        varchar(64)  not null,


    FOREIGN KEY (account_username) REFERENCES ACCOUNT(username) on delete cascade,
    FOREIGN KEY (role_name) REFERENCES ROLE (name) on delete cascade
);

CREATE TABLE oauth2_authorization (
                                      id varchar(100) NOT NULL,
                                      registered_client_id varchar(100) NOT NULL,
                                      principal_name varchar(200) NOT NULL,
                                      authorization_grant_type varchar(100) NOT NULL,
                                      authorized_scopes varchar(1000) DEFAULT NULL,
                                      attributes text DEFAULT NULL,
                                      state varchar(500) DEFAULT NULL,
                                      authorization_code_value text DEFAULT NULL,
                                      authorization_code_issued_at timestamp DEFAULT NULL,
                                      authorization_code_expires_at timestamp DEFAULT NULL,
                                      authorization_code_metadata text DEFAULT NULL,
                                      access_token_value text DEFAULT NULL,
                                      access_token_issued_at timestamp DEFAULT NULL,
                                      access_token_expires_at timestamp DEFAULT NULL,
                                      access_token_metadata text DEFAULT NULL,
                                      access_token_type varchar(100) DEFAULT NULL,
                                      access_token_scopes varchar(1000) DEFAULT NULL,
                                      oidc_id_token_value text DEFAULT NULL,
                                      oidc_id_token_issued_at timestamp DEFAULT NULL,
                                      oidc_id_token_expires_at timestamp DEFAULT NULL,
                                      oidc_id_token_metadata text DEFAULT NULL,
                                      refresh_token_value text DEFAULT NULL,
                                      refresh_token_issued_at timestamp DEFAULT NULL,
                                      refresh_token_expires_at timestamp DEFAULT NULL,
                                      refresh_token_metadata text DEFAULT NULL,
                                      user_code_value text DEFAULT NULL,
                                      user_code_issued_at timestamp DEFAULT NULL,
                                      user_code_expires_at timestamp DEFAULT NULL,
                                      user_code_metadata text DEFAULT NULL,
                                      device_code_value text DEFAULT NULL,
                                      device_code_issued_at timestamp DEFAULT NULL,
                                      device_code_expires_at timestamp DEFAULT NULL,
                                      device_code_metadata text DEFAULT NULL,
                                      PRIMARY KEY (id)
);
