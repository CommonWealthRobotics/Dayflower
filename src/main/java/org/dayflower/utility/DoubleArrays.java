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
package org.dayflower.utility;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import org.dayflower.java.io.DoubleArrayOutputStream;

/**
 * A class that consists exclusively of static methods that returns or performs various operations on {@code double[]} instances.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class DoubleArrays {
	private DoubleArrays() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, the elements of {@code arrayA} starting at {@code offsetArrayA} are equal to the elements of {@code arrayB} starting at {@code offsetArrayB}, {@code false} otherwise.
	 * <p>
	 * If either {@code arrayA} or {@code arrayB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code offsetArrayA} is less than {@code 0} or greater than or equal to {@code arrayA.length}, {@code offsetArrayB} is less than {@code 0} or greater than or equal to {@code arrayB.length}, {@code length} is less than {@code 0},
	 * {@code offsetArrayA + length} is less than {@code 0} or greater than {@code arrayA.length} or {@code offsetArrayB + length} is less than {@code 0} or greater than {@code arrayB.length}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param arrayA a {@code double[]}
	 * @param arrayB a {@code double[]}
	 * @param offsetArrayA the offset to start at in {@code arrayA}
	 * @param offsetArrayB the offset to start at in {@code arrayB}
	 * @param length the length of the sub-arrays to test for equality
	 * @return {@code true} if, and only if, the elements of {@code arrayA} starting at {@code offsetArrayA} are equal to the elements of {@code arrayB} starting at {@code offsetArrayB}, {@code false} otherwise
	 * @throws IllegalArgumentException thrown if, and only if, {@code offsetArrayA} is less than {@code 0} or greater than or equal to {@code arrayA.length}, {@code offsetArrayB} is less than {@code 0} or greater than or equal to
	 *                                  {@code arrayB.length}, {@code length} is less than {@code 0}, {@code offsetArrayA + length} is less than {@code 0} or greater than {@code arrayA.length} or {@code offsetArrayB + length} is less than {@code 0}
	 *                                  or greater than {@code arrayB.length}
	 * @throws NullPointerException thrown if, and only if, either {@code arrayA} or {@code arrayB} are {@code null}
	 */
	public static boolean equal(final double[] arrayA, final double[] arrayB, final int offsetArrayA, final int offsetArrayB, final int length) {
		Objects.requireNonNull(arrayA, "arrayA == null");
		Objects.requireNonNull(arrayB, "arrayB == null");
		
		ParameterArguments.requireRange(offsetArrayA, 0, arrayA.length - 1, "offsetArrayA");
		ParameterArguments.requireRange(offsetArrayB, 0, arrayB.length - 1, "offsetArrayB");
		ParameterArguments.requireRange(length, 0, Integer.MAX_VALUE, "length");
		ParameterArguments.requireRange(offsetArrayA + length, 0, arrayA.length, "offsetArrayA + length");
		ParameterArguments.requireRange(offsetArrayB + length, 0, arrayB.length, "offsetArrayB + length");
		
		for(int i = 0; i < length; i++) {
			if(!Doubles.equal(arrayA[offsetArrayA + i], arrayB[offsetArrayB + i])) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns a {@code double[]} representation of {@code objects} using {@code arrayFunction}.
	 * <p>
	 * If either {@code objects}, at least one of its elements, {@code arrayFunction} or at least one of its results are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.convert(objects, arrayFunction, 0);
	 * }
	 * </pre>
	 * 
	 * @param <T> the generic type
	 * @param objects a {@code List} of type {@code T} with {@code Object} instances to convert into {@code double[]} instances
	 * @param arrayFunction a {@code Function} that maps {@code Object} instances of type {@code T} into {@code double[]} instances
	 * @return a {@code double[]} representation of {@code objects} using {@code arrayFunction}
	 * @throws NullPointerException thrown if, and only if, either {@code objects}, at least one of its elements, {@code arrayFunction} or at least one of its results are {@code null}
	 */
	public static <T> double[] convert(final List<T> objects, final Function<T, double[]> arrayFunction) {
		return convert(objects, arrayFunction, 0);
	}
	
	/**
	 * Returns a {@code double[]} representation of {@code objects} using {@code arrayFunction}.
	 * <p>
	 * If either {@code objects}, at least one of its elements, {@code arrayFunction} or at least one of its results are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code minimumLength} is less than {@code 0}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param <T> the generic type
	 * @param objects a {@code List} of type {@code T} with {@code Object} instances to convert into {@code double[]} instances
	 * @param arrayFunction a {@code Function} that maps {@code Object} instances of type {@code T} into {@code double[]} instances
	 * @param minimumLength the minimum length of the returned {@code double[]} if, and only if, either {@code objects.isEmpty()} or the array has a length of {@code 0}
	 * @return a {@code double[]} representation of {@code objects} using {@code arrayFunction}
	 * @throws IllegalArgumentException thrown if, and only if, {@code minimumLength} is less than {@code 0}
	 * @throws NullPointerException thrown if, and only if, either {@code objects}, at least one of its elements, {@code arrayFunction} or at least one of its results are {@code null}
	 */
	public static <T> double[] convert(final List<T> objects, final Function<T, double[]> arrayFunction, final int minimumLength) {
		ParameterArguments.requireNonNullList(objects, "objects");
		
		Objects.requireNonNull(arrayFunction, "arrayFunction == null");
		
		ParameterArguments.requireRange(minimumLength, 0, Integer.MAX_VALUE, "minimumLength");
		
		if(objects.isEmpty()) {
			return create(minimumLength);
		}
		
		try(final DoubleArrayOutputStream doubleArrayOutputStream = new DoubleArrayOutputStream()) {
			for(final T object : objects) {
				doubleArrayOutputStream.write(arrayFunction.apply(object));
			}
			
			final double[] array = doubleArrayOutputStream.toDoubleArray();
			
			return array.length == 0 ? create(minimumLength) : array;
		}
	}
	
	/**
	 * Returns a {@code double[]} with a length of {@code length} and is filled with {@code 0.0D}.
	 * <p>
	 * If {@code length} is less than {@code 0}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.create(length, 0.0D);
	 * }
	 * </pre>
	 * 
	 * @param length the length of the {@code double[]}
	 * @return a {@code double[]} with a length of {@code length} and is filled with {@code 0.0D}
	 * @throws IllegalArgumentException thrown if, and only if, {@code length} is less than {@code 0}
	 */
	public static double[] create(final int length) {
		return create(length, 0.0D);
	}
	
	/**
	 * Returns a {@code double[]} with a length of {@code length} and is filled with {@code double} values from {@code doubleSupplier}.
	 * <p>
	 * If {@code length} is less than {@code 0}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * If {@code doubleSupplier} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param length the length of the {@code double[]}
	 * @param doubleSupplier the {@code DoubleSupplier} to fill the {@code double[]} with
	 * @return a {@code double[]} with a length of {@code length} and is filled with {@code double} values from {@code doubleSupplier}
	 * @throws IllegalArgumentException thrown if, and only if, {@code length} is less than {@code 0}
	 * @throws NullPointerException thrown if, and only if, {@code doubleSupplier} is {@code null}
	 */
	public static double[] create(final int length, final DoubleSupplier doubleSupplier) {
		final double[] array = new double[ParameterArguments.requireRange(length, 0, Integer.MAX_VALUE, "length")];
		
		Objects.requireNonNull(doubleSupplier, "doubleSupplier == null");
		
		for(int i = 0; i < array.length; i++) {
			array[i] = doubleSupplier.getAsDouble();
		}
		
		return array;
	}
	
	/**
	 * Returns a {@code double[]} with a length of {@code length} and is filled with {@code value}.
	 * <p>
	 * If {@code length} is less than {@code 0}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param length the length of the {@code double[]}
	 * @param value the {@code double} value to fill the {@code double[]} with
	 * @return a {@code double[]} with a length of {@code length} and is filled with {@code value}
	 * @throws IllegalArgumentException thrown if, and only if, {@code length} is less than {@code 0}
	 */
	public static double[] create(final int length, final double value) {
		final double[] array = new double[ParameterArguments.requireRange(length, 0, Integer.MAX_VALUE, "length")];
		
		Arrays.fill(array, value);
		
		return array;
	}
	
	/**
	 * Returns a {@code double[]} with a length of {@code length} and is filled with {@code value0}, {@code value1}, {@code value2} and {@code value3} in a repeated pattern.
	 * <p>
	 * If {@code length} is less than {@code 0} or it cannot be evenly divided by {@code 4}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param length the length of the {@code double[]}
	 * @param value0 the {@code double} at the relative offset {@code 0}
	 * @param value1 the {@code double} at the relative offset {@code 1}
	 * @param value2 the {@code double} at the relative offset {@code 2}
	 * @param value3 the {@code double} at the relative offset {@code 3}
	 * @return a {@code double[]} with a length of {@code length} and is filled with {@code value0}, {@code value1}, {@code value2} and {@code value3} in a repeated pattern
	 * @throws IllegalArgumentException thrown if, and only if, {@code length} is less than {@code 0} or it cannot be evenly divided by {@code 4}
	 */
	public static double[] create(final int length, final double value0, final double value1, final double value2, final double value3) {
		final double[] array = new double[ParameterArguments.requireRange(length, 0, Integer.MAX_VALUE, "length")];
		
		if(array.length % 4 != 0) {
			throw new IllegalArgumentException(String.format("%d %% 4 != 0", Integer.valueOf(length)));
		}
		
		for(int i = 0; i < length; i += 4) {
			array[i + 0] = value0;
			array[i + 1] = value1;
			array[i + 2] = value2;
			array[i + 3] = value3;
		}
		
		return array;
	}
	
	/**
	 * Performs a merge operation on the {@code double[]} instance {@code array} and the {@code double} {@code value}.
	 * <p>
	 * Returns a new {@code double[]} with {@code array} and {@code value} merged.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.merge(array, new double[] {value});
	 * }
	 * </pre>
	 * 
	 * @param array the {@code double[]} that comes first
	 * @param value the {@code double} that comes second
	 * @return a new {@code double[]} with {@code array} and {@code value} merged
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public static double[] merge(final double[] array, final double value) {
		return merge(array, new double[] {value});
	}
	
	/**
	 * Performs a merge operation on the {@code double[]} instances {@code arrayA} and {@code arrayB}.
	 * <p>
	 * Returns a new {@code double[]} with {@code arrayA} and {@code arrayB} merged.
	 * <p>
	 * If either {@code arrayA} or {@code arrayB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param arrayA the {@code double[]} that comes first
	 * @param arrayB the {@code double[]} that comes second
	 * @return a new {@code double[]} with {@code arrayA} and {@code arrayB} merged
	 * @throws NullPointerException thrown if, and only if, either {@code arrayA} or {@code arrayB} are {@code null}
	 */
	public static double[] merge(final double[] arrayA, final double[] arrayB) {
		Objects.requireNonNull(arrayA, "arrayA == null");
		Objects.requireNonNull(arrayB, "arrayB == null");
		
		final double[] arrayC = new double[arrayA.length + arrayB.length];
		
		final int offsetArrayA = 0;
		final int offsetArrayB = arrayA.length;
		
		System.arraycopy(arrayA, 0, arrayC, offsetArrayA, arrayA.length);
		System.arraycopy(arrayB, 0, arrayC, offsetArrayB, arrayB.length);
		
		return arrayC;
	}
	
	/**
	 * Performs a merge operation on the {@code double[]} instances in {@code arrays}.
	 * <p>
	 * Returns a new {@code double[]} with {@code arrays} merged.
	 * <p>
	 * If either {@code arrays} or at least one of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param arrays the {@code double[][]} instance to combine into one {@code double[]}
	 * @return a new {@code double[]} with {@code arrays} merged
	 * @throws NullPointerException thrown if, and only if, either {@code arrays} or at least one of its elements are {@code null}
	 */
	public static double[] merge(final double[]... arrays) {
		Objects.requireNonNull(arrays, "arrays == null");
		
		for(int i = 0; i < arrays.length; i++) {
			Objects.requireNonNull(arrays[i], "arrays[" + i + "] == null");
		}
		
		try(final DoubleArrayOutputStream doubleArrayOutputStream = new DoubleArrayOutputStream()) {
			for(final double[] array : arrays) {
				doubleArrayOutputStream.write(array);
			}
			
			return doubleArrayOutputStream.toDoubleArray();
		}
	}
	
	/**
	 * Performs a splice operation on the {@code double[]} instance {@code array}.
	 * <p>
	 * Returns a new {@code double[]} with the result of the operation.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.splice(array, offset, array.length);
	 * }
	 * </pre>
	 * 
	 * @param array the input {@code double[]}
	 * @param offset the offset for the removal, which may be negative
	 * @return a new {@code double[]} with the result of the operation
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public static double[] splice(final double[] array, final int offset) {
		return splice(array, offset, array.length);
	}
	
	/**
	 * Performs a splice operation on the {@code double[]} instance {@code array}.
	 * <p>
	 * Returns a new {@code double[]} with the result of the operation.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.splice(array, offset, length, new double[0]);
	 * }
	 * </pre>
	 * 
	 * @param array the input {@code double[]}
	 * @param offset the offset for the removal, which may be negative
	 * @param length the length of the removal, which may be negative
	 * @return a new {@code double[]} with the result of the operation
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public static double[] splice(final double[] array, final int offset, final int length) {
		return splice(array, offset, length, new double[0]);
	}
	
	/**
	 * Performs a splice operation on the {@code double[]} instance {@code array}.
	 * <p>
	 * Returns a new {@code double[]} with the result of the operation.
	 * <p>
	 * If either {@code array} or {@code arrayReplacement} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * DoubleArrays.splice(array, offset, length, arrayReplacement, new double[0]);
	 * }
	 * </pre>
	 * 
	 * @param array the input {@code double[]}
	 * @param offset the offset for the removal, which may be negative
	 * @param length the length of the removal, which may be negative
	 * @param arrayReplacement an {@code double[]} that acts as replacement
	 * @return a new {@code double[]} with the result of the operation
	 * @throws NullPointerException thrown if, and only if, either {@code array} or {@code arrayReplacement} are {@code null}
	 */
	public static double[] splice(final double[] array, final int offset, final int length, final double[] arrayReplacement) {
		return splice(array, offset, length, arrayReplacement, new double[0]);
	}
	
	/**
	 * Performs a splice operation on the {@code double[]} instance {@code array}.
	 * <p>
	 * Returns a new {@code double[]} with the result of the operation.
	 * <p>
	 * If either {@code array}, {@code arrayReplacement} or {@code arrayMatcher} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param array the input {@code double[]}
	 * @param offset the offset for the removal, which may be negative
	 * @param length the length of the removal, which may be negative
	 * @param arrayReplacement an {@code double[]} that acts as replacement
	 * @param arrayMatcher a {@code double[]} that matches the part of {@code array} that will be replaced
	 * @return a new {@code double[]} with the result of the operation
	 * @throws NullPointerException thrown if, and only if, either {@code array}, {@code arrayReplacement} or {@code arrayMatcher} are {@code null}
	 */
	public static double[] splice(final double[] array, final int offset, final int length, final double[] arrayReplacement, final double[] arrayMatcher) {
		Objects.requireNonNull(array, "array == null");
		Objects.requireNonNull(arrayReplacement, "arrayReplacement == null");
		
		final double[] arrayA = array;
		final double[] arrayB = arrayReplacement;
		final double[] arrayC = array;
		
		final int arrayASrcPos = 0;
		final int arrayALength = Ints.saturate(offset >= 0 ? offset : arrayA.length + offset, 0, arrayA.length);
		
		final int arrayBSrcPos = 0;
		final int arrayBLength = arrayB.length;
		
		final int arrayCSrcPos = Ints.saturate(length >= 0 ? length + arrayALength : arrayC.length + length, arrayALength, arrayC.length);
		final int arrayCLength = arrayC.length - arrayCSrcPos;
		
		final int arrayDSrcPos0 = 0;
		final int arrayDSrcPos1 = arrayALength;
		final int arrayDSrcPos2 = arrayALength + arrayBLength;
		final int arrayDLength  = arrayA.length - (arrayCSrcPos - arrayALength) + arrayBLength;
		
		if(arrayCSrcPos - arrayALength > 0 && !equal(array, arrayMatcher, arrayALength, 0, Ints.min(arrayCSrcPos - arrayALength, arrayMatcher.length))) {
			return array;
		}
		
		final double[] arrayD = new double[arrayDLength];
		
		System.arraycopy(arrayA, arrayASrcPos, arrayD, arrayDSrcPos0, arrayALength);
		System.arraycopy(arrayB, arrayBSrcPos, arrayD, arrayDSrcPos1, arrayBLength);
		System.arraycopy(arrayC, arrayCSrcPos, arrayD, arrayDSrcPos2, arrayCLength);
		
		return arrayD;
	}
	
	/**
	 * Returns the index of {@code value} in {@code array}, or {@code -1} if it cannot be found.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param value the {@code double} value to find the index for
	 * @param array the {@code double[]} to search for {@code value} in
	 * @return the index of {@code value} in {@code array}, or {@code -1} if it cannot be found
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public static int indexOf(final double value, final double[] array) {
		Objects.requireNonNull(array, "array == null");
		
		for(int i = 0; i < array.length; i++) {
			if(Doubles.equal(array[i], value)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of {@code value} in {@code array}, or {@code -1} if it cannot be found.
	 * <p>
	 * If either {@code value} or {@code array} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code isIncrementingByValueLength} is {@code true} and {@code array.length % value.length} is not equal to {@code 0}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * If the parameter argument {@code isIncrementingByValueLength} is {@code true}, this method assumes {@code array} contains sub-structures with equal lengths, namely {@code value.length}. This will yield a faster search, but also restrictions on
	 * {@code array}. It requires a length that is a multiple of {@code value.length}. If {@code array} does not contain sub-structures with equal lengths, {@code isIncrementingByValueLength} should be {@code false}.
	 * <p>
	 * If the parameter argument {@code isReturningRelativeIndex} is {@code true}, the relative index of {@code value} in {@code array} will be returned. The relative index represents the index of the sub-structure in {@code array}. It works best if
	 * the parameter argument {@code isIncrementingByValueLength} is {@code true} and its restrictions are met.
	 * <p>
	 * If the parameter argument {@code isReturningRelativeIndex} is {@code false}, the absolute index of {@code value} in {@code array} will be returned. The absolute index represents the index in {@code array}.
	 * 
	 * @param value the {@code double[]} value to find the index for
	 * @param array the {@code double[]} to search for {@code value} in
	 * @param isIncrementingByValueLength {@code true} if, and only if, {@code array} consists of sub-structures with a length of {@code value.length}, {@code false} otherwise
	 * @param isReturningRelativeIndex {@code true} if, and only if, the relative index should be returned, {@code false} otherwise
	 * @return the index of {@code value} in {@code array}, or {@code -1} if it cannot be found
	 * @throws IllegalArgumentException thrown if, and only if, {@code isIncrementingByValueLength} is {@code true} and {@code array.length % value.length} is not equal to {@code 0}
	 * @throws NullPointerException thrown if, and only if, either {@code value} or {@code array} are {@code null}
	 */
	public static int indexOf(final double[] value, final double[] array, final boolean isIncrementingByValueLength, final boolean isReturningRelativeIndex) {
		Objects.requireNonNull(value, "value == null");
		Objects.requireNonNull(array, "array == null");
		
		if(isIncrementingByValueLength) {
			ParameterArguments.requireExact(array.length % value.length, 0, "array.length % value.length");
		}
		
		final int count = isIncrementingByValueLength ? array.length / value.length : array.length;
		final int length = isIncrementingByValueLength ? value.length : 1;
		
		for(int indexAbsolute = 0, indexRelative = 0; indexRelative < count; indexAbsolute += length, indexRelative++) {
			if(equal(array, value, indexAbsolute, 0, length)) {
				return isReturningRelativeIndex ? indexRelative : indexAbsolute;
			}
		}
		
		return -1;
	}
}