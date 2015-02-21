/**
 * MatrixGeneratorTestsuite.java
 * */
package org.crf.libmatrix;

import static org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collection;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@RunWith(Parameterized.class)
public class MatrixGeneratorTestSuite {

	public MatrixGeneratorTestSuite(final MatrixGenerator generator) {
		this.generator = generator;
	}

	@Parameters
	public static final Collection<Object[]> generators() {
        return Arrays.asList(new Object[][] {{ new MatrixGenerator() }, { new ParallelMatrixGenerator() }});
		//~ return Arrays.asList(new Object[][] {{ new MatrixGenerator() }});
	}

	@Test
	public void testMatrixGenerationAndAdd() {
		Matrix generated, result;
		for(int n = 7; n < MAX_MATRIX_HEIGHT; ++n) {
			generated = generator.generate( n );
			result = generated.add( generated );
		}
	}

	@Test
	public void testMatrixGenerationAndMultiply() {
		Matrix generated, result;
		for(int n = 7; n < MAX_MATRIX_HEIGHT; ++n) {
			generated = generator.generate( n );
			result = generated.multiply( generated );
		}
	}

	private static final void logMemoryUsage(String where, double memoryInBits) {
		System.out.println(String.format("%s: %e[MB]", where, memoryInBits * BITS_TO_MBYTES ));
	}

	public static final int MAX_MATRIX_HEIGHT = 210;
	public static final double BITS_TO_MBYTES = 1.0 / (1000000 * 8);
	private final MatrixGenerator generator;
}
