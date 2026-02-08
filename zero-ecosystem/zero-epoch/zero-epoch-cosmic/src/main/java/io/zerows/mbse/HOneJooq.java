package io.zerows.mbse;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.vertx.core.MultiMap;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.mbse.metadata.KModule;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 「直接访问器」
 * Jooq专用的构造函数，构造符合最终条件的 {@link ADB} 对象，此对象可以帮助开发人员完成单模块的CRUD操作，并根据实际
 * 情况对当前 {@link KModule} 进行详细计算，计算结果会作用于函数内层，使得最终的数据库跨表操作生效。当前包名为
 * mixture，含义为混合。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class HOneJooq implements HOne<ADB> {

    @Override
    public ADB combine(final KModule module, final KModule connect, final MultiMap headers) {
        Objects.requireNonNull(module, "[ ZERO ] 主实体定义不可以为空");
        Objects.requireNonNull(headers, "[ ZERO ] 请求头信息不可以为空");

        final String dsName = module.getDs();

        final DBMany dbMany = DBMany.of();
        final DBS dbs = StrUtil.isEmpty(dsName) ? dbMany.get() : dbMany.get(dsName);
        // 构造 ADB 对象的基础参数
        final Class<?> daoCls = module.getDaoCls();
        // 绑定 pojo 映射文件
        final String pojo = module.getPojo();
        final ADB db;
        if(Ut.isNil(pojo)){
            db = DB.on(daoCls, dbs);            // 不带映射配置
        }else{
            db = DB.on(daoCls, pojo, dbs);      // 带映射配置
        }
        return db;
    }
}
