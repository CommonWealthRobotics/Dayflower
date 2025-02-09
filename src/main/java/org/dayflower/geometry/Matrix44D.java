/**
 * Copyright 2014 - 2022 J&#246;rgen Lundgren
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

import static org.dayflower.utility.Doubles.abs;
import static org.dayflower.utility.Doubles.cos;
import static org.dayflower.utility.Doubles.equal;
import static org.dayflower.utility.Doubles.sin;
import static org.dayflower.utility.Doubles.tan;
import static org.dayflower.utility.Doubles.toRadians;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;//TODO: Add Unit Tests!
import java.util.Objects;

import org.dayflower.node.Node;
import org.dayflower.utility.ParameterArguments;

import org.macroing.java.lang.Strings;

/**
 * A {@code Matrix44D} represents a 4 x 4 matrix with 16 {@code double}-based elements.
 * <p>
 * The default order of this {@code Matrix44D} class is row-major. It stores its rotation and translation vectors as column vectors.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Matrix44D implements Node {
	/**
	 * The offset for the element at index 0 or row 1 and column 1 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_1_1 = 0;
	
	/**
	 * The offset for the element at index 1 or row 1 and column 2 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_1_2 = 1;
	
	/**
	 * The offset for the element at index 2 or row 1 and column 3 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_1_3 = 2;
	
	/**
	 * The offset for the element at index 3 or row 1 and column 4 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_1_4 = 3;
	
	/**
	 * The offset for the element at index 4 or row 2 and column 1 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_2_1 = 4;
	
	/**
	 * The offset for the element at index 5 or row 2 and column 2 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_2_2 = 5;
	
	/**
	 * The offset for the element at index 6 or row 2 and column 3 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_2_3 = 6;
	
	/**
	 * The offset for the element at index 7 or row 2 and column 4 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_2_4 = 7;
	
	/**
	 * The offset for the element at index 8 or row 3 and column 1 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_3_1 = 8;
	
	/**
	 * The offset for the element at index 9 or row 3 and column 2 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_3_2 = 9;
	
	/**
	 * The offset for the element at index 10 or row 3 and column 3 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_3_3 = 10;
	
	/**
	 * The offset for the element at index 11 or row 3 and column 4 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_3_4 = 11;
	
	/**
	 * The offset for the element at index 12 or row 4 and column 1 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_4_1 = 12;
	
	/**
	 * The offset for the element at index 13 or row 4 and column 2 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_4_2 = 13;
	
	/**
	 * The offset for the element at index 14 or row 4 and column 3 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_4_3 = 14;
	
	/**
	 * The offset for the element at index 15 or row 4 and column 4 in the {@code double[]}.
	 */
	public static final int ARRAY_OFFSET_ELEMENT_4_4 = 15;
	
	/**
	 * The size of the {@code double[]}.
	 */
	public static final int ARRAY_SIZE = 16;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The value of the element at index 0 or row 1 and column 1.
	 */
	public final double element11;
	
	/**
	 * The value of the element at index 1 or row 1 and column 2.
	 */
	public final double element12;
	
	/**
	 * The value of the element at index 2 or row 1 and column 3.
	 */
	public final double element13;
	
	/**
	 * The value of the element at index 3 or row 1 and column 4.
	 */
	public final double element14;
	
	/**
	 * The value of the element at index 4 or row 2 and column 1.
	 */
	public final double element21;
	
	/**
	 * The value of the element at index 5 or row 2 and column 2.
	 */
	public final double element22;
	
	/**
	 * The value of the element at index 6 or row 2 and column 3.
	 */
	public final double element23;
	
	/**
	 * The value of the element at index 7 or row 2 and column 4.
	 */
	public final double element24;
	
	/**
	 * The value of the element at index 8 or row 3 and column 1.
	 */
	public final double element31;
	
	/**
	 * The value of the element at index 9 or row 3 and column 2.
	 */
	public final double element32;
	
	/**
	 * The value of the element at index 10 or row 3 and column 3.
	 */
	public final double element33;
	
	/**
	 * The value of the element at index 11 or row 3 and column 4.
	 */
	public final double element34;
	
	/**
	 * The value of the element at index 12 or row 4 and column 1.
	 */
	public final double element41;
	
	/**
	 * The value of the element at index 13 or row 4 and column 2.
	 */
	public final double element42;
	
	/**
	 * The value of the element at index 14 or row 4 and column 3.
	 */
	public final double element43;
	
	/**
	 * The value of the element at index 15 or row 4 and column 4.
	 */
	public final double element44;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Matrix44D} instance denoting the identity matrix.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Matrix44D(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	 * }
	 * </pre>
	 */
	public Matrix44D() {
		this(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Constructs a new {@code Matrix44D} instance given its element values.
	 * 
	 * @param element11 the value of the element at index 0 or row 1 and column 1
	 * @param element12 the value of the element at index 1 or row 1 and column 2
	 * @param element13 the value of the element at index 2 or row 1 and column 3
	 * @param element14 the value of the element at index 3 or row 1 and column 4
	 * @param element21 the value of the element at index 4 or row 2 and column 1
	 * @param element22 the value of the element at index 5 or row 2 and column 2
	 * @param element23 the value of the element at index 6 or row 2 and column 3
	 * @param element24 the value of the element at index 7 or row 2 and column 4
	 * @param element31 the value of the element at index 8 or row 3 and column 1
	 * @param element32 the value of the element at index 9 or row 3 and column 2
	 * @param element33 the value of the element at index 10 or row 3 and column 3
	 * @param element34 the value of the element at index 11 or row 3 and column 4
	 * @param element41 the value of the element at index 12 or row 4 and column 1
	 * @param element42 the value of the element at index 13 or row 4 and column 2
	 * @param element43 the value of the element at index 14 or row 4 and column 3
	 * @param element44 the value of the element at index 15 or row 4 and column 4
	 */
	public Matrix44D(final double element11, final double element12, final double element13, final double element14, final double element21, final double element22, final double element23, final double element24, final double element31, final double element32, final double element33, final double element34, final double element41, final double element42, final double element43, final double element44) {
		this.element11 = element11;
		this.element12 = element12;
		this.element13 = element13;
		this.element14 = element14;
		this.element21 = element21;
		this.element22 = element22;
		this.element23 = element23;
		this.element24 = element24;
		this.element31 = element31;
		this.element32 = element32;
		this.element33 = element33;
		this.element34 = element34;
		this.element41 = element41;
		this.element42 = element42;
		this.element43 = element43;
		this.element44 = element44;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code Matrix44D} instance.
	 * 
	 * @return a {@code String} representation of this {@code Matrix44D} instance
	 */
	@Override
	public String toString() {
		final String row1 = String.format("%s, %s, %s, %s", Strings.toNonScientificNotationJava(this.element11), Strings.toNonScientificNotationJava(this.element12), Strings.toNonScientificNotationJava(this.element13), Strings.toNonScientificNotationJava(this.element14));
		final String row2 = String.format("%s, %s, %s, %s", Strings.toNonScientificNotationJava(this.element21), Strings.toNonScientificNotationJava(this.element22), Strings.toNonScientificNotationJava(this.element23), Strings.toNonScientificNotationJava(this.element24));
		final String row3 = String.format("%s, %s, %s, %s", Strings.toNonScientificNotationJava(this.element31), Strings.toNonScientificNotationJava(this.element32), Strings.toNonScientificNotationJava(this.element33), Strings.toNonScientificNotationJava(this.element34));
		final String row4 = String.format("%s, %s, %s, %s", Strings.toNonScientificNotationJava(this.element41), Strings.toNonScientificNotationJava(this.element42), Strings.toNonScientificNotationJava(this.element43), Strings.toNonScientificNotationJava(this.element44));
		
		return String.format("new Matrix44D(%s, %s, %s, %s)", row1, row2, row3, row4);
	}
	
	/**
	 * Compares {@code object} to this {@code Matrix44D} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Matrix44D}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Matrix44D} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Matrix44D}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Matrix44D)) {
			return false;
		} else if(!equal(this.element11, Matrix44D.class.cast(object).element11)) {
			return false;
		} else if(!equal(this.element12, Matrix44D.class.cast(object).element12)) {
			return false;
		} else if(!equal(this.element13, Matrix44D.class.cast(object).element13)) {
			return false;
		} else if(!equal(this.element14, Matrix44D.class.cast(object).element14)) {
			return false;
		} else if(!equal(this.element21, Matrix44D.class.cast(object).element21)) {
			return false;
		} else if(!equal(this.element22, Matrix44D.class.cast(object).element22)) {
			return false;
		} else if(!equal(this.element23, Matrix44D.class.cast(object).element23)) {
			return false;
		} else if(!equal(this.element24, Matrix44D.class.cast(object).element24)) {
			return false;
		} else if(!equal(this.element31, Matrix44D.class.cast(object).element31)) {
			return false;
		} else if(!equal(this.element32, Matrix44D.class.cast(object).element32)) {
			return false;
		} else if(!equal(this.element33, Matrix44D.class.cast(object).element33)) {
			return false;
		} else if(!equal(this.element34, Matrix44D.class.cast(object).element34)) {
			return false;
		} else if(!equal(this.element41, Matrix44D.class.cast(object).element41)) {
			return false;
		} else if(!equal(this.element42, Matrix44D.class.cast(object).element42)) {
			return false;
		} else if(!equal(this.element43, Matrix44D.class.cast(object).element43)) {
			return false;
		} else if(!equal(this.element44, Matrix44D.class.cast(object).element44)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code Matrix44D} instance is invertible, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Matrix44D} instance is invertible, {@code false} otherwise
	 */
	public boolean isInvertible() {
		return abs(determinant()) >= 1.0e-12D;
	}
	
	/**
	 * Returns the determinant of this {@code Matrix44D} instance.
	 * 
	 * @return the determinant of this {@code Matrix44D} instance
	 */
	public double determinant() {
		final double a = this.element11 * this.element22 - this.element12 * this.element21;
		final double b = this.element11 * this.element23 - this.element13 * this.element21;
		final double c = this.element11 * this.element24 - this.element14 * this.element21;
		final double d = this.element12 * this.element23 - this.element13 * this.element22;
		final double e = this.element12 * this.element24 - this.element14 * this.element22;
		final double f = this.element13 * this.element24 - this.element14 * this.element23;
		final double g = this.element31 * this.element42 - this.element32 * this.element41;
		final double h = this.element31 * this.element43 - this.element33 * this.element41;
		final double i = this.element31 * this.element44 - this.element34 * this.element41;
		final double j = this.element32 * this.element43 - this.element33 * this.element42;
		final double k = this.element32 * this.element44 - this.element34 * this.element42;
		final double l = this.element33 * this.element44 - this.element34 * this.element43;
		
		return a * l - b * k + c * j + d * i - e * h + f * g;
	}
	
	/**
	 * Returns the value of the element at index {@code index}.
	 * <p>
	 * If {@code index} is less than {@code 0} or greater than {@code 15}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param index the index of the element whose value to return
	 * @return the value of the element at index {@code index}
	 * @throws IllegalArgumentException thrown if, and only if, {@code index} is less than {@code 0} or greater than {@code 15}
	 */
	public double getElement(final int index) {
		switch(index) {
			case 0:
				return this.element11;
			case 1:
				return this.element12;
			case 2:
				return this.element13;
			case 3:
				return this.element14;
			case 4:
				return this.element21;
			case 5:
				return this.element22;
			case 6:
				return this.element23;
			case 7:
				return this.element24;
			case 8:
				return this.element31;
			case 9:
				return this.element32;
			case 10:
				return this.element33;
			case 11:
				return this.element34;
			case 12:
				return this.element41;
			case 13:
				return this.element42;
			case 14:
				return this.element43;
			case 15:
				return this.element44;
			default:
				throw new IllegalArgumentException(String.format("Illegal index: index=%s", Integer.toString(index)));
		}
	}
	
	/**
	 * Returns the value of the element at row {@code row} and column {@code column}.
	 * <p>
	 * If either {@code row} or {@code column} are less than {@code 1} or greater than {@code 4}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * <strong>Note:</strong> Both {@code row} and {@code column} starts at {@code 1}, rather than {@code 0}, as is sometimes the case.
	 * 
	 * @param row the row of the element whose value to return
	 * @param column the column of the element whose value to return
	 * @return the value of the element at row {@code row} and column {@code column}
	 * @throws IllegalArgumentException thrown if, and only if, either {@code row} or {@code column} are less than {@code 1} or greater than {@code 4}
	 */
	public double getElement(final int row, final int column) {
		ParameterArguments.requireRange(row, 1, 4, "row");
		ParameterArguments.requireRange(column, 1, 4, "column");
		
		return getElement((row - 1) * 4 + (column - 1));
	}
	
	/**
	 * Returns a {@code double[]} representation of this {@code Matrix44D} instance.
	 * 
	 * @return a {@code double[]} representation of this {@code Matrix44D} instance
	 */
	public double[] toArray() {
		final double[] array = new double[ARRAY_SIZE];
		
		array[ARRAY_OFFSET_ELEMENT_1_1] = this.element11;
		array[ARRAY_OFFSET_ELEMENT_1_2] = this.element12;
		array[ARRAY_OFFSET_ELEMENT_1_3] = this.element13;
		array[ARRAY_OFFSET_ELEMENT_1_4] = this.element14;
		array[ARRAY_OFFSET_ELEMENT_2_1] = this.element21;
		array[ARRAY_OFFSET_ELEMENT_2_2] = this.element22;
		array[ARRAY_OFFSET_ELEMENT_2_3] = this.element23;
		array[ARRAY_OFFSET_ELEMENT_2_4] = this.element24;
		array[ARRAY_OFFSET_ELEMENT_3_1] = this.element31;
		array[ARRAY_OFFSET_ELEMENT_3_2] = this.element32;
		array[ARRAY_OFFSET_ELEMENT_3_3] = this.element33;
		array[ARRAY_OFFSET_ELEMENT_3_4] = this.element34;
		array[ARRAY_OFFSET_ELEMENT_4_1] = this.element41;
		array[ARRAY_OFFSET_ELEMENT_4_2] = this.element42;
		array[ARRAY_OFFSET_ELEMENT_4_3] = this.element43;
		array[ARRAY_OFFSET_ELEMENT_4_4] = this.element44;
		
		return array;
	}
	
	/**
	 * Returns a hash code for this {@code Matrix44D} instance.
	 * 
	 * @return a hash code for this {@code Matrix44D} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(new Object[] {
			Double.valueOf(this.element11), Double.valueOf(this.element12), Double.valueOf(this.element13), Double.valueOf(this.element14),
			Double.valueOf(this.element21), Double.valueOf(this.element22), Double.valueOf(this.element23), Double.valueOf(this.element24),
			Double.valueOf(this.element31), Double.valueOf(this.element32), Double.valueOf(this.element33), Double.valueOf(this.element34),
			Double.valueOf(this.element41), Double.valueOf(this.element42), Double.valueOf(this.element43), Double.valueOf(this.element44)
		});
	}
	
	/**
	 * Writes this {@code Matrix44D} instance to {@code dataOutput}.
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
			dataOutput.writeDouble(this.element11);
			dataOutput.writeDouble(this.element12);
			dataOutput.writeDouble(this.element13);
			dataOutput.writeDouble(this.element14);
			dataOutput.writeDouble(this.element21);
			dataOutput.writeDouble(this.element22);
			dataOutput.writeDouble(this.element23);
			dataOutput.writeDouble(this.element24);
			dataOutput.writeDouble(this.element31);
			dataOutput.writeDouble(this.element32);
			dataOutput.writeDouble(this.element33);
			dataOutput.writeDouble(this.element34);
			dataOutput.writeDouble(this.element41);
			dataOutput.writeDouble(this.element42);
			dataOutput.writeDouble(this.element43);
			dataOutput.writeDouble(this.element44);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code Matrix44D} instance denoting the identity matrix.
	 * 
	 * @return a new {@code Matrix44D} instance denoting the identity matrix
	 */
	public static Matrix44D identity() {
		return new Matrix44D();
	}
	
	/**
	 * Returns a new {@code Matrix44D} instance that is the inverse of {@code m}.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code m} cannot be inverted, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * To make sure {@code m} is invertible, consider calling {@link #isInvertible()}.
	 * 
	 * @param m a {@code Matrix44D} instance
	 * @return a new {@code Matrix44D} instance that is the inverse of {@code m}
	 * @throws IllegalArgumentException thrown if, and only if, {@code m} cannot be inverted
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public static Matrix44D inverse(final Matrix44D m) {
		final double a = m.element11 * m.element22 - m.element12 * m.element21;
		final double b = m.element11 * m.element23 - m.element13 * m.element21;
		final double c = m.element11 * m.element24 - m.element14 * m.element21;
		final double d = m.element12 * m.element23 - m.element13 * m.element22;
		final double e = m.element12 * m.element24 - m.element14 * m.element22;
		final double f = m.element13 * m.element24 - m.element14 * m.element23;
		final double g = m.element31 * m.element42 - m.element32 * m.element41;
		final double h = m.element31 * m.element43 - m.element33 * m.element41;
		final double i = m.element31 * m.element44 - m.element34 * m.element41;
		final double j = m.element32 * m.element43 - m.element33 * m.element42;
		final double k = m.element32 * m.element44 - m.element34 * m.element42;
		final double l = m.element33 * m.element44 - m.element34 * m.element43;
		
		final double determinant = a * l - b * k + c * j + d * i - e * h + f * g;
		final double determinantReciprocal = 1.0D / determinant;
		
		if(abs(determinant) < 1.0e-12D) {
			throw new IllegalArgumentException("The Matrix44D 'm' cannot be inverted!");
		}
		
		final double element11 = (+m.element22 * l - m.element23 * k + m.element24 * j) * determinantReciprocal;
		final double element12 = (-m.element12 * l + m.element13 * k - m.element14 * j) * determinantReciprocal;
		final double element13 = (+m.element42 * f - m.element43 * e + m.element44 * d) * determinantReciprocal;
		final double element14 = (-m.element32 * f + m.element33 * e - m.element34 * d) * determinantReciprocal;
		final double element21 = (-m.element21 * l + m.element23 * i - m.element24 * h) * determinantReciprocal;
		final double element22 = (+m.element11 * l - m.element13 * i + m.element14 * h) * determinantReciprocal;
		final double element23 = (-m.element41 * f + m.element43 * c - m.element44 * b) * determinantReciprocal;
		final double element24 = (+m.element31 * f - m.element33 * c + m.element34 * b) * determinantReciprocal;
		final double element31 = (+m.element21 * k - m.element22 * i + m.element24 * g) * determinantReciprocal;
		final double element32 = (-m.element11 * k + m.element12 * i - m.element14 * g) * determinantReciprocal;
		final double element33 = (+m.element41 * e - m.element42 * c + m.element44 * a) * determinantReciprocal;
		final double element34 = (-m.element31 * e + m.element32 * c - m.element34 * a) * determinantReciprocal;
		final double element41 = (-m.element21 * j + m.element22 * h - m.element23 * g) * determinantReciprocal;
		final double element42 = (+m.element11 * j - m.element12 * h + m.element13 * g) * determinantReciprocal;
		final double element43 = (-m.element41 * d + m.element42 * b - m.element43 * a) * determinantReciprocal;
		final double element44 = (+m.element31 * d - m.element32 * b + m.element33 * a) * determinantReciprocal;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a new {@code Matrix44D} instance that looks in the direction of {@code eye} to {@code lookAt} and has an up-direction of {@code up}.
	 * <p>
	 * If either {@code eye}, {@code lookAt} or {@code up} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is essentially equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.multiply(Matrix44D.translate(eye), Matrix44D.rotate(new OrthonormalBasis33D(Vector3D.direction(eye, lookAt), up)));
	 * }
	 * </pre>
	 * 
	 * @param eye a {@link Point3D} instance that represents the eye to look from
	 * @param lookAt a {@code Point3D} instance that represents the point to look at
	 * @param up a {@link Vector3D} instance that represents the up-direction
	 * @return a new {@code Matrix44D} instance that looks in the direction of {@code eye} to {@code lookAt} and has an up-direction of {@code up}
	 * @throws NullPointerException thrown if, and only if, either {@code eye}, {@code lookAt} or {@code up} are {@code null}
	 */
	public static Matrix44D lookAt(final Point3D eye, final Point3D lookAt, final Vector3D up) {
		final Vector3D w = Vector3D.directionNormalized(eye, lookAt);
		final Vector3D u = Vector3D.normalize(Vector3D.crossProduct(Vector3D.normalize(up), w));
		final Vector3D v = Vector3D.crossProduct(w, u);
		
		final double element11 = u.x;
		final double element12 = v.x;
		final double element13 = w.x;
		final double element14 = eye.x;
		final double element21 = u.y;
		final double element22 = v.y;
		final double element23 = w.y;
		final double element24 = eye.y;
		final double element31 = u.z;
		final double element32 = v.z;
		final double element33 = w.z;
		final double element34 = eye.z;
		final double element41 = 0.0D;
		final double element42 = 0.0D;
		final double element43 = 0.0D;
		final double element44 = 1.0D;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Multiplies the element values of {@code mLHS} with the element values of {@code mRHS}.
	 * <p>
	 * Returns a new {@code Matrix44D} instance with the result of the multiplication.
	 * <p>
	 * If either {@code mLHS} or {@code mRHS} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param mLHS the {@code Matrix44D} instance on the left-hand side
	 * @param mRHS the {@code Matrix44D} instance on the right-hand side
	 * @return a new {@code Matrix44D} instance with the result of the multiplication
	 * @throws NullPointerException thrown if, and only if, either {@code mLHS} or {@code mRHS} are {@code null}
	 */
	public static Matrix44D multiply(final Matrix44D mLHS, final Matrix44D mRHS) {
		final double element11 = mLHS.element11 * mRHS.element11 + mLHS.element12 * mRHS.element21 + mLHS.element13 * mRHS.element31 + mLHS.element14 * mRHS.element41;
		final double element12 = mLHS.element11 * mRHS.element12 + mLHS.element12 * mRHS.element22 + mLHS.element13 * mRHS.element32 + mLHS.element14 * mRHS.element42;
		final double element13 = mLHS.element11 * mRHS.element13 + mLHS.element12 * mRHS.element23 + mLHS.element13 * mRHS.element33 + mLHS.element14 * mRHS.element43;
		final double element14 = mLHS.element11 * mRHS.element14 + mLHS.element12 * mRHS.element24 + mLHS.element13 * mRHS.element34 + mLHS.element14 * mRHS.element44;
		final double element21 = mLHS.element21 * mRHS.element11 + mLHS.element22 * mRHS.element21 + mLHS.element23 * mRHS.element31 + mLHS.element24 * mRHS.element41;
		final double element22 = mLHS.element21 * mRHS.element12 + mLHS.element22 * mRHS.element22 + mLHS.element23 * mRHS.element32 + mLHS.element24 * mRHS.element42;
		final double element23 = mLHS.element21 * mRHS.element13 + mLHS.element22 * mRHS.element23 + mLHS.element23 * mRHS.element33 + mLHS.element24 * mRHS.element43;
		final double element24 = mLHS.element21 * mRHS.element14 + mLHS.element22 * mRHS.element24 + mLHS.element23 * mRHS.element34 + mLHS.element24 * mRHS.element44;
		final double element31 = mLHS.element31 * mRHS.element11 + mLHS.element32 * mRHS.element21 + mLHS.element33 * mRHS.element31 + mLHS.element34 * mRHS.element41;
		final double element32 = mLHS.element31 * mRHS.element12 + mLHS.element32 * mRHS.element22 + mLHS.element33 * mRHS.element32 + mLHS.element34 * mRHS.element42;
		final double element33 = mLHS.element31 * mRHS.element13 + mLHS.element32 * mRHS.element23 + mLHS.element33 * mRHS.element33 + mLHS.element34 * mRHS.element43;
		final double element34 = mLHS.element31 * mRHS.element14 + mLHS.element32 * mRHS.element24 + mLHS.element33 * mRHS.element34 + mLHS.element34 * mRHS.element44;
		final double element41 = mLHS.element41 * mRHS.element11 + mLHS.element42 * mRHS.element21 + mLHS.element43 * mRHS.element31 + mLHS.element44 * mRHS.element41;
		final double element42 = mLHS.element41 * mRHS.element12 + mLHS.element42 * mRHS.element22 + mLHS.element43 * mRHS.element32 + mLHS.element44 * mRHS.element42;
		final double element43 = mLHS.element41 * mRHS.element13 + mLHS.element42 * mRHS.element23 + mLHS.element43 * mRHS.element33 + mLHS.element44 * mRHS.element43;
		final double element44 = mLHS.element41 * mRHS.element14 + mLHS.element42 * mRHS.element24 + mLHS.element43 * mRHS.element34 + mLHS.element44 * mRHS.element44;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance for perspective projection.
	 * <p>
	 * If {@code fieldOfView} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param fieldOfView an {@link AngleD} instance with the field of view
	 * @param aspectRatio the aspect ratio
	 * @param zNear the distance to the near plane on the Z-axis
	 * @param zFar the distance to the far plane on the Z-axis
	 * @return a {@code Matrix44D} instance for perspective projection
	 * @throws NullPointerException thrown if, and only if, {@code fieldOfView} is {@code null}
	 */
//	TODO: Add Unit Tests!
	public static Matrix44D perspective(final AngleD fieldOfView, final double aspectRatio, final double zNear, final double zFar) {
		final double tanHalfFieldOfView = tan(fieldOfView.getRadians() / 2.0D);
		
		final double element11 = 1.0D / (tanHalfFieldOfView * aspectRatio);
		final double element12 = 0.0D;
		final double element13 = 0.0D;
		final double element14 = 0.0D;
		final double element21 = 0.0D;
		final double element22 = 1.0D / tanHalfFieldOfView;
		final double element23 = 0.0D;
		final double element24 = 0.0D;
		final double element31 = 0.0D;
		final double element32 = 0.0D;
		final double element33 = zFar / (zFar - zNear);
		final double element34 = -zFar * zNear / (zFar - zNear);
		final double element41 = 0.0D;
		final double element42 = 0.0D;
		final double element43 = 1.0D;
		final double element44 = 0.0D;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a new {@code Matrix44D} instance by reading it from {@code dataInput}.
	 * <p>
	 * If {@code dataInput} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataInput the {@code DataInput} instance to read from
	 * @return a new {@code Matrix44D} instance by reading it from {@code dataInput}
	 * @throws NullPointerException thrown if, and only if, {@code dataInput} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static Matrix44D read(final DataInput dataInput) {
		try {
			return new Matrix44D(dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble(), dataInput.readDouble());
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the axis represented by {@code v}.
	 * <p>
	 * If either {@code a} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a an {@link AngleD} instance
	 * @param v a {@link Vector3D} instance that represents an axis
	 * @return a {@code Matrix44D} instance that rotates along the axis represented by {@code v}
	 * @throws NullPointerException thrown if, and only if, either {@code a} or {@code v} are {@code null}
	 */
	public static Matrix44D rotate(final AngleD a, final Vector3D v) {
		final Vector3D w = Vector3D.normalize(v);
		
		final double cos = a.cos();
		final double sin = a.sin();
		final double oneMinusCos = 1.0D - cos;
		
		final double element11 = oneMinusCos * w.x * w.x + cos;
		final double element12 = oneMinusCos * w.x * w.y - sin * w.z;
		final double element13 = oneMinusCos * w.x * w.z + sin * w.y;
		final double element14 = 0.0D;
		final double element21 = oneMinusCos * w.x * w.y + sin * w.z;
		final double element22 = oneMinusCos * w.y * w.y + cos;
		final double element23 = oneMinusCos * w.y * w.z - sin * w.x;
		final double element24 = 0.0D;
		final double element31 = oneMinusCos * w.x * w.z - sin * w.y;
		final double element32 = oneMinusCos * w.y * w.z + sin * w.x;
		final double element33 = oneMinusCos * w.z * w.z + cos;
		final double element34 = 0.0D;
		final double element41 = 0.0D;
		final double element42 = 0.0D;
		final double element43 = 0.0D;
		final double element44 = 1.0D;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the axis represented by {@code x}, {@code y} and {@code z}.
	 * <p>
	 * If {@code a} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.rotate(a, new Vector3D(x, y, z));
	 * }
	 * </pre>
	 * 
	 * @param a an {@link AngleD} instance
	 * @param x the component value along the X-axis
	 * @param y the component value along the Y-axis
	 * @param z the component value along the Z-axis
	 * @return a {@code Matrix44D} instance that rotates along the axis represented by {@code x}, {@code y} and {@code z}
	 * @throws NullPointerException thrown if, and only if, {@code a} is {@code null}
	 */
	public static Matrix44D rotate(final AngleD a, final double x, final double y, final double z) {
		return rotate(a, new Vector3D(x, y, z));
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates using {@code o}.
	 * <p>
	 * If {@code o} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param o an {@link OrthonormalBasis33D} instance
	 * @return a {@code Matrix44D} instance that rotates using {@code o}
	 * @throws NullPointerException thrown if, and only if, {@code o} is {@code null}
	 */
	public static Matrix44D rotate(final OrthonormalBasis33D o) {
		return rotate(o.w, o.v, o.u);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates using {@code q}.
	 * <p>
	 * If {@code q} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param q a {@link Quaternion4D} instance
	 * @return a {@code Matrix44D} instance that rotates using {@code q}
	 * @throws NullPointerException thrown if, and only if, {@code q} is {@code null}
	 */
//	TODO: Add Unit Tests!
	public static Matrix44D rotate(final Quaternion4D q) {
		final double uX = 1.0D - 2.0D * (q.y * q.y + q.z * q.z);
		final double uY = 0.0D + 2.0D * (q.x * q.y - q.w * q.z);
		final double uZ = 0.0D + 2.0D * (q.x * q.z + q.w * q.y);
		final double vX = 0.0D + 2.0D * (q.x * q.y + q.w * q.z);
		final double vY = 1.0D - 2.0D * (q.x * q.x + q.z * q.z);
		final double vZ = 0.0D + 2.0D * (q.y * q.z - q.w * q.x);
		final double wX = 0.0D + 2.0D * (q.x * q.z - q.w * q.y);
		final double wY = 0.0D + 2.0D * (q.y * q.z + q.w * q.x);
		final double wZ = 1.0D - 2.0D * (q.x * q.x + q.y * q.y);
		
		final Vector3D u = new Vector3D(uX, uY, uZ);
		final Vector3D v = new Vector3D(vX, vY, vZ);
		final Vector3D w = new Vector3D(wX, wY, wZ);
		
		return rotate(w, v, u);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates using {@code w} and {@code v}.
	 * <p>
	 * If either {@code w} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3D} instance
	 * @param v a {@code Vector3D} instance
	 * @return a {@code Matrix44D} instance that rotates using {@code w} and {@code v}
	 * @throws NullPointerException thrown if, and only if, either {@code w} or {@code v} are {@code null}
	 */
	public static Matrix44D rotate(final Vector3D w, final Vector3D v) {
		final Vector3D wNormalized = Vector3D.normalize(w);
		final Vector3D uNormalized = Vector3D.normalize(Vector3D.crossProduct(Vector3D.normalize(v), wNormalized));
		final Vector3D vNormalized = Vector3D.crossProduct(wNormalized, uNormalized);
		
		return rotate(wNormalized, vNormalized, uNormalized);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates using {@code w}, {@code v} and {@code u}.
	 * <p>
	 * If either {@code w}, {@code v} or {@code u} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * UX, VX, WX, 0
	 * UY, VY, WY, 0
	 * UZ, VZ, WZ, 0
	 *  0,  0,  0, 1
	 * }
	 * </pre>
	 * 
	 * @param w a {@link Vector3D} instance
	 * @param v a {@code Vector3D} instance
	 * @param u a {@code Vector3D} instance
	 * @return a {@code Matrix44D} instance that rotates using {@code w}, {@code v} and {@code u}
	 * @throws NullPointerException thrown if, and only if, either {@code w}, {@code v} or {@code u} are {@code null}
	 */
	public static Matrix44D rotate(final Vector3D w, final Vector3D v, final Vector3D u) {
		final double element11 = u.x;
		final double element12 = v.x;
		final double element13 = w.x;
		final double element14 = 0.0D;
		final double element21 = u.y;
		final double element22 = v.y;
		final double element23 = w.y;
		final double element24 = 0.0D;
		final double element31 = u.z;
		final double element32 = v.z;
		final double element33 = w.z;
		final double element34 = 0.0D;
		final double element41 = 0.0D;
		final double element42 = 0.0D;
		final double element43 = 0.0D;
		final double element44 = 1.0D;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the X-axis.
	 * <p>
	 * If {@code a} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1,   0,    0, 0
	 * 0, cos, -sin, 0
	 * 0, sin,  cos, 0
	 * 0,   0,    0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an {@link AngleD} instance
	 * @return a {@code Matrix44D} instance that rotates along the X-axis
	 * @throws NullPointerException thrown if, and only if, {@code a} is {@code null}
	 */
	public static Matrix44D rotateX(final AngleD a) {
		final double cos = a.cos();
		final double sin = a.sin();
		
		return new Matrix44D(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, cos, -sin, 0.0D, 0.0D, sin, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the X-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1,   0,    0, 0
	 * 0, cos, -sin, 0
	 * 0, sin,  cos, 0
	 * 0,   0,    0, 1
	 * }
	 * </pre>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.rotateX(a, false);
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees
	 * @return a {@code Matrix44D} instance that rotates along the X-axis
	 */
	public static Matrix44D rotateX(final double a) {
		return rotateX(a, false);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the X-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1,   0,    0, 0
	 * 0, cos, -sin, 0
	 * 0, sin,  cos, 0
	 * 0,   0,    0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees or radians
	 * @param isRadians {@code true} if, and only if, {@code a} is in radians, {@code false} otherwise
	 * @return a {@code Matrix44D} instance that rotates along the X-axis
	 */
	public static Matrix44D rotateX(final double a, final boolean isRadians) {
		final double radians = isRadians ? a : toRadians(a);
		
		final double cos = cos(radians);
		final double sin = sin(radians);
		
		return new Matrix44D(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, cos, -sin, 0.0D, 0.0D, sin, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Y-axis.
	 * <p>
	 * If {@code a} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 *  cos, 0, sin, 0
	 *    0, 1,   0, 0
	 * -sin, 0, cos, 0
	 *    0, 0,   0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an {@link AngleD} instance
	 * @return a {@code Matrix44D} instance that rotates along the Y-axis
	 * @throws NullPointerException thrown if, and only if, {@code a} is {@code null}
	 */
	public static Matrix44D rotateY(final AngleD a) {
		final double cos = a.cos();
		final double sin = a.sin();
		
		return new Matrix44D(cos, 0.0D, sin, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, -sin, 0.0D, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Y-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 *  cos, 0, sin, 0
	 *    0, 1,   0, 0
	 * -sin, 0, cos, 0
	 *    0, 0,   0, 1
	 * }
	 * </pre>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.rotateY(a, false);
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees
	 * @return a {@code Matrix44D} instance that rotates along the Y-axis
	 */
	public static Matrix44D rotateY(final double a) {
		return rotateY(a, false);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Y-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 *  cos, 0, sin, 0
	 *    0, 1,   0, 0
	 * -sin, 0, cos, 0
	 *    0, 0,   0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees or radians
	 * @param isRadians {@code true} if, and only if, {@code a} is in radians, {@code false} otherwise
	 * @return a {@code Matrix44D} instance that rotates along the Y-axis
	 */
	public static Matrix44D rotateY(final double a, final boolean isRadians) {
		final double radians = isRadians ? a : toRadians(a);
		
		final double cos = cos(radians);
		final double sin = sin(radians);
		
		return new Matrix44D(cos, 0.0D, sin, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, -sin, 0.0D, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Z-axis.
	 * <p>
	 * If {@code a} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * cos, -sin, 0, 0
	 * sin,  cos, 0, 0
	 *   0,    0, 1, 0
	 *   0,    0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an {@link AngleD} instance
	 * @return a {@code Matrix44D} instance that rotates along the Z-axis
	 * @throws NullPointerException thrown if, and only if, {@code a} is {@code null}
	 */
	public static Matrix44D rotateZ(final AngleD a) {
		final double cos = a.cos();
		final double sin = a.sin();
		
		return new Matrix44D(cos, -sin, 0.0D, 0.0D, sin, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Z-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * cos, -sin, 0, 0
	 * sin,  cos, 0, 0
	 *   0,    0, 1, 0
	 *   0,    0, 0, 1
	 * }
	 * </pre>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.rotateZ(a, false);
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees
	 * @return a {@code Matrix44D} instance that rotates along the Z-axis
	 */
	public static Matrix44D rotateZ(final double a) {
		return rotateZ(a, false);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that rotates along the Z-axis.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * cos, -sin, 0, 0
	 * sin,  cos, 0, 0
	 *   0,    0, 1, 0
	 *   0,    0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param a an angle in degrees or radians
	 * @param isRadians {@code true} if, and only if, {@code a} is in radians, {@code false} otherwise
	 * @return a {@code Matrix44D} instance that rotates along the Z-axis
	 */
	public static Matrix44D rotateZ(final double a, final boolean isRadians) {
		final double radians = isRadians ? a : toRadians(a);
		
		final double cos = cos(radians);
		final double sin = sin(radians);
		
		return new Matrix44D(cos, -sin, 0.0D, 0.0D, sin, cos, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.scale(v.x, v.y, v.z);
	 * }
	 * </pre>
	 * 
	 * @param v a {@link Vector3D} instance that contains the scale factors along the X-, Y- and Z-axes
	 * @return a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public static Matrix44D scale(final Vector3D v) {
		return scale(v.x, v.y, v.z);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.scale(s, s, s);
	 * }
	 * </pre>
	 * 
	 * @param s the scale factor along the X-, Y- and Z-axes
	 * @return a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes
	 */
	public static Matrix44D scale(final double s) {
		return scale(s, s, s);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * x, 0, 0, 0
	 * 0, y, 0, 0
	 * 0, 0, z, 0
	 * 0, 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the scale factor along the X-axis
	 * @param y the scale factor along the Y-axis
	 * @param z the scale factor along the Z-axis
	 * @return a {@code Matrix44D} instance that scales along the X-, Y- and Z-axes
	 */
	public static Matrix44D scale(final double x, final double y, final double z) {
		return new Matrix44D(x, 0.0D, 0.0D, 0.0D, 0.0D, y, 0.0D, 0.0D, 0.0D, 0.0D, z, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance for screen space transformation.
	 * 
	 * @param width the width of the screen
	 * @param height the height of the screen
	 * @return a {@code Matrix44D} instance for screen space transformation
	 */
//	TODO: Add Unit Tests!
	public static Matrix44D screenSpaceTransform(final double width, final double height) {
		final double halfWidth = width * 0.5D;
		final double halfHeight = height * 0.5D;
		
		final double element11 = halfWidth;
		final double element12 = 0.0D;
		final double element13 = 0.0D;
		final double element14 = halfWidth - 0.5D;
		final double element21 = 0.0D;
		final double element22 = -halfHeight;
		final double element23 = 0.0D;
		final double element24 = halfHeight - 0.5D;
		final double element31 = 0.0D;
		final double element32 = 0.0D;
		final double element33 = 1.0D;
		final double element34 = 0.0D;
		final double element41 = 0.0D;
		final double element42 = 0.0D;
		final double element43 = 0.0D;
		final double element44 = 1.0D;
		
		return new Matrix44D(element11, element12, element13, element14, element21, element22, element23, element24, element31, element32, element33, element34, element41, element42, element43, element44);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that translates along the X-, Y- and Z-axes.
	 * <p>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * Matrix44D.translate(p.x, p.y, p.z);
	 * }
	 * </pre>
	 * 
	 * @param p a {@link Point3D} instance that contains the translation factors along the X-, Y- and Z-axes
	 * @return a {@code Matrix44D} instance that translates along the X-, Y- and Z-axes
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public static Matrix44D translate(final Point3D p) {
		return translate(p.x, p.y, p.z);
	}
	
	/**
	 * Returns a {@code Matrix44D} instance that translates along the X-, Y- and Z-axes.
	 * <p>
	 * The layout looks like this:
	 * <pre>
	 * {@code
	 * 1, 0, 0, x
	 * 0, 1, 0, y
	 * 0, 0, 1, z
	 * 0, 0, 0, 1
	 * }
	 * </pre>
	 * 
	 * @param x the translation factor along the X-axis
	 * @param y the translation factor along the Y-axis
	 * @param z the translation factor along the Z-axis
	 * @return a {@code Matrix44D} instance that translates along the X-, Y- and Z-axes
	 */
	public static Matrix44D translate(final double x, final double y, final double z) {
		return new Matrix44D(1.0D, 0.0D, 0.0D, x, 0.0D, 1.0D, 0.0D, y, 0.0D, 0.0D, 1.0D, z, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a new {@code Matrix44D} instance that is the transpose of {@code m}.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m a {@code Matrix44D} instance
	 * @return a new {@code Matrix44D} instance that is the transpose of {@code m}
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public static Matrix44D transpose(final Matrix44D m) {
		return new Matrix44D(m.element11, m.element21, m.element31, m.element41, m.element12, m.element22, m.element32, m.element42, m.element13, m.element23, m.element33, m.element43, m.element14, m.element24, m.element34, m.element44);
	}
}