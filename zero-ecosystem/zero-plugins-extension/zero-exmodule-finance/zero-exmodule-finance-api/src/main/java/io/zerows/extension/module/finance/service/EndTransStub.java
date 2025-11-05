package io.zerows.extension.module.finance.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.finance.common.DataTran;
import io.zerows.extension.module.finance.common.em.EmTran;
import io.zerows.extension.module.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.module.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.module.finance.domain.tables.pojos.FTrans;

import java.util.List;
import java.util.Set;

/**
 * 交易步骤
 *
 * @author lang : 2024-01-24
 */
public interface EndTransStub {

    Future<FTrans> createBySettlement(JsonObject data, FSettlement settlement);

    Future<FTrans> createBySettlement(JsonObject data, List<FSettlement> settlements);

    Future<FTrans> createByDebt(JsonObject data, List<FDebt> debts);

    Future<List<DataTran>> fetchAsync(Set<String> keys, Set<EmTran.Type> typeSet);

    Future<JsonObject> fetchAsync(String key);
}