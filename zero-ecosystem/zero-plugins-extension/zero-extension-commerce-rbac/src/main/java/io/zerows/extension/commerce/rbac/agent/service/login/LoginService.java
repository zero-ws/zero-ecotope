package io.zerows.extension.commerce.rbac.agent.service.login;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.agent.service.business.UserStub;
import io.zerows.extension.commerce.rbac.atom.ScToken;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.commerce.rbac.exception._80203Exception404UserNotFound;
import io.zerows.extension.commerce.rbac.exception._80204Exception401PasswordWrong;
import io.zerows.extension.commerce.rbac.exception._80220Exception423UserDisabled;
import io.zerows.extension.commerce.rbac.uca.logged.ScUser;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;
import io.zerows.extension.commerce.rbac.util.Sc;
import jakarta.inject.Inject;

import java.util.Objects;

public class LoginService implements LoginStub {

    private static final OLog LOGGER = Ut.Log.security(LoginService.class);
    private final ScClock<ScToken> cache;
    @Inject
    private transient UserStub userStub;

    public LoginService() {
        this.cache = ClockFactory.ofToken(this.getClass());
    }

    @Override
    @SuppressWarnings("all")
    public Future<JsonObject> execute(final String username, final String password) {
        /* Find the user record with username */
        return Sc.lockVerify(username, () -> Ux.Jooq.on(SUserDao.class).<SUser>fetchOneAsync(AuthKey.USER_NAME, username).compose(fetched -> {
            /* Not Found */
            if (Objects.isNull(fetched)) {
                LOGGER.warn(AuthMsg.LOGIN_USER, username);
                return FnVertx.failOut(_80203Exception404UserNotFound.class, username);
            }
            /* Locked User */
            final Boolean isLock = Objects.isNull(fetched.getActive()) ? Boolean.FALSE : fetched.getActive();
            if (!isLock) {
                LOGGER.warn(AuthMsg.LOGIN_LOCKED, username);
                return FnVertx.failOut(_80220Exception423UserDisabled.class, username);
            }
            /* Password Wrong */
            if (Objects.isNull(password) || !password.equals(fetched.getPassword())) {


                // Lock On when password invalid
                LOGGER.warn(AuthMsg.LOGIN_PWD, username);
                return Sc.lockOn(username)
                    .compose(nil -> FnVertx.failOut(_80204Exception401PasswordWrong.class, username));
            }


            // Lock Off when login successfully
            LOGGER.info(AuthMsg.LOGIN_SUCCESS, username);
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
