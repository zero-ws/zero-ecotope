package io.zerows.extension.commerce.finance.atom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBill;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBillItem;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.unity.Ux;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 「账单聚合模型」
 * 账单聚合模型以 账单明细 为核心进行模型构建，横跨结算单和账单的基础数据，最终的数据结构如下：
 * <pre><code>
 *     {
 *         "items": [],
 *         "settlements": [],
 *         "bills": []
 *     }
 * </code></pre>
 * 此模型主要用于账单界面的数据统一提取，有了这个模型之后，您就可以在：账单 - 结算 两个环节中进行数据整合
 * 此处的整合不牵涉关联部分，简单说三个节点各自代表不同的对象
 * <pre><code>
 *     - items：账单明细
 *     - settlements：结算单
 *     - bills：账单
 *     账单明细中会包含结算单和账单的关联信息，分别使用 settlementId 和 billId 来关联。
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BillData implements Serializable {
    private final transient List<FBill> bills = new ArrayList<>();
    private final transient List<FBillItem> items = new ArrayList<>();
    private final transient List<FSettlement> settlements = new ArrayList<>();

    public Future<List<FBill>> bill(final List<FBill> bills) {
        this.bills.clear();
        this.bills.addAll(bills);
        return Ux.future(bills);
    }

    public Future<List<FBillItem>> items(final List<FBillItem> items) {
        this.items.clear();
        this.items.addAll(items);
        return Ux.future(items);
    }

    public Future<List<FSettlement>> settlement(final List<FSettlement> settlements) {
        this.settlements.clear();
        this.settlements.addAll(settlements);
        return Ux.future(settlements);
    }

    /**
     * 响应结构生成器，根据 reduce 参数来判断是否做强校验
     * <pre><code>
     *     - 强校验模式：对账单和结算单进行过滤，只保留当前账单明细中存在的账单和结算单
     *       这种模式下更符合业务场景，即所有返回的账单和结算单都和当前明细相关
     *       当明细出现过滤或筛选的时候，这种模式是有效的。
     *
     *     - 非强校验模式：不做过滤，直接返回所有的账单和结算单
     * </code></pre>
     *
     * @param reduce 是否强校验
     *
     * @return {@link io.vertx.core.json.JsonObject}
     */
    public Future<JsonObject> response(final boolean reduce) {
        final JsonObject response = new JsonObject();
        response.put(KName.ITEMS, Ux.toJson(this.items));
        if (reduce) {
            final Set<String> bIds = this.items.stream()
                .map(FBillItem::getBillId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            final List<FBill> bills = this.bills.stream()
                .filter(item -> bIds.contains(item.getKey()))
                .collect(Collectors.toList());
            final Set<String> sIds = this.items.stream()
                .map(FBillItem::getSettlementId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            final List<FSettlement> settlements = this.settlements.stream()
                .filter(item -> sIds.contains(item.getKey()))
                .collect(Collectors.toList());
            response.put("bills", Ux.toJson(bills));
            response.put("settlements", Ux.toJson(settlements));
        } else {
            response.put("bills", Ux.toJson(this.bills));
            response.put("settlements", Ux.toJson(this.settlements));
        }
        return Ux.future(response);
    }
}
