-- 插入开发人员账号 r2dev
INSERT INTO `S_USER` (`ID`, `CODE`, `USERNAME`, `PASSWORD`, `REALNAME`, `ALIAS`, `AVATAR`, `DESCRIPTION`,
                      `MOBILE`, `EMAIL`, `ALIPAY`, `LDAP_ID`, `LDAP_PATH`, `LDAP_MAIL`,
                      `WE_ID`, `WE_OPEN`, `WE_UNION`, `CP_ID`, `CP_OPEN`, `CP_UNION`,
                      `TYPE`, `CATEGORY`, `MODEL_ID`, `MODEL_KEY`,
                      `SIGMA`, `APP_ID`, `TENANT_ID`, `DIRECTORY_ID`, `LANGUAGE`, `ACTIVE`, `METADATA`,
                      `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), NULL, 'r2dev', '$sha512$D88mO6p7WAHmb+6ikgqygpRV7WLb3iH8evu2WXNbUspCTMmOuYaari7h1qgeRu/vE9drl2uH2IGuxmk4M5MCUA',
        'r2dev', 'Developer', NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        '${SIGMA}', '${APP_ID}', '${TENANT_ID}', NULL, 'zh_CN', 1, NULL,
        NOW(), 'rachel-momo', NOW(), 'rachel-momo');

-- 插入管理人员账号 r2admin
INSERT INTO `S_USER` (`ID`, `CODE`, `USERNAME`, `PASSWORD`, `REALNAME`, `ALIAS`, `AVATAR`, `DESCRIPTION`,
                      `MOBILE`, `EMAIL`, `ALIPAY`, `LDAP_ID`, `LDAP_PATH`, `LDAP_MAIL`,
                      `WE_ID`, `WE_OPEN`, `WE_UNION`, `CP_ID`, `CP_OPEN`, `CP_UNION`,
                      `TYPE`, `CATEGORY`, `MODEL_ID`, `MODEL_KEY`,
                      `SIGMA`, `APP_ID`, `TENANT_ID`, `DIRECTORY_ID`, `LANGUAGE`, `ACTIVE`, `METADATA`,
                      `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), NULL, 'r2admin', '$sha512$D88mO6p7WAHmb+6ikgqygpRV7WLb3iH8evu2WXNbUspCTMmOuYaari7h1qgeRu/vE9drl2uH2IGuxmk4M5MCUA',
        'r2admin', 'Administrator', NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        '${SIGMA}', '${APP_ID}', '${TENANT_ID}', NULL, 'zh_CN', 1, NULL,
        NOW(), 'rachel-momo', NOW(), 'rachel-momo');

-- 插入普通用户账号 r2user
INSERT INTO `S_USER` (`ID`, `CODE`, `USERNAME`, `PASSWORD`, `REALNAME`, `ALIAS`, `AVATAR`, `DESCRIPTION`,
                      `MOBILE`, `EMAIL`, `ALIPAY`, `LDAP_ID`, `LDAP_PATH`, `LDAP_MAIL`,
                      `WE_ID`, `WE_OPEN`, `WE_UNION`, `CP_ID`, `CP_OPEN`, `CP_UNION`,
                      `TYPE`, `CATEGORY`, `MODEL_ID`, `MODEL_KEY`,
                      `SIGMA`, `APP_ID`, `TENANT_ID`, `DIRECTORY_ID`, `LANGUAGE`, `ACTIVE`, `METADATA`,
                      `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY`)
VALUES (UUID(), NULL, 'r2user', '$sha512$D88mO6p7WAHmb+6ikgqygpRV7WLb3iH8evu2WXNbUspCTMmOuYaari7h1qgeRu/vE9drl2uH2IGuxmk4M5MCUA',
        'r2user', 'User', NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        '${SIGMA}', '${APP_ID}', '${TENANT_ID}', NULL, 'zh_CN', 1, NULL,
        NOW(), 'rachel-momo', NOW(), 'rachel-momo');