package io.zerows.sdk.security;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.EmSecure;

import java.io.Serializable;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Acl extends Serializable {
    /*
     * Acl configuration to get
     * seeker configuration
     */
    Acl config(JsonObject config);

    JsonObject config();

    /*
     * projection calculation
     */
    Set<String> aclVisible();

    /*
     * JsonObject calculation
     */
    JsonObject acl();

    /*
     * Phase
     */
    EmSecure.ActPhase phase();

    /*
     * Record bind
     */
    void bind(JsonObject record);
}
