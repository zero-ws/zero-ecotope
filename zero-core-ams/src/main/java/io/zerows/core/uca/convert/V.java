package io.zerows.core.uca.convert;

@SuppressWarnings("all")
public class V {

    public static Vto vInstant() {
        return CACHE.CCT_VTO.pick(InstantVto::new, InstantVto.class.getName());
    }

    public static Vto vInteger() {
        return CACHE.CCT_VTO.pick(IntVto::new, IntVto.class.getName());
    }

    public static Vto vLong() {
        return CACHE.CCT_VTO.pick(LongVto::new, LongVto.class.getName());
    }

    public static Vto vShort() {
        return CACHE.CCT_VTO.pick(ShortVto::new, ShortVto.class.getName());
    }

    public static Vto vDouble() {
        return CACHE.CCT_VTO.pick(DoubleVto::new, DoubleVto.class.getName());
    }

    public static Vto vFloat() {
        return CACHE.CCT_VTO.pick(FloatVto::new, FloatVto.class.getName());
    }

    public static Vto vBoolean() {
        return CACHE.CCT_VTO.pick(BooleanVto::new, BooleanVto.class.getName());
    }

    public static Vto vDate() {
        return CACHE.CCT_VTO.pick(DateVto::new, DateVto.class.getName());
    }
}
