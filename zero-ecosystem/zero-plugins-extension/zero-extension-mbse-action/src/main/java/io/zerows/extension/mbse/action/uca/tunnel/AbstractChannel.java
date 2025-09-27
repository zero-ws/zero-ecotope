package io.zerows.extension.mbse.action.uca.tunnel;

import io.zerows.common.datamation.KDictConfig;
import io.zerows.common.datamation.KFabric;
import io.zerows.core.uca.log.Annal;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.core.annotations.Contract;
import io.zerows.core.constant.KWeb;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.core.web.mbse.atom.runner.ActIn;
import io.zerows.core.web.mbse.atom.runner.ActOut;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.core.web.model.zdk.Commercial;
import io.zerows.core.web.scheduler.atom.Mission;
import io.zerows.extension.mbse.action.exception._501ChannelErrorException;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtChannel;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtComponent;
import io.zerows.extension.mbse.action.uca.monitor.JtMonitor;
import io.zerows.extension.mbse.action.util.Jt;
import io.zerows.specification.modeling.HRecord;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract channel
 * reference matrix
 * Name                 Database            KIntegration         Mission
 * AdaptorChannel       Yes                 No                  No
 * ConnectorChannel     Yes                 Yes                 No
 * DirectorChannel      Yes                 No                  Yes
 * ActorChannel         Yes                 Yes                 Yes
 * <p>
 * For above support list, here are some rules:
 * 1) Request - Response MODE, Client send request
 * 2) Publish - Subscribe MODE, Server send request
 * <p>
 * For common usage, it should use AdaptorChannel instead of other three types; If you want to send
 * request to third part interface ( API ), you can use ConnectorChannel instead of others.
 * <p>
 * The left two: ActorChannel & DirectorChannel are Background task in zero ( Job Support ), the
 * difference between them is that whether the channel support KIntegration.
 * <p>
 * The full feature of channel should be : ActorChannel
 */
public abstract class AbstractChannel implements JtChannel {

    private final transient JtMonitor monitor = JtMonitor.create(this.getClass());
    /* This field will be injected by zero directly from backend */
    @Contract
    private transient Commercial commercial;
    @Contract
    private transient Mission mission;
    /*
     * In `Job` mode, the dictionary may came from `JobIncome`.
     * In `Api` mode, the dictionary is null reference here.
     */
    @Contract
    private transient ConcurrentMap<String, JsonArray> dictionary;

    @Override
    public Future<Envelop> transferAsync(final Envelop envelop) {
        /*
         * Build record and init
         */
        final Class<?> recordClass = this.commercial.recordComponent();
        /*
         * Build component and init
         */
        final Class<?> componentClass = this.commercial.businessComponent();
        if (Objects.isNull(componentClass)) {
            /*
             * null class of component
             */
            return Ut.Bnd.failOut(_501ChannelErrorException.class, this.getClass(), (Object) null);
        } else {
            return this.createRequest(envelop, recordClass).compose(request -> {
                /*
                 * Create new component here
                 * It means that Channel/Component must contains new object
                 * Container will create new Channel - Component to process request
                 * Instead of singleton here.
                 *  */
                final JtComponent component = Ut.instance(componentClass);
                if (Objects.nonNull(component)) {
                    this.monitor.componentHit(componentClass, recordClass);
                    /*
                     * Initialized first and then
                     */
                    Ux.debug();
                    /*
                     * Options without `mapping` here
                     */
                    return this.initAsync(component, request)
                        /*
                         * Contract here
                         * 1) Definition in current channel
                         * 2) Data came from request ( XHeader )
                         */
                        .compose(initialized -> Anagogic.componentAsync(component, this.commercial, this::createFabric))
                        .compose(initialized -> Anagogic.componentAsync(component, envelop))
                        /*
                         * Children initialized
                         */
                        .compose(initialized -> component.transferAsync(request))
                        /*
                         * Response here for future custom
                         */
                        .compose(actOut -> this.createResponse(actOut, envelop))
                        /*
                         * Otherwise;
                         */
                        .otherwise(Ux.otherwise());
                } else {
                    /*
                     * singleton singleton error
                     */
                    return Ut.Bnd.failOut(_501ChannelErrorException.class, this.getClass(), componentClass.getName());
                }
            });
        }
    }

    private Future<Envelop> createResponse(final ActOut actOut, final Envelop envelop) {
        return Ux.future(actOut.envelop(this.commercial.mapping()).from(envelop));
    }

    /*
     * OnOff `dictionary` here for usage
     * 1) When `Job`, assist data may be initialized before.
     * 2) When `Api`, here will initialize assist data.
     * 3) Finally the data will bind to request
     */
    private Future<ActIn> createRequest(final Envelop envelop, final Class<?> recordClass) {
        /*
         * Data object, could not be singleton
         *  */
        final HRecord definition = Ut.instance(recordClass);
        /*
         * First step for channel
         * Initialize the `ActIn` object and reference
         */
        final ActIn request = new ActIn(envelop);
        request.bind(this.commercial.mapping());
        request.connect(definition);

        return Ux.future(request);
    }

    private Future<KFabric> createFabric() {
        /*
         * Dict configuration
         */
        final KDictConfig dict = this.commercial.dict();
        if (Objects.isNull(this.dictionary)) {
            final String appKey = this.commercial.app();
            final String identifier = this.commercial.identifier();
            return Jt.toDictionary(appKey, KWeb.CACHE.DIRECTORY, identifier, dict).compose(dictionary -> {
                /*
                 * Bind dictionary to current dictionary reference
                 */
                this.dictionary = dictionary;
                return Ux.future(KFabric.create().dictionary(dictionary).epsilon(dict.configUse()));
            });
        } else {
            return Ux.future(KFabric.create().dictionary(this.dictionary).epsilon(dict.configUse()));
        }
    }

    /*
     * Initialize component
     */
    public abstract Future<Boolean> initAsync(JtComponent component, ActIn request);

    protected Annal getLogger() {
        return Annal.get(this.getClass());
    }

    // ------------- Rename configuration object -------------
    /*
     * Get service definition from `Commercial`
     */
    protected Commercial commercial() {
        return this.commercial;
    }

    /*
     * Get job definition from `Mission`
     */
    protected Mission mission() {
        return this.mission;
    }
}
