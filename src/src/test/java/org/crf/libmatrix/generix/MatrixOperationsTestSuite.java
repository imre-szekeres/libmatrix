/**
 * MatrixOperationsTestSuite.java
 * */
package org.crf.libmatrix.generix;

import org.crf.libmatrix.generix.numerix.I4;
import org.crf.libmatrix.generix.numerix.I3;
import org.crf.libmatrix.generix.numerix.I2;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class MatrixOperationsTestSuite {

	@Test
	public void testAdd() {
		Matrix<I3, I3> m0 = Matrix.create( I3.instance(), new double[] { 1, 2, 3,
			                                                             4, 5, 6,
																		 7, 8, 9 });
		Matrix<I3, I3> m1 = m0;
		Matrix<I3, I3> actual = m1.add(m0);

		Matrix<I3, I3> expected = Matrix.create( I3.instance(), new double[] { 2,  4,  6,
													                           8,  10, 12,
																			   14, 16, 18  });
		Assert.assertEquals( expected, actual );
	}

	@Test
	public void testMultiply() {
		Matrix<I3, I2> m0 = Matrix.create( I3.instance(), I2.instance(), 
		                                   new double[] { 1, 2,
											              3, 4,
											              5, 6 });
		Matrix<I2, I3> m1 = Matrix.create( I2.instance(), I3.instance(),
		                                   new double[] { 2, 3, 4,
											              5, 6, 7 });
		Matrix<I2, I2> actual = m1.multiply( m0 );
		Matrix<I2, I2> expected = Matrix.create( I2.instance(), new double[] { 31, 40,
													                           58, 76 });
		Assert.assertEquals( expected, actual );


		Matrix<I2, I4> m2 = Matrix.create( I2.instance(), I4.instance(), 
		                                   new double[] { 1, 2, 3, 4,
											              5, 6, 7, 8 });
		Matrix<I3, I4> actual2 = m0.multiply( m2 );
		Matrix<I3, I4> expected2 = Matrix.create( I3.instance(), I4.instance(), 
		                                          new double[] { 11, 14, 17, 20,
			                                                     23, 30, 37, 44,
			                                                     35, 46, 57, 68 });
		Assert.assertEquals( expected2, actual2 );
	}
}
