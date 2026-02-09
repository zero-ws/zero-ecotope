package io.zerows.extension.module.lbs.boot;

import io.zerows.epoch.store.DBFlyway;

import java.util.List;

public class DBFlywayLbs implements DBFlyway {
    @Override
    public List<String> waitFlyway(final String dbType) {
        return List.of(
            "classpath:database/lbs/" + dbType + "/"
        );
    }
}
