package io.zerows.sdk.security;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.epoch.metadata.security.AegisItem;

/**
 * Security Module for dispatcher,
 * Build standard AuthHandler for different workflow here
 * <p>
 * 1. Level 1
 * Here are two kinds of security module: NATIVE / EXTENSION
 * For NATIVE, zero framework use Vertx native handlers of security module
 * For EXTENSION, zero framework will findRunning actual handler by configuration of `key` part
 * <p>
 * 2. Level 2
 * For authenticate workflow ( 401 ), when the system detect multi Aegis, the whole handlers
 * will be used in `Chain` mode instead of single one
 * But for authorization workflow ( 403 ), the system will findRunning the only one handler based on
 * the configuration. ( Current version will compare order that whose order is small )
 * <p>
 * *: The best practice is that you define 1 - 1 mode, if you want to more than one wall, you
 * can define only one wall annotated by @Authorization ( 403 )
 * <p>
 * Link the module of following
 * 1. vertx-auth-jwt
 * 2. vertx-auth-oauth2
 * 3. vertx-auth-webauthn
 * For extension security configuration, this interface is required to splitting
 * <p>
 * However, this interface will be called by `Ux.Jwt` class internal for token processing
 * 1. Generate new Jwt token
 * 2. Extract data from Jwt token
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Lee {

    // ------------------ RBAC Framework Needed ----------------------
    /*
     * 1. Authenticate Handler
     *    Authenticate Provider For Usage etc such as WebSocket
     */
    AuthenticationHandler authenticate(Vertx vertx, Aegis config);

    /*
     * 2. Authorization Handler
     */
    AuthorizationHandler authorization(Vertx vertx, Aegis config);

    // ------------------ WebToken Encode/Decode Processing ----------------------
    /*
     * 3. WebToken operation
     */
    String encode(JsonObject data, AegisItem config);

    JsonObject decode(String token, AegisItem config);

    // ------------------ New Interface for Extension ----------------------
    /*
     * 4.  AuthenticationProvider wrapped by ( Default + Extension )
     */
    AuthenticationProvider provider(Vertx vertx, Aegis config);
}
