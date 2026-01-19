package io.zerows.epoch.store;

import io.r2mo.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.r2mo.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.basicore.MDMeta;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.management.OCacheDao;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 此处的 Dao 扫描主要针对 Norm / OSGI 环境下的 Dao 扫描：
 * <pre>
 *     1. {@link AbstractVertxDAO}
 *        {@link VertxPojo}
 *     2. 扫描一次之后缓存将会被填充，后续过程中就不会再次扫描
 * </pre>
 * 此处扫描的结果也会被 zero-plugins-excel 模块使用。
 *
 * @author lang : 2025-10-31
 */
@Actor(value = "DATABASE", sequence = -207)
public class DBDaoActor extends AbstractHActor {
    private final HBundle caller;

    public DBDaoActor() {
        this.caller = HPI.findOverwrite(HBundle.class);
    }

    /**
     * 执行 Dao 扫描处理 Extension 部分的初始化任务，此任务主要用于扫描 {@link AbstractVertxDAO} 类的子类，并提取 {@link MDMeta} 信息。
     * <pre>
     *     1. 加载当前环境中扫描的类信息，底层调用 {@link OCacheClass} 完成类扫描
     *     2. 计算双哈希表
     *        table = {@link Class}, Dao 类
     *        table = {@link Class}, Pojo 类
     *     3. 构造 {@link MDMeta} 信息并存储到 {@link OCacheDao} 中
     * </pre>
     *
     * @param config   数据库配置（只是为了触发保证合法性）
     * @param vertxRef Vert.x 实例
     * @return 初始化结果
     */
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        // 1. 类扫描信息
        final Set<Class<?>> scanned = OCacheClass.of(this.caller).value();
        // 2. 表名 = Class<?> ( Dao )
        final ConcurrentMap<String, Class<?>> daoMap = new ConcurrentHashMap<>();
        final ConcurrentMap<String, Class<?>> pojoMap = new ConcurrentHashMap<>();
        scanned.forEach(clazz -> {
            if (AbstractVertxDAO.class.isAssignableFrom(clazz)) {
                final String table = MDMeta.toTable(clazz);
                daoMap.put(table, clazz);
            } else if (VertxPojo.class.isAssignableFrom(clazz)) {
                final String table = MDMeta.toTable(clazz);
                pojoMap.put(table, clazz);
            }
        });
        // 3. 缓存填充
        final OCacheDao repository = OCacheDao.of(this.caller);
        final StringBuilder vLog = new StringBuilder("( Dao ) 元数据扫描结果 / {}：\n");
        new TreeSet<>(daoMap.keySet()).stream().filter(pojoMap::containsKey).forEach(table -> {
            final Class<?> daoCls = daoMap.get(table);
            final Class<?> pojoCls = pojoMap.get(table);
            final MDMeta meta = new MDMeta(daoCls, pojoCls);
            repository.add(meta);
            vLog.append(String.format("%32s", table))
                .append(" | \uD83C\uDF97️ ").append(String.format("%-88s", daoCls.getName()))
                .append(" \uD83E\uDED0 ").append(pojoCls.getName()).append("\n");
        });
        this.vLog(vLog.toString(), daoMap.size());
        // 3. 表名 = Class<?>
        return Future.succeededFuture(Boolean.TRUE);
    }
}
