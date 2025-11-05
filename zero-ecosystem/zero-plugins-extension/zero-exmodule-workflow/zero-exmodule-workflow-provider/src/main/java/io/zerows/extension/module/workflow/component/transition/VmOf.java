package io.zerows.extension.module.workflow.component.transition;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.workflow.common.em.TodoStatus;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.support.Ut;

/**
 * 选择器，用于选择不同的 Vm 组件做状态部分的执行，合并到一起
 *
 * @author lang : 2023-06-29
 */
public class VmOf {
    private static final Cc<String, Vm> CC_VM = Cc.openThread();

    public static Vm gateway(final WTodo wTask) {
        final TodoStatus todoStatus = Ut.toEnum(wTask.getStatus(), TodoStatus.class);
        // 上一个状态是 FINISHED
        if (TodoStatus.FINISHED == todoStatus) {
            return CC_VM.pick(VmCommon::new, VmCommon.class.getName());
        }
        // 上一个状态是 REJECTED
        if (TodoStatus.REJECTED == todoStatus) {
            return CC_VM.pick(VmReject::new, VmReject.class.getName());
        }
        // 上一个状态是 REDO
        if (TodoStatus.REDO == todoStatus) {
            return CC_VM.pick(VmRedo::new, VmRedo.class.getName());
        }
        // 上一个无状态 Empty
        return CC_VM.pick(VmEmpty::new, VmEmpty.class.getName());
    }

    public static Vm gateway(final JsonObject params) {
        final String status = Ut.valueString(params, KName.STATUS);
        final String phase = Ut.valueString(params, KName.PHASE);
        // 第一优先级：开始节点
        // status = null
        if (Ut.isNil(status)) {
            return CC_VM.pick(VmCommon::new, VmCommon.class.getName());
        }
        // 第二优先级：拒绝
        // phase = REJECTED
        if (TodoStatus.REJECTED.name().equals(phase)) {
            return CC_VM.pick(VmReject::new, VmReject.class.getName());
        }
        // 第三优先级：驳回
        // phase = REDO
        if (TodoStatus.REDO.name().equals(phase)) {
            return CC_VM.pick(VmRedo::new, VmRedo.class.getName());
        }
        // 默认优先级：正常
        return CC_VM.pick(VmCommon::new, VmCommon.class.getName());
    }
}
