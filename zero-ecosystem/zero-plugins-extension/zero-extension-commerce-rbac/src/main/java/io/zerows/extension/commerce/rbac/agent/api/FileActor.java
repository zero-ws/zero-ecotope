package io.zerows.extension.commerce.rbac.agent.api;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.commerce.rbac.eon.Addr;
import io.zerows.extension.commerce.rbac.uca.acl.relation.IdcStub;
import io.zerows.plugins.office.excel.ExcelClient;
import io.zerows.plugins.office.excel.atom.ExRecord;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

@Queue
public class FileActor {

    @Infusion
    private transient ExcelClient client;

    @Address(Addr.User.IMPORT)
    public Future<Envelop> importFile(final Envelop request) {
        /* Import data here for result */
        final String filename = Ux.getString(request);

        final File file = new File(filename);
        if (!file.exists()) {
            return Ux.future(Envelop.success(Boolean.FALSE));
        }
        return Fn.jvmOr(() -> {
            final JsonObject headers = request.headersX().copy();
            /*
             * Read file to inputStream
             */
            final InputStream inputStream = new FileInputStream(file);
            /*
             * Set<ExTable>
             */
            final Set<ExTable> tables = this.client.ingest(inputStream, true)
                .stream().filter(Objects::nonNull)
                .filter(item -> Objects.nonNull(item.getName()))
                .filter(item -> item.getName().equals("S_USER"))
                .collect(Collectors.toSet());
            /*
             * No directory here of importing
             */
            final JsonArray prepared = new JsonArray();
            tables.stream().flatMap(table -> {
                final List<JsonObject> records = table.get().stream()
                    .filter(Objects::nonNull)
                    .map(ExRecord::toJson)
                    .collect(Collectors.toList());
                LOG.Web.info(this.getClass(), "Table: {0}, Records: {1}", table.getName(), String.valueOf(records.size()));
                return records.stream();
            }).forEach(record -> {
                /*
                 * Default get injection
                 * 1）App Env:
                 * -- "sigma": "X-Sigma"
                 * -- "id": "X-Id"
                 * -- "appKey": "X-Key"
                 */
                record.mergeIn(headers, true);
                /*
                 * Required: username, mobile, email
                 */
                if (Ut.isIn(record, KName.USERNAME)) {
                    record.put(KName.LANGUAGE, KWeb.ARGS.V_LANGUAGE);
                    prepared.add(record);
                } else {
                    LOG.Web.warn(this.getClass(), "Ignored record: {0}", record.encode());
                }
            });
            final String sigma = headers.getString(KName.SIGMA);
            final IdcStub stub = IdcStub.create(sigma);

            final String user = request.userId();
            return stub.saveAsync(prepared, user)
                /*
                 * The User import has not response here for result
                 * The old code is `safeJvm` and without any response send to client,
                 * it means that it's wrong here for usage.
                 * Below line will resolve the issue of User Importing.
                 */
                .compose(userA -> Ux.future(Envelop.success(userA)));
        });
    }
}
