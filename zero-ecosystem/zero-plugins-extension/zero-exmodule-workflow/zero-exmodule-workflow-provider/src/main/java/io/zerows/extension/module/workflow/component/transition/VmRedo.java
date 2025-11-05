package io.zerows.extension.module.workflow.component.transition;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.workflow.common.em.TodoStatus;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author lang : 2023-06-29
 */
class VmRedo implements Vm {
    @Override
    public void generate(final WTodo generated, final WTodo wTask, final WTicket ticket) {
        // 驳回
        generated.setStatus(TodoStatus.PENDING.name());
        // 临时版本
        if (Objects.isNull(generated.getToUser())) {
            // 拒绝到开单人手中
            generated.setToUser(ticket.getOpenBy());
            generated.setAcceptedBy(ticket.getOpenBy());
            generated.setAcceptedAt(LocalDateTime.now());
        }
    }

    @Override
    public void end(final JsonObject normalized, final WTransition transition) {
        final String phase = Ut.valueString(normalized, KName.PHASE);
        // phase = REDO ----> REDO
        normalized.put(KName.STATUS, phase);
    }
}
