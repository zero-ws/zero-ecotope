package io.zerows.plugins.oauth2;

import io.zerows.epoch.store.DBFlyway;

import java.util.List;

public class OAuth2Flyway implements DBFlyway {
    @Override
    public List<String> waitFlyway(final String dbType) {
        return List.of(
            "classpath:database/oauth2/" + dbType + "/"
        );
    }
}
