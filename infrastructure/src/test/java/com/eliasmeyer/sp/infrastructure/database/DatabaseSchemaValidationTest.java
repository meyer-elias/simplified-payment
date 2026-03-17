package com.eliasmeyer.sp.infrastructure.database;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DatabaseSchemaValidationTest {

	@Test
	void liquibaseSchemaShouldBeValidatedByHibernate() {
		// O teste passa se o contexto Quarkus subiu sem erros.
		//
		// O que acontece nos bastidores:
		// 1. Quarkus sobe
		// 2. Liquibase roda e cria o schema (migrate-at-start=true)
		// 3. Hibernate valida o schema contra as entidades (strategy=validate)
		// 4. Se houver qualquer diferença o boot lança SchemaManagementException
		//    e o teste falha com mensagem clara indicando a divergência
	}
}