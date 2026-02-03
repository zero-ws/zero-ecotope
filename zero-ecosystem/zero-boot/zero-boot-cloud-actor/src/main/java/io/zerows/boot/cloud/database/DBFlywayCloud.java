package io.zerows.boot.cloud.database;

import io.zerows.epoch.store.DBFlyway;

import java.util.List;

public class DBFlywayCloud implements DBFlyway {
    @Override
    public List<String> waitFlyway(final String dbType) {
        return List.of(
            "classpath:database/r2admin/" + dbType + "/"
        );
    }
}
