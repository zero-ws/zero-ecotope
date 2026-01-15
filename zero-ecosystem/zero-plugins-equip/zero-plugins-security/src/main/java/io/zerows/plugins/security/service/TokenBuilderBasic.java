package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilder;

import java.util.Base64;

public class TokenBuilderBasic implements TokenBuilder {
    @Override
    public String accessOf(final UserAt userAt) {
        final MSUser user = userAt.logged();
        final String token = user.getUsername() + ":" + user.getPassword();
        return Base64.getEncoder().encodeToString(token.getBytes());
    }
}
