package io.zerows.epoch.common.uca.qr.syntax;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.uca.qr.Criteria;
import io.zerows.epoch.common.uca.qr.Pager;
import io.zerows.epoch.common.uca.qr.Sorter;
import io.zerows.epoch.support.UtBase;

import java.util.HashSet;
import java.util.Set;

class IrQr implements Ir {

    private Pager pager;
    private Sorter sorter;
    private Set<String> projection;
    private Criteria criteria;

    IrQr(final JsonObject input) {
        // Building
        this.init(input);
    }

    @SuppressWarnings("unchecked")
    private void init(final JsonObject input) {
        this.pager = Pager.create(input.getJsonObject(KEY_PAGER));
        this.sorter = Sorter.create(input.getJsonArray(KEY_SORTER));
        this.projection = new HashSet<String>(input.getJsonArray(KEY_PROJECTION).getList());
        this.criteria = Criteria.create(input.getJsonObject(KEY_CRITERIA));
    }

    @Override
    public Set<String> getProjection() {
        return this.projection;
    }

    @Override
    public Pager getPager() {
        return this.pager;
    }

    @Override
    public Sorter getSorter() {
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
        result.put(KEY_PAGER, this.pager.toJson());
        final JsonObject sorters = this.sorter.toJson();
        final JsonArray array = new JsonArray();
        UtBase.itJObject(sorters, Boolean.class).forEach(entry -> {
            final Boolean value = entry.getValue();
            final String key = entry.getKey();
            array.add(key + "," + (value ? "ASC" : "DESC"));
        });
        result.put(KEY_PROJECTION, UtBase.toJArray(this.projection));
        result.put(KEY_CRITERIA, this.criteria.toJson());
        return result;
    }
}
