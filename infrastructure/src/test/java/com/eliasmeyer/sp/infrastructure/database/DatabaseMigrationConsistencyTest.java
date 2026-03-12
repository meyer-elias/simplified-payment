package com.eliasmeyer.sp.infrastructure.database;

import static org.junit.jupiter.api.Assertions.fail;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.StringWriter;
import java.sql.Connection;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.serializer.ChangeLogSerializer;
import liquibase.serializer.ChangeLogSerializerFactory;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DatabaseMigrationConsistencyTest {

	@Inject
	AgroalDataSource dataSource;

	@Test
	void shouldEnsureLiquibaseMatchesHibernateEntities() throws Exception {
		try (Connection connection = dataSource.getConnection()) {

			Database database =
				DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(
						new JdbcConnection(connection));

			// Snapshot do schema atual (Liquibase)
			Database liquibaseDatabase = database;

			// Snapshot do schema esperado (Hibernate)
			Database hibernateDatabase =
				DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(
						new JdbcConnection(connection));

			DiffResult diff = DiffGeneratorFactory.getInstance()
				.compare(hibernateDatabase, liquibaseDatabase, new CompareControl());

			if (!diff.areEqual()) {

				System.out.println("\n❌ Diferenças detectadas:\n");

				diff.getMissingObjects().forEach(obj ->
					System.out.println("Faltando no Liquibase: " + obj));

				DiffOutputControl diffOutputControl = new DiffOutputControl();
				diffOutputControl.setIncludeCatalog(false);
				diffOutputControl.setIncludeSchema(false);

				DiffToChangeLog diffToChangeLog =
					new DiffToChangeLog(diff, diffOutputControl);

				StringWriter writer = new StringWriter();

				ChangeLogSerializer serializer =
					ChangeLogSerializerFactory.getInstance()
						.getSerializer("yaml");

				diffToChangeLog.print(writer.toString(), serializer);

				System.out.println("\n💡 Migration sugerida automaticamente:\n");
				System.out.println(writer);

				fail("Liquibase migrations estão inconsistentes com as entidades.");
			}
		}
	}
}
