/**
 * LibMatrixTestLayer.java
 * */
package org.crf.libmatrix;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class ConstructMatrixTestSuite {

	@Test
	public void testConstruction() {
		Matrix m2x2 = new Matrix( 2 );
		Matrix m2x2_2 = new Matrix(2, new double[] { 1, 0,
			                                         0, 1 });
		assertIdentity( m2x2 );
		Assert.assertEquals( m2x2_2, m2x2 );	                              

		double[] backingArray = new double[] { 1, 2, 3, 4, 5 };
		Matrix m4x7 = new Matrix(4, 7, backingArray);
		assertArrayConstructed( backingArray, m4x7 );
	}

	private static final void assertIdentity(Matrix identity) {
		double expected;
		for(int i = 0; i < identity.getHeight(); ++i) {
			for(int j = 0; j < identity.getWidth(); ++j) {
				expected = (j == i) ? 1 : 0;
				Assert.assertEquals( expected, identity.get(i, j), EPSILON );
			}
		}
	}

	private static final void assertArrayConstructed(double[] backing, Matrix actual) {
		Matrix.Constraints.forArrayLength( actual.getHeight(), actual.getWidth(), backing );

		for(int i = 0, j = 0, index = 0; (i < actual.getHeight() && index < backing.length); ++i)
			for(; (j < actual.getWidth() && (index = i*actual.getWidth() + j) < backing.length); ++j)
				Assert.assertEquals( backing[index], actual.get(i, j), EPSILON );
	}

	@Test
	public void testToSring() {
		String actual = (new Matrix( 2 )).toString();

		Assert.assertNotNull( actual );
		Assert.assertFalse( actual.isEmpty() );
		
		String expected = String.format("| 1.0 0.0 |\n| 0.0 1.0 |\n");
		Assert.assertEquals( expected, actual );
	}

	@Test
	public void testSizeConstraintsSuccess() {
		ConstraintsTestSuite.testSizeConstraintsSuccess();
	}

	@Test(expected = ArithmeticException.class)
	public void testSizeConstraintsFailure() {
		ConstraintsTestSuite.testSizeConstraintsFailure();
	}

	private static final class ConstraintsTestSuite {
		
		static final void testSizeConstraintsSuccess() {
			Random random = new Random( System.nanoTime() );
			int randHeight = random.nextInt(3000) + 1;
			int randWidth = random.nextInt(3000) + 1;
			System.out.println(String.format("\nrh: %d\trw: %d\n", randHeight, randWidth));

			Matrix.Constraints.forSize(randHeight, randWidth);
		}

		static final void testSizeConstraintsFailure() {
			Matrix.Constraints.forSize(-1, 350);
		}
	}

	public static final double EPSILON = 0.001;
}
