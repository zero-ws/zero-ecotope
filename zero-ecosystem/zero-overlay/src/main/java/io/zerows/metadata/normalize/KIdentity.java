package io.zerows.metadata.normalize;

import io.zerows.specification.atomic.HCopier;

import java.io.Serializable;
import java.util.Objects;

/*
 * Identifier structure for identifier
 * 1) static identifier: the definition of direct
 * 2) dynamic identifier: the identifier came from identifierComponent
 */
public class KIdentity implements Serializable, HCopier<KIdentity> {
    private String identifier;
    private String sigma;
    private Class<?> identifierComponent;

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public Class<?> getIdentifierComponent() {
        return this.identifierComponent;
    }

    public void setIdentifierComponent(final Class<?> identifierComponent) {
        this.identifierComponent = identifierComponent;
    }

    public String getSigma() {
        return this.sigma;
    }

    public void setSigma(final String sigma) {
        this.sigma = sigma;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KIdentity)) {
            return false;
        }
        final KIdentity identity = (KIdentity) o;
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
