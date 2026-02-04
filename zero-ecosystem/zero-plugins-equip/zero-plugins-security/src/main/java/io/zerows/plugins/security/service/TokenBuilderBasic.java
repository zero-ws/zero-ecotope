package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.vertx.common.cache.AkkaOr;
import io.vertx.core.Future;

import java.util.Base64;

public class TokenBuilderBasic implements TokenBuilder {
    @Override
    public Akka<String> accessOf(final UserAt userAt) {
        final MSUser user = userAt.logged();
        final String token = user.getUsername() + ":" + user.getPassword();
        return AkkaOr.of(Future.succeededFuture(Base64.getEncoder().encodeToString(token.getBytes())));
    }
}
