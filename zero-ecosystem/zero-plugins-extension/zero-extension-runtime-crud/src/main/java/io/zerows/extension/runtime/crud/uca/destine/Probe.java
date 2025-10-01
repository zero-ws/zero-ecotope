package io.zerows.extension.runtime.crud.uca.destine;

import io.zerows.epoch.enums.modeling.EmModel;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.module.domain.atom.specification.KPoint;

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

    static Probe of(final EmModel.Join join) {
        Objects.requireNonNull(join);
        final Supplier<Probe> supplier = POOL.PROBE_MAP.getOrDefault(join, null);
        if (Objects.isNull(supplier)) {
            return null;
        }
        return POOL.CCT_PROBE.pick(supplier, join.name());
    }

    IxMod create(KPoint point, IxMod active);
}
