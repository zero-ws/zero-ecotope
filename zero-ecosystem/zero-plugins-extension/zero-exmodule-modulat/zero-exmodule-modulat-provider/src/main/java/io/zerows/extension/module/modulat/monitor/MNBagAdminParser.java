package io.zerows.extension.module.modulat.monitor;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.monitor.metadata.MetricParser;
import io.zerows.support.Ut;

/**
 * @author lang : 2025-12-29
 */
class MNBagAdminParser implements MetricParser<MNBagAdminMeta> {
    @Override
    public MNBagAdminMeta build(final JsonObject config) {
        final MNBagAdminMeta item = new MNBagAdminMeta();
        item.id(Ut.valueString(config, KName.KEY));
        item.group("G.BagAdmin");
        item.name(Ut.valueString(config, KName.CODE));
        return item;
    }
}
