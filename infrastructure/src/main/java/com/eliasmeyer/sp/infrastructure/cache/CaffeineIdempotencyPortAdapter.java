package com.eliasmeyer.sp.infrastructure.cache;

import com.eliasmeyer.sp.core.application.ports.out.IdempotencyPort;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CaffeineIdempotencyPortAdapter implements IdempotencyPort {

	private static final String KEY_PREFIX = "idempotency:transferencia:";

	private final Cache<String, Boolean> cache;

	@Inject
	public CaffeineIdempotencyPortAdapter(
		@ConfigProperty(name = "idempotency.ttl.seconds", defaultValue = "86400")
		long ttlSeconds,
		@ConfigProperty(name = "idempotency.max.size", defaultValue = "10000")
		long maxSize) {
		this.cache = Caffeine.newBuilder()
			.expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
			.maximumSize(maxSize)
			.build();
	}

	@Override
	public void registrar(String key) {
		cache.put(KEY_PREFIX + key, Boolean.TRUE);
	}

	@Override
	public boolean jaProcessado(String key) {
		return cache.getIfPresent(KEY_PREFIX + key) != null;
	}
}
