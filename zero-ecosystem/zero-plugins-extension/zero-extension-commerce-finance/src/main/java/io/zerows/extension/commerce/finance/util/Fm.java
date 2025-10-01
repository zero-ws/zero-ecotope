package io.zerows.extension.commerce.finance.util;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.uca.log.Log;
import io.zerows.epoch.common.uca.log.LogModule;
import io.zerows.extension.commerce.finance.atom.TranData;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBook;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.module.domain.atom.specification.KNaming;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public final class Fm {
    private Fm() {
    }

    /**
     * 直接从 data 中提取 preAuthorize 节点来构造预授权对象
     * {@link FPreAuthorize}
     */
    public static FPreAuthorize toAuthorize(final JsonObject data) {
        return FmCombine.toAuthorize(data);
    }

    public static JsonObject toTransaction(final JsonObject response, final List<TranData> tranData) {
        return FmCombine.toTransaction(response, tranData);
    }

    public static BigDecimal calcAmount(final BigDecimal start, final BigDecimal adjust,
                                        final boolean income, final String status) {
        return FmAmount.calcAmount(start, adjust, income, status);
    }

    public static BigDecimal calcAmount(final BigDecimal start, final BigDecimal adjust,
                                        final boolean income) {
        return FmAmount.calcAmount(start, adjust, income, FmConstant.Status.VALID);
    }

    public static JsonObject qrBook(final KNaming spec) {
        return FmBook.qrBook(spec);
    }

    public static FBook umBook(final KNaming spec) {
        return FmBook.umBook(spec);
    }

    public static List<FBook> umBook(final KNaming spec, final List<FBook> books) {
        return FmBook.umBook(spec, books);
    }

    public interface LOG {
        String MODULE = "χρηματοδότηση";

        LogModule Book = Log.modulat(MODULE).extension("Book");
    }
}
