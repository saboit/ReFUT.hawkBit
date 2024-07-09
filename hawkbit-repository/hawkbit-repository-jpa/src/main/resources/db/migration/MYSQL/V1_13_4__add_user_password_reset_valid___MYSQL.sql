alter table sp_user
    add password_reset_hash varchar(20),
    add password_reset_valid bigint;
