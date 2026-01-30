package io.zerows.epoch.web;

import io.r2mo.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.zerows.support.Ut;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-10
 */
public class MDMeta implements Serializable {
    private static final ConcurrentMap<Class<?>, String> TABLE_MAP = new ConcurrentHashMap<>();

    private final Class<?> dao;
    private final Class<?> pojo;
    private final String table;
    @Getter
    private final boolean isEntity;

    public MDMeta(final Class<?> dao, final Class<?> pojo) {
        this.dao = dao;
        this.pojo = pojo;
        this.table = MDMeta.toTable(dao);
        // 基础规范中 R_ 为非实体表，而是关系表，如果使用此前缀会自动转换成关系表
        this.isEntity = !this.table.startsWith("R_");
    }

    /**
     * 转换成表名，从类名开始计算，这里支持两种模式的表计算
     * <pre><code>
     *     XMenuDao -> X_MENU
     *     XMenu -> X_MENU
     * </code></pre>
     *
     * @param clazz 类名
     * @return 表名
     */
    public static String toTable(final Class<?> clazz) {
        Objects.requireNonNull(clazz);
        // 缓存中提取，如果可提取提前结束
        if (TABLE_MAP.containsKey(clazz)) {
            return TABLE_MAP.get(clazz);
        }
        // 表名计算
        final String inputName = clazz.getSimpleName();
        final String input;
        if (AbstractVertxDAO.class.isAssignableFrom(clazz)) {
            // X????Dao -> X????
            input = inputName.substring(0, inputName.length() - 3);
        } else {
            // X???? -> X????
            input = inputName;
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char currentChar = input.charAt(i);
            // 当遇到大写字母时，在前面添加下划线
            if (Character.isUpperCase(currentChar) && i > 0) {
                result.append("_");
            }
            result.append(Character.toUpperCase(currentChar));
        }
        final String table = result.toString();
        // 缓存回写
        TABLE_MAP.put(clazz, table);
        return table;
    }

    public Class<?> dao() {
        return this.dao;
    }

    public Class<?> pojo() {
        return this.pojo;
    }

    public String table() {
        return this.table;
    }

    public String toLine() {
        return "\t" + Ut.fromAdjust(this.table, 20) + " | " +
            "Dao = " + this.dao.getName() + " / " +
            "Pojo = " + this.pojo.getName() + (this.isEntity ? "" : " ( REL ) ");
    }
}
