/**
 * ParallelMatrix.java
 * */
package org.crf.libmatrix;

import org.crf.libmatrix.core.LibraryConfiguration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

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

	public ParallelMatrix(int height, double[] matrix) {
		this( height, height, matrix );
	}

	public ParallelMatrix(int height, int width, double[] matrix) {
		super( height, width, matrix );
		this.deltaRow = height / POOL_SIZE + 1;
	}

/** Unchecking Constructor.. */
	private ParallelMatrix(int height, int width, double[] matrix, boolean isChecked) {
		super( height, width, matrix, isChecked );
		this.deltaRow = height / POOL_SIZE + 1;
	}

	/**
	 * @param rhs
	 * @return the product of this {@link ParallelMatrix} and the one passed as rhs
	 * @see {@link Matrix#multiply(Matrix)}
	 * */
	@Override
	public final ParallelMatrix multiply(final Matrix rhs) {
		Matrix.Constraints.forMultiply(this, rhs);
		int dRow = deltaRow;

		List<LineMultiplier> threads = new ArrayList<>( POOL_SIZE );
		for(int i = 0; i < height; i += dRow) {
			dRow = (i + dRow) < height ? dRow : (height - i);
			threads.add(new LineMultiplier( this,
					                        rhs, 
					                        i,
					                        i + dRow ));
			//~ final int index = i;
            //~ threadPool.execute(() -> {
				//~ final int from = index;
				//~ final int to = (index + dRow) < height ? dRow : (height - index);
			//~ 
				//~ //double[] values = new double[ to - from ];
				//~ double value;
				//~ for(int row = from; row < height; ++row) {
					//~ for(int col = 0; col < rhs.width; ++col) {
//~ 
						//~ value = 0.0;
						//~ for(int k = 0; k < width; ++k)
							//~ value += get(row, col) * get(k, col);
//~ 
						//~ matrix[ row*rhs.width + col ] = value;
					//~ }
				//~ }
			//~ });
		}
		return new ParallelMatrix( height, rhs.width, invokeAll(this, rhs, threads), false );
	}

	/**
	 * 
	 * */
	private static final double[] invokeAll(final Matrix lhs, final Matrix rhs, Collection<? extends LineMultiplier> threads) {
		try {
			List<Future<LineMultiplier.MatrixPart>> parts = THREAD_POOL.invokeAll( threads );
			double[] matrix = new double[ lhs.height*rhs.width ];

			for(Future<LineMultiplier.MatrixPart> part : parts) {
				LineMultiplier.MatrixPart result = part.get();
				System.arraycopy( result.matrixPart, 0,
								  matrix, result.startIndex,
								  result.matrixPart.length );
			}
			return matrix;
		} catch(Exception e) {
			System.err.println(String.format("ERROR: %s", e));
			return null;
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

	private static final ExecutorService THREAD_POOL;
	private static final int POOL_SIZE;
	static {
		POOL_SIZE = ((Integer) LibraryConfiguration.get(LibraryConfiguration.MTX_THREAD_COUNT)).intValue();
		THREAD_POOL = Executors.newFixedThreadPool( POOL_SIZE );
	}

	private final int deltaRow;

	/**
	 * 
	 * */
	private static final class LineMultiplier 
									implements Callable<LineMultiplier.MatrixPart> {
		
		LineMultiplier( final Matrix lhs, 
				        final Matrix rhs,
				        final int fromRow,
				        final int toRow ) {
			this.lhs = lhs;
			this.rhs = rhs;
			this.fromRow = fromRow;
			this.toRow = toRow;
		}

		@Override
		public LineMultiplier.MatrixPart call() {
			final double[] values = new double[ (toRow - fromRow)*rhs.width ];
			for(int i = fromRow; i < toRow; ++i)
				for(int j = 0; j < rhs.width; ++j)
					for(int k = 0; k < lhs.width; ++k)
						values[(i - fromRow)*rhs.width + j] += lhs.get(i, k)*rhs.get(k, j);

			return new LineMultiplier.MatrixPart( fromRow*rhs.width, values );
		}

		final Matrix lhs;
		final Matrix rhs;
		final int fromRow;
		final int toRow;

		private static final class MatrixPart {

			MatrixPart(final int startIndex, final double[] matrixPart) {
				this.startIndex = startIndex;
				this.matrixPart = matrixPart;
			}
			final int startIndex;
			final double[] matrixPart;
		}
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
