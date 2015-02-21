/**
 * ParallelMatrix.java
 * */
package org.crf.libmatrix;

import org.crf.libmatrix.core.LibraryConfiguration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
		Matrix.Constraints.forMultiply(this, rhs);

		double[] matrix = new double[ height*rhs.width ];
		int poolSize = ((Integer) LibraryConfiguration.get(LibraryConfiguration.MTX_THREAD_COUNT)).intValue(); 
		ExecutorService threadPool = Executors.newFixedThreadPool( poolSize );

		int dRow = height/poolSize + 1;
		for(int i = 0; i < height; i += dRow) {
			dRow = (i + dRow) < height ? dRow : (height - i);
			threadPool.execute(new LineMultiplier( this,
					                               rhs, 
					                               i,
					                               i + dRow,
					                               matrix ));
		}
		waitFor( threadPool );
		return new ParallelMatrix( height, rhs.width, matrix );
	}	

	/**
	 * 
	 * */
	private static final void waitFor(ExecutorService threadPool) {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
		} catch(InterruptedException e) {
// TODO: throw..
			System.err.println("Execution timed out..");
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + Matrix.hashCode(matrix);
		result = prime * result + width;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParallelMatrix))
			return false;
		ParallelMatrix other = (ParallelMatrix) obj;
		if (height != other.height)
			return false;
		if (!Arrays.equals(matrix, other.matrix))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	/**
	 * 
	 * */
	private static final class LineMultiplier 
									implements Runnable {
		
		LineMultiplier( final Matrix lhs, 
				        final Matrix rhs,
				        final int fromRow,
				        final int toRow,
				        double[] matrix ) {
			this.lhs = lhs;
			this.rhs = rhs;
			this.fromRow = fromRow;
			this.toRow = toRow;
			this.matrix = matrix;
		}

		/**
		 * 
		 * */
		@Override
		public void run() {
			double value;
			for(int i = fromRow; i < toRow; ++i) {
				for(int j = 0; j < rhs.width; ++j) {
					value = 0.0;
					for(int k = 0; k < lhs.width; ++k)
						value += lhs.get(i, k)*rhs.get(k, j);

					matrix[ i*rhs.width + j ] = value;
				}
			}
		}

		final Matrix lhs;
		final Matrix rhs;
		final int fromRow;
		final int toRow;
		private volatile double[] matrix;
	}
}
