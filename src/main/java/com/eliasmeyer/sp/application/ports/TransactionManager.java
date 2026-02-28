package com.eliasmeyer.sp.application.ports;

import java.util.function.Supplier;

public interface TransactionManager {

	<T> T execute(Supplier<T> action);
}
