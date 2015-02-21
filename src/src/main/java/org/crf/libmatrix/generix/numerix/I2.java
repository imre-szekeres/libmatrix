/**
 * I2.java
 * */
package org.crf.libmatrix.generix.numerix;

import org.crf.libmatrix.core.Singleton;
import org.crf.libmatrix.core.ThreadSafe;

/**
 * Represents the integer value 2.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@Singleton
@ThreadSafe
public class I2 implements Int {

	private I2( ) {
		super( );
	}

	/**
	 * Factory method for the only <code>Two</code> instance.
	 * 
	 * @return the only instance of the <code>class</code>
	 * */
	public static final I2 instance() {
		return INSTANCE;
	}

	/**
	 * @return the constant 2
	 * */
	public int getValue() {
		return 2;
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return 31 + getValue();
	}

	private static final I2 INSTANCE = new I2( );
}
