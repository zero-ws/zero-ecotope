package io.zerows.extension.commerce.rbac.uca.authorization;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.uca.authorization.child.GcCritical;
import io.zerows.extension.commerce.rbac.uca.authorization.child.GcHorizon;
import io.zerows.extension.commerce.rbac.uca.authorization.child.GcOverlook;
import io.zerows.extension.commerce.rbac.uca.authorization.detent.*;
import io.zerows.extension.commerce.rbac.uca.authorization.direct.GdCritical;
import io.zerows.extension.commerce.rbac.uca.authorization.direct.GdHorizon;
import io.zerows.extension.commerce.rbac.uca.authorization.direct.GdOverlook;
import io.zerows.extension.commerce.rbac.uca.authorization.extend.GeCritical;
import io.zerows.extension.commerce.rbac.uca.authorization.extend.GeHorizon;
import io.zerows.extension.commerce.rbac.uca.authorization.extend.GeOverlook;
import io.zerows.extension.commerce.rbac.uca.authorization.inherit.GiCritical;
import io.zerows.extension.commerce.rbac.uca.authorization.inherit.GiHorizon;
import io.zerows.extension.commerce.rbac.uca.authorization.inherit.GiOverlook;
import io.zerows.extension.commerce.rbac.uca.authorization.parent.GpCritical;
import io.zerows.extension.commerce.rbac.uca.authorization.parent.GpHorizon;
import io.zerows.extension.commerce.rbac.uca.authorization.parent.GpOverlook;
import io.zerows.extension.commerce.rbac.uca.logged.ProfileGroup;
import io.zerows.extension.commerce.rbac.uca.logged.ProfileRole;
import io.zerows.unity.Ux;

import java.util.List;

/*
 * Detent for ( ProfileType = Detent )
 *
 * Be careful:
 * If there existing constructor data parameter such as JsonObject or List<ProfileGroup>, it means
 * each time this object will stored single data here, in this kind of situation, we could not use
 * Pool.DETENT_POOL, if you used Pool.DETENT_POOL, the system will ignore
 * input parameter and used cached instead, it means that different user authorization may
 * shared the first time input parameters. It's wrong.
 *
 * There are some points:
 * 1. For tool object ( No constructor parameters ), we could cached ScDetent.
 * 2. For non tool object ( Input constructor parameters ), we mustn't cached ScDetent.
 */
public interface ScDetent {

    static ScDetent user(final JsonObject input) {
        return new ScDetentRole(input);
    }

    static ScDetent group(final JsonObject input) {
        return new ScDetentGroup(input);
    }

    static ScDetent parent(final JsonObject input,
                           final List<ProfileGroup> profiles) {
        return new ScDetentParent(input, profiles);
    }

    static ScDetent inherit(final JsonObject input,
                            final List<ProfileGroup> profiles) {
        return new ScDetentInherit(input, profiles);
    }

    static ScDetent extend(final JsonObject input,
                           final List<ProfileGroup> profiles) {
        return new ScDetentExtend(input, profiles);
    }

    static ScDetent children(final JsonObject input,
                             final List<ProfileGroup> profiles) {
        return new ScDetentChild(input, profiles);
    }

    JsonObject proc(List<ProfileRole> profiles);

    default Future<JsonObject> procAsync(final List<ProfileRole> profiles) {
        return Ux.future(this.proc(profiles));
    }

    /*
     * Internal default group
     */
    interface Group {


        Cc<String, ScDetent> CC_DETENT = Cc.open();

        static ScDetent horizon() {
            return CC_DETENT.pick(GdHorizon::new, GdHorizon.class.getName());
            // return RFn.po?l(Pool.DETENT_POOL, GdHorizon.class.getName(), GdHorizon::new);
        }

        static ScDetent critical() {
            return CC_DETENT.pick(GdCritical::new, GdCritical.class.getName());
            // return RFn.po?l(Pool.DETENT_POOL, GdCritical.class.getName(), GdCritical::new);
        }

        static ScDetent overlook() {
            return CC_DETENT.pick(GdOverlook::new, GdOverlook.class.getName());
            // return RFn.p?ol(Pool.DETENT_POOL, GdOverlook.class.getName(), GdOverlook::new);
        }

        /*
         * Group : Parent ( Exclude Current )
         */
        interface Parent {

            static ScDetent horizon() {
                return CC_DETENT.pick(GpHorizon::new, GpHorizon.class.getName());
                // return RFn.po?l(Pool.DETENT_POOL, GpHorizon.class.getName(), GpHorizon::new);
            }

            static ScDetent critical(final List<ProfileGroup> original) {
                return new GpCritical(original);
            }

            static ScDetent overlook(final List<ProfileGroup> original) {
                return new GpOverlook(original);
            }
        }

        /*
         * Group : Child ( Exclude Current )
         */
        interface Child {
            static ScDetent horizon() {
                return CC_DETENT.pick(GcHorizon::new, GcHorizon.class.getName());
                // return RFn.po?l(Pool.DETENT_POOL, GcHorizon.class.getName(), GcHorizon::new);
            }

            static ScDetent critical(final List<ProfileGroup> original) {
                return new GcCritical(original);
            }

            static ScDetent overlook(final List<ProfileGroup> original) {
                return new GcOverlook(original);
            }
        }

        interface Inherit {
            static ScDetent horizon(final List<ProfileGroup> original) {
                return new GiHorizon(original);
            }

            static ScDetent critical(final List<ProfileGroup> original) {
                return new GiCritical(original);
            }

            static ScDetent overlook(final List<ProfileGroup> original) {
                return new GiOverlook(original);
            }
        }

        interface Extend {
            static ScDetent horizon(final List<ProfileGroup> original) {
                return new GeHorizon(original);
            }

            static ScDetent critical(final List<ProfileGroup> original) {
                return new GeCritical(original);
            }

            static ScDetent overlook(final List<ProfileGroup> original) {
                return new GeOverlook(original);
            }
        }
    }
}
