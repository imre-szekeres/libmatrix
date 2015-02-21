/**
 * I4.java
 * */
package org.crf.libmatrix.generix.numerix;

import org.crf.libmatrix.core.Singleton;
import org.crf.libmatrix.core.ThreadSafe;

/**
 * Represents the integer value 4.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@Singleton
@ThreadSafe
public class I4 implements Int {

	private I4( ) {
		super( );
	}

	/**
	 * Factory method for the only <code>Two</code> instance.
	 * 
	 * @return the only instance of the <code>class</code>
	 * */
	public static final I4 instance() {
		return INSTANCE;
	}

	/**
	 * @return the constant 4
	 * */
	public int getValue() {
		return 4;
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return 31 + getValue();
	}

	private static final I4 INSTANCE = new I4( );
}

