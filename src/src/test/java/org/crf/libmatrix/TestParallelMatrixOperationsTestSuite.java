/**
 * TestParallelMatrixOperationsTestSuite.java
 * */
package org.crf.libmatrix;

import org.crf.libmatrix.Matrix;
import org.crf.libmatrix.ParallelMatrix;
import org.crf.libmatrix.ParallelMatrixGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class TestParallelMatrixOperationsTestSuite {


	@Test(expected = ArithmeticException.class)
	public void testInvalidParallelMultiplication() {
		ParallelMatrix result = (new ParallelMatrix( 2 )).multiply(new ParallelMatrix(21, 31));
	}

	@Test
	public void testParallelMatrixMultiplication() {
		Assert.assertEquals( new ParallelMatrix(2), new ParallelMatrix(2) );
		//~ ParallelMatrix actual = new ParallelMatrix(4);
		//~ actual = actual.multiply( actual );
		//~ ParallelMatrix expected = new ParallelMatrix(4);
		//~ Assert.assertEquals( expected, actual );
//~ 
		//~ actual = new ParallelMatrix( 3, new double[][] {{ 1, 2, 3 },
				                                        //~ { 4, 5, 6 },
				                                        //~ { 7, 8, 9 }});
		//~ expected = new ParallelMatrix( 3, new double[][] {{ 30,  36,  42 },
				                                          //~ { 66,  81,  96 },
				                                          //~ { 102, 126, 150 }});
		//~ Assert.assertEquals(expected, actual.multiply( actual ));
//~ 
		//~ ParallelMatrix one = new ParallelMatrix(4, 5, new double[][] {{ 1,  2,  3,  4,  5 }, 
						                                             //~ { 6,  7,  8,  9,  10 }, 
						                                             //~ { 11, 12, 13, 14, 15 }, 
						                                             //~ { 16, 17, 18, 19, 20 }});
		//~ ParallelMatrix two = new ParallelMatrix(5, 6, new double[][] {{ 7,  8,  9,  10, 11, 12 }, 
								                                      //~ { 13, 14, 15, 16, 17, 18 }, 
								                                      //~ { 21, 13, 31, 2,  5,  6  }, 
								                                      //~ { 13, 1,  9,  8,  21, 23 },
								                                      //~ { 1,  2,  3,  4,  5,  7  }});
		//~ actual = one.multiply( two );
		//~ expected = new ParallelMatrix( 4, 6, new double[][] {{ 153, 89,  183,  100, 169,  193  },
				                                            //~ { 428, 279, 518,  300, 464,  523  },
				                                            //~ { 703, 469, 853,  500, 759,  853  },
				                                            //~ { 978, 659, 1188, 700, 1054, 1183 }});
		//~ Assert.assertEquals( expected, actual );
	}
}
