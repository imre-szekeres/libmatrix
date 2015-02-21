/**
 * LibMatrixTestLayer.java
 * */
package org.crf.libmatrix.generix;

import org.crf.libmatrix.generix.numerix.I2;
import org.crf.libmatrix.generix.numerix.I3;
import org.crf.libmatrix.generix.numerix.Int;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class ConstructMatrixTestSuite {

	public static final double EPSILON = 0.001;

	@Test
	public void testIdentityConstruction() {
		Matrix<I2, I2> identity = Matrix.create(I2.instance());
		verifyIdentity( identity );

		Matrix<I2, I3> third = Matrix.create(I2.instance(), I3.instance());
		verifyIdentity( third );
	}

	private static final <N extends Int, K extends Int> void verifyIdentity(Matrix<N, K> identity) {
		System.out.println();
		double actual, expected;
		for( int i = 0, N = identity.height(); 
		     i < N;
		     ++i ) {
			System.out.print("| ");
			for( int j = 0, K = identity.width();
			     j < K;
			     ++j ) {
				actual = identity.get(i, j);
			    System.out.print(String.format("%s ", actual));

				expected = (i == j) ? 1.0 : 0.0;
				Assert.assertEquals(expected, actual, EPSILON);
			}
			System.out.print("|\n");
	    }
	    System.out.println();
	}

	@Test
	public void testArrayCreation() {
		Matrix<I2, I3> m0 = Matrix.create( I2.instance(), I3.instance(), new double[] { 1, 2, 3, 4, 5, 6 } );
		Matrix<I2, I3> m1 = Matrix.create( I2.instance(), I3.instance(), new double[] { 1, 2, 3, 4, 5, 6 } );
		Matrix<I2, I3> m2 = Matrix.create( I2.instance(), I3.instance(), new double[] { 4, 5, 6, 4, 5, 6 } );

		Assert.assertEquals(m0, m1);
		Assert.assertFalse(m0.equals(m2));

		Matrix<I2, I2> identity = Matrix.create( I2.instance() );
		Matrix<I2, I2> expected = Matrix.create( I2.instance(), new double[] { 1, 0,
			                                                                   0, 1 });
		Assert.assertEquals( expected, identity );
	}

	@Test(expected = ArithmeticException.class)
	public void testInvalidArrayCreation() {
		Matrix<I2, I2> mtx = Matrix.create( I2.instance(), new double[] { 1, 2, 3, 4, 5, 6, 7 } );
	}
}
