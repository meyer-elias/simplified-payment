package com.eliasmeyer.sp.core.application.ports;

/**
 * Abstrai o gerenciamento de transações na camada de aplicação.
 *
 * <p>O contrato desta interface segue o padrão <em>Template Method</em>: a implementação concreta
 * é responsável por abrir a transação, fazer commit em caso de sucesso e <strong>rollback
 * automático</strong> em caso de exceção — sem necessidade de um metodo {@code rollback()}
 * explícito.
 *
 * <p>A camada de aplicação nunca deve gerenciar o ciclo de vida da transação manualmente;
 * basta passar a ação desejada e tratar as exceções de negócio normalmente.
 *
 * <p><strong>Nota de breaking change:</strong> A partir desta versão, o metodo foi simplificado
 * para {@code void execute(Runnable)}. Casos de uso que precisem retornar valores de dentro da
 * transação devem usar mecanismos externos (ex: retornar via parâmetro por referência ou
 * reestruturar a lógica para não depender de retorno da transação).
 */
public interface AppTransactionManager {

	/**
	 * Executa {@code action} dentro de uma transação e retorna o resultado. Se {@code action}
	 * lançar qualquer exceção, a transação é revertida (rollback) automaticamente.
	 *
	 * @param action lógica a ser executada dentro da transação
	 */
	void execute(Runnable action);
}
