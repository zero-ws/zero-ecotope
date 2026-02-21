package io.zerows.extension.module.ambient.component;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.function.Fn;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.platform.apps.KApp;
import io.zerows.platform.apps.KArk;
import io.zerows.platform.apps.KDS;
import io.zerows.platform.apps.KPivot;
import io.zerows.platform.exception._80307Exception501KDSNone;
import io.zerows.platform.management.StoreApp;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class BuilderOfHApp extends AbstractBuilder<HArk> {

    @Override
    public <R> void updateConditional(final HArk target, final R source) {
        if (source instanceof final List<?> sourceList) {
            /*
             * 数据库构造部分，此处必须保证 master / master-workflow / master-history
             * 其中 appId = master 的数据源必须存在
             */
            final KDS kds = target.datasource();
            Fn.jvmKo(Objects.isNull(kds), _80307Exception501KDSNone.class);

            // 判空 / List 是空无法知道类型
            if (sourceList.isEmpty()) {
                final HApp app = target.app();
                final DBS master = kds.findRunning(app.id());   // 此处会反向写入
                final Database database = master.getDatabase();
                log.info("{} 应用数据库 / URL = `{}`", AtConstant.K_PREFIX_APP, database.getUrl());
            } else {
                final BuilderOf<DBS> builder = BuilderOf.of(BuilderOfDBS::new);
                sourceList.stream().<XSource>mapMulti((item, consumer) -> {
                    if (item instanceof final XSource x) {
                        consumer.accept(x);
                    }
                }).map(builder::create).filter(Objects::nonNull).forEach(kds::registry);
                log.info("{} 子数据库构造完成！数量 = {}", AtConstant.K_PREFIX_APP, sourceList.size());
                target.apply(kds);
            }
        }
    }

    /**
     * <pre>
     *     核心四维度属性
     *     - id                 Z_APP_ID
     *     - name               Z_APP
     *     - tenant             Z_TENANT
     *     - ns                 Z_NS
     * </pre>
     *
     * @param source 输入的第二实体
     * @param <R>    输入实体类型
     * @return 创建好的 HArk 容器
     */
    @Override
    public <R> HArk create(final R source) {
        if (source instanceof final XApp app) {
            // 检查和初始化
            final HApp appH = this.ensureConnect(app);


            // 标识部分 / data
            final JsonObject data = this.createData(app);
            // 配置部分 / option
            final JsonObject option = this.createOption(app);
            appH.data(data).option(option);


            // 构造 HArk
            return KArk.of(appH);
        }
        throw new IllegalArgumentException(AtConstant.K_PREFIX_APP + " 无法识别输入 / " + source.getClass().getName());
    }

    private JsonObject createOption(final XApp app) {
        final JsonObject option = new JsonObject();
        {
            // backend 后端
            option.put(KName.App.ENTRY, app.getEntry());
            option.put(KName.App.DOMAIN, app.getDomain());
            option.put(KName.App.PORT, app.getPort());
            option.put(KName.App.CONTEXT, app.getContext());
            option.put(KName.App.ENDPOINT, app.getEndpoint());

            // frontend 前端
            option.put(KName.App.URL_LOGIN, app.getUrlLogin());
            option.put(KName.App.URL_ADMIN, app.getUrlAdmin());
            option.put(KName.App.URL_HEALTH, app.getUrlHealth());

            // appSecret
            option.put(KName.APP_SECRET, app.getAppSecret());
            option.put(KName.METADATA, app.getMetadata());

            /*
             * Auditor 审计专用信息
             * createdAt, createdBy
             * updatedAt, updatedBy
             */
            final JsonObject auditor = new JsonObject();
            auditor.put(KName.CREATED_BY, app.getCreatedBy());
            if (Objects.nonNull(app.getCreatedAt())) {
                auditor.put(KName.CREATED_AT, Ut.parse(app.getCreatedAt()).toInstant());
            }
            auditor.put(KName.UPDATED_BY, app.getUpdatedBy());
            if (Objects.nonNull(app.getUpdatedAt())) {
                auditor.put(KName.UPDATED_AT, Ut.parse(app.getUpdatedAt()).toInstant());
            }
            option.put(KName.AUDITOR, auditor);
        }
        return option;
    }

    private JsonObject createData(final XApp app) {
        final JsonObject data = new JsonObject();
        data.put(KName.STATUS, app.getStatus());
        {

            // 核心标识部分
            data.put(KName.ID, app.getId());
            data.put(KName.KEY, app.getId());

            /*
             * 注意 XApp 中 id 和 appId 的含义有所不同
             * - id 是 XApp 的主键，唯一标识一条记录
             * - appId 是当前应用的附属应用 id，即当应用
             *   - 成为子应用时，appId = 父应用 id
             *   - 成为独立应用时，appId = null
             */
            data.put(KName.APP_ID, app.getAppId());
            data.put(KName.APP_KEY, app.getAppKey());
            data.put(KName.SIGMA, app.getSigma());

            data.put(KName.NAME, app.getName());
            data.put(KName.CODE, app.getCode());
            data.put(KName.LANGUAGE, app.getLanguage());
            data.put(KName.ACTIVE, app.getActive());


            // 业务属性 / business
            data.put(KName.App.TITLE, app.getTitle());
            data.put(KName.App.EMAIL, app.getEmail());
            data.put(KName.App.ICP, app.getIcp());
            data.put(KName.App.COPY_RIGHT, app.getCopyRight());
            data.put(KName.App.LOGO, app.getLogo());
            data.put(KName.App.FAVICON, app.getFavicon());
        }
        return data;
    }

    /**
     * 确保连接正确
     * <pre>
     *     1. 必须匹配
     *        - id
     *        - name
     *     2. 可选匹配，无值设置，有值则必须匹配
     *        - ns
     *        - tenant
     * </pre>
     *
     * @param app 输入应用实体
     * @return HApp 应用容器
     */
    private HApp ensureConnect(final XApp app) {
        // 连接检查
        final HApp appH = StoreApp.of().valueGet(app.getId());
        final HApp appT = new KApp(app.getName())
            .ns(app.getNamespace())
            .tenant(app.getTenantId())
            .id(app.getId());
        // 截断构造，直接返回
        if (Objects.isNull(appH)) {
            return appT;
        }
        return KPivot.tryConnect(appT, appH);
    }
}
