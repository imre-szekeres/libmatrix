/**
 * TestMatrixOperationsTestSuite.java
 * */
package org.crf.libmatrix;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class TestMatrixOperationsTestSuite {

	@Test
	public void testMatrixAddition() {
		Matrix actual = (new Matrix( 2 )).add(new Matrix( 2 ));
		
		Assert.assertNotNull( actual );

		Matrix expected = new Matrix(2, new double[] { 2, 0, 0, 2 });
		Assert.assertEquals( expected, actual );

		Matrix m0 = new Matrix( 3, new double[] { 1, 2, 3,
			                                      4, 5, 6,
												  7, 8, 9 });
		Matrix m1 = m0;
		actual = m1.add(m0);

		expected = new Matrix( 3, new double[] { 2,  4,  6,
												 8,  10, 12,
												 14, 16, 18  });
		Assert.assertNotNull( actual );
		Assert.assertEquals( expected, actual );
	}

	@Test(expected = ArithmeticException.class)
	public void testInvalidAddition() {
		Matrix result = (new Matrix( 2 )).add(new Matrix(21, 31));
	}

	@Test
	public void testMatrixMultiplication() {
		Matrix m0 = new Matrix( 3, 2, new double[] { 1, 2,
											         3, 4,
											         5, 6 });
		Matrix m1 = new Matrix( 2, 3, new double[] { 2, 3, 4,
											         5, 6, 7 });
		Matrix actual = m1.multiply( m0 );
		Matrix expected = new Matrix( 2, new double[] { 31, 40,
													    58, 76 });
		Assert.assertEquals( expected, actual );
		Matrix m2 = new Matrix( 2, 4, new double[] { 1, 2, 3, 4,
											         5, 6, 7, 8 });
		actual = m0.multiply( m2 );
		expected = new Matrix( 3, 4, new double[] { 11, 14, 17, 20,
			                                        23, 30, 37, 44,
			                                        35, 46, 57, 68 });
		Assert.assertEquals( expected, actual );
	}
}
