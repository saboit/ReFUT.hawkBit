-- MySQL script to create a new tenant
--
-- Creates a tenant, necessary master data, user "admin" with password "admin" and administrator role.

set @tenant = 'COFFEE';
set @admin_email = 'admin@example.com';
set @use_mfa = 0;

set @now = CAST(1000*UNIX_TIMESTAMP(current_timestamp(4)) AS UNSIGNED INTEGER);

INSERT INTO `sp_distribution_set_type` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`, `colour`, `deleted`, `type_key`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Default type with Firmware/OS and optional app(s).',	'OS with app(s)',	NULL,	CONV('0', 2, 10) + 0,	'os_app');
set @ds_os_app = LAST_INSERT_ID();

INSERT INTO `sp_distribution_set_type` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`, `colour`, `deleted`, `type_key`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Default type with Firmware/OS only.',	'OS only',	NULL,	CONV('0', 2, 10) + 0,	'os');
set @ds_os = LAST_INSERT_ID();

INSERT INTO `sp_distribution_set_type` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`, `colour`, `deleted`, `type_key`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Default type with app(s) only.',	'App(s) only',	NULL,	CONV('0', 2, 10) + 0,	'app');
set @ds_app = LAST_INSERT_ID();

INSERT INTO `sp_tenant` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `default_ds_type`)
VALUES (NULL,	@now,	'system',	@now,	'system',	1,	@tenant, @ds_app);

INSERT INTO `sp_software_module_type` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`, `colour`, `deleted`, `type_key`, `max_ds_assignments`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Core firmware or operationg system',	'OS',	NULL,	CONV('0', 2, 10) + 0,	'os',	1);
set @sm_os = LAST_INSERT_ID();

INSERT INTO `sp_software_module_type` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`, `colour`, `deleted`, `type_key`, `max_ds_assignments`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Application Addons',	'Application',	NULL,	CONV('0', 2, 10) + 0,	'application',	2147483647);
set @sm_app = LAST_INSERT_ID();

INSERT INTO `sp_ds_type_element` (`mandatory`, `distribution_set_type`, `software_module_type`) VALUES
(1,	@ds_os_app,	@sm_os),
(0,	@ds_os_app,	@sm_app),
(1,	@ds_os,	@sm_os),
(1,	@ds_app, @sm_app);

INSERT INTO `sp_user_role` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `description`, `name`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'Administrator',	'admin');

set @userrole_id = LAST_INSERT_ID();

INSERT INTO `sp_user_role_privilege` (`user_role_id`, `privilege`) VALUES
(@userrole_id,	'APPROVE_ROLLOUT'),
(@userrole_id,	'CREATE_REPOSITORY'),
(@userrole_id,	'CREATE_ROLLOUT'),
(@userrole_id,	'CREATE_TARGET'),
(@userrole_id,	'CREATE_USER_MNG'),
(@userrole_id,	'DELETE_REPOSITORY'),
(@userrole_id,	'DELETE_ROLLOUT'),
(@userrole_id,	'DELETE_TARGET'),
(@userrole_id,	'DELETE_USER_MNG'),
(@userrole_id,	'DOWNLOAD_REPOSITORY_ARTIFACT'),
(@userrole_id,	'HANDLE_ROLLOUT'),
(@userrole_id,	'READ_REPOSITORY'),
(@userrole_id,	'READ_ROLLOUT'),
(@userrole_id,	'READ_TARGET'),
(@userrole_id,	'READ_TARGET_SECURITY_TOKEN'),
(@userrole_id,	'READ_USER_MNG'),
(@userrole_id,	'TENANT_CONFIGURATION'),
(@userrole_id,	'UPDATE_REPOSITORY'),
(@userrole_id,	'UPDATE_ROLLOUT'),
(@userrole_id,	'UPDATE_TARGET'),
(@userrole_id,	'UPDATE_USER_MNG');

INSERT INTO `sp_user` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `username`, `email`, `password`, `first_name`, `last_name`, `login_hash`, `user_role_id`, `login_hash_valid`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'admin',	@admin_email,	'{bcrypt}$2a$10$2Ap6zRQa7pJB3XnY68x1qOZUeo7Vw4J5eYEl.CQ5HbzxwzYnGRFOi',	'Main',	'Administrator',	NULL,	@userrole_id,	NULL);

INSERT INTO `sp_tenant_configuration` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `conf_key`, `conf_value`) VALUES
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'action.cleanup.enabled',	'false'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'repository.actions.autoclose.enabled',	'false'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'rollout.approval.enabled',	'false'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'authentication.targettoken.enabled',	'true'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'authentication.gatewaytoken.enabled',	'false'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'anonymous.download.enabled',	'false'),
(NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'authentication.header.enabled',	'false');

-- only if MFA is requested
INSERT INTO `sp_tenant_configuration` (`id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `optlock_revision`, `tenant`, `conf_key`, `conf_value`)
SELECT NULL,	@now,	'system',	@now,	'system',	1,	@tenant,	'authentication.login.mfa',	'email'
WHERE @use_mfa = 1;
