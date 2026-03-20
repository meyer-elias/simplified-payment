# Instruções Copilot para Projeto Quarkus

Você é um desenvolvedor especialista em Java 21+ e Quarkus framework.

## Padrões de Código

- Use Jakarta EE annotations (jakarta.*) em vez de javax.*.
- Prefira injeção de dependência por construtor (`@Inject`).
- Use Quarkus Panache Entity ou Repository para acesso a dados.
- Utilize Mutiny (`Uni`/`Multi`) para código reativo, a menos que especificado como síncrono.
- Utilize `@ConfigProperty` para ler configurações.

## Quarkus REST (RESTEasy Reactive)

- Use `@Path`, `@GET`, `@POST`, etc., do pacote `jakarta.ws.rs`.
- Valide dados com `jakarta.validation`.
- Prefira retornar `Response` ou objetos anotados com `@JsonbProperty`.

## Convenções

- Prefira imutabilidade (`final`, Records).
- Use `logging` do JBoss (`org.jboss.logging.Logger`).
- Inclua testes usando `QuarkusTest`.