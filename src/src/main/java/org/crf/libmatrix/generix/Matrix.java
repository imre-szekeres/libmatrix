/**
 * Matrix.java
 * */
package org.crf.libmatrix.generix;


import java.util.Arrays;

import org.crf.libmatrix.generix.numerix.Int;
import org.crf.libmatrix.core.ThreadSafe;
import org.crf.libmatrix.core.Immutable;

/**
 * Represents an NxK Matrix, where
 *     N: height -- number of rows
 *     K: width  -- number of columns
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
@Immutable
@ThreadSafe
public class Matrix <N extends Int, K extends Int> {

	protected Matrix(N height, K width) {
		super( );
		this.height = height;
		this.width = width;
		this.matrix = identityFor(height, width);
	}

	protected Matrix(N height, K width, double[] matrix) {
		super( );
		this.height = height;
		this.width = width;
		copy( matrix );
	}

	/**
	 * Factory method for creating an NxK <code>Matrix</code> what only contains 1.0-s in its
	 * main diagonal and zeros otherwise, with the height and width passed as arguments.
	 * 
	 * @param height
	 * @param width
	 * 
	 * @return an NxK identity-like {@link Matrix}
	 * */
	public static <N extends Int, K extends Int> Matrix<N, K> create(N height, K width) {
		return new Matrix<>( height, width );
	}

	/**
	 * Factory method for creating an NxN identity <code>Matrix</code> from the given height.
	 * 
	 * @param height
	 * @return an NxN identity {@link Matrix}
	 * */
	public static <N extends Int> Matrix<N, N> create(N height) {
		return new Matrix<>( height, height );
	}

	/**
	 * Creates a general purpose NxK <code>Matrix</code> constructed from the values 
	 * specified in the array of doubles given.
	 * <p>
	 * The length of the array must be less or equal to the product of heigth and width,
	 * in case the length is less than the expected, the elements not specified are set to zero.
	 * 
	 * @param height
	 * @param width
	 * @param matrix
	 * 
	 * @return the {@link Matrix} contructed
	 * @see {@link Matrix#copy(double[])}
	 * */
	public static <N extends Int, K extends Int> Matrix<N, K> create(N height, K width, double[] matrix) {
		return new Matrix<>( height, width, matrix );
	}

	/**
	 * Creates a general purpose NxN <code>Matrix</code> constructed from the values 
	 * specified in the array of doubles given.
	 * <p>
	 * The length of the array must be less or equal to the height on the power of two,
	 * in case the length is less than the expected, the elements not specified are set to zero.
	 * 
	 * @param height
	 * @param matrix
	 * 
	 * @return the {@link Matrix} contructed
	 * @see {@link Matrix#copy(double[])}
	 * */
	public static <N extends Int> Matrix<N, N> create(N height, double[] matrix) {
		return new Matrix<>( height, height, matrix );
	}

	/**
	 * Instantiates the backing array and copies the values passed as an array of <code>double</code>s 
	 * into it in case it is less in length than the expected size of the <code>Matrix</code>. 
	 * 
	 * @param matrix
	 * @throws {@link ArithmeticException} in case the length of the array passed
	 *         is larger than width*height
	 * @see {@link java.lang.System#arraycopy(java.lang.Object, int, java.lang.Object, int, int)}
	 * */
	private final void copy(double[] matrix) {
		if (matrix.length > width.getValue()*height.getValue())
			throw new ArithmeticException( "Array given is too large!" );

		else if (matrix.length == height.getValue()*width.getValue())
			this.matrix = matrix;

		else {
			this.matrix = new double[ width.getValue()*height.getValue() ];
			System.arraycopy( matrix, 0,       /** src */
							  this.matrix, 0,  /** dest */
							  matrix.length  );
		}
	}

	/**
	 * Creates an Identity like backing array for the passed parameters width, height,
	 * each element is zero except for the ones in the main diagonal, which have the value of
	 * one.
	 * 
	 * @param height
	 * @param width
	 * @return a reference to the array constructed
	 * */
	private static final <N extends Int, K extends Int> double[] identityFor(N height, K width) {
		int w = width.getValue();
		int h = height.getValue();

		double[] matrix = new double[ h*w ];
		for( int i = 0, min = Math.min(h, w);
		     i < min; 
		     ++i )
		    matrix[ i*w + i ] = 1.0;
		return matrix;
	}

	/**
	 * Returns value which would be in the i-th row and the j-th column contained by this 
	 * <code>Matrix</code> instance.
	 * 
	 * @param i
	 * @param j
	 * @return value which would be in the i-th row and the 
	 *         j-th column
	 * @see {@link Matrix#indexOf(int, int)}
	 * */
	public final double get(int i, int j) {
		return matrix[indexOf( i, j )];
	}

	/**
	 * Calculates the proper index for the backing array from the parameters passed
	 * as argument.
	 * <p>
	 * Allows pythonic negative indexing.
	 * 
	 * @param i identifies the i-th row of the <code>Matrix</code>
	 * @param j identifies the j-th column of the <code>Matrix</code>
	 * @return the index of the value which would be in the i-th row and the 
	 *         j-th column
	 * */
	protected final int indexOf(int i, int j) {
		int w = width();
		i = (i >= 0) ? i : (height() + i);
		j = (j >= 0) ? j : (w  + j);
		return i*w + j;
	}

	/**
	 * @return the height of the {@link Matrix}
	 * @see {@link Int#getValue()}
	 * */
	public final int height() {
		return height.getValue();
	}

	/**
	 * @return the width of the {@link Matrix}
	 * @see {@link Int#getValue()}
	 * */
	public final int width() {
		return width.getValue();
	}

	/**
	 * Adds two <code>Matrix</code>es and returns the result -- a completely new <code>Matrix</code>.
	 * 
	 * @param rhs
	 * @return the sum of this {@link Matrix} and the one passed as argument..
	 * */
	public Matrix<N, K> add(Matrix<N, K> rhs) {
		int h = height();
		int w = width();
		double[] matrix = new double[ h*w ];

		for(int i = 0; i < matrix.length; ++i)
			matrix[i] = this.matrix[i] + rhs.matrix[i];
		return new Matrix<>(this.height, this.width, matrix);
	}

	/**
	 * Multiplies two <code>Matrix</code>es.
	 * 
	 * @param rhs
	 * @return the multiplication of this {@link Matrix} and the one passed as rhs
	 * */
	public <K2 extends Int> Matrix<N, K2> multiply(Matrix<K, K2> rhs) {
 		int h1 = height();
 		int w1 = width();
 		int w2 = rhs.width();

		double[] matrix = new double[ h1*w2 ];
		for(int i = 0; i < h1; ++i) {
			for(int j = 0; j < w2; ++j) {
				for(int k = 0; k < w1; ++k)
					matrix[i*w2 + j] += get(i, k) * rhs.get(k, j);
			}
		}
		return new Matrix<>( this.height, rhs.width, matrix );
	}

	/**
	 * @see {@link Object#toString()}
	 * */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0, h = height(); i < h; ++i) {
			builder.append("| ");
			for(int j = 0, w = width(); j < w; ++j)
				builder.append(String.format("%s ", get(i, j)));
			builder.append("|\n");
		}
		return builder.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + Arrays.hashCode(matrix);
		result = prime * result + ((width == null) ? 0 : width.hashCode());
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
		if (!(obj instanceof Matrix))
			return false;
		@SuppressWarnings("rawtypes")
		Matrix other = (Matrix) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (!Arrays.equals(matrix, other.matrix))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}


// private Fields representing the Matirx..
	private final N height;
	private final K width;
	private double[] matrix;
}
