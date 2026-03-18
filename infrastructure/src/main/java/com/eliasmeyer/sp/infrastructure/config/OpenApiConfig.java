package com.eliasmeyer.sp.infrastructure.config;

import io.smallrye.openapi.internal.models.info.Contact;
import io.smallrye.openapi.internal.models.info.Info;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;

@ApplicationScoped
class OpenApiConfig implements OASFilter {

	@Override
	public void filterOpenAPI(OpenAPI openAPI) {
		openAPI.setInfo(new Info().title("Simplified Payment API").version("1.0").contact(
				new Contact().name("Elias Meyer").email("eliasmeyer@gmail.com")
					.url("https://github.com/meyer-elias"))
			.description("API padrão para o Simplified Payment"));
	}
}
