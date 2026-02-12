package com.eliasmeyer.sp.domain.shared.identifier;

import java.util.UUID;

public class Id extends GenericIdentifier<UUID> {

    public Id() {
        super(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return String.format("Id[%s:%s]",
                this.getValue().getClass().getSimpleName(), this.getValue().toString());
    }
}
