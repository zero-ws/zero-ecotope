package io.zerows.extension.mbse.modulat.uca.configure;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.mbse.modulat.domain.tables.pojos.BBag;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class CombinerKit {

    static JsonObject normalize(final BBag bag) {
        final JsonObject bagJ = new JsonObject();
        /*
         * nameAbbr, nameFull, name
         */
        bagJ.put(KName.NAME, bag.getName());
        bagJ.put("nameAbbr", bag.getNameAbbr());
        bagJ.put("nameFull", bag.getNameFull());

        /*
         * uiIcon, uiSort, uiStyle
         */
        bagJ.put(KName.UI_ICON, bag.getUiIcon());
        bagJ.put(KName.UI_STYLE, Ut.toJObject(bag.getUiStyle()));
        bagJ.put(KName.UI_SORT, bag.getUiSort());
        bagJ.put(KName.KEY, bag.getKey());
        return bagJ;
    }
}
