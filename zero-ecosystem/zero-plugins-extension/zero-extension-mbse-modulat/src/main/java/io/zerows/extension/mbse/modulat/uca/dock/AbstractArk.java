package io.zerows.extension.mbse.modulat.uca.dock;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.modulat.agent.service.BagArgService;
import io.zerows.extension.mbse.modulat.agent.service.BagArgStub;
import io.zerows.extension.mbse.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.mbse.modulat.domain.tables.pojos.BBlock;
import io.zerows.extension.mbse.modulat.uca.configure.Combiner;
import io.zerows.extension.runtime.skeleton.eon.em.TypeBag;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractArk implements Ark {

    protected JsonObject buildQr(final String input, final EmModel.By by) {
        // 除开 K-KERNEL 不读取
        return this.buildQr(input, by, Set.of(TypeBag.EXTENSION, TypeBag.COMMERCE, TypeBag.FOUNDATION));
    }

    protected JsonObject buildQr(final String input, final EmModel.By by, final Set<TypeBag> bags) {
        final JsonObject conditionJ = Ux.whereAnd();
        switch (by) {
            case BY_KEY -> conditionJ.put(KName.APP_KEY, input);
            case BY_SIGMA -> conditionJ.put(KName.SIGMA, input);
            case BY_TENANT -> conditionJ.put(KName.Tenant.ID, input);
            default -> conditionJ.put(KName.APP_ID, input);
        }
        if (Objects.nonNull(bags) && !bags.isEmpty()) {
            if (VValue.ONE == bags.size()) {
                final TypeBag bag = bags.iterator().next();
                conditionJ.put(KName.TYPE, bag.key());
            } else {
                final Set<String> bagNames = bags.stream()
                    .map(TypeBag::key).collect(Collectors.toSet());
                // type,i = [bag1, bag2, bag3]
                conditionJ.put(KName.TYPE + ",i", Ut.toJArray(bagNames));
            }
        }
        return conditionJ;
    }

    protected Future<JsonObject> configureBag(final BBag bag) {
        final BagArgStub stub = Ut.singleton(BagArgService.class);
        return stub.seekBlocks(bag).compose(blocks -> {
            final Combiner<BBag, List<BBlock>> combiner = Combiner.forBlock();
            return combiner.configure(bag, blocks);
        });
    }
}
