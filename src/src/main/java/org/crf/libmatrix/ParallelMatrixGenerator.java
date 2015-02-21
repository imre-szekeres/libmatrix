/**
 * ParallelMatixGenerator.java
 * */
package org.crf.libmatrix;

/**
 * 
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class ParallelMatrixGenerator 
				extends MatrixGenerator {

	public ParallelMatrixGenerator() {
		super( );
	}

	/**
	 * 
	 * */
	@Override
	public final ParallelMatrix generate(int height) {
		return this.generate(height, height);
	}

	/**
	 * 
	 * */
	@Override
	public final ParallelMatrix generate(int height, int width) {
		double[][] matrix = new double[ height ][];
		for(int i = 0; i < height; ++i)
			matrix[i] = generateRandom( 1, width );
		return new ParallelMatrix( height, width, matrix );
	}
}
