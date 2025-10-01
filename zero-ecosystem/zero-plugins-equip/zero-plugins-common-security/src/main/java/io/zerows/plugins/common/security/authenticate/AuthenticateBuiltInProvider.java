package io.zerows.plugins.common.security.authenticate;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.corpus.security.atom.Aegis;
import io.zerows.epoch.common.log.Annal;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AuthenticateBuiltInProvider implements AuthenticationProvider {

    private final static Annal LOGGER = Annal.get(AuthenticateBuiltInProvider.class);
    private final transient Aegis aegis;
    private transient Function<JsonObject, Future<User>> userFn;

    @SuppressWarnings("all")
    private AuthenticateBuiltInProvider(final Aegis aegis) {
        this.aegis = aegis;
        // @AuthorizedUser method process user data
        final Method method = aegis.getAuthorizer().getUser();
        if (Objects.nonNull(method)) {
            this.userFn = (json) -> (Future<User>) Fn.jvmOr(() -> method.invoke(aegis.getProxy(), json));
        }
    }

    public static AuthenticateBuiltInProvider provider(final Aegis aegis) {
        return new AuthenticateBuiltInProvider(aegis);
    }

    private void authenticateInternal(final JsonObject credentials, final Handler<AsyncResult<User>> handler) {
        AuthenticateGateway.userVerified(credentials, this.aegis, (res) -> {
            /*
             * Here the result should not be null and handler
             * 1. The internal method `userVerified` will be sure the callback logical
             *    - 401 ( method = null )
             *    - 401 ( the executor is handler and throw out WebException )
             *    - XXX ( Throw out internal error )
             * 2. The internal method `userVerified` will be system level
             *    following code will be income level
             *    - check = true:  Success
             *    - check = false: 401 ( Business Validated Failure )
             *
             * In this kind of situation, following code is not needed.
             *     if (res.succeeded()) {
             *          <Current Method Code Logical>
             *     }
             */
            if (res.succeeded()) {
                final Boolean checked = res.result();
                if (checked) {
                    // Success to passed validation
                    LOGGER.info("[ Auth ]\u001b[0;32m 401 Authenticated successfully!\u001b[m");
                    AuthenticateGateway.userCached(credentials,
                        // Build `User`
                        () -> handler.handle(this.buildUser(credentials)));
                } else {
                    // 401 Workflow
                    handler.handle(FnVertx.failOut(_401UnauthorizedException.class, "[ ZERO ] 认证权限不够！"));
                }
            } else {
                // Validated
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    private Future<User> buildUser(final JsonObject credentials) {
        if (Objects.isNull(this.userFn)) {
            // Attribute should be empty here
            return Future.succeededFuture(User.create(credentials, new JsonObject()));
        } else {
            return this.userFn.apply(credentials);
        }
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        return null;
    }

    public void authenticate(final JsonObject credentials, final Handler<AsyncResult<User>> handler) {
        /*
         * 1. Read User information from user cache
         *    Zero framework provide cache pool to store user logged information to avoid
         *    duplicated action the code logical in @Wall to simply and speed up the
         *    401 authenticate.
         * 2. Here the credentials data structure is as following;
         *    {
         *        "access_token": "xxx",
         *        "session": "vert.x session id",
         *        "habitus": "the user unique key in zero session pool",
         *        "user": "user key"
         *    }
         */
        AuthenticateGateway.userCached(credentials,

            /*
             * The major code logical that has been defined
             * @Wall class to action your own code here.
             */
            () -> this.authenticateInternal(credentials, handler),

            // Build `User`
            () -> handler.handle(this.buildUser(credentials))
        );
    }
}
