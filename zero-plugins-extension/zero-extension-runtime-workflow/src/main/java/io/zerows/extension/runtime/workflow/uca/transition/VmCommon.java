package io.zerows.extension.runtime.workflow.uca.transition;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTodo;
import io.zerows.extension.runtime.workflow.eon.em.TodoStatus;

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
