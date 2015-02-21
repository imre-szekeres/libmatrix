/**
 * MatixGenerator.java
 * */
package org.crf.libmatrix;

import java.util.Random;

/**
 * 
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class MatrixGenerator {

	public MatrixGenerator() {
		this.random = new Random( System.nanoTime() );
	}

	/**
	 * 
	 * */
	public Matrix generate(int height) {
		return generate(height, height);
	}

	/**
	 * 
	 * */
	public Matrix generate(int height, int width) {
		return new Matrix(height, width, generateRandom( height, width ));
	}

	/**
	 * 
	 * */
	protected final double[] generateRandom(int height, int width) {
		Matrix.Constraints.forSize(height, width);
		int size = height*width;
		double[] matrix = new double[ size ];
		for(int i = 0; i < size; ++i)
			matrix[ i ] = random.nextDouble();
		return matrix;
	}


	private final Random random;
}
