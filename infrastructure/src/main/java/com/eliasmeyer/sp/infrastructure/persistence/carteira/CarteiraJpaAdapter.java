package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface CarteiraJpaAdapter extends CrudRepository<CarteiraEntity, String> {


}
