/**
 * MatrixTestLayer.java
 * */
package org.crf.libmatrix.generix;

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
	MatrixOperationsTestSuite.class
})
public class MatrixTestLayer {
}
