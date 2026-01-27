package io.zerows.extension.module.finance.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.finance.domain.tables.pojos.FBillItem;
import io.zerows.program.Ux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2024-01-18
 */
class MakerBillItemTransfer implements Maker<List<FBillItem>, FBillItem> {

    /**
     * 「更新账单子项」（批量版）
     * 此方法为处理账单子项的更新专用方法，将新的数据写入到账单子项中
     * <pre><code>
     *     1. 此处不用查询数据库，items 中已经包含了账单项相关数据
     *     2. 主要更新数据
     *        - updatedBy / updatedAt
     *        - relatedId
     *        - createdBy / createdAt
     * </code></pre>
     *
     * @param data     忽略不急
     * @param itemFrom 旧数据信息
     * @return {@link FBillItem}
     */
    @Override
    public Future<List<FBillItem>> buildAsync(final JsonArray data, final List<FBillItem> itemFrom) {
        // data 忽略不计

        final List<FBillItem> itemTo = new ArrayList<>();
        itemFrom.forEach(item -> {
            // 此处序列化和反序列化是必须的，防止引用重复，此处要的效果是拷贝
            final JsonObject itemJson = Ux.toJson(item);
            final FBillItem itemN = Ux.fromJson(itemJson, FBillItem.class);
            itemN.setRelatedId(item.getKey());
            itemN.setCreatedAt(LocalDateTime.now());
            itemN.setCreatedBy(item.getUpdatedBy());
            itemTo.add(itemN);
        });
        return Ux.future(itemTo);
    }
}
