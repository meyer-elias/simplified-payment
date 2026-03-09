module simplified.payment.core {
	// Domain - exportado para infrastructure (JPA, etc)
	exports com.eliasmeyer.sp.core.domain.model.usuario;
	exports com.eliasmeyer.sp.core.domain.model.carteira;
	exports com.eliasmeyer.sp.core.domain.model.transferencia;
	exports com.eliasmeyer.sp.core.domain.model.transferencia.eventos;
	exports com.eliasmeyer.sp.core.domain.shared;
	exports com.eliasmeyer.sp.core.domain.shared.identifier;
	exports com.eliasmeyer.sp.core.domain.ports.in.transferencia;
	exports com.eliasmeyer.sp.core.domain.ports.in.usuario;
	exports com.eliasmeyer.sp.core.domain.ports.out.transferencia;
	exports com.eliasmeyer.sp.core.domain.ports.out.usuario;
	exports com.eliasmeyer.sp.core.domain.ports.out;

	// Application - exportado para infrastructure (implementação de ports)
	exports com.eliasmeyer.sp.core.application.ports;
	exports com.eliasmeyer.sp.core.application.exception;
	exports com.eliasmeyer.sp.core.application.shared;
	exports com.eliasmeyer.sp.core.application.usecase.transferencia;
	exports com.eliasmeyer.sp.core.application.usecase.transferencia.handler;
	exports com.eliasmeyer.sp.core.application.usecase.user;
	exports com.eliasmeyer.sp.core.domain.model.transferencia.exception;
	exports com.eliasmeyer.sp.core.domain.model.carteira.exception;
	exports com.eliasmeyer.sp.core.domain.ports.out.carteira;

	// Módulos requeridos pelo core
	requires java.base;
}