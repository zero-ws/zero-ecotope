package io.zerows.extension.module.rbac.serviceimpl;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScAuthMsg;
import io.zerows.extension.module.rbac.domain.tables.daos.OUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.OUser;
import io.zerows.extension.module.rbac.exception._80202Exception401CodeGeneration;
import io.zerows.extension.module.rbac.servicespec.AuthStub;
import io.zerows.extension.module.rbac.servicespec.CodeStub;
import io.zerows.extension.module.rbac.servicespec.LoginStub;
import io.zerows.extension.module.rbac.servicespec.TokenStub;
import jakarta.inject.Inject;

import java.util.Objects;

public class AuthService implements AuthStub {

    @Inject
    private transient CodeStub codeStub;
    @Inject
    private transient LoginStub loginStub;
    @Inject
    private transient TokenStub tokenStub;

    @Override
    @SuppressWarnings("all")
    public Future<JsonObject> authorize(final JsonObject filters) {
        this.logger().info(ScAuthMsg.CODE_FILTER, filters.encode());
        return DB.on(OUserDao.class).<OUser>fetchOneAsync(filters).compose(item -> {
            if (Objects.isNull(item)) {
                // Could not identify OUser record, error throw.
                final String clientId = filters.getString(ScAuthKey.F_CLIENT_ID);
                final String clientSecret = filters.getString(ScAuthKey.F_CLIENT_SECRET);
                return FnVertx.failOut(_80202Exception401CodeGeneration.class, clientId, clientSecret);
            } else {
                // Provide correct parameters, OUser record existing.
                return this.codeStub.authorize(item.getClientId());
            }
        });
    }

    @Override
    public Future<JsonObject> token(final JsonObject params, final Session session) {
        final String code = params.getString(ScAuthKey.AUTH_CODE);
        final String clientId = params.getString(ScAuthKey.CLIENT_ID);
        this.logger().info(ScAuthMsg.CODE_VERIFY, clientId, code);
        return this.codeStub.verify(clientId, code)
            .compose(verified -> this.tokenStub.execute(verified, session));
    }


    @Override
    public Future<JsonObject> login(final JsonObject params) {
        final String username = params.getString(ScAuthKey.USER_NAME);
        final String password = params.getString(ScAuthKey.PASSWORD);
        this.logger().info(ScAuthMsg.LOGIN_INPUT, username);
        return this.loginStub.execute(username, password);
    }
}
