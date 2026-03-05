module simplified.payment.infrastructure {
	requires simplified.payment.core;
	requires bcrypt;

	// Exporta implementações para uso pelo app
	exports com.eliasmeyer.sp.infrastructure.security;
}