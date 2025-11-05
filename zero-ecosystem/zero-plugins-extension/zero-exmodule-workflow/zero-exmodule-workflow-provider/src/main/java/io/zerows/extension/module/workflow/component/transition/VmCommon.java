package io.zerows.extension.module.workflow.component.transition;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.workflow.common.em.TodoStatus;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.support.Ut;

/**
 * @author lang : 2023-06-29
 */
class VmCommon implements Vm {
    @Override
    public void generate(final WTodo generated, final WTodo wTask, final WTicket ticket) {
        generated.setStatus(TodoStatus.PENDING.name());
    }

    @Override
    public void end(final JsonObject normalized, final WTransition transition) {
        final String status = Ut.valueString(normalized, KName.STATUS);
        if (Ut.isNil(status)) {
            // 开始
            // status = null ----> FINISHED
            normalized.put(KName.STATUS, TodoStatus.FINISHED.name());
        } else {
            // 不拒绝
            // status = PENDING: 完成处理
            if (TodoStatus.PENDING.name().equals(status)) {
                normalized.put(KName.STATUS, TodoStatus.FINISHED.name());
            }
        }
    }
}
