package io.zerows.extension.module.rbac.plugins;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.webflow.UnderApply;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.component.acl.region.CommonCosmo;
import io.zerows.extension.module.rbac.component.acl.region.Cosmo;
import io.zerows.extension.module.rbac.component.acl.region.SeekCosmo;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/*
 * Extension in RBAC module
 * 1) Region calculation
 * 2) Visitant calculation ( Extension More )
 */
@Slf4j
public class ApplyDataRegion implements UnderApply {

    private static final Set<HttpMethod> METHOD_SET = Set.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS);
    private transient final JsonObject config = new JsonObject();

    @Override
    public UnderApply bind(final JsonObject config) {
        this.config.mergeIn(config);
        return this;
    }

    private static final Cc<String, Cosmo> CC_COSMO = Cc.openThread();

    @Override
    public Future<Envelop> before(final RoutingContext context, final Envelop envelop) {
        if (this.isDisabled(context)) {
            // Data Region disabled
            return Ux.future(envelop);
        }

        /* Get Critical parameters */
        return Sc.cacheView(context, envelop.habitus()).compose(matrix -> {
            if (this.isRegion(matrix)) {
                log.info("{} --> DataRegion 前置：uri = {}, region = {}",
                    ScConstant.K_PREFIX, context.request().path(), matrix.encode());
                /*
                 * Select cosmo by matrix
                 */
                final Cosmo cosmo = this.cosmo(matrix);
                return cosmo.before(envelop, matrix);
            } else {
                /*
                 * Matrix null or empty
                 */
                return Ux.future(envelop);
            }
        }).otherwise(Fx.otherwiseFn(() -> envelop));
    }

    @Override
    public Future<Envelop> after(final RoutingContext context, final Envelop response) {
        if (this.isDisabled(context)) {
            // Data Region disabled
            return Ux.future(response);
        }

        /* Get Critical parameters */
        return Sc.cacheView(context, response.habitus()).compose(matrix -> {
            if (this.isRegion(matrix)) {
                log.info("{} <-- DataRegion 后置：{}", ScConstant.K_PREFIX, matrix.encode());
                /*
                 * Select cosmo by matrix
                 */
                final Cosmo cosmo = this.cosmo(matrix);
                return cosmo.after(response, matrix);
            } else {
                /*
                 * Matrix null or empty
                 */
                return Ux.future(response);
            }
        }).otherwise(Fx.otherwiseFn(() -> response));
    }

    private Cosmo cosmo(final JsonObject matrix) {
        /* Build DataCosmo */
        if (matrix.containsKey(KName.SEEKER)) {
            /*
             * Virtual resource region calculation
             */
            return CC_COSMO.pick(SeekCosmo::new, SeekCosmo.class.getName());
        } else {
            /*
             * Actual resource region calculation
             */
            return CC_COSMO.pick(CommonCosmo::new, CommonCosmo.class.getName());
        }
    }


    /*
     * 启用数据域的核心条件
     * 1. 已配置了 prefix，且请求路径必须是以 prefix 开始
     * 2. HttpMethod 方法必须是 POST / GET 两种
     *    2.1. GET 查询数据时启用数据域
     *    2.2. POST 查询数据时启用数据域
     * 3. 将来考虑扩展模式（OPTIONS方法默认启用）
     */
    protected boolean isDisabled(final RoutingContext context) {
        final HttpMethod method = context.request().method();
        if (!METHOD_SET.contains(method)) {
            // HttpMethod 不匹配
            return true;
        }
        final String prefix = Ut.valueString(this.config, KName.PREFIX);
        if (Ut.isNil(prefix)) {
            // prefix 未配置
            log.warn("Data Region require config `prefix` attribute findRunning, but now is null. Disabled! ");
            return true;
        }
        final String requestUri = context.request().path();
        return !requestUri.startsWith(prefix);
    }

    /*
     * DataMatrix 是否启用 DataRegion 的专用判断方法
     * 1. matrix == null
     *    返回 false，不启用视图流程
     * 2. matrix 中包含：projection / criteria / credit / rows 数据
     *    启用视图流程
     * 3. matrix 中包含：seeker
     *    启用视图流程，并且是 visitant 流程
     */
    protected boolean isRegion(final JsonObject matrix) {
        if (Objects.isNull(matrix)) {
            return false;               // 禁用视图流程
        }
        boolean isEnabled = Ut.isNotNil(Ut.valueJArray(matrix, VName.KEY_PROJECTION));
        if (isEnabled) {
            return true;                // 启用流程 projection 有值
        }
        isEnabled = Ut.isNotNil(Ut.valueJArray(matrix, KName.Rbac.CREDIT));
        if (isEnabled) {
            return true;                // 启用流程 credit 有值
        }
        isEnabled = Ut.isNotNil(Ut.valueJObject(matrix, KName.Rbac.ROWS));
        if (isEnabled) {
            return true;                // 启用流程 rows 有值
        }
        isEnabled = Ut.isNotNil(Ut.valueJObject(matrix, VName.KEY_CRITERIA));
        if (isEnabled) {
            return true;                // 启用流程 criteria
        }
        // 最后检查 seeker 流程
        final boolean seeker = Ut.isNotNil(Ut.valueJObject(matrix, KName.SEEKER));
        if (seeker) {
            // seeker = true, 检查 view 是否存在
            final JsonObject viewJ = Ut.valueJObject(matrix, KName.VIEW);
            return Ut.isNotNil(viewJ);
        }
        return false;
    }
}
