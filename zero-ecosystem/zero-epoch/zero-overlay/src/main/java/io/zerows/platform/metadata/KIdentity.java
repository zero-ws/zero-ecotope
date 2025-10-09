package io.zerows.platform.metadata;

import io.zerows.specification.atomic.HCopier;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/*
 * Identifier structure for identifier
 * 1) static identifier: the definition of direct
 * 2) dynamic identifier: the identifier came from identifierComponent
 */
@Data
public class KIdentity implements Serializable, HCopier<KIdentity> {
    private String identifier;
    private String sigma;
    private Class<?> identifierComponent;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final KIdentity identity)) {
            return false;
        }
        return this.identifier.equals(identity.identifier) &&
            this.sigma.equals(identity.sigma) &&
            this.identifierComponent.equals(identity.identifierComponent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KIdentity> CHILD copy() {
        final KIdentity identity = new KIdentity();
        identity.identifier = this.identifier;
        identity.sigma = this.sigma;
        identity.identifierComponent = this.identifierComponent;
        return (CHILD) identity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier, this.sigma, this.identifierComponent);
    }

    @Override
    public String toString() {
        return "KIdentity{" +
            "identifier='" + this.identifier + '\'' +
            ", sigma='" + this.sigma + '\'' +
            ", identifierComponent=" + this.identifierComponent +
            '}';
    }
}
