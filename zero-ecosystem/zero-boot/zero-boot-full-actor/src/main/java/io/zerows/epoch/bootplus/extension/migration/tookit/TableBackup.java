package io.zerows.epoch.bootplus.extension.migration.tookit;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.Pager;
import io.zerows.component.qr.Pagination;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.Environment;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;

import java.util.Objects;

import static io.zerows.epoch.bootplus.extension.refine.Ox.LOG;

public class TableBackup extends AbstractStatic {
    /*
     * Jooq 处理
     */
    public TableBackup(final Environment environment) {
        super(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        /*
         * 读取当前系统中的数据（按 sigma 查询）
         * 备份的阀值：batch
         */
        Integer batchSize = config.getInteger("batch.common");
        if (Objects.isNull(batchSize)) {
            batchSize = 10000;
        }
        /*
         * 第一页数据读取
         */
        final Pager pager = Pager.create(1, batchSize);
        final Pagination first = new Pagination(pager);
        /*
         * 读取第二页数据
         */
        return Ux.complex(pagination -> {
            final Pager eachPage = pagination.getPager();
            final JsonObject filters = new JsonObject();
            if (!config.containsKey("all")) {
                final HApp app = this.ark.app();
                final String sigma = app.option(KName.SIGMA);
                filters.put(KName.SIGMA, sigma);
            }
            final JsonObject condition = this.toCondition(eachPage, filters);
            /*
             * 表数据和参数
             */
            LOG.Shell.info(this.getClass(), "访问表：{0}, 条件：{1}",
                this.jooq.table(), condition.encode());
            return this.jooq.searchJAsync(condition)
                .compose(data -> this.ioAsync(data, config, eachPage.getPage()));
        }).apply(first).compose(result -> Ux.future(config));
    }
}
