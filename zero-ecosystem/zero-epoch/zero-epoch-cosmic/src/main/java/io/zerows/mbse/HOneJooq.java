package io.zerows.mbse;

import io.vertx.core.MultiMap;
import io.zerows.mbse.metadata.KModule;
import io.zerows.epoch.database.cp.DS;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.platform.enums.EmDS;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 「直接访问器」
 * Jooq专用的构造函数，构造符合最终条件的 {@link UxJooq} 对象，此对象可以帮助开发人员完成单模块的CRUD操作，并根据实际
 * 情况对当前 {@link KModule} 进行详细计算，计算结果会作用于函数内层，使得最终的数据库跨表操作生效。当前包名为
 * mixture，含义为混合。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class HOneJooq implements HOne<UxJooq> {

    @Override
    public UxJooq combine(final KModule module, final KModule connect, final MultiMap headers) {
        /*
         * 两个基础参数不可以为空，此处必须保证基础参数的合法性，但是传入的 connect 必须为空，由于此处方法是单纯的 Jooq 方法
         * 而且是单表连接专用方法，所以 connect 必须为空，否则也直接抛出对应异常以确认请求本身的合法性，只是此处不使用自定义
         * 异常，而是直接采用JVM级别的异常来完成参数的基本检查。但是JVM中是没有类似 requireNotNull 的方法来处理 null 引用
         * 的检查的，所以此处所有的 null 引用的检查应该直接从代码中忽略。
         */
        Objects.requireNonNull(module);
        Objects.requireNonNull(headers);


        /*
         * 当前类中不支持双表或多表的连接，所以此处主要针对单表执行 UxJooq 的构造，但是有几点需要说明
         * 1）当前环境中的 headers 中可能会包含一部分自定义请求头如：
         *    - X-Sigma
         *    - X-Lang
         *    - X-App-Id
         *    - X-App-Key
         *    这些自定义头看起来不会在实际执行中使用，但有可能在某些场景下需要针对此处的自定义头信息执行相关合并
         * 2）必须检查 daoCls 不可以为空
         *    类名若不合法，此处在构造的 KModule 的时候就已经给于了 null 引用，所以此处直接检查不为空即可，所以此处
         *    会再次调用 Objects.requireNonNull 方法来检查 daoCls 的合法性以确认当前 daoCls 不为空的情况。
         */
        final Class<?> daoCls = module.getDaoCls();
        Objects.requireNonNull(daoCls);


        final UxJooq dao;
        final EmDS.Stored mode = module.getMode();
        if (EmDS.Stored.DYNAMIC == mode) {
            dao = Ux.channelS(DS.class,
                /* ---->「默认」`provider` 配置的标准数据源（Jooq专用）*/ () -> Ux.Jooq.on(daoCls),
                /* 动态数据源定义，X_SOURCE，开启动态建模专用 */ ds -> Ux.Jooq.on(daoCls, ds.switchDs(headers))
            );
        } else if (EmDS.Stored.HISTORY == mode) {
            /* `orbit` 配置的历史数据源专用处理 */
            dao = Ux.Jooq.ons(daoCls);
        } else if (EmDS.Stored.EXTENSION == mode) {
            /* 扩展数据源专用 */
            final String modeKey = module.getModeKey();
            if (Ut.isNil(modeKey)) {
                /* ---->「默认」`provider` 配置的标准数据源（Jooq专用）*/
                dao = Ux.Jooq.on(daoCls);
            } else {
                /*
                 * 扩展数据源直接在配置文件中开新配置来完成相关定制，此处新配置主要为 vertx-jooq.yml
                 * 中的扩展部分，使用此处的 key 可直接实现数据源的切换，同时也可以实现数据源的扩展。
                 * */
                dao = Ux.Jooq.on(daoCls, modeKey);
            }
        } else {
            /* ---->「默认」`provider` 配置的标准数据源（Jooq专用）*/
            dao = Ux.Jooq.on(daoCls);
        }

        /* 「遗留系统」根据当前模块中的配置查看是否包含了 pojo 配置，若包含pojo配置需要执行绑定 */
        final String pojo = module.getPojo();
        dao.on(pojo);
        return dao;
    }
}
