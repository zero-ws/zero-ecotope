package io.zerows.sdk.security;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.EmSecure;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Acl extends Serializable {
    /*
     * Acl configuration to findRunning
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

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface View extends Serializable {
        /* Get name */
        String field();

        /* Depend set */
        View depend(boolean depend);

        /* If depend */
        boolean isDepend();

        /* If access */
        boolean isAccess();

        /* If edition */
        boolean isEdit();

        /* If readonly */
        boolean isReadOnly();

        /* Complex Process */
        default ConcurrentMap<String, View> complexMap() {
            return new ConcurrentHashMap<>();
        }
    }
}
