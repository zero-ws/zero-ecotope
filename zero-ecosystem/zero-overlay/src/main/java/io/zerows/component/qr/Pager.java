package io.zerows.component.qr;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.Annal;
import io.zerows.platform.exception._60023Exception400QrPageInvalid;
import io.zerows.platform.exception._60024Exception500QueryMetaNull;
import io.zerows.platform.exception._60025Exception400QrPageIndex;

import java.io.Serializable;
import java.util.Objects;

public class Pager implements Serializable {

    private static final Annal LOGGER = Annal.get(Pager.class);
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    /**
     * Start page: >= 1
     */
    private int page;
    /**
     * Page size
     */
    private int size;
    /**
     * From index: offset
     */
    private int start;
    /**
     * To index: limit
     */
    private int end;

    private Pager(final Integer page, final Integer size) {
        this.init(page, size);
    }

    private Pager(final JsonObject pageJson) {
        this.ensure(pageJson);
        this.init(pageJson.getInteger(PAGE), pageJson.getInteger(SIZE));
    }

    /**
     * Create pager by page, size
     *
     * @param page page index + 1
     * @param size page size
     *
     * @return valid Pager of new
     */
    public static Pager create(final Integer page, final Integer size) {
        return new Pager(page, size);
    }

    /**
     * Another mode to create Pager
     *
     * @param pageJson parsed pager
     *
     * @return valid Pager
     */
    public static Pager create(final JsonObject pageJson) {
        return new Pager(pageJson);
    }

    @SuppressWarnings("all")
    private void ensure(final JsonObject pageJson) {
        // Pager building checking
        Fn.jvmKo(Objects.isNull(pageJson), _60024Exception500QueryMetaNull.class);
        // Required
        Fn.jvmKo(!pageJson.containsKey(PAGE), _60023Exception400QrPageInvalid.class, PAGE);
        Fn.jvmKo(!pageJson.containsKey(SIZE), _60023Exception400QrPageInvalid.class, SIZE);
    }

    private void init(final Integer page, final Integer size) {
        // Page/Size
        Fn.jvmKo(1 > page, _60025Exception400QrPageIndex.class, page);
        this.page = page;
        // Default Size is 10
        this.size = 0 < size ? size : 10;
        // Caculate
        this.start = (this.page - 1) * this.size;
        this.end = this.page * this.size;
    }

    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        data.put(PAGE, this.page);
        data.put(SIZE, this.size);
        return data;
    }

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    @Override
    public String toString() {
        return "Pager{" +
            "page=" + this.page +
            ", size=" + this.size +
            ", start=" + this.start +
            ", end=" + this.end +
            '}';
    }
}
