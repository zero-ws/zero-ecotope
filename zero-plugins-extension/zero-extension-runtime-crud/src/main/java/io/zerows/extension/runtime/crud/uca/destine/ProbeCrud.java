package io.zerows.extension.runtime.crud.uca.destine;

import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.module.domain.atom.specification.KPoint;

/**
 * @author lang : 2023-08-18
 */
class ProbeCrud implements Probe {

    @Override
    public IxMod create(final KPoint point, final IxMod active) {
        // 数据提取
        final Envelop envelop = active.envelop();
        // 创建新的模型
        final IxMod standBy = IxMod.of(point.getCrud()).envelop(envelop);

        // 连接设置之后返回 standBy
        active.connected(standBy);
        return standBy;
    }
}
