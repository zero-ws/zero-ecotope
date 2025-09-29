package io.zerows.extension.commerce.finance.atom;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.exception.web._80413Exception501NotImplement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTransItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTransOf;
import io.zerows.specification.atomic.HJson;
import io.zerows.unity.Ux;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 交易模型，对接模型信息如：
 * <pre><code>
 *     1. {@link FTransOf} 交易关联模型
 *     2. {@link FTrans} 唯一的交易模型
 *     3. {@link FTransItem} 交易明细列表
 * </code></pre>
 *
 * @author lang : 2024-01-28
 */
public class TranData implements Serializable, HJson {

    private final List<FTransOf> transOf = new ArrayList<>();
    private final List<FTransItem> items = new ArrayList<>();
    private FTrans trans;

    private String id;

    private TranData() {
    }

    public static TranData instance() {
        return new TranData();
    }

    public TranData of(final List<FTransOf> ofs) {
        this.transOf.clear();
        this.transOf.addAll(ofs);
        return this;
    }

    public TranData items(final List<FTransItem> items) {
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    public boolean isIn(final String key) {
        if (this.transOf.isEmpty()) {
            return false;
        }
        return this.transOf.stream().anyMatch(item -> key.equals(item.getObjectId()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TranData tranData = (TranData) o;
        return Objects.equals(this.id, tranData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public TranData transaction(final FTrans trans) {
        if (Objects.nonNull(trans)) {
            this.trans = trans;
            this.id = trans.getKey();
        }
        return this;
    }

    public FTrans transaction() {
        return this.trans;
    }

    /**
     * 数据结构如：
     * <pre><code>
     *     {
     *          "...": "...",
     *          "of": [],
     *          "items": []
     *     }
     * </code></pre>
     *
     * @return {@link JsonObject}
     */
    @Override
    public JsonObject toJson() {
        final JsonObject response = new JsonObject();
        if (Objects.nonNull(this.trans)) {
            response.mergeIn(Ux.toJson(this.trans));
        }
        response.put("of", Ux.toJson(this.transOf));
        response.put(KName.ITEMS, Ux.toJson(this.items));
        return response;
    }

    @Override
    public void fromJson(final JsonObject json) {
        throw new _80413Exception501NotImplement();
    }


}
