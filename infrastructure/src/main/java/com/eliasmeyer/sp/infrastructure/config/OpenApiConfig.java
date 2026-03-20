package com.eliasmeyer.sp.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;

@ApplicationScoped
class OpenApiConfig implements OASFilter {

	@Override
	public void filterOpenAPI(OpenAPI openAPI) {
		openAPI.setInfo(OASFactory.createInfo()
			.title("Simplified Payment API")
			.version("1.0")
			.contact(OASFactory.createContact()
				.name("Elias Meyer")
				.email("eliasmeyer@gmail.com")
				.url("https://github.com/meyer-elias"))
			.description("API padrão para o Simplified Payment"));
	}
}
