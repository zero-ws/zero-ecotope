package io.zerows.extension.module.mbsecore.component.apply;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.common.em.EntityType;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;

import java.util.Locale;

import static io.zerows.extension.module.mbsecore.boot.Ao.LOG;

class EntityDefault implements AoDefault {

    @Override
    public void applyJson(final JsonObject entity) {
        LOG.Uca.debug(this.getClass(), "「DFT」实体输入值: {0}", entity.encode());
        /*
         * 默认值：
         * key
         * type
         * tableName
         * active
         * language
         * metadata
         */
        AoDefault.apply(entity, "type", EntityType.ENTITY.name());
        AoDefault.apply(entity, "tableName", this.getTable(entity.getString(KName.IDENTIFIER)));
        AoDefault.apply(entity);
    }

    private String getTable(final String identifier) {
        return Ut.isNil(identifier) ? VString.EMPTY :
            identifier.replace('.', '_')
                .toUpperCase(Locale.getDefault());
    }
}
