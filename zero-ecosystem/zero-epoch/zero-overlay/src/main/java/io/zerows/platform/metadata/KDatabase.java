package io.zerows.platform.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.annotations.ClassYml;
import io.zerows.platform.enums.EmDS;
import io.zerows.specification.atomic.HCopier;
import io.zerows.specification.atomic.HJson;
import io.zerows.support.base.UtBase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 抽象数据配置
 * 原 Database 部分从此处继承，实现标准的数据库相关定义，且后续所有数据库定义可以直接在 HDatabase 中继承
 * 而不需要开发自定义的数据库适配定义等信息
 *
 * @author lang : 2023/5/2
 */
@Data
@ClassYml
@Slf4j
public class KDatabase implements Serializable, HCopier<KDatabase>, HJson {

    /**
     * 📋 数据库配置常量
     * <pre>
     *     🎯 功能说明：
     *     - 定义数据库配置的 JSON 键名常量
     *     - 提供统一的配置项访问接口
     *     - 避免硬编码字符串错误
     * </pre>
     */
    public static class Option {
        public static final String CATEGORY = "category";
        public static final String HOSTNAME = "hostname";
        public static final String PORT = "port";
        public static final String INSTANCE = "instance";
        public static final String URL = "url";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String DRIVER_CLASS_NAME = "driver-class-name";
        public static final String OPTIONS = "options";
    }

    /*
     * Get current jooq configuration for EmApp / Source
     */
    /* Database options for different pool */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject options = new JsonObject();
    /* Database host name */
    private String hostname;
    /* Database instance name */
    private String instance;
    /* Database port number */
    private Integer port;
    /* Database category */
    private EmDS.Category category = EmDS.Category.MYSQL8;
    /* JDBC connection string */
    private String url;
    /* Database username */
    private String username;
    /* Database password */
    private String password;
    /* Database driver class */
    @JsonProperty(Option.DRIVER_CLASS_NAME)      // 升级新版
    private String driverClassName;

    /* Database Connection Testing */
    public static boolean test(final KDatabase database) {
        try {
            DriverManager.getConnection(database.getUrl(), database.getUsername(), database.getSmartPassword());
            return true;
        } catch (final SQLException ex) {
            // Debug for database connection
            ex.printStackTrace();
            return false;
        }
    }

    public static KDatabase configure(final JsonObject databaseJ) {
        final JsonObject jooq = UtBase.valueJObject(databaseJ);
        final KDatabase database = new KDatabase();
        database.fromJson(jooq);
        return database;
    }


    /* Database Connection Testing */
    public boolean test() {
        return KDatabase.test(this);
    }

    public String getSmartPassword() {
        final Boolean enabled = ENV.of().get(EnvironmentVariable.HED_ENABLED, false, Boolean.class);
        log.info("[ ZERO ] HED 加解密模块是否启用: {}", enabled);
        if (enabled) {
            // HED_ENABLED=true
            return UtBase.decryptRSAV(this.password);
        } else {
            return this.password;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getOption(final String optionKey) {
        final JsonObject options = this.options;
        final Object value = options.getValue(optionKey);
        return Objects.isNull(value) ? null : (T) value;
    }

    public <T> T getOption(final String optionKey, final T defaultValue) {
        final T result = this.getOption(optionKey);
        return Objects.isNull(result) ? defaultValue : result;
    }

    public Long getLong(final String optionKey, final Long defaultValue) {
        final Long result = this.getLong(optionKey);
        return Objects.isNull(result) ? defaultValue : result;
    }

    public Long getLong(final String optionKey) {
        final JsonObject options = this.options;
        return options.getLong(optionKey);
    }

    public JsonObject getOptions() {
        return Objects.isNull(this.options) ? new JsonObject() : this.options;
    }

    @Override
    public JsonObject toJson() {
        /*
         * 由于不同类型序列化在应用中有所差异，所以此处序列化的标准方法
         * 采用手工执行，而不直接使用序列化操作，若使用序列化则不同序列化
         * 子系统会导致序列化结果不一致
         */
        final JsonObject databaseJ = new JsonObject();
        databaseJ.put(Option.CATEGORY, this.category.name());
        databaseJ.put(Option.HOSTNAME, this.hostname);
        databaseJ.put(Option.PORT, this.port);
        databaseJ.put(Option.INSTANCE, this.instance);
        databaseJ.put(Option.URL, this.url);
        databaseJ.put(Option.USERNAME, this.username);
        databaseJ.put(Option.PASSWORD, this.password);
        databaseJ.put(Option.DRIVER_CLASS_NAME, this.driverClassName);
        databaseJ.put(Option.OPTIONS, this.options);
        return databaseJ;
    }

    @Override
    public void fromJson(final JsonObject data) {
        if (UtBase.isNotNil(data)) {
            // category
            this.category = UtBase.toEnum(() -> data.getString(Option.CATEGORY), EmDS.Category.class, EmDS.Category.MYSQL5);
            // hostname
            this.hostname = data.getString(Option.HOSTNAME);
            // port
            this.port = data.getInteger(Option.PORT);
            // instance
            this.instance = data.getString(Option.INSTANCE);
            this.url = data.getString(Option.URL);
            // username
            this.username = data.getString(Option.USERNAME);
            // password
            this.password = data.getString(Option.PASSWORD);
            this.driverClassName = data.getString(Option.DRIVER_CLASS_NAME);
            // options
            final JsonObject options = UtBase.valueJObject(data, Option.OPTIONS);
            if (UtBase.isNotNil(options)) {
                this.options.mergeIn(options);
                log.info("[ ZERO ] 数据库配置项：{}", this.options.encode());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KDatabase> CHILD copy() {
        final JsonObject json = this.toJson().copy();
        final KDatabase database = new KDatabase();
        database.fromJson(json);
        return (CHILD) database;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KDatabase kDatabase = (KDatabase) o;
        return Objects.equals(this.url, kDatabase.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url);
    }

    @Override
    public String toString() {
        return "Database{" +
            "hostname='" + this.hostname + '\'' +
            ", instance='" + this.instance + '\'' +
            ", port=" + this.port +
            ", category=" + this.category +
            ", jdbcUrl='" + this.url + '\'' +
            ", username='" + this.username + '\'' +
            ", password='" + this.password + '\'' +
            ", driverClassName='" + this.driverClassName + '\'' +
            ", options=" + (Objects.isNull(this.options) ? "{}" : this.options.encodePrettily()) +
            '}';
    }
}