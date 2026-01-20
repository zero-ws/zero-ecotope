package io.zerows.plugins.oauth2;

/**
 * OAuth2 专用常量池
 * <p>
 * 严格遵循以下标准：
 * - RFC 6749: The OAuth 2.0 Authorization Framework
 * - RFC 7636: Proof Key for Code Exchange (PKCE)
 * - RFC 7519: JSON Web Token (JWT)
 * - RFC 8628: OAuth 2.0 Device Authorization Grant (Device Flow)
 * - RFC 8693: OAuth 2.0 Token Exchange
 * - RFC 7662: OAuth 2.0 Token Introspection
 */
public interface OAuth2Constant {

    String K_PREFIX = "[ PLUG ] ( OAuth2 )";

    // ---------------------------------------------------------
    // 1. 核心标准参数 (RFC 6749 Core)
    // ---------------------------------------------------------
    String GRANT_TYPE = "grant_type";
    String RESPONSE_TYPE = "response_type";         // [新增] 授权端点响应类型 (code, token 等)
    String CLIENT_ID = "client_id";
    String CLIENT_SECRET = "client_secret";
    String SCOPE = "scope";
    String STATE = "state";
    String CODE = "code";

    // 令牌端点响应
    String ACCESS_TOKEN = "access_token";
    String TOKEN_TYPE = "token_type";
    String EXPIRES_IN = "expires_in";
    String REFRESH_TOKEN = "refresh_token";
    String REDIRECT_URI = "redirect_uri";

    // 密码模式 (Legacy)
    String USERNAME = "username";                   // [新增] Password 模式用户名
    String PASSWORD = "password";                   // [新增] Password 模式密码

    // ---------------------------------------------------------
    // 2. 错误处理参数 (RFC 6749 Error)
    // ---------------------------------------------------------
    String ERROR = "error";                         // [新增] 错误码
    String ERROR_DESCRIPTION = "error_description"; // [新增] 错误描述
    String ERROR_URI = "error_uri";                 // [新增] 错误文档 URI

    // ---------------------------------------------------------
    // 3. 令牌内省与撤销 (Introspection & Revocation)
    // ---------------------------------------------------------
    String TOKEN = "token";                         // [新增] 内省/撤销时的令牌参数
    String TOKEN_TYPE_HINT = "token_type_hint";     // [新增] 令牌类型提示 (access_token / refresh_token)
    String ACTIVE = "active";                       // [新增] 内省响应状态

    // ---------------------------------------------------------
    // 4. 客户端断言与注册 (Client Assertion & Registration)
    // ---------------------------------------------------------
    String CLIENT_ASSERTION_TYPE = "client_assertion_type"; // [新增]
    String CLIENT_ASSERTION = "client_assertion";           // [新增]
    String ASSERTION = "assertion";                         // [新增]
    String REGISTRATION_ID = "registration_id";             // [新增]

    // ---------------------------------------------------------
    // 5. PKCE 扩展参数 (RFC 7636)
    // ---------------------------------------------------------
    String CODE_VERIFIER = "code_verifier";
    String CODE_CHALLENGE = "code_challenge";
    String CODE_CHALLENGE_METHOD = "code_challenge_method";

    // ---------------------------------------------------------
    // 6. 设备授权模式参数 (RFC 8628 Device Flow)
    // ---------------------------------------------------------
    String DEVICE_CODE = "device_code";             // [新增] 设备码
    String USER_CODE = "user_code";                 // [新增] 用户码 (显示给用户输入的短码)
    String VERIFICATION_URI = "verification_uri";   // [新增] 验证 URI
    String VERIFICATION_URI_COMPLETE = "verification_uri_complete"; // [新增] 带参验证 URI
    String INTERVAL = "interval";                   // [新增] 轮询间隔

    // ---------------------------------------------------------
    // 7. 令牌交换参数 (RFC 8693 Token Exchange)
    // ---------------------------------------------------------
    String AUDIENCE = "audience";                   // [新增] 目标受众
    String RESOURCE = "resource";                   // [新增] 目标资源
    String REQUESTED_TOKEN_TYPE = "requested_token_type"; // [新增]
    String ISSUED_TOKEN_TYPE = "issued_token_type";       // [新增]
    String SUBJECT_TOKEN = "subject_token";               // [新增]
    String SUBJECT_TOKEN_TYPE = "subject_token_type";     // [新增]
    String ACTOR_TOKEN = "actor_token";                   // [新增]
    String ACTOR_TOKEN_TYPE = "actor_token_type";         // [新增]

    // ---------------------------------------------------------
    // 8. JWT / JWK 专用键 (RFC 7515 / 7519)
    // ---------------------------------------------------------
    String KID = "kid";
    String ALG = "alg";
    String ISS = "iss";
    String SUB = "sub";
    String AUD = "aud"; // 注意与 Token Exchange 的 AUDIENCE 参数区分，这里通常是 JWT Claims key
    String IAT = "iat";
    String EXP = "exp";
    String JTI = "jti";
    String NBF = "nbf"; // [新增] Not Before

    // ---------------------------------------------------------
    // 9. 标准错误响应码 (Error Codes)
    // ---------------------------------------------------------
    interface Error {
        String INVALID_REQUEST = "invalid_request";
        String INVALID_CLIENT = "invalid_client";
        String INVALID_GRANT = "invalid_grant";
        String UNAUTHORIZED_CLIENT = "unauthorized_client";
        String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
        String INVALID_SCOPE = "invalid_scope";
        String ACCESS_DENIED = "access_denied";
        String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type"; // [新增]
        String SERVER_ERROR = "server_error";                           // [新增]
        String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";     // [新增]
    }

    // ---------------------------------------------------------
    // 10. 数据库字段对应关系 (Database Mapping POJO)
    // ---------------------------------------------------------
    interface Field {
        String CLIENT_ID = "clientId";
        String CLIENT_SECRET = "clientSecret";
        String GRANT_TYPES = "authorizationGrantTypes";
        String REDIRECT_URIS = "redirectUris";
        String SCOPES = "scopes";
    }
}