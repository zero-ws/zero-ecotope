package io.zerows.extension.commerce.rbac.eon;

import io.zerows.epoch.constant.KName;

public interface AuthKey {
    /**
     * Default state field
     */
    String INITIALIZE_ROLE = "initializeRole";
    String INITIALIZE_PERMISSIONS = "initializePermissions";
    String DEFAULT_RESOURCE_ID = "91a78ce8-30c7-4894-b235-730eb3e61255";
    String PERMISSIONS = "permissions";

    String DEFAULT = "DEFAULT";
    String OWNER_TYPE_ROLE = "ROLE";
    String STATE = "state";
    String SCOPE = "scope";
    String AUTH_CODE = "code";

    String USER_NAME = "username";
    String PASSWORD = "password";
    String PRIORITY = "priority";
    String CAPTCHA_IMAGE = "captcha";
    /**
     * Request Parameters
     */
    String CLIENT_ID = "client_id";
    String CLIENT_SECRET = "client_secret";
    String RESPONSE_TYPE = "response_type";

    String ACCESS_TOKEN = KName.ACCESS_TOKEN;
    String REFRESH_TOKEN = "refresh_token";
    String IAT = "iat";
    /**
     * Pojo Field
     */
    String F_USER_ID = "userId";
    String F_ROLE_ID = "roleId";
    String F_PERM_ID = "permId";
    String F_GROUP_ID = "groupId";
    String F_PARENT_ID = "parentId";

    String F_CLIENT_ID = "clientId";
    String F_CLIENT_SECRET = "clientSecret";
    String F_GRANT_TYPE = "grantType";

    /**
     * Authorization Workflow
     */
    String F_URI = "uri";
    String F_URI_REQUEST = "requestUri";
    String F_METHOD = "method";
    String F_METADATA = "metadata";
    String F_HEADERS = "headers";

    /**
     * Could not configure authorization pool
     */
    String PROFILE_PERM = "PERM";
    String PROFILE_ROLE = "ROLE";

    interface Acl {
        /*
         * Five constant for checking
         */
        String BEFORE_PROJECTION = "BEFORE_PROJECTION";
        String BEFORE_CRITERIA = "BEFORE_CRITERIA";
        String AFTER_RECORD = "AFTER_RECORD";
        String AFTER_ROWS = "AFTER_ROWS";
        String AFTER_COLLECTION = "AFTER_COLLECTION";
    }
}
