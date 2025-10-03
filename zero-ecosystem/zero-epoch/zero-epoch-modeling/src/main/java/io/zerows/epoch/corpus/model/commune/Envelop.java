package io.zerows.epoch.corpus.model.commune;

import io.r2mo.base.web.ForStatus;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.webflow.WebState;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.exception._40032Exception500IndexExceed;
import io.zerows.epoch.corpus.security.token.JwtToken;
import io.zerows.enums.modeling.EmValue;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.security.authority.Acl;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Envelop implements Serializable {
    private final static ForStatus STATE = SPI.V_STATUS;

    /* Basic Data for Envelop such as: Data, Error, Status */
    private final WebState status;
    private final WebException error;
    private final JsonObject data;

    /* Additional Data for Envelop, Assist Data here. */
    private final Assist assist = new Assist();
    /* Communicate Key in Event Bus, to identify the Envelop */
    private String key;
    private Acl acl;
    /*
     * Constructor for Envelop creation, two constructor for
     * 1) Success Envelop
     * 2) Failure Envelop
     * All Envelop are private mode with non-single because it's
     * Data Object instead of tool or other reference
     */

    /**
     * @param data   input data that stored into Envelop
     * @param status http status of this Envelop
     * @param <T>    The type of stored data
     */
    private <T> Envelop(final T data, final WebState status) {
        this.data = Rib.input(data);
        this.error = null;
        this.status = status;
    }

    /**
     * @param error input error that stored into Envelop
     */
    private Envelop(final WebException error) {
        this.status = error.getStatus();
        this.error = error;
        this.data = FnVertx.adapt(error);
    }

    /*
     * Static method to create new Envelop with different fast mode here.
     * 1) Success: 204 Empty Envelop ( data = null )
     * 2) Success: 200 With input data ( data = Tool )
     * 3) Success: XXX with input data ( data = Tool ) XXX means that you can have any HttpStatus
     * 4) Error: 500 Default error with description
     * 5) Error: XXX with input WebException
     * 6) Error: 500 Default with Throwable ( JVM Error )
     */
    // 204, null
    public static Envelop ok() {
        return new Envelop(null, SPI.V_STATUS.ok());
    }

    public static Envelop okJson() {
        return new Envelop(new JsonObject(), STATE.ok());
    }

    // 200, Tool
    public static <T> Envelop success(final T entity) {
        return new Envelop(entity, STATE.ok());
    }

    // xxx, Tool
    public static <T> Envelop success(final T entity, final WebState status) {
        return new Envelop(entity, status);
    }

    // default error 500
    public static Envelop failure(final String message) {
        return new Envelop(new _500ServerInternalException(message));
    }

    // default error 500 ( JVM Error )
    public static Envelop failure(final Throwable ex) {
        // Action, 异常处理
        return Action.outFailure(ex);
    }

    // other error with WebException
    public static Envelop failure(final WebException error) {
        return new Envelop(error);
    }

    /*
     * 包装专用方法，用来执行数据封装，在 RPC 持续请求中会用到
     */
    public static <T> Envelop moveOn(final T entity) {
        // Action, 为空时的基本操作
        return Action.outNext(entity);
    }

    // ------------------ Above are initialization method -------------------
    /*
     * Predicate to check envelop to see whether is't valid
     * Error = null means valid
     */
    public boolean valid() {
        return null == this.error;
    }

    public WebException error() {
        return this.error;
    }

    // ------------------ Below are data part -------------------
    /* Get `data` part */
    public <T> T data() {
        return Rib.get(this.data);
    }

    /* Get `Http Body` part only */
    public JsonObject body() {
        return Rib.getBody(this.data);
    }

    public JsonObject request() {
        return this.assist.requestSmart();
    }

    /* Get `data` part by type */
    public <T> T data(final Class<T> clazz) {
        return Rib.get(this.data, clazz);
    }

    /* Get `data` part by argIndex here */
    public <T> T data(final Integer argIndex, final Class<T> clazz) {
        Fn.jvmKo(!Rib.isIndex(argIndex), _40032Exception500IndexExceed.class, argIndex);
        return Rib.get(this.data, clazz, argIndex);
    }

    /* Set value in `data` part */
    public void value(final String field, final Object value) {
        Rib.set(this.data, field, value, null);
    }

    /* Set value in `data` part ( with Index ) */
    public void value(final Integer argIndex, final String field, final Object value) {
        Rib.set(this.data, field, value, argIndex);
    }

    // ------------------ Below are response Part -------------------
    /* String */
    public String outString() {
        return this.outJson().encode();
    }

    /* InJson */
    public JsonObject outJson() {
        return Rib.outJson(this.data, this.error);
    }

    /* Buffer */
    public Buffer outBuffer() {
        return Rib.outBuffer(this.data, this.error);
    }

    /* Future */
    /*
    public Future<Envelop> toFuture() {
        return Future.succeededFuture(this);
    }*/

    // ------------------ Below are Bean Get -------------------
    /* HttpStatusCode */
    public WebState status() {
        return this.status;
    }

    /* Communicate Id */
    public Envelop key(final String key) {
        this.key = key;
        return this;
    }

    public Envelop acl(final Acl acl) {
        this.acl = acl;
        return this;
    }

    public Acl acl() {
        return this.acl;
    }

    public String key() {
        return this.key;
    }

    // ------------------ Below are JqTool part ----------------
    private void reference(final Consumer<JsonObject> consumer) {
        final JsonObject reference = Rib.getBody(this.data);
        if (Objects.nonNull(reference)) {
            consumer.accept(reference);
        }
    }

    /* JqTool Part for projection */
    public void onV(final JsonArray projection) {
        this.reference(reference -> Ut.irQV(reference, projection, false));
    }

    public void inV(final JsonArray projection) {
        this.reference(reference -> Ut.irQV(reference, projection, true));
    }

    /* JqTool Part for criteria */
    public void onH(final JsonObject criteria) {
        this.reference(reference -> Ut.irAndQH(reference, criteria, false));
    }

    public void inH(final JsonObject criteria) {
        this.reference(reference -> Ut.irAndQH(reference, criteria, true));
    }

    public void onMe(final EmValue.Bool active, final boolean app) {
        // Me Action
        Action.inMe(this, active, app);
    }


    public void onAcl(final Acl acl) {
        // Acl Action
        Action.outAcl(acl, this.data);
    }

    // ------------------ Below are assist method -------------------
    /*
     * Assist Data for current Envelop, all these methods will resolve the issue
     * of EventBus splitted. Because all the request data could not be got from Worker class,
     * then the system will get some reference/data into Envelop and then after
     * this envelop passed from EventBus address, it also could keep state here.
     */
    /* Extract data from Context Map */
    public <T> T context(final String key, final Class<T> clazz) {
        return this.assist.getContextData(key, clazz);
    }

    /* Get user data from User of Context */
    public String identifier(final String field) {
        return this.assist.principal(field);
    }

    /* Get Headers */
    public MultiMap headers() {
        return this.assist.headers();
    }

    public JsonObject headersX() {
        return this.assist.headersX();
    }

    public void headers(final MultiMap headers) {
        this.assist.headers(headers);
    }

    /* Session */
    public Session session() {
        return this.assist.session();
    }

    public void session(final Session session) {
        this.assist.session(session);
    }

    /* Uri */
    public String uri() {
        return this.assist.uri();
    }

    public void uri(final String uri) {
        this.assist.uri(uri);
    }

    /* Method of Http */
    public HttpMethod method() {
        return this.assist.method();
    }

    public void method(final HttpMethod method) {
        this.assist.method(method);
    }

    /* Context Set */
    public void content(final Map<String, Object> data) {
        this.assist.context(data);
    }

    /*
     * Bind Routing Context to process Assist structure
     */
    public Envelop bind(final RoutingContext context) {
        Action.copyFrom(this.assist, context);
        return this;
    }

    public RoutingContext context() {
        return this.assist.reference();
    }

    /*
     * Copy information to `to`
     * return to
     */
    public Envelop to(final Envelop to) {
        if (Objects.isNull(to)) {
            return null;
        }
        Action.copyTo(this, to);
        return to;
    }

    /*
     * Copy information from `from`
     * return this;
     */
    public Envelop from(final Envelop from) {
        Action.copyFrom(this, from);
        return this;
    }

    // ------------------ Security Parth -------------------
    public String userId() {
        return JwtToken.of(this.user()).user();
    }

    public User user() {
        return this.assist.user();
    }

    public void user(final User user) {
        this.assist.user(user);
    }

    public String habitus() {
        return this.assist.principal(KName.HABITUS);
    }

    /*
     * WebToken Part
     */
    public String token() {
        return this.assist.principal(KName.ACCESS_TOKEN);
    }

    public String token(final String field) {
        final String jwt = this.assist.principal(KName.ACCESS_TOKEN);
        final JsonObject user = JwtToken.of(jwt).data();
        return user.getString(field);
    }

    @Override
    public String toString() {
        return "Envelop{" +
            "status=" + this.status +
            ", error=" + this.error +
            ", data=" + this.data +
            ", assist=" + this.assist.toString() +
            ", key='" + this.key + '\'' +
            '}';
    }
}
