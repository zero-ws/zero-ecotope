package io.zerows.extension.commerce.rbac.eon;

/*
 * Rbac plugin/rbac/configuration.json configuration data.
 * It's used in current module only
 */
public interface ScConstant {

    String BUNDLE_SYMBOLIC_NAME = "zero-extension-commerce-rbac";


    // 权限池
    String POOL_PERMISSIONS = "POOL_PERMISSIONS";
    // 资源池
    String POOL_RESOURCES = "POOL_RESOURCES";
    // 管理专用池
    String POOL_ADMIN = "POOL_ADMIN";
    // 登录限制
    String POOL_LIMITATION = "POOL_LIMITATION";

    // ----------------- 统一归口部分的池
    // 图片验证码
    String POOL_CODE_IMAGE = "POOL_CODE_IMAGE";
    // 短信验证码
    String POOL_CODE_SMS = "POOL_CODE_SMS";
    // 授权码池
    String POOL_CODE = "POOL_CODE";
    // 令牌池
    String POOL_TOKEN = "POOL_TOKEN";
}
