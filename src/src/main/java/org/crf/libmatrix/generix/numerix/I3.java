/**
 * I3.java
 * */
package org.crf.libmatrix.generix.numerix;

import org.crf.libmatrix.core.Singleton;
import org.crf.libmatrix.core.ThreadSafe;

/**
 * Represents the integer value 3.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@Singleton
@ThreadSafe
public class I3 implements Int {

	private I3( ) {
		super( );
	}

	/**
	 * Factory method for the only <code>Two</code> instance.
	 * 
	 * @return the only instance of the <code>class</code>
	 * */
	public static final I3 instance() {
		return INSTANCE;
	}

	/**
	 * @return the constant 3
	 * */
	public int getValue() {
		return 3;
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return 31 + getValue();
	}

	private static final I3 INSTANCE = new I3( );
}
