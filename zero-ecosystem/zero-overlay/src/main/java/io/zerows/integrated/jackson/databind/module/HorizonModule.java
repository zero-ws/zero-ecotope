package io.zerows.integrated.jackson.databind.module;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.MonthDayDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.DurationKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.InstantKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.MonthDayKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.OffsetTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.PeriodKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.YearMonthKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneIdKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZoneOffsetKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.ZonedDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.databind.AdjustDateTimeDeserializer;
import io.zerows.integrated.jackson.databind.ByteArraySerializer;
import io.zerows.integrated.jackson.databind.JsonArrayDeserializer;
import io.zerows.integrated.jackson.databind.JsonArraySerializer;
import io.zerows.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.integrated.jackson.databind.JsonObjectSerializer;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;

/**
 * @author lang : 2023-05-09
 */
public class HorizonModule extends SimpleModule {
    @Serial
    private static final long serialVersionUID = 1L;

    public HorizonModule() {
        super(PackageVersion.VERSION);
        // Serializer
        this.addSerializer(JsonObject.class, new JsonObjectSerializer());
        this.addSerializer(JsonArray.class, new JsonArraySerializer());
        this.addSerializer(byte[].class, new ByteArraySerializer());
        // this.addSerializer(KRuleTerm.class, new RuleTermSerializer());
        // Deserializer
        this.addDeserializer(JsonObject.class, new JsonObjectDeserializer());
        this.addDeserializer(JsonArray.class, new JsonArrayDeserializer());
        // this.addDeserializer(KRuleTerm.class, new RuleTermDeserializer());
        // Default Time
        this.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        this.addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME);
        this.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
        this.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        // Fix Date Time issue
        this.addDeserializer(LocalDateTime.class, new AdjustDateTimeDeserializer());
        this.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        this.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
        this.addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
        this.addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
        this.addDeserializer(Period.class, JSR310StringParsableDeserializer.PERIOD);
        this.addDeserializer(Year.class, YearDeserializer.INSTANCE);
        this.addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
        this.addDeserializer(ZoneId.class, JSR310StringParsableDeserializer.ZONE_ID);
        this.addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);
        this.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        this.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        this.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        this.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        this.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
        this.addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
        this.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        this.addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
        this.addSerializer(Period.class, new ToStringSerializer(Period.class));
        this.addSerializer(Year.class, YearSerializer.INSTANCE);
        this.addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);
        this.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        this.addSerializer(ZoneId.class, new ToStringSerializer(ZoneId.class));
        this.addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));
        this.addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);
        this.addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(Instant.class, InstantKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(LocalTime.class, LocalTimeKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(MonthDay.class, MonthDayKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(OffsetDateTime.class, OffsetDateTimeKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(OffsetTime.class, OffsetTimeKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(Period.class, PeriodKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(Year.class, YearKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(YearMonth.class, YearMonthKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(ZonedDateTime.class, ZonedDateTimeKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(ZoneId.class, ZoneIdKeyDeserializer.INSTANCE);
        this.addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);
    }

    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(context);
        context.addValueInstantiators(new ValueInstantiators.Base() {
            @Override
            public ValueInstantiator findValueInstantiator(final DeserializationConfig config, final BeanDescription beanDesc, final ValueInstantiator defaultInstantiator) {
                final JavaType type = beanDesc.getType();
                final Class<?> raw = type.getRawClass();
                if (ZoneId.class.isAssignableFrom(raw) && defaultInstantiator instanceof final StdValueInstantiator inst) {
                    final AnnotatedClass ac;
                    if (raw == ZoneId.class) {
                        ac = beanDesc.getClassInfo();
                    } else {
                        ac = AnnotatedClassResolver.resolve(config, config.constructType(ZoneId.class), config);
                    }

                    if (!inst.canCreateFromString()) {
                        final AnnotatedMethod factory = HorizonModule.this._findFactory(ac, "of", String.class);
                        if (factory != null) {
                            inst.configureFromStringCreator(factory);
                        }
                    }
                }

                return defaultInstantiator;
            }
        });
    }

    @SuppressWarnings("all")
    /*
     * [WARN]
     * - String name
     * - Class... argTypes
     * - if(!argType.isAssignableFrom(argTypes[i]))
     */
    protected AnnotatedMethod _findFactory(final AnnotatedClass cls, final String name, final Class... argTypes) {
        final int argCount = argTypes.length;
        final Iterator<?> var5 = cls.getFactoryMethods().iterator();

        AnnotatedMethod method;
        do {
            if (!var5.hasNext()) {
                return null;
            }

            method = (AnnotatedMethod) var5.next();
        } while (!name.equals(method.getName()) || method.getParameterCount() != argCount);

        for (int i = 0; i < argCount; ++i) {
            final Class<?> argType = method.getParameter(i).getRawType();
            if (!argType.isAssignableFrom(argTypes[i])) {
            }
        }

        return method;
    }
}
