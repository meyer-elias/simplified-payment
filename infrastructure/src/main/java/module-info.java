module simplified.payment.infrastructure {
	requires simplified.payment.core;
	requires bcrypt;
	requires jakarta.persistence;
	requires jakarta.cdi;
	requires jakarta.data;
	requires org.hibernate.orm.core;
	requires jakarta.ws.rs;
	requires resteasy.reactive.common;
	requires jakarta.validation;
	requires quarkus.hibernate.orm.panache;
	requires quarkus.panache.common;

	// Exporta implementações para uso pelo app
	exports com.eliasmeyer.sp.infrastructure.security;
}