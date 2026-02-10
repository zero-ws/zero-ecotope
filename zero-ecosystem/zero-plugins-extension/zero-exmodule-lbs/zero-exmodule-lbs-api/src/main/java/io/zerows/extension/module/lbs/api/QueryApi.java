package io.zerows.extension.module.lbs.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@EndPoint
@Path("/api")
public interface QueryApi {
    /*
     * Countries
     */
    @Path("/countries")
    @GET
    @Address(Addr.PickUp.COUNTRIES)
    @OpenApi
    String queryCountries();

    /*
     * State from Country
     */
    @Path("/states/query/{countryId}")
    @GET
    @Address(Addr.PickUp.STATE_BY_COUNTRY)
    @OpenApi
    String queryStates(@PathParam("countryId") String countryId);

    /*
     * City from State
     */
    @Path("/cities/query/{stateId}")
    @GET
    @Address(Addr.PickUp.CITY_BY_STATE)
    @OpenApi
    String queryCities(@PathParam("stateId") String stateId);

    /*
     * Region from City
     */
    @Path("/regions/query/{cityId}")
    @GET
    @Address(Addr.PickUp.REGION_BY_CITY)
    @OpenApi
    String queryRegions(@PathParam("cityId") String cityId);

    /*
     * When init based join Region here
     */
    @Path("/regions/meta/{id}")
    @GET
    @Address(Addr.PickUp.REGION_META)
    @OpenApi
    String initRegion(@PathParam("id") String id);

    @Path("/tents")
    @GET
    @Address(Addr.PickUp.TENT_BY_SIGMA)
    @OpenApi
    String getTents(@HeaderParam(KWeb.HEADER.X_SIGMA) String sigma);

    @Path("/floors")
    @GET
    @Address(Addr.PickUp.FLOOR_BY_SIGMA)
    @OpenApi
    String getFloors(@HeaderParam(KWeb.HEADER.X_SIGMA) String sigma);
}
