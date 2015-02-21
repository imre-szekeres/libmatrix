/**
 * LibraryConfiguration.java
 */
package org.crf.libmatrix.core;

import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author cvirtue
 * @version "%I%, %G%"
 */
public class LibraryConfiguration {


	/**
	 * Overrides the configuration value specified by its name by the value
	 * passed as argument.
	 * <p>
	 * Also returns the previous value set.
	 * 
	 * @param name
	 * @param value
	 * @return the original value
	 * */
	public static final Object set(final String name, final Object value) {
		return CONFIGURATION.put(name, value);
	}

	/**
	 * @param name
	 * @return the current value corresponding to the name specified
	 * */
	public static final Object get(final String name) {
		return CONFIGURATION.get( name );
	}

	public static final String MTX_THREAD_COUNT = "mtx-thread-count";

	private static final Map<String, Object> CONFIGURATION;
	static {
		CONFIGURATION = new HashMap<>();
		CONFIGURATION.put(MTX_THREAD_COUNT, Integer.valueOf(8));
	}
}
