package io.zerows.plugins.flyway;

import java.util.Set;

public interface FlywayDev {

    Set<String> DEV_PRIVATE = Set.of(
        "DEV_MOBILE",
        "DEV_EMAIL",
        "DEV_ALIPAY",
        "DEV_WE_UNION",
        "DEV_CP_UNION"
    );
}
