/**
 * MatrixTestLayer.java
 * */
package org.crf.libmatrix;

import static org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@RunWith(Suite.class)
@SuiteClasses({
	ConstructMatrixTestSuite.class,
	TestMatrixOperationsTestSuite.class,
	MatrixGeneratorTestSuite.class
})
public class MatrixTestLayer {
}
