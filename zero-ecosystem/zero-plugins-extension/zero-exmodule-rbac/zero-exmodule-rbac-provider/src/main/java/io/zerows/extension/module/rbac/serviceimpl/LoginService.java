package io.zerows.extension.module.rbac.serviceimpl;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.module.rbac.exception._80220Exception423UserDisabled;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.servicespec.LoginStub;
import io.zerows.extension.module.rbac.servicespec.UserStub;
import io.zerows.plugins.security.exception._80203Exception404UserNotFound;
import io.zerows.plugins.security.exception._80204Exception401PasswordWrong;
import io.zerows.program.Ux;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class LoginService implements LoginStub {

    @Inject
    private transient UserStub userStub;

    public LoginService() {
    }

    @Override
    public Future<JsonObject> execute(final String username, final String password) {
        /* Find the user record with username */
        return Sc.lockVerify(username, () -> DB.on(SUserDao.class).<SUser>fetchOneAsync(ScAuthKey.USER_NAME, username).compose(fetched -> {
            /* Not Found */
            if (Objects.isNull(fetched)) {
                log.warn("{} [ Ακριβώς ] username = {} 用户不存在.", ScConstant.K_PREFIX, username);
                return FnVertx.failOut(_80203Exception404UserNotFound.class, username);
            }
            /* Locked User */
            final Boolean isLock = Objects.isNull(fetched.getActive()) ? Boolean.FALSE : fetched.getActive();
            if (!isLock) {
                log.warn("{} [ Κλειδωμένο ] username = {} 用户被锁定。", ScConstant.K_PREFIX, username);
                return FnVertx.failOut(_80220Exception423UserDisabled.class, username);
            }
            /* Password Wrong */
            if (Objects.isNull(password) || !password.equals(fetched.getPassword())) {


                // Lock On when password invalid
                log.warn("{} [ Λάθος ] username = {} 用户密码错误。", ScConstant.K_PREFIX, username);
                return Sc.lockOn(username)
                    .compose(nil -> FnVertx.failOut(_80204Exception401PasswordWrong.class, username));
            }


            // Lock Off when login successfully
            log.info("{} [ Επιτυχία ] username = {} 用户登录成功。", ScConstant.K_PREFIX, username);
            return Sc.lockOff(username).compose(nil -> Ux.future(fetched));
        }).compose(user -> this.userStub.fetchAuthorized(user)));
    }

    @Override
    public Future<Boolean> logout(final String token, final String habitus) {
        /*
         * Delete WebToken from `ACCESS_TOKEN`
         */
        return ScUser.logout(habitus);
    }
}
