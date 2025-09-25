package io.zerows.extension.commerce.finance.agent.service.end;

import io.zerows.extension.commerce.finance.domain.tables.pojos.FDebt;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FSettlement;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FTrans;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.finance.eon.em.EmTran;
import io.zerows.extension.commerce.finance.atom.TranData;

import java.util.List;
import java.util.Set;

/**
 * 交易步骤
 *
 * @author lang : 2024-01-24
 */
public interface TransStub {

    Future<FTrans> createBySettlement(JsonObject data, FSettlement settlement);

    Future<FTrans> createBySettlement(JsonObject data, List<FSettlement> settlements);

    Future<FTrans> createByDebt(JsonObject data, List<FDebt> debts);

    Future<List<TranData>> fetchAsync(Set<String> keys, Set<EmTran.Type> typeSet);

    Future<JsonObject> fetchAsync(String key);
}