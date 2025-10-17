package io.zerows.extension.runtime.report.uca.process;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.report.atom.RAggregator;
import io.zerows.extension.runtime.report.atom.RDimension;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpDimension;
import io.zerows.extension.runtime.report.eon.RpConstant;
import io.zerows.extension.runtime.report.eon.em.EmDim;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-10-29
 */
class DimProcTree extends AbstractDimProc {

    DimProcTree(final HBundle owner) {
        super(owner);
    }

    @Override
    public Future<RDimension> dimAsync(final JsonObject params, final JsonArray source, final KpDimension dimension) {
        // 维度定义处理
        final JsonObject treeGroup = Ut.toJObject(dimension.getDataGroup());
        final EmDim.Type.Tree treeRegion = Ut.toEnum(Ut.valueString(treeGroup, "region"), EmDim.Type.Tree.class, null);
        if (Objects.isNull(treeRegion)) {
            return Ut.future();
        }

        final JsonArray processed = this.dimBuild(source, treeRegion, treeGroup);
        final RDimension dimNorm = new RDimension(dimension.getCode());
        dimNorm.data(processed);
        {
            final JsonObject dimField = Ut.toJObject(dimension.getDataField());
            Ut.<JsonObject>itJObject(dimField).forEach(entry -> {
                final RAggregator aggregator = new RAggregator(entry.getValue());
                dimNorm.rule(entry.getKey(), aggregator);
            });
        }

        return Ut.future(dimNorm);
    }

    private JsonArray dimBuild(final JsonArray data, final EmDim.Type.Tree mode,
                               final JsonObject config) {
        final String keyTree = Ut.valueString(config, "keyTree", KName.PARENT_ID);
        final String keyId = Ut.valueString(config, "keyId", KName.KEY);
        final String keyLeaf = Ut.valueString(config, "keyLeaf", "leaf");

        final String labelField = Ut.valueString(config, "labelField");
        final String labelDivider = Ut.valueString(config, "labelDivider");
        final Kv<String, String> labelConfig = Kv.create(labelField, labelDivider);

        final JsonArray result = new JsonArray();
        if (EmDim.Type.Tree.LEAF == mode) {
            // 只选子节点
            Ut.itJArray(data).filter(item -> Ut.valueT(item, keyLeaf, Boolean.class)).forEach(item -> {
                /*
                 * 提交 fieldLabel / fieldKey
                 * fieldLabel -> dimDisplay
                 * fieldKey -> dimKey
                 */
                final JsonObject itemFinal = item.copy();
                itemFinal.put(RpConstant.DimField.DISPLAY, this.dimDisplay(item, data, labelConfig));       // 注意区别
                itemFinal.put(RpConstant.DimField.KEY, Ut.valueString(item, keyId));
                result.add(itemFinal);
            });
        } else if (EmDim.Type.Tree.ROOT == mode) {
            // 只选根节点
            Ut.itJArray(data).filter(item -> Ut.isNil(Ut.valueString(item, keyTree))).forEach(item -> {
                // 遍历当前节点
                final JsonObject itemFinal = item.copy();
                // 儿子
                itemFinal.put(RpConstant.DimField.CHILDREN, this.dimChildren(item, data, keyId));
                // 自身
                itemFinal.put(RpConstant.DimField.DISPLAY, Ut.valueString(item, labelField));               // 注意区别
                itemFinal.put(RpConstant.DimField.KEY, Ut.valueString(item, keyId));
                result.add(itemFinal);
            });
        } else {
            // 全选节点
            Ut.itJArray(data).forEach(item -> {
                // 遍历当前节点
                final JsonObject itemFinal = item.copy();
                // 儿子
                itemFinal.put(RpConstant.DimField.CHILDREN, this.dimChildren(item, data, keyId));
                // 自身
                itemFinal.put(RpConstant.DimField.DISPLAY, this.dimDisplay(item, data, labelConfig));       // 注意区别
                //todo
                itemFinal.put(RpConstant.DimField.KEY, Ut.valueString(item, labelField));
                result.add(itemFinal);
            });
        }
        return result;
    }

    private JsonArray dimChildren(final JsonObject item, final JsonArray data, final String keyId) {
        final JsonArray children = Ut.elementClimb(item, data);
        final Set<String> ids = new HashSet<>();
        Ut.itJArray(children).forEach(child -> ids.add(Ut.valueString(child, keyId)));
        return Ut.toJArray(ids);
    }

    private String dimDisplay(final JsonObject item, final JsonArray data, final Kv<String, String> config) {
        final String labelField = config.key();
        final String labelDivider = config.value();
        final JsonArray branch = Ut.elementClimb(item, data);
        final List<String> labels = new ArrayList<>();
        for (int idx = branch.size() - 1; idx >= 0; idx--) {
            final JsonObject node = branch.getJsonObject(idx);
            labels.add(Ut.valueString(node, labelField));
        }
        return Ut.fromJoin(labels, labelDivider);
    }
}
