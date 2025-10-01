package io.zerows.extension.mbse.basement.uca.reference;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.support.FnBase;
import io.zerows.epoch.common.shared.reference.RResult;
import io.zerows.epoch.common.shared.reference.RRule;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.atom.element.DataTpl;
import io.zerows.extension.mbse.basement.exception._80540Exception501AnonymousAtom;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.HReference;
import io.zerows.unity.Ux;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ## Abstract Reference Processor
 *
 * ### 1. Intro
 *
 * Template pattern to provide all the calculation metadata in current class, all sub-classes could share the data structure that current class defined.
 *
 * ### 2. Components
 *
 * Here are two hash maps that stored `field = xx`, the `xx` means components of following two categories:
 *
 * - RaySource: The field calculator component that contains code logical ( Action ).
 * - DataQRule: The field definition rules POJO data object that contains metadata definition ( Rule ).
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractRay<T> implements AoRay<T> {
    /**
     * The reference to {@link io.zerows.extension.mbse.basement.atom.element.DataTpl} that be related to model definition.
     */
    protected transient DataTpl tpl;
    /**
     * The hashmap reference of `field = {@link RaySource}`.
     */
    protected transient ConcurrentMap<String, RaySource> input =
        new ConcurrentHashMap<>();
    /**
     * The hashmap reference of `field = {@link RRule}`.
     */
    protected transient ConcurrentMap<String, RResult> output =
        new ConcurrentHashMap<>();

    /**
     * Bind the component to data model template {@link io.zerows.extension.mbse.basement.atom.element.DataTpl}.
     *
     * The critical code logical is as following:
     *
     * - Bind the {@link io.zerows.extension.mbse.basement.atom.element.DataTpl} to instance member `tpl`.
     * - Be sure the {@link DataAtom} in {@link io.zerows.extension.mbse.basement.atom.element.DataTpl} is valid.
     * - Calculate the two hash maps in this method.
     *
     * @param tpl {@link io.zerows.extension.mbse.basement.atom.element.DataTpl} The template that will be bind.
     *
     * @return {@link AoRay} The component reference
     * @throws _80540Exception501AnonymousAtom Atom in tpl contains errors.
     */
    @Override
    public AoRay<T> on(final DataTpl tpl) {
        this.tpl = tpl;
        final DataAtom atom = tpl.atom();
        if (Objects.isNull(atom)) {
            throw new _80540Exception501AnonymousAtom();
        }
        final HReference reference = atom.reference();
        reference.refInput().forEach((identifier, quote) -> {
            /* RaySource */
            final RaySource source = RaySource.create(quote);
            this.input.put(identifier, source);
        });
        this.output.putAll(reference.refOutput());
        return this;
    }

    /**
     * This method will modify the input {@link HRecord} element(s).
     *
     * Here contains `shorten` code logical when the hash map is EMPTY, skip reference calculator.
     *
     * @param input Input element of {@link HRecord} for single/multi
     *
     * @return Return the modified data record(s).
     */
    @Override
    public T doRay(final T input) {
        if (this.input.isEmpty()) {
            return input;
        } else {
            return this.exec(input);
        }
    }

    @Override
    public Future<T> doRayAsync(final T input) {
        if (this.input.isEmpty()) {
            return Ux.future(input);
        } else {
            return this.execAsync(input);
        }
    }

    /**
     * This method must be inherit by all sub-classes, it provide reference data mounting.
     *
     * @param input Input element of {@link HRecord} for single/multi
     *
     * @return Return the modified data record(s).
     */
    public abstract T exec(T input);

    public abstract Future<T> execAsync(T input);

    protected Future<ConcurrentMap<String, JsonArray>> thenCombine(final List<Future<ConcurrentMap<String, JsonArray>>> futures) {
        return FnBase.combineT(futures).compose(listMap -> {
            final ConcurrentMap<String, JsonArray> response = new ConcurrentHashMap<>();
            listMap.forEach(response::putAll);
            return Ux.future(response);
        });
    }
}
