/**
 * Matrix.java
 * */
package org.crf.libmatrix;

import java.util.Arrays;

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
public class Matrix {

/** ParallelMatrix support.. */
	protected Matrix(int height, int width, double[] matrix, boolean isChecked) {
		this.height = height;
		this.width = width;
		this.matrix = matrix;
	}

	/**
	 * "Syntax sugar" for creating an NxN <code>Matrix</code>
	 * */
	public Matrix(int height) {
		this( height, height );
	}

	public Matrix(int height, int width) {
		this( width, height, identityFor(height, width) );
	}

	/**
	 * "Syntax sugar" for creating an NxN <code>Matrix</code>
	 * */
	public Matrix(int height, double[] matrix) {
		this( height, height, matrix );
	}

	public Matrix(int height, int width, double[] matrix) {
		this.height = height;
		this.width = width;
		copy( matrix );
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
	private static double[] identityFor(int height, int width) {
		Constraints.forSize(height, width);

		double[] matrix = new double[ width*height ];
		for(int i = 0, min = Math.min(width, height); i < min; ++i) 
			matrix[ i*width + i ] = 1.0;
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
	protected void copy(double[] matrix) {
	    Constraints.forSize(height, width);
		Constraints.forArrayLength(height, width, matrix);

		int size = height*width;
		if (size == matrix.length)
			this.matrix = matrix;
		else {
			this.matrix = new double[ size ];
			System.arraycopy( matrix, 0,
			                  this.matrix, 0,
			                  matrix.length );
		}
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
		i = (i >= 0) ? i : (height + i);
		j = (j >= 0) ? j : (width + j);
		return i*width + j;
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
	public double get(int i, int j) {
		return matrix[indexOf( i, j )];
	}

	/**
	 * @return the height of the {@link Matrix}
	 * @see {@link Numeric#getValue()}
	 * */
	public final int getHeight() {
		return height;
	}

	/**
	 * @return the width of the {@link Matrix}
	 * @see {@link Numeric#getValue()}
	 * */
	public final int getWidth() {
		return width;
	}

	/**
	 * Adds two <code>Matrix</code>es and returns the result -- a completely new <code>Matrix</code>.
	 * 
	 * @param rhs
	 * @return the sum of this {@link Matrix} and the one passed as argument..
	 * */
	public Matrix add(final Matrix rhs) {
		Constraints.forAdd(this, rhs);
		for(int i = 0; i < matrix.length; ++i)
			matrix[i] = this.matrix[i] + rhs.matrix[i];
		return new Matrix( this.height, this.width, matrix, false );
	}

	/**
	 * Multiplies two <code>Matrix</code>es.
	 * 
	 * @param rhs
	 * @return the multiplication of this {@link Matrix} and the one passed as rhs
	 * */
	public Matrix multiply(final Matrix rhs) {
		double[] matrix = new double[ height*rhs.width ];
		for(int i = 0; i < height; ++i) {
			for(int j = 0; j < rhs.width; ++j) {
				for(int k = 0; k < width; ++k)
					matrix[i*rhs.width + j] += get(i, k) * rhs.get(k, j);
			}
		}
		return new Matrix( height, rhs.width, matrix, false );
	}

	/**
	 * @see {@link Object#toString()}
	 * */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < height; ++i) {
			builder.append("| ");
			for(int j = 0; j < width; ++j)
				builder.append(String.format("%s ", get(i, j)));
			builder.append("|\n");
		}
		return builder.toString();
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
	 * Returns a hash code value for the array
	 * @param array the array to create a hash code value for
	 * @return a hash code value for the array
	 */
	protected static int hashCode(double[] array) {
		int prime = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			long temp = Double.doubleToLongBits(array[index]);
			result = prime * result + (int) (temp ^ (temp >>> 32));
		}
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
		if (!(obj instanceof Matrix))
			return false;
		Matrix other = (Matrix) obj;
		if (height != other.height)
			return false;
		if (!Arrays.equals(matrix, other.matrix))
			return false;
		if (width != other.width)
			return false;
		return true;
	}


// Matrix descriptors
	protected final int height;
	protected final int width;
	protected double[] matrix;


	/**
	 * Verifies the arithmetic constraints defined on <code>Matrix</code>es, and 
	 * upon assertion violation it throws <code>ArithmeticException</code>.
	 * <p>
	 * Also verifies some semantic constraints regarding the length of the passed array 
	 * and the height, width values.
	 * */
	static final class Constraints {

		private Constraints() {
		}

		/**
		 * Verifies that the array complies to the policy of being smaller than or equal to the product of height and 
		 * width in length.
		 * 
		 * 
		 * @param height
		 * @param width
		 * @param array
		 * @throws {@link ArithmeticException} in case the array is larger than height*width
		 * */
		static final void forArrayLength(int height, int width, double[] array) {
			if (array == null || array.length > height*width)
				throw new ArithmeticException("The length of the backing array must not exceed the overall size of the matrix.");
		}

		/**
		 * Verifies that each parameter passed is greater than zero.
		 * 
		 * @param height
		 * @param width
		 * @throws {@link ArithmeticException} in case one of the parameters is less than or equal to zero
		 * */
		static final void forSize(int height, int width) {
			if (height <= 0 || width <= 0)
				throw new ArithmeticException("Height and width of the matrix must be greater than zero.");
		}

		/**
		 * Verifies the two <code>Matrix</code>es against the mathematical prerequisites for matrix 
		 * addition.
		 * 
		 * @param lhs
		 * @param rhs
		 * @throws {@link ArithmeticException} in case the two {@link Matrix}es don't comply
		 * */
		static final void forAdd(Matrix lhs, Matrix rhs) {
			if (lhs.height != rhs.height || lhs.width != rhs.width)
				throw new ArithmeticException("Both operands of matrix addition must have the same height and width.");
		}

		/**
		 * Verifies the two <code>Matrix</code>es against the mathematical prerequisites for matrix 
		 * multiplication.
		 * 
		 * @param lhs
		 * @param rhs
		 * @throws {@link ArithmeticException} in case the two {@link Matrix}es don't comply
		 * */
		static final void forMultiply(Matrix lhs, Matrix rhs) {
			if (lhs.width != rhs.height)
				throw new ArithmeticException("Matrix on the left handside must have the width same as the height of the one on the right handside.");
		}
	}
}
