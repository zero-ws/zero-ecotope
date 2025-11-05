package io.zerows.extension.module.mbsecore.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.skeleton.spi.ExInit;

/*
 * OOB数据初始化专用接口
 */
public interface AoRefine extends ExInit {

    Cc<String, AoRefine> CC_SKELETON = Cc.open();

    static AoRefine combine() {
        return CC_SKELETON.pick(AoRefineCombine::new, AoRefineCombine.class.getName());
        //  Fn.po?l(Pool.REFINE_POOL, CombineRefine.class.getName(),CombineRefine::new);
    }

    static AoRefine schema() {
        return CC_SKELETON.pick(AoRefineSchema::new, AoRefineSchema.class.getName());
        // return Fn.po?l(Pool.REFINE_POOL, SchemaRefine.class.getName(),SchemaRefine::new);
    }

    static AoRefine model() {
        return CC_SKELETON.pick(AoRefineModel::new, AoRefineModel.class.getName());
        // return Fn.po?l(Pool.REFINE_POOL, ModelRefine.class.getName(), ModelRefine::new);
    }
}
