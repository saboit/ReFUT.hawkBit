create table sp_user
(
    id               bigint      not null auto_increment,
    created_at       bigint,
    created_by       varchar(64),
    last_modified_at bigint,
    last_modified_by varchar(64),
    optlock_revision bigint,
    tenant           varchar(40) not null,
    username         varchar(64) not null,
    email            varchar(128),
    password         varchar(64),
    first_name       varchar(64),
    last_name        varchar(64),
    login_hash       varchar(20),
    user_role_id     bigint not null,
    primary key (id)
);

alter table sp_user
    add constraint uk_user_username unique (username, tenant);

alter table sp_user
    add index sp_idx_user_username (username);

alter table sp_user
    add constraint fk_user_user_role
        foreign key (user_role_id)
            references sp_user_role (id)
            on update cascade on delete restrict;
