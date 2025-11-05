package io.zerows.extension.module.rbac.component.authorization.detent;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.metadata.logged.ProfileGroup;
import io.zerows.extension.module.rbac.metadata.logged.ProfileRole;
import io.zerows.extension.module.rbac.component.authorization.ScDetent;

import java.util.List;

public class ScDetentExtend implements ScDetent {

    private transient final JsonObject input;
    private transient final List<ProfileGroup> original;

    public ScDetentExtend(final JsonObject input,
                          final List<ProfileGroup> original) {
        this.input = input;
        this.original = original;
    }

    @Override
    public JsonObject proc(final List<ProfileRole> profiles) {
        final JsonObject extend = new JsonObject();
        /* SeekGroup = EXTEND_HORIZON */
        extend.mergeIn(ScDetent.Group.Extend.horizon(this.original).proc(profiles));
        /* SeekGroup = EXTEND_CRITICAL */
        extend.mergeIn(ScDetent.Group.Extend.critical(this.original).proc(profiles));
        /* SeekGroup = EXTEND_OVERLOOK */
        extend.mergeIn(ScDetent.Group.Extend.overlook(this.original).proc(profiles));
        return this.input.mergeIn(extend);
    }
}
