create table sp_user_role
(
    id               bigint      not null auto_increment,
    created_at       bigint,
    created_by       varchar(64),
    last_modified_at bigint,
    last_modified_by varchar(64),
    optlock_revision bigint,
    tenant           varchar(40) not null,
    description      varchar(512),
    name             varchar(64) not null,
    primary key (id)
);

alter table sp_user_role
    add constraint uk_user_role_name unique (name, tenant);

create table sp_user_role_privilege
(
    user_role_id     bigint,
    privilege        varchar(64),
    primary key (user_role_id, privilege)
);

alter table sp_user_role_privilege
    add constraint fk_user_role_privilege_user_role
        foreign key (user_role_id)
            references sp_user_role (id)
            on update cascade on delete cascade;

