package io.zerows.extension.runtime.ambient.agent.api.file;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.Annal;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.commune.XHeader;
import io.zerows.extension.runtime.ambient.agent.service.file.DocRStub;
import io.zerows.extension.runtime.ambient.eon.Addr;
import io.zerows.extension.runtime.ambient.eon.AtMsg;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

@Queue
public class AttachActor {

    private static final Annal LOGGER = Annal.get(AttachActor.class);

    @Inject
    private transient DocRStub reader;

    @Address(Addr.File.UPLOAD)
    public Future<JsonObject> upload(final JsonObject content, final XHeader header) {
        /*
         * New file processing workflow, here should be careful
         * 1. ADD:
         * -- 1.1. Upload the file to server ( Do not insert record into database )
         * -- 1.2. When add record, based configured the file field in CRUD, insert the record
         *
         * 2. EDIT:
         * -- 2.1. Upload the file to server
         * -- 2.2. Remove all the related attachment and files
         * -- 2.3. Update all the attachments
         */
        LOG.File.info(LOGGER, AtMsg.FILE_UPLOAD, content.encodePrettily());
        Ut.valueToJObject(content, KName.METADATA);
        content.put(KName.SIGMA, header.getSigma());
        content.put(KName.ACTIVE, Boolean.TRUE);
        /*
         * ExIo to extract from
         *
         * directory ( Code ) -> directoryId
         *
         * Here are three situation
         * 0) Pre Process -> directory calculation ( For dynamic processing )
         *
         * The normalized parameters are ( directory ) here for split workflow.
         * 1) Contains `: expression directory
         * 2) Contains non `: directory code instead ( Because the code part will not contains / and ` )
         */
        return Ux.future(content);
    }

    @Address(Addr.File.DOWNLOAD)
    public Future<Buffer> download(final JsonObject filters) {
        LOG.File.info(LOGGER, AtMsg.FILE_DOWNLOAD, filters.encodePrettily());
        return this.reader.downloadDoc(filters.getString(KName.KEY));
    }
}
