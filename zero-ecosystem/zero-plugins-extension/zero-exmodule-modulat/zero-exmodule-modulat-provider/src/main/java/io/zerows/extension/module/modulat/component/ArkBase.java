package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBlock;
import io.zerows.extension.module.modulat.serviceimpl.BagArgService;
import io.zerows.extension.module.modulat.servicespec.BagArgStub;
import io.zerows.extension.skeleton.common.enums.TypeBag;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ArkBase implements Ark {

    protected JsonObject buildQr(final String input, final EmModel.By by) {
        // 除开 K-KERNEL 不读取
        return this.buildQr(input, by, Set.of(TypeBag.EXTENSION, TypeBag.COMMERCE, TypeBag.FOUNDATION));
    }

    protected JsonObject buildQr(final String input, final EmModel.By by, final Set<TypeBag> bags) {
        // 抽取分别处理的判断逻辑，这种逻辑有利于复用逻辑，方便后续扩展
        final JsonObject conditionJ = by.whereBy(input);
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
