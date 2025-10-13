package io.zerows.cortex.management.uri;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.basicore.WebActor;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.basicore.WebReceipt;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UriStore {
    private static final ConcurrentMap<String, UriMeta> URIS = new ConcurrentHashMap<>();
    private static UriStore INSTANCE;

    static {
        /*
         * Initialize data here.
         */
        final WebActor actor = OCacheActor.entireValue();
        final Set<WebReceipt> receipts = actor.getReceipts();
        final ConcurrentMap<String, WebReceipt> receiptMap = Ut.elementMap(new ArrayList<>(receipts), WebReceipt::getAddress);
        final Set<WebEvent> events = actor.getEvents();
        /*
         * UriMeta building
         */
        for (final WebEvent event : events) {
            /*
             * Uri Meta
             */
            final UriMeta uriMeta = new UriMeta();
            uriMeta.setMethod(event.getMethod());
            uriMeta.setUri(event.getPath());
            uriMeta.setDynamic(Boolean.FALSE);
            /*
             * name / comment
             */
            uriMeta.setComment(event.getPath());
            uriMeta.setName(event.getPath());
            /*
             * Get Address
             */
            final Method method = event.getAction();
            final Address annotation = method.getDeclaredAnnotation(Address.class);
            if (Objects.nonNull(annotation)) {
                /*
                 * Worker Address Injection
                 */
                final String address = annotation.value();
                if (Objects.nonNull(address) && receiptMap.containsKey(address)) {
                    /*
                     * Worker Build
                     */
                    final WebReceipt receipt = receiptMap.get(address);
                    uriMeta.setAddress(address);
                    uriMeta.setWorkerMethod(receipt.getMethod());
                }
            }
            /*
             * Cache Key calculation
             */
            final String cacheKey = uriMeta.getCacheKey();
            if (Ut.isNotNil(cacheKey)) {
                URIS.put(cacheKey, uriMeta);
            }
        }
    }

    private UriStore() {
    }

    /*
     * Store URIs when container scanned up
     * Lazy initialized and extract from Event/Receipt
     */
    static UriStore create() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new UriStore();
        }
        return INSTANCE;
    }

    /*
     * Add UriMeta into storage
     */
    UriStore add(final UriMeta meta) {
        final String cacheKey = meta.getCacheKey();
        if (Ut.isNotNil(cacheKey)) {
            URIS.put(cacheKey, meta);
        }
        return this;
    }

    /*
     * Get all Uris, sort by `uri` here
     */
    List<UriMeta> getAll() {
        final List<UriMeta> uris = new ArrayList<>(URIS.values());
        uris.sort(Comparator.comparing(UriMeta::getUri));
        return uris;
    }

    /*
     * Search UriMeta by `keyword`
     */
    List<UriMeta> search(final String keyword) {
        if (Ut.isNil(keyword)) {
            return new ArrayList<>();
        } else {
            final List<UriMeta> sortedList = this.getAll();
            return sortedList.stream().filter(item -> {
                if (item.getUri().contains(keyword)) {
                    return true;
                }
                if (Ut.isNil(item.getName())) {
                    return false;
                } else {
                    return item.getName().contains(keyword);
                }
            }).collect(Collectors.toList());
        }
    }
}
