package io.zerows.core.web.io.uca.request.mime;

import io.zerows.core.exception.WebException;
import io.zerows.core.fn.Fx;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.web.io.exception._415MediaNotSupportException;
import io.zerows.core.web.model.atom.Event;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

final class MediaAtom {

    private static final Annal LOGGER = Annal.get(MediaAtom.class);

    static void accept(final Event event,
                       final MediaType type) throws WebException {
        final Set<MediaType> medias = event.getConsumes();
        if (!medias.contains(MediaType.WILDCARD_TYPE)) {
            /* 1. Start to parsing expected type **/
            boolean match = medias.stream()
                .anyMatch(media ->
                    MediaType.MEDIA_TYPE_WILDCARD.equals(media.getType()) ||
                        media.getType().equalsIgnoreCase(type.getType()));
            /* 2. Type checking **/
            Fx.outWeb(!match, LOGGER,
                _415MediaNotSupportException.class,
                MediaAtom.class, type, medias);
            /* 3. Start to parsing expected sub type **/
            match = medias.stream()
                .anyMatch(media ->
                    MediaType.MEDIA_TYPE_WILDCARD.equals(media.getSubtype()) ||
                        media.getSubtype().equalsIgnoreCase(type.getSubtype())
                );
            /* 4. Subtype checking **/
            Fx.outWeb(!match, LOGGER,
                _415MediaNotSupportException.class,
                MediaAtom.class, type, medias);
        }
    }
}
