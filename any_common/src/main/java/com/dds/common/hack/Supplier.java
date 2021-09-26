package com.dds.common.hack;

/**
 * Represents a supplier of results.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 */
public interface Supplier<T> {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T get();
}
