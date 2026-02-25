package com.eliasmeyer.sp.domain.shared.identifier;

import java.util.UUID;

/**
 * Identificador único universal (UUID) para agregados raiz.
 * <p>
 * Encapsula um UUID gerado seguindo o padrão IdGenerator,
 * garantindo que cada instância tenha um identificador único e imutável.
 */
public class Id extends GenericIdentifier<UUID> {

    /**
     * Cria um identificador com UUID aleatório.
     */
    public Id() {
        super(UUID.randomUUID());
    }

    /**
     * Reconstrói um identificador a partir de um UUID existente.
     * Útil para persistência e reconstrução de agregados.
     *
     * @param uuid o UUID a encapsular
     */
    public Id(UUID uuid) {
        super(uuid);
    }

    /**
     * Reconstrói um identificador a partir de uma String UUID.
     *
     * @param uuidString representação string do UUID
     */
    public Id(String uuidString) {
        super(UUID.fromString(uuidString));
    }

    @Override
    public String toString() {
        return String.format("Id[%s:%s]",
                this.getValue().getClass().getSimpleName(), this.getValue());
    }
}
