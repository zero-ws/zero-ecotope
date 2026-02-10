package io.zerows.extension.module.ambient.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.ambient.domain.tables.XActivity;
import io.zerows.extension.module.ambient.domain.tables.XActivityRule;
import io.zerows.extension.module.ambient.domain.tables.XCategory;
import io.zerows.extension.module.ambient.domain.tables.XSource;

import java.util.List;
import java.util.Map;

public class TypeOfAmbientJsonObject extends TypeOfJsonObject {
    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // XCategory
            Map.of(
                XCategory.X_CATEGORY.RUN_CONFIG.getName(), XCategory.X_CATEGORY.getName(),
                XCategory.X_CATEGORY.TREE_CONFIG.getName(), XCategory.X_CATEGORY.getName()
            ),
            // XSource
            Map.of(
                XSource.X_SOURCE.JDBC_CONFIG.getName(), XSource.X_SOURCE.getName()
            ),
            // XActivityRule
            Map.of(
                XActivityRule.X_ACTIVITY_RULE.HOOK_CONFIG.getName(), XActivityRule.X_ACTIVITY_RULE.getName(),
                XActivityRule.X_ACTIVITY_RULE.RULE_CONFIG.getName(), XActivityRule.X_ACTIVITY_RULE.getName(),
                XActivityRule.X_ACTIVITY_RULE.RULE_TPL.getName(), XActivityRule.X_ACTIVITY_RULE.getName()
            ),
            // XActivity
            Map.of(
                XActivity.X_ACTIVITY.RECORD_NEW.getName(), XActivity.X_ACTIVITY.getName(),
                XActivity.X_ACTIVITY.RECORD_OLD.getName(), XActivity.X_ACTIVITY.getName()
            )
        );
    }
}
