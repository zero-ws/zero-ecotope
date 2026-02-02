INSERT INTO `S_ROLE` (`ID`, `CODE`, `NAME`, `COMMENT`, `POWER`, `SIGMA`, `TENANT_ID`, `APP_ID`, `ACTIVE`, `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), 'ROLE.DEVELOPER', '开发人员', '拥有系统开发和维护相关权限的角色。', 0, '${SIGMA}', '${TENANT_ID}', '${APP_ID}', 1, NOW(), 'rachel-momo', NOW(), 'rachel-momo');
INSERT INTO `S_ROLE` (`ID`, `CODE`, `NAME`, `COMMENT`, `POWER`, `SIGMA`, `TENANT_ID`, `APP_ID`, `ACTIVE`, `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), 'ROLE.MANAGER', '管理人员', '拥有团队管理、项目监督等高级权限的角色。', 0, '${SIGMA}', '${TENANT_ID}', '${APP_ID}', 1, NOW(), 'rachel-momo', NOW(), 'rachel-momo');
INSERT INTO `S_ROLE` (`ID`, `CODE`, `NAME`, `COMMENT`, `POWER`, `SIGMA`, `TENANT_ID`, `APP_ID`, `ACTIVE`, `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), 'ROLE.USER', '普通用户', '拥有基础操作权限的角色。', 0, '${SIGMA}', '${TENANT_ID}', '${APP_ID}', 1, NOW(), 'rachel-momo', NOW(), 'rachel-momo');