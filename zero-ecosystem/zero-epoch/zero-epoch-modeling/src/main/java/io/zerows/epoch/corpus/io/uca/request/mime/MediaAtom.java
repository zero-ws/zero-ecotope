package io.zerows.epoch.corpus.io.uca.request.mime;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.exception._60006Exception415MediaNotSupport;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

final class MediaAtom {


    static void accept(final ActorEvent event,
                       final MediaType type) throws WebException {
        final Set<MediaType> medias = event.getConsumes();
        if (!medias.contains(MediaType.WILDCARD_TYPE)) {
            /* 1. Start to parsing expected type **/
            boolean match = medias.stream()
                .anyMatch(media ->
                    MediaType.MEDIA_TYPE_WILDCARD.equals(media.getType()) ||
                        media.getType().equalsIgnoreCase(type.getType()));
            /* 2. Type checking **/
            Fn.jvmKo(!match, _60006Exception415MediaNotSupport.class, type, medias);
            /* 3. Start to parsing expected sub type **/
            match = medias.stream()
                .anyMatch(media ->
                    MediaType.MEDIA_TYPE_WILDCARD.equals(media.getSubtype()) ||
                        media.getSubtype().equalsIgnoreCase(type.getSubtype())
                );
            /* 4. Subtype checking **/
            Fn.jvmKo(!match, _60006Exception415MediaNotSupport.class, type, medias);
        }
    }
}
