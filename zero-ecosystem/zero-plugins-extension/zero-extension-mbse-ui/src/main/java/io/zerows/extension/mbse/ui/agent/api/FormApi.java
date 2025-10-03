package io.zerows.extension.mbse.ui.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.io.annotations.BodyParam;
import io.zerows.extension.mbse.ui.eon.Addr;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@EndPoint
@Path("/api")
public interface FormApi {
    /*
     * update form and related fields and options
     */
    @Path("/ui-form/cascade/:key")
    @PUT
    @Address(Addr.Control.PUT_FORM_CASCADE)
    JsonObject putFormCascade(@PathParam(KName.KEY) String key,
                              @BodyParam JsonObject body);

    /*
     * delete form and related fields and options
     */
    @Path("/ui-form/:key")
    @DELETE
    @Address(Addr.Control.DELETE_FORM)
    Boolean deleteForm(@PathParam(KName.KEY) String key);

}
