module simplified.payment.infrastructure {
	requires simplified.payment.core;
	requires bcrypt;
	requires jakarta.persistence;
	requires jakarta.cdi;
	requires jakarta.data;
	requires org.hibernate.orm.core;

	// Exporta implementações para uso pelo app
	exports com.eliasmeyer.sp.infrastructure.security;
}