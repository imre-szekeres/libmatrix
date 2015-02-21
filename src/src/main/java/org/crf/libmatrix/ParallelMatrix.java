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
		this( height, height );
	}

	public ParallelMatrix(int height, int width) {
		this( height, width, identityFor(height, width) );
	}

	public ParallelMatrix(int height, double[][] matrix) {
		this( height, height, matrix );
	}

	public ParallelMatrix(int height, int width, double[][] matrix) {
		super( height, width, null, false );
		copy( matrix );
	}

// for internal operations..
	private ParallelMatrix(double[][] matrix) {
		super( matrix.length, matrix[0].length, null, false );
		this.matrix = matrix;
	}

	/**
	 * @param height
	 * @param width
	 * @return a 2 dimensional backing array for an identiy {@link ParallelMatrix}
	 * */
	private static final double[][] identityFor(int height, int width) {
		Matrix.Constraints.forSize(height, width);

// Could be parallelized..
		double[][] matrix = new double[ height ][];
		for(int i = 0; i < height; ++i) {
			matrix[i] = new double[ width ];
			matrix[i][i] = 1.0;
		}
		return matrix;
	}

	/**
	 * @param matrix
	 * @see {@link Matrix#copy(double[])}
	 * */
	private final void copy(double[][] matrix) {
        Matrix.Constraints.forSize(height, width);
        ParallelMatrix.Constraints.forArraySize(height, width, matrix);
        this.matrix = new double[ height ][];
// Could be parallelized..
        for(int i = 0; i < height; ++i) {
			if (matrix[i].length == width)
				this.matrix[i] = matrix[i];
			else {
				this.matrix[i] = new double[ width ];
				System.arraycopy( matrix[i], 0,
				                  this.matrix[i], 0,
				                  matrix.length );
			}
		}
	}

	/**
	 * @param i
	 * @param j
	 * @return the value at (i,j)
	 * @see {@link Matrix#get(int, int)}
	 * */
	@Override
	public final double get(int i, int j) {
		i = (i >= 0) ? i : (height + i);
		j = (j >= 0) ? j : (width + j);
		return matrix[i][j];
	}

	/**
	 * @param rhs
	 * @return a {@link ParallelMatrix} wich is the sum of this {@link ParallelMatrix}
	 *         and the rhs
	 * @see {@link Matrix#add(Matrix)}
	 * */
	public final ParallelMatrix add(final Matrix rhs) {
		Matrix.Constraints.forAdd(this, rhs);
		double[][] matrix = new double[ height ][];
// TODO: parallelize
		for(int i = 0; i < height; ++i) {
			matrix[i] = new double[ width ];
			for(int j = 0; j < width; ++j)
				matrix[i][j] += this.get(i, j) + rhs.get(i, j);
		}
		return new ParallelMatrix( matrix );
	}

	/**
	 * @param rhs
	 * @return a new {@link ParallelMatrix} which is the product of
	 *         this {@link ParallelMatrix} and the rhs passed as argument
	 * @see {@link Matrix#multiply(Matrix)}
	 * */
	@Override
	public final ParallelMatrix multiply(final Matrix rhs) {
		Matrix.Constraints.forMultiply(this, rhs);

		double[][] matrix = new double[ height ][];
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
		return new ParallelMatrix( matrix );
	}	

	/**
	 * @param threadPool
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + Arrays.deepHashCode(this.matrix);
		result = prime * result + width;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
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
		if (!Arrays.deepEquals(this.matrix, other.matrix))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

/** Shadowing the super.matrix */
	private double[][] matrix;

	/**
	 * 
	 * */
	private static final class LineMultiplier 
									implements Runnable {
		
		LineMultiplier( final Matrix lhs, 
				        final Matrix rhs,
				        final int fromRow,
				        final int toRow,
				        double[][] matrix ) {
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
			double[] row;
			for(int i = fromRow; i < toRow; ++i) {

				row = new double[ rhs.width ];
				for(int j = 0; j < rhs.width; ++j) {
					for(int k = 0; k < lhs.width; ++k)
						row[j] += lhs.get(i, k)*rhs.get(k, j);
				}
				matrix[i] = row;
			}
		}

		final Matrix lhs;
		final Matrix rhs;
		final int fromRow;
		final int toRow;
		private volatile double[][] matrix;
	}

	static final class Constraints {

		static final void forArraySize(int height, int width, double[][] matrix) {
			if (matrix == null || matrix.length > height)
				throw new ArithmeticException("The height of the backing matrix must not exceed the height of the matrix.");
			for(int i = 0; i < height; ++i)
				Matrix.Constraints.forArrayLength( 1, width, matrix[i] );
		}
	}
}
