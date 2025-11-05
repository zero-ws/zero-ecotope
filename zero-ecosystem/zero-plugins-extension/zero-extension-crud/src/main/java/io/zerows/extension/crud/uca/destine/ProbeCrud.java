package io.zerows.extension.crud.uca.destine;

import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.crud.uca.desk.IxMod;

/**
 * @author lang : 2023-08-18
 */
class ProbeCrud implements Probe {

    @Override
    public IxMod create(final KJoin.Point point, final IxMod active) {
        // 数据提取
        final Envelop envelop = active.envelop();
        // 创建新的模型
        final IxMod standBy = IxMod.of(point.getCrud()).envelop(envelop);

        // 连接设置之后返回 standBy
        active.connected(standBy);
        return standBy;
    }
}
