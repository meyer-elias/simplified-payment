package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface TransferenciaJpaAdapter extends CrudRepository<TransferenciaEntity, String> {

}
