package io.zerows.extension.module.ambient.component;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.r2mo.typed.enums.DatabaseType;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.support.Ut;

public class BuilderOfDBS extends AbstractBuilder<DBS> {
    @Override
    public <R> DBS create(final R source) {
        if (source instanceof final XSource sourceX) {
            // 动态构造数据库
            final Database database = new Database();
            final String nameDynamic = sourceX.getId();
            database.name(nameDynamic);
            database.setHostname(sourceX.getHostname());
            database.setInstance(sourceX.getInstance());
            database.setPort(sourceX.getPort());
            database.setUsername(sourceX.getUsername());
            database.setPassword(sourceX.getPassword());


            // 连接属性
            final String category = sourceX.getCategory();
            final DatabaseType type = Ut.toEnum(category, DatabaseType.class, DatabaseType.MYSQL_8);
            database.setType(type);
            database.setUrl(sourceX.getJdbcUrl());
            database.setDriverClassName(sourceX.getDriverClassName());


            // 数据库配置
            final JsonObject jdbcConfig = Ut.toJObject(sourceX.getJdbcConfig());
            final JObject optionJ = SPI.J();
            optionJ.put(jdbcConfig.getMap());
            database.setOptions(optionJ);

            // 数据库扩展配置
            final JsonObject jdbcExtension = Ut.toJObject(sourceX.getMetadata());
            Ut.itJObject(jdbcExtension)
                .forEach(entry -> database.putExtension(entry.getKey(), entry.getValue()));

            // 构造 DBS
            return DBMany.of().put(nameDynamic, database);
        }
        return null;
    }
}
