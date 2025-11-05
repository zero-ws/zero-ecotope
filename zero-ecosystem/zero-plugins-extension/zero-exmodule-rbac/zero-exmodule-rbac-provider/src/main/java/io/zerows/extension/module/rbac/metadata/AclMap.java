package io.zerows.extension.module.rbac.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.sdk.security.Acl;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AclMap extends AclBase {

    private final ConcurrentMap<String, Acl.View> map = new ConcurrentHashMap<>();

    public AclMap(final String field, final boolean view, final JsonObject config) {
        super(field, view);
        Ut.<JsonObject>itJObject(config, (value, childField) -> {
            final Acl.View item = new AclItem(childField, value);
            this.map.put(childField, item);
        });
    }

    @Override
    public boolean isAccess() {
        return true;    // Must be access
    }

    @Override
    public ConcurrentMap<String, Acl.View> complexMap() {
        return this.map;
    }
}
