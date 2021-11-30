/**
 * Copyright 2014 - 2021 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.geometry;

import static org.dayflower.utility.Floats.abs;
import static org.dayflower.utility.Floats.cos;
import static org.dayflower.utility.Floats.equal;
import static org.dayflower.utility.Floats.sin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;//TODO: Add Unit Tests!
import java.util.Objects;

import org.dayflower.java.lang.Strings;
import org.dayflower.node.Node;
import org.dayflower.utility.ParameterArguments;

/**
 * A {@code Matrix33F} represents a 3 x 3 matrix with 9 {@code float}-based elements.
 * <p>
 * The default order of this {@code Matrix33F} class is row-major.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Matrix33F implements Node {
	private final float element11;
	private final float element12;
	private final float element13;
	private final float element21;
	private final float element22;
	private final float element23;
	private final float element31;
	private final float element32;
	private final float element33;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Matrix33F} instance denoting the identity matrix.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Matrix33F(1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	 * }
	 * </pre>
	 */
	public Matrix33F() {
		this(1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Constructs a new {@code Matrix33F} instance given its element values.
	 * 
	 * @param element11 the value of the element at index 0 or row 1 and column 1
	 * @param element12 the value of the element at index 1 or row 1 and column 2
	 * @param element13 the value of the element at index 2 or row 1 and column 3
	 * @param element21 the value of the element at index 3 or row 2 and column 1
	 * @param element22 the value of the element at index 4 or row 2 and column 2
	 * @param element23 the value of the element at index 5 or row 2 and column 3
	 * @param element31 the value of the element at index 6 or row 3 and column 1
	 * @param element32 the value of the element at index 7 or row 3 and column 2
	 * @param element33 the value of the element at index 8 or row 3 and column 3
	 */
	public Matrix33F(final float element11, final float element12, final float element13, final float element21, final float element22, final float element23, final float element31, final float element32, final float element33) {
		this.element11 = element11;
		this.element12 = element12;
		this.element13 = element13;
		this.element21 = element21;
		this.element22 = element22;
		this.element23 = element23;
		this.element31 = element31;
		this.element32 = element32;
		this.element33 = element33;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code Matrix33F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Matrix33F} instance
	 */
	@Override
	public String toString() {
		final String row1 = String.format("%sF, %sF, %sF", Strings.toNonScientificNotation(this.element11), Strings.toNonScientificNotation(this.element12), Strings.toNonScientificNotation(this.element13));
		final String row2 = String.format("%sF, %sF, %sF", Strings.toNonScientificNotation(this.element21), Strings.toNonScientificNotation(this.element22), Strings.toNonScientificNotation(this.element23));
		final String row3 = String.format("%sF, %sF, %sF", Strings.toNonScientificNotation(this.element31), Strings.toNonScientificNotation(this.element32), Strings.toNonScientificNotation(this.element33));
		
		return String.format("new Matrix33F(%s, %s, %s)", row1, row2, row3);
	}
	
	/**
	 * Compares {@code object} to this {@code Matrix33F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Matrix33F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Matrix33F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Matrix33F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Matrix33F)) {
			return false;
		} else if(!equal(this.element11, Matrix33F.class.cast(object).element11)) {
			return false;
		} else if(!equal(this.element12, Matrix33F.class.cast(object).element12)) {
			return false;
		} else if(!equal(this.element13, Matrix33F.class.cast(object).element13)) {
			return false;
		} else if(!equal(this.element21, Matrix33F.class.cast(object).element21)) {
			return false;
		} else if(!equal(this.element22, Matrix33F.class.cast(object).element22)) {
			return false;
		} else if(!equal(this.element23, Matrix33F.class.cast(object).element23)) {
			return false;
		} else if(!equal(this.element31, Matrix33F.class.cast(object).element31)) {
			return false;
		} else if(!equal(this.element32, Matrix33F.class.cast(object).element32)) {
			return false;
		} else if(!equal(this.element33, Matrix33F.class.cast(object).element33)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code Matrix33F} instance is invertible, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Matrix33F} instance is invertible, {@code false} otherwise
	 */
//	TODO: Add Unit Tests!
	public boolean isInvertible() {
		return abs(determinant()) >= 1.0e-12F;
	}
	
	/**
	 * Returns the determinant of this {@code Matrix33F} instance.
	 * 
	 * @return the determinant of this {@code Matrix33F} instance
	 */
//	TODO: Add Unit Tests!
	public float determinant() {
		final float a = this.element11;
		final float b = this.element12;
		final float c = this.element13;
		final float d = this.element21 * this.element32 - this.element22 * this.element31;
		final float e = this.element21 * this.element22 - this.element23 * this.element31;
		final float f = this.element22 * this.element33 - this.element32 * this.element23;
		
		return a * f - b * e + c * d;
	}
	
	/**
	 * Returns the value of the element at index {@code index}.
	 * <p>
	 * If {@code index} is less than {@code 0} or greater than {@code 8}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param index the index of the element whose value to return
	 * @return the value of the element at index {@code index}
	 * @throws IllegalArgumentException thrown if, and only if, {@code index} is less than {@code 0} or greater than {@code 8}
	 */
	public float getElement(final int index) {
		switch(index) {
			case 0:
				return this.element11;
			case 1:
				return this.element12;
			case 2:
				return this.element13;
			case 3:
				return this.element21;
			case 4:
				return this.element22;
			case 5:
				return this.element23;
			case 6:
				return this.element31;
			case 7:
				return this.element32;
			case 8:
				return this.element33;
			default:
				throw new IllegalArgumentException(String.format("Illegal index: index=%s", Integer.toString(index)));
		}
	}
	
	/**
	 * Returns the value of the element at row {@code row} and column {@code column}.
	 * <p>
	 * If either {@code row} or {@code column} are less than {@code 1} or greater than {@code 3}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * <strong>Note:</strong> Both {@code row} and {@code column} starts at {@code 1}, rather than {@code 0}, as is sometimes the case.
	 * 
	 * @param row the row of the element whose value to return
	 * @param column the column of the element whose value to return
	 * @return the value of the element at row {@code row} and column {@code column}
	 * @throws IllegalArgumentException thrown if, and only if, either {@code row} or {@code column} are less than {@code 1} or greater than {@code 3}
	 */
	public float getElement(final int row, final int column) {
		ParameterArguments.requireRange(row, 1, 3, "row");
		ParameterArguments.requireRange(column, 1, 3, "column");
		
		final int index = (row - 1) * 3 + (column - 1);
		
		return getElement(index);
	}
	
	/**
	 * Returns the value of the element at index 0 or row 1 and column 1.
	 * 
	 * @return the value of the element at index 0 or row 1 and column 1
	 */
	public float getElement11() {
		return this.element11;
	}
	
	/**
	 * Returns the value of the element at index 1 or row 1 and column 2.
	 * 
	 * @return the value of the element at index 1 or row 1 and column 2
	 */
	public float getElement12() {
		return this.element12;
	}
	
	/**
	 * Returns the value of the element at index 2 or row 1 and column 3.
	 * 
	 * @return the value of the element at index 2 or row 1 and column 3
	 */
	public float getElement13() {
		return this.element13;
	}
	
	/**
	 * Returns the value of the element at index 3 or row 2 and column 1.
	 * 
	 * @return the value of the element at index 3 or row 2 and column 1
	 */
	public float getElement21() {
		return this.element21;
	}
	
	/**
	 * Returns the value of the element at index 4 or row 2 and column 2.
	 * 
	 * @return the value of the element at index 4 or row 2 and column 2
	 */
	public float getElement22() {
		return this.element22;
	}
	
	/**
	 * Returns the value of the element at index 5 or row 2 and column 3.
	 * 
	 * @return the value of the element at index 5 or row 2 and column 3
	 */
	public float getElement23() {
		return this.element23;
	}
	
	/**
	 * Returns the value of the element at index 6 or row 3 and column 1.
	 * 
	 * @return the value of the element at index 6 or row 3 and column 1
	 */
	public float getElement31() {
		return this.element31;
	}
	
	/**
	 * Returns the value of the element at index 7 or row 3 and column 2.
	 * 
	 * @return the value of the element at index 7 or row 3 and column 2
	 */
	public float getElement32() {
		return this.element32;
	}
	
	/**
	 * Returns the value of the element at index 8 or row 3 and column 3.
	 * 
	 * @return the value of the element at index 8 or row 3 and column 3
	 */
	public float getElement33() {
		return this.element33;
	}
	
	/**
	 * Returns a hash code for this {@code Matrix33F} instance.
	 * 
	 * @return a hash code for this {@code Matrix33F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(new Object[] {
			Float.valueOf(this.element11), Float.valueOf(this.element12), Float.valueOf(this.element13),
			Float.valueOf(this.element21), Float.valueOf(this.element22), Float.valueOf(this.element23),
			Float.valueOf(this.element31), Float.valueOf(this.element32), Float.valueOf(this.element33)
		});
	}
	
	/**
	 * Writes this {@code Matrix33F} instance to {@code dataOutput}.
	 * <p>
	 * If {@code dataOutput} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataOutput the {@code DataOutput} instance to write to
	 * @throws NullPointerException thrown if, and only if, {@code dataOutput} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public void write(final DataOutput dataOutput) {
		try {
			dataOutput.writeFloat(this.element11);
			dataOutput.writeFloat(this.element12);
			dataOutput.writeFloat(this.element13);
			dataOutput.writeFloat(this.element21);
			dataOutput.writeFloat(this.element22);
			dataOutput.writeFloat(this.element23);
			dataOutput.writeFloat(this.element31);
			dataOutput.writeFloat(this.element32);
			dataOutput.writeFloat(this.element33);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code Matrix33F} instance denoting the identity matrix.
	 * 
	 * @return a new {@code Matrix33F} instance denoting the identity matrix
	 */
	public static Matrix33F identity() {
		return new Matrix33F();
	}
	
	/**
	 * Returns a new {@code Matrix33F} instance that is the inverse of {@code matrix}.
	 * <p>
	 * If {@code matrix} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code matrix} cannot be inverted, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * To make sure {@code matrix} is invertible, consider calling {@link #isInvertible()}.
	 * 
	 * @param matrix a {@code Matrix33F} instance
	 * @return a new {@code Matrix33F} instance that is the inverse of {@code matrix}
	 * @throws IllegalArgumentException thrown if, and only if, {@code matrix} cannot be inverted
	 * @throws NullPointerException thrown if, and only if, {@code matrix} is {@code null}
	 */
//	TODO: Add Unit Tests!
	public static Matrix33F inverse(final Matrix33F matrix) {
		if(!matrix.isInvertible()) {
			throw new IllegalArgumentException("The Matrix33F 'matrix' cannot be inverted!");
		}
		
		final float determinant = matrix.determinant();
		final float determinantReciprocal = 1.0F / determinant;
		
		final float element11 = (matrix.element22 * matrix.element33 - matrix.element32 * matrix.element23) * determinantReciprocal;
		final float element12 = (matrix.element13 * matrix.element32 - matrix.element12 * matrix.element33) * determinantReciprocal;
		final float element13 = (matrix.element12 * matrix.element23 - matrix.element13 * matrix.element22) * determinantReciprocal;
		final float element21 = (matrix.element23 * matrix.element31 - matrix.element21 * matrix.element33) * determinantReciprocal;
		final float element22 = (matrix.element11 * matrix.element33 - matrix.element13 * matrix.element31) * determinantReciprocal;
		final float element23 = (matrix.element21 * matrix.element13 - matrix.element11 * matrix.element23) * determinantReciprocal;
		final float element31 = (matrix.element21 * matrix.element32 - matrix.element31 * matrix.element22) * determinantReciprocal;
		final float element32 = (matrix.element31 * matrix.element12 - matrix.element11 * matrix.element32) * determinantReciprocal;
		final float element33 = (matrix.element11 * matrix.element22 - matrix.element21 * matrix.element12) * determinantReciprocal;
		
		return new Matrix33F(element11, element12, element13, element21, element22, element23, element31, element32, element33);
	}
	
	/**
	 * Multiplies the element values of {@code matrixLHS} with the element values of {@code matrixRHS}.
	 * <p>
	 * Returns a new {@code Matrix33F} instance with the result of the multiplication.
	 * <p>
	 * If either {@code matrixLHS} or {@code matrixRHS} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param matrixLHS the {@code Matrix33F} instance on the left-hand side
	 * @param matrixRHS the {@code Matrix33F} instance on the right-hand side
	 * @return a new {@code Matrix33F} instance with the result of the multiplication
	 * @throws NullPointerException thrown if, and only if, either {@code matrixLHS} or {@code matrixRHS} are {@code null}
	 */
//	TODO: Add Unit Tests!
	public static Matrix33F multiply(final Matrix33F matrixLHS, final Matrix33F matrixRHS) {
		final float element11 = matrixLHS.element11 * matrixRHS.element11 + matrixLHS.element12 * matrixRHS.element21 + matrixLHS.element13 * matrixRHS.element31;
		final float element12 = matrixLHS.element11 * matrixRHS.element12 + matrixLHS.element12 * matrixRHS.element22 + matrixLHS.element13 * matrixRHS.element32;
		final float element13 = matrixLHS.element11 * matrixRHS.element13 + matrixLHS.element12 * matrixRHS.element23 + matrixLHS.element13 * matrixRHS.element33;
		final float element21 = matrixLHS.element21 * matrixRHS.element11 + matrixLHS.element22 * matrixRHS.element21 + matrixLHS.element23 * matrixRHS.element31;
		final float element22 = matrixLHS.element21 * matrixRHS.element12 + matrixLHS.element22 * matrixRHS.element22 + matrixLHS.element23 * matrixRHS.element32;
		final float element23 = matrixLHS.element21 * matrixRHS.element13 + matrixLHS.element22 * matrixRHS.element23 + matrixLHS.element23 * matrixRHS.element33;
		final float element31 = matrixLHS.element31 * matrixRHS.element11 + matrixLHS.element32 * matrixRHS.element21 + matrixLHS.element33 * matrixRHS.element31;
		final float element32 = matrixLHS.element31 * matrixRHS.element12 + matrixLHS.element32 * matrixRHS.element22 + matrixLHS.element33 * matrixRHS.element32;
		final float element33 = matrixLHS.element31 * matrixRHS.element13 + matrixLHS.element32 * matrixRHS.element23 + matrixLHS.element33 * matrixRHS.element33;
		
		return new Matrix33F(element11, element12, element13, element21, element22, element23, element31, element32, element33);
	}
	
	/**
	 * Returns a new {@code Matrix33F} instance by reading it from {@code dataInput}.
	 * <p>
	 * If {@code dataInput} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataInput the {@code DataInput} instance to read from
	 * @return a new {@code Matrix33F} instance by reading it from {@code dataInput}
	 * @throws NullPointerException thrown if, and only if, {@code dataInput} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static Matrix33F read(final DataInput dataInput) {
		try {
			final float element11 = dataInput.readFloat();
			final float element12 = dataInput.readFloat();
			final float element13 = dataInput.readFloat();
			final float element21 = dataInput.readFloat();
			final float element22 = dataInput.readFloat();
			final float element23 = dataInput.readFloat();
			final float element31 = dataInput.readFloat();
			final float element32 = dataInput.readFloat();
			final float element33 = dataInput.readFloat();
			
			return new Matrix33F(element11, element12, element13, element21, element22, element23, element31, element32, element33);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that rotates around the origin.
	 * <p>
	 * If {@code angle} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * +cos, sin, 0
	 * -sin, cos, 0
	 *    0,   0, 1
	 * }
	 * </pre>
	 * 
	 * @param angle an {@link AngleF} instance
	 * @return a {@code Matrix33F} instance that rotates around the origin
	 * @throws NullPointerException thrown if, and only if, {@code angle} is {@code null}
	 */
//	TODO: Add Unit Tests!
	public static Matrix33F rotate(final AngleF angle) {
		final float cos = cos(angle.getRadians());
		final float sin = sin(angle.getRadians());
		
		return new Matrix33F(cos, sin, 0.0F, -sin, cos, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that scales along the X- and Y-axes.
	 * <p>
	 * If {@code vector} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix33F.scale(vector.getX(), vector.getY());
	 * }
	 * </pre>
	 * 
	 * @param vector a {@link Vector2F} instance that contains the scale factors along the X- and Y-axes
	 * @return a {@code Matrix33F} instance that scales along the X- and Y-axes
	 * @throws NullPointerException thrown if, and only if, {@code vector} is {@code null}
	 */
	public static Matrix33F scale(final Vector2F vector) {
		return scale(vector.getX(), vector.getY());
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that scales along the X- and Y-axes.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix33F.scale(scalar, scalar);
	 * }
	 * </pre>
	 * 
	 * @param scalar the scale factor along the X- and Y-axes
	 * @return a {@code Matrix33F} instance that scales along the X- and Y-axes
	 */
	public static Matrix33F scale(final float scalar) {
		return scale(scalar, scalar);
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that scales along the X- and Y-axes.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * x, 0, 0
	 * 0, y, 0
	 * 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the scale factor along the X-axis
	 * @param y the scale factor along the Y-axis
	 * @return a {@code Matrix33F} instance that scales along the X- and Y-axes
	 */
	public static Matrix33F scale(final float x, final float y) {
		return new Matrix33F(x, 0.0F, 0.0F, 0.0F, y, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that translates along the X- and Y-axes.
	 * <p>
	 * If {@code point} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix33F.translate(point.getX(), point.getY());
	 * }
	 * </pre>
	 * 
	 * @param point a {@link Point2F} instance that contains the translation factors along the X- and Y-axes
	 * @return a {@code Matrix33F} instance that translates along the X- and Y-axes
	 * @throws NullPointerException thrown if, and only if, {@code point} is {@code null}
	 */
	public static Matrix33F translate(final Point2F point) {
		return translate(point.getX(), point.getY());
	}
	
	/**
	 * Returns a {@code Matrix33F} instance that translates along the X- and Y-axes.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1, 0, x
	 * 0, 1, y
	 * 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the translation factor along the X-axis
	 * @param y the translation factor along the Y-axis
	 * @return a {@code Matrix33F} instance that translates along the X- and Y-axes
	 */
	public static Matrix33F translate(final float x, final float y) {
		return new Matrix33F(1.0F, 0.0F, x, 0.0F, 1.0F, y, 0.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a new {@code Matrix33F} instance that is the transpose of {@code matrix}.
	 * <p>
	 * If {@code matrix} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param matrix a {@code Matrix33F} instance
	 * @return a new {@code Matrix33F} instance that is the transpose of {@code matrix}
	 * @throws NullPointerException thrown if, and only if, {@code matrix} is {@code null}
	 */
	public static Matrix33F transpose(final Matrix33F matrix) {
		final float element11 = matrix.element11;
		final float element12 = matrix.element21;
		final float element13 = matrix.element31;
		final float element21 = matrix.element12;
		final float element22 = matrix.element22;
		final float element23 = matrix.element32;
		final float element31 = matrix.element13;
		final float element32 = matrix.element23;
		final float element33 = matrix.element33;
		
		return new Matrix33F(element11, element12, element13, element21, element22, element23, element31, element32, element33);
	}
}