package io.zerows.extension.module.rbac.component.acl.rapier;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.rbac.common.ScOwner;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class QuestAcl implements Quest {
    private final transient SyntaxAop syntaxAop;

    QuestAcl() {
        this.syntaxAop = new SyntaxAop();
    }

    private Future<JsonArray> syncViews(final SResource resource, final JsonArray viewData) {
        final List<Future<JsonObject>> futures = new ArrayList<>();
        Ut.itJArray(viewData).forEach(viewJ -> {
            final ScOwner owner = new ScOwner(
                Ut.valueString(viewJ, KName.OWNER),
                Ut.valueString(viewJ, KName.OWNER_TYPE)
            );
            owner.bind(
                Ut.valueString(viewJ, KName.NAME),
                Ut.valueString(viewJ, KName.POSITION)
            );
            futures.add(Quinn.visit().saveAsync(resource, owner, viewJ));
        });
        return Fx.combineA(futures);
    }

    @Override
    public Future<Envelop> beforeAsync(final Envelop request, final JsonObject matrixJ) {
        // 在执行之前调用，处理 BEFORE 语法
        final JsonObject body = request.request();
        return this.syntaxAop.aclBefore(body, matrixJ, request.headersX()).compose(acl -> {
            // 绑定专用
            request.acl(acl);
            return Ux.future(request);
        });
    }

    @Override
    public Future<Envelop> afterAsync(final Envelop response, final JsonObject matrixJ) {
        // 在执行之前调用，处理 BEFORE 语法
        final JsonObject body = response.request();
        return this.syntaxAop.aclAfter(body, matrixJ, response.headersX()).compose(acl -> {
            // 绑定专用
            response.acl(acl);
            /*
             * Append data of `acl` into description for future usage
             * This feature is ok when RunPhase = DELAY because the EAGER
             * will impact our current request response directly.
             *
             * But this node should returned all critical data
             * 1) access, The fields that you could visit
             * 2) edition, The fields that you could edit
             * 3) record, The fields of all current record
             */
            response.onAcl(acl);
            return Ux.future(response);
        });
    }
}
