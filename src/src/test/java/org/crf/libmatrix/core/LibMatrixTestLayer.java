/**
 * LibMatrixTestLayer.java
 * */
package org.crf.libmatrix.core;

import static org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@RunWith(Suite.class)
@SuiteClasses({
	org.crf.libmatrix.MatrixTestLayer.class,
	org.crf.libmatrix.ParallelMatrixTestLayer.class,
	org.crf.libmatrix.generix.MatrixTestLayer.class
})
public class LibMatrixTestLayer {
}
