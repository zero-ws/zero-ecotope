package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.platform.enums.modeling.EmModel;


public class ExModulatCommon implements ExModulat {
    /**
     * 追加应用特殊的配置项对应值
     * <pre>
     *     最终成型的数据
     *     - mXxx = {}
     *     - mYyy = {}
     * </pre>
     * 特殊的 mXxx 的配置对应的值
     * <pre>
     *     mXxx 键值提取的基本规则，如果 entry 有值，表示为入口 Bag，这种场景下会直接提取 mXxx 的值，若子模块中有和它同名
     *     的 mXxx，则直接使用父类的 mXxx 键值用来存储相关数据信息，取值必须基于 Block 才可执行。
     *
     *     父子 store 的提取规则
     *     1. 父 store + 子 store (null），直接使用 父 store 的值
     *     2. 父 null  + 子 store，直接使用子 store 的值
     *     3. 父 store + 子 store，同时使用父子 store 的值
     * </pre>
     *
     * @param appId 应用Id
     * @param open  是否包含开放性属性
     * @return 异步数据
     */
    @Override
    public Future<JsonObject> extension(final String appId, final boolean open) {
        final EquipFor equipFor = EquipFor.of(open);
        return equipFor.configure(appId, EmModel.By.BY_ID);
    }
}
