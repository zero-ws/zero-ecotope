package io.zerows.extension.crud.uca;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.mbse.metadata.KModule;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.enums.modeling.EmModel;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 创建 {@link IxMod} 专用的方法，根据连接类型的差异，创建模型时候
 * 有很多不一样的地方，此处用于主备模型
 * <pre><code>
 *     1. 根据主动模型 active 计算连接点
 *     2. 根据提取的连接点计算 standBy 的辅助模型
 *     3. 最终拥有的副作用就是修改 active 的备用模型
 * </code></pre>
 * 此处不用担心缓存问题，zero-crud 缓存的是内置的两个模型，即
 * <pre><code>
 *     module 变量
 *       {@link IxMod#module()}
 *         -> {@link KModule}
 *     connect 变量
 *       {@link IxMod#connected()}
 *         -> {@link KModule}
 * </code></pre>
 *
 * @author lang : 2023-08-18
 */
public interface Probe {

    @Memory(Probe.class)
    Cc<String, Probe> CC_SKELETON = Cc.openThread();

    static Probe of(final EmModel.Join join) {
        Objects.requireNonNull(join);
        final Supplier<Probe> supplier = ProbeTool.PROBE_SUPPLIER.getOrDefault(join, null);
        if (Objects.isNull(supplier)) {
            return null;
        }
        return CC_SKELETON.pick(supplier, join.name());
    }

    IxMod create(KJoin.Point point, IxMod active);
}
