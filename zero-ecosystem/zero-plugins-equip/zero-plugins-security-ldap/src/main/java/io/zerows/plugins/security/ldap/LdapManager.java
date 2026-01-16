package io.zerows.plugins.security.ldap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.ldap.LdapAuthentication;
import io.vertx.ext.auth.ldap.LdapAuthenticationOptions;
import io.zerows.support.Ut;
import lombok.Getter;

import java.util.Objects;

class LdapManager {

    private static final Cc<Integer, LdapManager> CC_MANAGER = Cc.open();

    private final JsonObject options = new JsonObject();
    private final Vertx vertxRef;

    @Getter
    private AuthenticationProvider provider;

    private LdapManager(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    static LdapManager of(final Vertx vertxRef) {
        return CC_MANAGER.pick(() -> new LdapManager(vertxRef), System.identityHashCode(vertxRef));
    }

    public AuthenticationProvider createProvider(final JsonObject options) {
        if (Objects.isNull(this.provider)) {
            final JsonArray queries = Ut.valueJArray(options, "authenticationQuery");
            final ChainAuth chainAuth = ChainAuth.any();
            Ut.itJString(queries).forEach(item -> {
                final JsonObject queryJ = options.copy();
                queryJ.put("authenticationQuery", item);
                final LdapAuthenticationOptions queryOptions = new LdapAuthenticationOptions(queryJ);
                final AuthenticationProvider queryProvider = LdapAuthentication.create(this.vertxRef, queryOptions);
                chainAuth.add(queryProvider);
            });
            this.provider = chainAuth;
            this.options.mergeIn(options, true);
        }
        return this.provider;
    }
}
