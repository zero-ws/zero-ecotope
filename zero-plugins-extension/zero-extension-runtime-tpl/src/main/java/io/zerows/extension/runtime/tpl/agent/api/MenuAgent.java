package io.zerows.extension.runtime.tpl.agent.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.runtime.tpl.eon.Addr;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@EndPoint
@Path("/api")
public interface MenuAgent {
    /*
     * Fetch Menu List by
     * - page
     * - position
     * - type
     */
    @POST
    @Path("/my/menu/fetch")
    @Address(Addr.Menu.MY_FETCH)
    JsonArray fetchMy(@BodyParam JsonObject body);

    /*
     * Create new My menu
     * - X_MENU
     *      - icon, text, uri
     * - X_MENU_MY
     *      - type， FIXED
     *      - page， FIXED
     *      - position， FIXED
     *      - owner, XHeader
     *      - uiSort    Ui
     *      - uiColorFg - Ui  ( Color Picker )
     *      - uiColorBg - Ui  ( Color Picker )
     *  When Tree
     *      - key
     *      - uiParent
     *
     * - Delete Condition
     * {
     *      "owner": "xxx",
     *      "page": "",
     *      "position": "",
     *      "type": ""
     *      "menus": [
     *      ]
     * }
     */
    @POST
    @Path("/my/menu/save")
    @Address(Addr.Menu.MY_SAVE)
    JsonArray saveMy(@BodyParam JsonObject body);
}
