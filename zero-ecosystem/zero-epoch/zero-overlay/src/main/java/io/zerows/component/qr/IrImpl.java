package io.zerows.component.qr;

import io.r2mo.base.dbe.syntax.QPager;
import io.r2mo.base.dbe.syntax.QSorter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.support.base.UtBase;

import java.util.HashSet;
import java.util.Set;

class IrImpl implements Ir {

    private QPager pager;
    private QSorter sorter;
    private Set<String> projection;
    private Criteria criteria;

    IrImpl(final JsonObject input) {
        // Building
        this.init(input);
    }

    @SuppressWarnings("unchecked")
    private void init(final JsonObject input) {
        this.pager = QPager.of(input.getJsonObject(VName.KEY_PAGER));
        this.sorter = QSorter.of(input.getJsonArray(VName.KEY_SORTER));
        this.projection = new HashSet<String>(input.getJsonArray(VName.KEY_PROJECTION).getList());
        this.criteria = Criteria.create(input.getJsonObject(VName.KEY_CRITERIA));
    }

    @Override
    public Set<String> getProjection() {
        return this.projection;
    }

    @Override
    public QPager getPager() {
        return this.pager;
    }

    @Override
    public QSorter getSorter() {
        return this.sorter;
    }

    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }

    @Override
    public void setQr(final String field, final Object value) {
        if (null == this.criteria) {
            this.criteria = Criteria.create(new JsonObject());
        }
        this.criteria.save(field, value);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject result = new JsonObject();
        result.put(VName.KEY_PAGER, this.pager.toJson());
        final JsonObject sorters = this.sorter.toJson();
        final JsonArray array = new JsonArray();
        UtBase.itJObject(sorters, Boolean.class).forEach(entry -> {
            final Boolean value = entry.getValue();
            final String key = entry.getKey();
            array.add(key + "," + (value ? "ASC" : "DESC"));
        });
        result.put(VName.KEY_PROJECTION, UtBase.toJArray(this.projection));
        result.put(VName.KEY_CRITERIA, this.criteria.toJson());
        return result;
    }
}
