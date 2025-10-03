package io.zerows.epoch.corpus.mbse.uca.mixture;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.component.destine.Hymn;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.jooq.operation.UxJoin;
import io.zerows.epoch.corpus.mbse.atom.specification.KModule;
import io.zerows.epoch.metadata.specification.KJoin;
import io.zerows.epoch.metadata.specification.KPoint;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 「连接访问器」
 * Join专用的构造函数，构造符合最终条件的 {@link UxJoin} 对象，此对象可以帮助开发人员完成跨表的CRUD操作，并根据实际情况
 * 对当前 {@link KModule} 和关联模块 {@link KModule} 执行详细计算，计算结果会作用于函数内层，使得最终数据库跨表操作生
 * 效。当前包名为 mixture，含义为混合。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class HOneJoin implements HOne<UxJoin> {
    @Override
    public UxJoin combine(final KModule module, final KModule connect, final MultiMap headers) {
        /*
         * 两个基础参数不可以为空，此处为连接基础，由于此处做的是双表 Join，传入的 connect 必须不能为空，否则直接抛出
         * 对应异常，此处不使用自定义异常，而是直接使用JVM级别的异常完成参数基本操作。
         */
        Objects.requireNonNull(module);
        Objects.requireNonNull(connect);


        /*
         * 直接构造 UxJoin 对象，然后针对此对象执行 JOIN 配置，此处构造的基础步骤如：
         * - 构造 UxJoin 对象
         * - 提取当前模块的连接信息
         * - 提取当前模块的连接点
         * 连接点本身是根据 IxMod 中存储的 source 以及 target 直接计算而形成，此处不同的 JOIN 模式的最终结果不一样
         * - 父主表：（动态选择）直接使用主键连接
         * - 父从表：（静态唯一选择）使用 keyJoin 连接
         */
        final UxJoin dao = Ux.Join.on();
        final KJoin join = module.getConnect();
        final KPoint source = join.getSource();


        /*
         * 从 source 中提取键值执行 JOIN 和自动计算
         * - keyJoin：高优先级，若定义了 keyJoin 则直接使用 keyJoin 连接
         * - key：主键连接，默认优先级，未定义 keyJoin 时使用
         * 此处注意的是绑定 pojo 文件使用的是内置判断，而非外置判断，所以此处不需要判断 pojo 是否为空，若
         * pojo 文件为 null 或 “”，则直接跳过绑定阶段。
         */
        final String keyJoin = source.getKeyJoin();
        if (Objects.isNull(keyJoin)) {
            dao.add(module.getDaoCls());                // 主键连接
        } else {
            dao.add(module.getDaoCls(), keyJoin);       // keyJoin连接
        }
        final String pojoS = module.getPojo();
        dao.pojo(module.getDaoCls(), pojoS);


        /*
         * 构造 Hymn 接口（String模式），直接根据 identifier 解析连接点相关信息，然后执行连接点
         * 的 JOIN 设置，此处连接点是根据 connect 中的 identifier 计算而得，并且在 JOIN 模式下
         * 此处解析出的连接点不可以为空，否则直接抛出异常。
         *
         * UxJoin对象在处理主连接点和子连接点时，会根据连接点的类型执行不同的操作
         * - 主连接点使用了 add 方法，此处的 add 方法是直接使用了主键连接
         * - 子连接点使用了 join 方法，此处的 join 方法是直接使用了 keyJoin 连接
         * 且两种连接点模式都支持 pojo 的绑定。
         */
        final Hymn<String> hymn = Hymn.ofString(join);
        final KPoint target = hymn.pointer(connect.identifier());
        Objects.requireNonNull(target);
        final Class<?> daoCls = connect.getDaoCls();
        dao.join(daoCls, target.getKeyJoin());


        /*
         * 别名解析，直接解析 synonym 中的内容，然后执行别名绑定，此处的别名绑定是针对当前模块的别名绑定
         */
        final JsonObject synonym = target.getSynonym();
        Ut.<String>itJObject(synonym, (aliasField, field) -> dao.alias(daoCls, field, aliasField));


        // 绑定 Pojo
        final String pojoT = connect.getPojo();
        dao.pojo(connect.getDaoCls(), pojoT);
        return dao;
    }
}
