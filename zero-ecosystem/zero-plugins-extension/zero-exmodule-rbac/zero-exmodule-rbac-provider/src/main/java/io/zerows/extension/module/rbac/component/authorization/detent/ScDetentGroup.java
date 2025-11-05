package io.zerows.extension.module.rbac.component.authorization.detent;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.metadata.logged.ProfileRole;
import io.zerows.extension.module.rbac.component.authorization.ScDetent;

import java.util.List;

/*
 * Horizon Group
 */
public class ScDetentGroup implements ScDetent {

    private transient final JsonObject input;

    public ScDetentGroup(final JsonObject input) {
        this.input = input;
    }

    @Override
    public JsonObject proc(final List<ProfileRole> profiles) {
        final JsonObject group = new JsonObject();
        /* SeekGroup -> Horizon */
        group.mergeIn(ScDetent.Group.horizon().proc(profiles));
        /* SeekGroup -> Critical */
        group.mergeIn(ScDetent.Group.critical().proc(profiles));
        /* SeekGroup -> Overlook */
        group.mergeIn(ScDetent.Group.overlook().proc(profiles));

        return this.input.mergeIn(group);
    }
}
