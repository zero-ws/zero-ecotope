package io.zerows.epoch.spi.channel;

import io.zerows.support.Ut;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * Definition for each channel here and parsed configuration.
 */
public class KLexeme<T> implements Serializable {
    /*
     * Parameters
     */
    private final transient List<String> params = new ArrayList<>();
    /*
     * Interface class definition
     */
    private final transient Class<?> interfaceCls;
    private final transient T reference;

    /*
     * Implementation class definition
     */
    public KLexeme(final Class<?> interfaceCls, final T reference) {
        this.interfaceCls = interfaceCls;
        this.reference = reference;
        /* get names */
        this.parseParams(interfaceCls);
    }

    private void parseParams(final Class<?> interfaceCls) {
        /* Interface definition */
        final Field[] constants = interfaceCls.getDeclaredFields();
        final Set<String> fieldSet = new TreeSet<>();
        Arrays.stream(constants).map(Field::getName)
            .filter(item -> item.startsWith("ARG"))
            .forEach(fieldSet::add);
        fieldSet.forEach(field -> {
            /* Sequence should be here define */
            final Object value = Ut.field(interfaceCls, field);
            this.params.add((String) value);
        });
    }

    public T instance() {
        return this.reference;
    }

    public Class<?> clazz() {
        return this.interfaceCls;
    }

    public List<String> params() {
        return this.params;
    }
}
