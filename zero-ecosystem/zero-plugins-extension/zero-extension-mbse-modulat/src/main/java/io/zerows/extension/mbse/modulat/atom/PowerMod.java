package io.zerows.extension.mbse.modulat.atom;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.extension.mbse.modulat.store.OCacheMod;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HMod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 模块能源配置，此能源配置用来构造 BLOCK 中的核心数据，作为 PowerApp 的基础，底层用于构造 {@link OCacheMod} 所需的数据
 * 结构。
 *
 * @author lang : 2024-07-08
 */
public class PowerMod implements HMod {
    private static final ConcurrentMap<String, Class<?>> BLOCK_TYPE = new ConcurrentHashMap<>() {
        {
            // Common string configuration here
            this.put("STRING", String.class);

            // Integer Here for configuration
            this.put("INTEGER", Integer.class);

            // Boolean here for enable/disable configuration
            this.put("BOOLEAN", Boolean.class);

            // Float here for decimal processing
            this.put("DECIMAL", BigDecimal.class);

            // TIME / DATE / DATETIME
            this.put("TIME", LocalTime.class);
            this.put("DATE", LocalDate.class);
            this.put("DATETIME", LocalDateTime.class);
        }
    };

    private final ConcurrentMap<String, Object> storedData = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Class<?>> storedType = new ConcurrentHashMap<>();
    private final String name;
    private HApp app;

    public PowerMod(final String name, final JsonObject dataJ) {
        this.name = name;
        /*
         * Field `field = get`
         * __metadata captured `field = Class<?>`
         */
        final JsonObject metadata = Ut.valueJObject(dataJ, KName.__.METADATA);
        dataJ.fieldNames().forEach(field -> {
            /*
             * storedType processing
             */
            final String typeStr = metadata.getString(field);
            if (Ut.isNotNil(typeStr)) {
                /*
                 * get processing
                 */
                final Object value = dataJ.getValue(field);
                if (Objects.nonNull(value)) {
                    // Fix: java.lang.NullPointerException
                    this.storedData.put(field, value);
                }
                final Class<?> clazz = BLOCK_TYPE.getOrDefault(typeStr, String.class);
                this.storedType.put(field, clazz);
            }
        });
    }

    @Override
    public HMod app(final HApp appRef) {
        this.app = appRef;
        return this;
    }

    @Override
    public HApp app() {
        return this.app;
    }

    @Override
    public String id() {
        if (Objects.isNull(this.app)) {
            return "*" + VString.SLASH + this.name;
        } else {
            return this.app.id() + VString.SLASH + this.name;
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    @SuppressWarnings("all")
    public <T> T value(final String field, final T defaultValue) {
        final Object value = this.storedData.getOrDefault(field, null);
        if (Objects.nonNull(value)) {
            final Class<?> type = this.storedType.getOrDefault(field, String.class);
            return (T) Ut.aiValue(value.toString(), type);
        } else {
            return defaultValue;
        }
    }
}
