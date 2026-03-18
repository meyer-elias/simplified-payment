package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class CarteiraJpaAdapter implements PanacheRepositoryBase<CarteiraEntity, String> {

}
