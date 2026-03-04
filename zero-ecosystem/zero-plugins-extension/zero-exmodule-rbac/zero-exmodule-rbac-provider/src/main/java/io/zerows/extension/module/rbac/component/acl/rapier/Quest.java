package io.zerows.extension.module.rbac.component.acl.rapier;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.Envelop;

/**
 * 资源访问者统一新接口，统一资源访问者逻辑
 * 1) 消费端
 * 2) 管理端移除
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Quest {

    Cc<String, Quest> CC_QUEST = Cc.openThread();

    static Quest syntax() {
        return CC_QUEST.pick(QuestAcl::new);
    }

    /*
     * 消费端
     * JsonObject = {
     * }
     * 输出
     * DataAcl
     * -- LIST
     * -- FORM
     */
    Future<Envelop> beforeAsync(Envelop request, JsonObject matrixJ);

    Future<Envelop> afterAsync(Envelop request, JsonObject matrixJ);
}
