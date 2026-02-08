package io.zerows.extension.skeleton.database;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.epoch.constant.KName;

import java.util.List;
import java.util.Locale;

public class TypeOfJooqMetadata extends TypeOfJsonObject {

    @Override
    protected List<String> regexField() {
        return List.of(
            KName.METADATA.toUpperCase(Locale.ROOT)
        );
    }
}
