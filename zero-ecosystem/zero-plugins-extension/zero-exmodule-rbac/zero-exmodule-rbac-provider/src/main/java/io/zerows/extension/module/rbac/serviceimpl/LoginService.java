package io.zerows.extension.module.rbac.serviceimpl;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScAuthMsg;
import io.zerows.extension.module.rbac.component.ScClock;
import io.zerows.extension.module.rbac.component.ScClockFactory;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.module.rbac.exception._80220Exception423UserDisabled;
import io.zerows.extension.module.rbac.metadata.ScToken;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.servicespec.LoginStub;
import io.zerows.extension.module.rbac.servicespec.UserStub;
import io.zerows.plugins.security.exception._80203Exception404UserNotFound;
import io.zerows.plugins.security.exception._80204Exception401PasswordWrong;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;

public class LoginService implements LoginStub {

    private static final LogO LOGGER = Ut.Log.security(LoginService.class);
    private final ScClock<ScToken> cache;
    @Inject
    private transient UserStub userStub;

    public LoginService() {
        this.cache = ScClockFactory.ofToken(this.getClass());
    }

    @Override
    @SuppressWarnings("all")
    public Future<JsonObject> execute(final String username, final String password) {
        /* Find the user record with username */
        return Sc.lockVerify(username, () -> DB.on(SUserDao.class).<SUser>fetchOneAsync(ScAuthKey.USER_NAME, username).compose(fetched -> {
            /* Not Found */
            if (Objects.isNull(fetched)) {
                LOGGER.warn(ScAuthMsg.LOGIN_USER, username);
                return FnVertx.failOut(_80203Exception404UserNotFound.class, username);
            }
            /* Locked User */
            final Boolean isLock = Objects.isNull(fetched.getActive()) ? Boolean.FALSE : fetched.getActive();
            if (!isLock) {
                LOGGER.warn(ScAuthMsg.LOGIN_LOCKED, username);
                return FnVertx.failOut(_80220Exception423UserDisabled.class, username);
            }
            /* Password Wrong */
            if (Objects.isNull(password) || !password.equals(fetched.getPassword())) {


                // Lock On when password invalid
                LOGGER.warn(ScAuthMsg.LOGIN_PWD, username);
                return Sc.lockOn(username)
                    .compose(nil -> FnVertx.failOut(_80204Exception401PasswordWrong.class, username));
            }


            // Lock Off when login successfully
            LOGGER.info(ScAuthMsg.LOGIN_SUCCESS, username);
            return Sc.lockOff(username).compose(nil -> Ux.future(fetched));
        }).compose(user -> this.userStub.fetchAuthorized(user)));
    }

    @Override
    public Future<Boolean> logout(final String token, final String habitus) {
        /*
         * Delete WebToken from `ACCESS_TOKEN`
         */
        return this.cache.remove(token).compose(removed -> ScUser.logout(habitus));
    }
}
