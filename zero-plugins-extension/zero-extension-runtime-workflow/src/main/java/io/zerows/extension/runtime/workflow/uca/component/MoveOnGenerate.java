package io.zerows.extension.runtime.workflow.uca.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.uca.central.AbstractMoveOn;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MoveOnGenerate extends AbstractMoveOn {
    @Override
    public Future<WRecord> transferAsync(final WRequest request, final WTransition wTransition) {
        Objects.requireNonNull(this.todoKit);
        Objects.requireNonNull(this.linkageKit);
        /*
         * Here will copy the todo from original WRecord first
         *
         * 1) The next task will be put into WTransition instance
         * 2) The Gear will determine the mode from WTransition
         *
         * Here are three parameters
         * 1. Record        -> The based original WRecord that will be generate todo
         * 2. JsonObject    -> requestJ that came from input
         * 3. WTransition   -> Transition ( The `to` will be used )
         **/
        final JsonObject requestJ = request.request();
        return this.todoKit.generateAsync(requestJ, wTransition, request.record())
            /*
             * 此处同步数据基础信息，但不重新加载 linkage 数据信息，由于 linkage 数据信息在此处执行时
             * 已经存在，若重新加载位于更新之后，会加载到更新之后的数据信息作为最终数据信息，所以此处
             * 针对 record 需要调整，检查 record 信息如下
             */
            .compose(record -> this.linkageKit.syncAsync(requestJ, record));
    }
}
