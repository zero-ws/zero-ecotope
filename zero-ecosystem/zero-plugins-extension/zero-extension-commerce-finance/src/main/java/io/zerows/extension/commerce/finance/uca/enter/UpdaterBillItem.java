package io.zerows.extension.commerce.finance.uca.enter;

import io.zerows.extension.commerce.finance.domain.tables.daos.FBillItemDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;

/**
 * 对象查询器，根据传入数据信息读取对象信息
 *
 * @author lang : 2024-01-18
 */
class UpdaterBillItem implements Maker<String, FBillItem> {
    /**
     * 「更新账单子项」
     * 此方法为处理账单子项的更新专用方法，将新的数据写入到账单子项中
     * <pre><code>
     *     1. 根据 key 查询账单子项
     *     2. 此处确认可以查询，然后将 data 写入到账单子项数据中
     * </code></pre>
     *
     * @param key  账单子项的主键
     * @param data 更新的数据
     *
     * @return {@link FBillItem}
     */
    @Override
    public Future<FBillItem> buildAsync(final JsonObject data, final String key) {
        return Ux.Jooq.on(FBillItemDao.class).fetchJByIdAsync(key)
            // 将传入的 data 数据更新到原始数据中构造新的对象
            .compose(queried -> {
                final JsonObject normalized = queried.copy().mergeIn(data);
                final FBillItem item = Ux.fromJson(normalized, FBillItem.class);
                return Ux.future(item);
            });
    }
}
