package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBlock;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public interface Combiner<I, M> {

    Cc<String, Combiner> CC_SKELETON = Cc.openThread();

    static Combiner<BBag, ConcurrentMap<String, BBag>> forBag() {
        return CC_SKELETON.pick(CombinerBag::new, CombinerBag.class.getName());
    }

    static Combiner<BBag, List<BBlock>> forBlock() {
        return CC_SKELETON.pick(CombinerBlock::new, CombinerBlock.class.getName());
    }

    static Combiner<JsonObject, BBag> outDao() {
        return CC_SKELETON.pick(CombinerDao::new, CombinerDao.class.getName());
    }

    static Combiner<JsonObject, BBag> outBag() {
        return CC_SKELETON.pick(CombinerOutBag::new, CombinerOutBag.class.getName());
    }

    static Combiner<JsonObject, Collection<BBag>> outChildren() {
        return CC_SKELETON.pick(CombinerOutChildren::new, CombinerOutChildren.class.getName());
    }

    Future<JsonObject> configure(I bag, M map);
}
