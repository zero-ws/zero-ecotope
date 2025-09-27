package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Me;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.core.fn.Fn;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.documentation.agent.service.ClauseStub;
import io.zerows.extension.commerce.documentation.eon.Addr;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.*;

/**
 * @author lang : 2023-09-25
 */
@Queue
public class AdminActor {

    @Inject
    private transient ClauseStub stub;

    @Address(Addr.Clause.SAVE)
    @Me
    public Future<JsonArray> saveDoc(final JsonObject body,
                                     final User user) {
        // 提取数据部分，并执行分流
        final JsonArray dataA = Ut.valueJArray(body, KName.DATA);
        final String auditor = Ux.keyUser(user);

        final JsonArray queueA = new JsonArray();
        final JsonArray queueU = new JsonArray();
        final Set<String> keyIn = new HashSet<>();
        Ut.itJArray(dataA).forEach(dataJ -> {
            final Boolean stored = Ut.valueT(dataJ, KName.STORED, Boolean.class);
            Objects.requireNonNull(stored);

            final String key = Ut.valueString(dataJ, KName.KEY);
            if (Ut.isNotNil(key)) {
                keyIn.add(key);
            }
            // sigma / language / active / createdAt
            if (stored) {
                // 更新
                dataJ.put(KName.UPDATED_BY, auditor);
                dataJ.put(KName.UPDATED_AT, Instant.now());
                queueU.add(dataJ);
            } else {
                // 添加
                final JsonObject newJ = dataJ.copy();
                Ut.valueCopy(newJ, body,
                    KName.SIGMA,
                    KName.LANGUAGE,
                    KName.ACTIVE);
                newJ.put(KName.CREATED_BY, auditor);
                newJ.put(KName.CREATED_AT, Instant.now());
                queueA.add(newJ);
            }
        });
        // 提取主数据部分
        final JsonObject record = Ut.valueJObject(body, KName.RECORD);
        // 删除数据
        return this.removeAsync(keyIn, record)
            .compose(removed -> {
                // 保存数据
                final List<Future<JsonArray>> futures = new ArrayList<>();
                futures.add(this.stub.createAsync(queueA, record));
                futures.add(this.stub.updateAsync(queueU, record));
                return Fn.combineT(futures);
            })
            .compose(items -> {
                // 追加 stored 字段
                final JsonArray response = new JsonArray();
                items.forEach(each -> Ut.itJArray(each)
                    .peek(eachJ -> eachJ.put(KName.STORED, Boolean.TRUE))
                    .forEach(response::add)
                );
                return Ux.future(response);
            });
    }

    private Future<Boolean> removeAsync(final Set<String> keys,
                                        final JsonObject record) {
        final String key = Ut.valueString(record, KName.KEY);
        if (Ut.isNil(key)) {
            return Ux.futureT();
        }
        return this.stub.fetchByDoc(key).compose(clauses -> {
            // 存储在数据库中的 keys，keyStored
            final Set<String> keyStored = Ut.valueSetString(clauses, KName.KEY);
            // 输入的 keys，直接使用 keyStored - keys 就可以得到需要删除的 keys
            keyStored.removeAll(keys);
            // 删除旧数据
            if (keyStored.isEmpty()) {
                return Ux.futureT();
            }
            return this.stub.removeByKeys(key, keyStored);
        });
    }

    @Address(Addr.Clause.BY_DOC)
    public Future<JsonArray> fetchClause(final String docKey) {
        return this.stub.fetchByDoc(docKey);
    }
}
