package io.zerows.extension.commerce.finance.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBook;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.module.domain.atom.specification.KNaming;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface BookStub {
    // Fetch Books
    Future<List<FBook>> fetchAsync(JsonObject criteria);

    // Fetch By Order
    Future<List<FBook>> fetchByOrder(String orderId);

    // Fetch Authorize by Books
    Future<List<FPreAuthorize>> fetchAuthorize(List<FBook> books);

    // Create Main
    Future<List<FBook>> createAsync(List<FBook> books, KNaming spec);

    // Fetch Book with income and items
    Future<JsonObject> fetchByKey(String key);
}
