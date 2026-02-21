package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.common.BkConstant;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBlock;
import io.zerows.extension.module.modulat.serviceimpl.BagArgService;
import io.zerows.extension.module.modulat.servicespec.BagArgStub;
import io.zerows.extension.skeleton.common.enums.TypeBag;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EquipForBase implements EquipFor {

    protected JsonObject buildQr(final String value, final EmModel.By by) {
        Objects.requireNonNull(by, "[ XMOD ] 查询维度 by 不可为空");
        // 抽取基础判断逻辑
        final JsonObject conditionJ = by.whereBy(value);
        final Set<String> bagNames = Set.of(
            TypeBag.FOUNDATION,
            TypeBag.COMMERCE,
            TypeBag.EXTENSION
        ).stream().map(TypeBag::key).collect(Collectors.toSet());
        conditionJ.put(KName.TYPE + ",i", Ut.toJArray(bagNames));
        return conditionJ;
    }

    protected Future<Map<String, List<BBag>>> fetchBags(final JsonObject condition) {
        this.log().info("{} 查询所有功能包：{}", BkConstant.K_PREFIX, condition);
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition).compose(bags -> {

            return null;
        });
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected Future<JsonObject> dataAsync(final BBag bag) {
        final BagArgStub stub = Ut.singleton(BagArgService.class);
        return stub.seekBlocks(bag).compose(blocks -> {
            final Combiner<BBag, List<BBlock>> combiner = Combiner.forBlock();
            return combiner.configure(bag, blocks);
        });
    }
}
