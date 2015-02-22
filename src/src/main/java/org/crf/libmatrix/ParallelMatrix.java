/**
 * ParallelMatrix.java
 * */
package org.crf.libmatrix;

import org.crf.libmatrix.core.LibraryConfiguration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

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

/** Unchecking Constructor.. */
	private ParallelMatrix(int height, int width, double[] matrix, boolean isChecked) {
		super( height, width, matrix, isChecked );
	}

	/**
	 * @param rhs
	 * @return the product of this {@link ParallelMatrix} and the one passed as rhs
	 * @see {@link Matrix#multiply(Matrix)}
	 * */
	@Override
	public final ParallelMatrix multiply(final Matrix rhs) {
		Matrix.Constraints.forMultiply(this, rhs);

		double[] matrix = new double[ height*rhs.width ];
		List<Thread> threadPool = new ArrayList<>( POOL_SIZE );

		int dRow = height/POOL_SIZE + 1;
		for(int i = 0; i < height; i += dRow) {
			dRow = (i + dRow) < height ? dRow : (height - i);
			threadPool.add(new Thread(new LineMultiplier( this,
					                                      rhs, 
					                                      i,
					                                      i + dRow,
					                                      matrix )));
			threadPool.get(threadPool.size() - 1).start();
		}		
		waitFor( threadPool );
		return new ParallelMatrix( height, rhs.width, matrix );
	}	

	/**
	 * Waits for all the threads run in the thread pool to finish their operations.
	 * @param threadPool
	 * */
	private static final void waitFor(List<? extends Thread> threadPool) {
		for(Thread thread : threadPool)
			try { thread.join(); } catch(final Exception e) {}
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

	private static final int POOL_SIZE;
	static {
		POOL_SIZE = ((Integer) LibraryConfiguration.get(LibraryConfiguration.MTX_THREAD_COUNT)).intValue();
	}

    /**
     * 
     * */
	public static final class Generator 
	                            extends Matrix.Generator {

        public Generator( ) {
			super( );
		}

		/**
		 * @param height
		 * @return a {@link ParallelMatrix} with a size of height*height
		 * @see {@link Matrix.Generator#generate(int)}
		 * */
		@Override
		public final ParallelMatrix generate(int height) {
			return this.generate( height, height );
		}

        /**
         * @param height
         * @param width
         * @return a {@link ParallelMatrix} with a size of height*width
         * @see {@link Matrix.Generator#generate(int, int)}
         * @see {@link Matrix.Generator#generateRandom(int, int)}
         * */
		@Override
		public final ParallelMatrix generate(int height, int width) {
            return new ParallelMatrix( height, width, super.generateRandom( height, width ), false );
		}
	}
}
