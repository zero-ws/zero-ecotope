package io.zerows.extension.commerce.lbs.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LCityDao;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LCountryDao;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LFloorDao;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LRegionDao;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LStateDao;
import io.zerows.extension.commerce.lbs.domain.tables.daos.LTentDao;
import io.zerows.extension.commerce.lbs.domain.tables.pojos.LCity;
import io.zerows.extension.commerce.lbs.domain.tables.pojos.LRegion;
import io.zerows.extension.commerce.lbs.domain.tables.pojos.LState;
import io.zerows.extension.commerce.lbs.eon.Addr;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.function.Supplier;

@Queue
public class QueryActor {
    @Address(Addr.PickUp.COUNTRIES)
    public Future<JsonArray> queryCountries(final Envelop request) {
        return DB.on(LCountryDao.class)
            .fetchAllAsync()
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.STATE_BY_COUNTRY)
    public Future<JsonArray> queryStates(final String countryId) {
        return DB.on(LStateDao.class)
            .fetchAsync("countryId", countryId)
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.CITY_BY_STATE)
    public Future<JsonArray> queryCities(final String stateId) {
        return DB.on(LCityDao.class)
            .fetchAsync("stateId", stateId)
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.REGION_BY_CITY)
    public Future<JsonArray> queryRegions(final String cityId) {
        return DB.on(LRegionDao.class)
            .fetchAsync("cityId", cityId)
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.TENT_BY_SIGMA)
    public Future<JsonArray> getTents(final String sigma) {
        return DB.on(LTentDao.class)
            .fetchAsync("sigma", sigma)
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.FLOOR_BY_SIGMA)
    public Future<JsonArray> getFloors(final String sigma) {
        return DB.on(LFloorDao.class)
            .fetchAsync("sigma", sigma)
            .compose(Ux::futureA);
    }

    @Address(Addr.PickUp.REGION_META)
    public Future<JsonObject> initRegion(final String id) {
        /*
         * Region -> City -> State -> Country
         */
        final JsonObject response = new JsonObject();
        return Ux.future(id)
            /*
             * Region Instance
             */
            .compose(regionId -> this.combine(response, "regionId",
                () -> regionId))
            .compose(regionId -> DB.on(LRegionDao.class)
                .<LRegion>fetchByIdAsync(regionId)
            )
            /*
             * Region -> City
             */
            .compose(region -> this.combine(response, "cityId",
                region::getCityId))
            .compose(cityId -> DB.on(LCityDao.class)
                .<LCity>fetchByIdAsync(cityId)
            )
            /*
             * City -> State
             */
            .compose(city -> this.combine(response, "stateId",
                city::getStateId))
            .compose(stateId -> DB.on(LStateDao.class)
                .<LState>fetchByIdAsync(stateId)
            )
            /*
             * State -> Country
             */
            .compose(state -> this.combine(response, "countryId",
                state::getCountryId))
            .compose(countryId -> Ux.future(response));
    }

    private Future<String> combine(
        final JsonObject data, final String field,
        final Supplier<String> valueFun
    ) {
        final String value = valueFun.get();
        if (Ut.isNotNil(value)) {
            data.put(field, value);
        }
        return Ux.future(value);
    }
}
