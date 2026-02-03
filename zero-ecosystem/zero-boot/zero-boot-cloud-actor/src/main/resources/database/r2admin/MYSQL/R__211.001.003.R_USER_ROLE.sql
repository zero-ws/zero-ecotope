-- 将 r2dev 与 ROLE.DEVELOPER 关联
INSERT INTO `R_USER_ROLE` (`USER_ID`, `ROLE_ID`, `PRIORITY`)
SELECT u.`ID`, r.`ID`, 1
FROM `S_USER` u,
     `S_ROLE` r
WHERE u.`USERNAME` = 'r2dev'
  AND r.`CODE` = 'ROLE.DEVELOPER'
  AND u.`SIGMA` = '${SIGMA}'
  AND u.`TENANT_ID` = '${TENANT_ID}'
  AND r.`SIGMA` = '${SIGMA}'
  AND r.`TENANT_ID` = '${TENANT_ID}';

-- 将 r2admin 与 ROLE.MANAGER 关联
INSERT INTO `R_USER_ROLE` (`USER_ID`, `ROLE_ID`, `PRIORITY`)
SELECT u.`ID`, r.`ID`, 1
FROM `S_USER` u,
     `S_ROLE` r
WHERE u.`USERNAME` = 'r2admin'
  AND r.`CODE` = 'ROLE.MANAGER'
  AND u.`SIGMA` = '${SIGMA}'
  AND u.`TENANT_ID` = '${TENANT_ID}'
  AND r.`SIGMA` = '${SIGMA}'
  AND r.`TENANT_ID` = '${TENANT_ID}';

-- 将 r2user 与 ROLE.USER 关联
INSERT INTO `R_USER_ROLE` (`USER_ID`, `ROLE_ID`, `PRIORITY`)
SELECT u.`ID`, r.`ID`, 1
FROM `S_USER` u,
     `S_ROLE` r
WHERE u.`USERNAME` = 'r2user'
  AND r.`CODE` = 'ROLE.USER'
  AND u.`SIGMA` = '${SIGMA}'
  AND u.`TENANT_ID` = '${TENANT_ID}'
  AND r.`SIGMA` = '${SIGMA}'
  AND r.`TENANT_ID` = '${TENANT_ID}';