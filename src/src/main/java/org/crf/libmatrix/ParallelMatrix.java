/**
 * ParallelMatrix.java
 * */
package org.crf.libmatrix;

import org.crf.libmatrix.core.LibraryConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class ParallelMatrix 
				extends Matrix {

	public ParallelMatrix(int height) {
		super( height );
	}

	public ParallelMatrix(int height, int width) {
		super( height, width );
	}

	public ParallelMatrix(int height, double[] matrix) {
		super( height, matrix );
	}

	public ParallelMatrix(int height, int width, double[] matrix) {
		super( height, width, matrix );
	}

	/**
	 * 
	 * */
	@Override
	public final ParallelMatrix multiply(final Matrix rhs) {
		double[] matrix = new double[ height*rhs.width ];
		int poolSize = ((Integer) LibraryConfiguration.get(LibraryConfiguration.MTX_THREAD_COUNT)).intValue(); 
		ExecutorService threadPool = Executors.newFixedThreadPool( poolSize );

		return new ParallelMatrix( height, rhs.width, matrix );
	}

	private static final class LineMultiplier 
									implements Runnable {
		/**
		 * 
		 * */
		@Override
		public void run() {
// TODO: implement
		}
	}
}
