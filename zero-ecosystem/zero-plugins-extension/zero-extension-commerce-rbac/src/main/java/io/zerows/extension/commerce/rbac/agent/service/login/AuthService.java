package io.zerows.extension.commerce.rbac.agent.service.login;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.extension.commerce.rbac.agent.service.login.pre.CodeStub;
import io.zerows.extension.commerce.rbac.domain.tables.daos.OUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.OUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.exception._80202Exception401CodeGeneration;
import io.zerows.epoch.database.DB;
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
        this.logger().info(AuthMsg.CODE_FILTER, filters.encode());
        return DB.on(OUserDao.class).<OUser>fetchOneAsync(filters).compose(item -> {
            if (Objects.isNull(item)) {
                // Could not identify OUser record, error throw.
                final String clientId = filters.getString(AuthKey.F_CLIENT_ID);
                final String clientSecret = filters.getString(AuthKey.F_CLIENT_SECRET);
                return FnVertx.failOut(_80202Exception401CodeGeneration.class, clientId, clientSecret);
            } else {
                // Provide correct parameters, OUser record existing.
                return this.codeStub.authorize(item.getClientId());
            }
        });
    }

    @Override
    public Future<JsonObject> token(final JsonObject params, final Session session) {
        final String code = params.getString(AuthKey.AUTH_CODE);
        final String clientId = params.getString(AuthKey.CLIENT_ID);
        this.logger().info(AuthMsg.CODE_VERIFY, clientId, code);
        return this.codeStub.verify(clientId, code)
            .compose(verified -> this.tokenStub.execute(verified, session));
    }


    @Override
    public Future<JsonObject> login(final JsonObject params) {
        final String username = params.getString(AuthKey.USER_NAME);
        final String password = params.getString(AuthKey.PASSWORD);
        this.logger().info(AuthMsg.LOGIN_INPUT, username);
        return this.loginStub.execute(username, password);
    }
}
