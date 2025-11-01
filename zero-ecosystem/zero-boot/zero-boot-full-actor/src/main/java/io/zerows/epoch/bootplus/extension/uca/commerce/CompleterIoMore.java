package io.zerows.epoch.bootplus.extension.uca.commerce;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.boot.extension.util.Ox;
import io.zerows.epoch.bootplus.extension.uca.concrete.Arrow;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowAdd;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowDelete;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowEdit;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowFind;
import io.zerows.epoch.bootplus.extension.uca.plugin.SwitcherAgile;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.atom.data.DataGroup;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.operation.HDao;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class CompleterIoMore implements CompleterIo<JsonArray> {

    protected final transient HDao dao;
    protected final transient DataAtom atom;
    protected transient Switcher switcher;

    protected CompleterIoMore(final HDao dao, final DataAtom atom) {
        this.atom = atom;
        this.dao = dao;
    }


    @Override
    public CompleterIo<JsonArray> bind(final Switcher switcher) {
        /*
         * Batch 模式下，永远维持 ArrowSwitcher 静态处理
         * 也就是说 ArrowSwitcher 在批量模式下为局部变量，而不使用单独变量
         */
        this.switcher = switcher;
        return this;
    }

    @Override
    public Future<JsonArray> create(final JsonArray input) {
        return this.switcher(input).compose(groupSet ->
            this.compressor(groupSet, ArrowAdd::new));
    }

    @Override
    public Future<JsonArray> update(final JsonArray input) {
        return this.switcher(input).compose(groupSet ->
            this.compressor(groupSet, ArrowEdit::new));
    }

    @Override
    public Future<JsonArray> remove(final JsonArray input) {
        return this.switcher(input).compose(groupSet ->
            this.compressor(groupSet, ArrowDelete::new));
    }

    @Override
    public Future<JsonArray> find(final JsonArray input) {
        return this.switcher(input).compose(groupSet ->
            this.compressor(groupSet, ArrowFind::new));
    }

    /*
     * 动态静态转换专用方法，如果绑定的 switcher = null 则执行切换
     * 从动态切换到静态中处理
     */
    protected Future<Set<DataGroup>> switcher(final JsonArray input) {
        if (Objects.isNull(this.switcher)) {
            final Set<DataGroup> groups = new HashSet<>();
            groups.add(DataGroup.create(this.atom).add(input));
            return Ux.future(groups);
        } else {
            return this.switcher.atom(input, this.atom);
        }
    }

    private Future<JsonArray> compressor(final Set<DataGroup> group, final Supplier<Arrow> supplier) {
        return Ox.runGroup(group, (input, atom) -> {
            final SwitcherAgile switcher = new SwitcherAgile().initialize(atom, this.dao);
            return switcher.switchAsync(supplier).compose(arrow -> arrow.processAsync(input));
        });
    }
}
