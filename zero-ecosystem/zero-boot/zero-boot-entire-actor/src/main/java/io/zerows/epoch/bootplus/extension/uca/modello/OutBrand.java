package io.zerows.epoch.bootplus.extension.uca.modello;

import io.vertx.core.json.JsonObject;
import io.zerows.metadata.program.Kv;
import io.zerows.extension.runtime.ambient.osgi.spi.component.ExAttributeComponent;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.property.OComponent;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class OutBrand extends ExAttributeComponent implements OComponent {
    @Override
    public Object after(final Kv<String, Object> kv, final HRecord record, final JsonObject combineData) {
        return this.translateTo(kv.value(), combineData);
    }
}
