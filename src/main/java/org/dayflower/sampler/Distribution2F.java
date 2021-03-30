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
package org.dayflower.sampler;

import static org.dayflower.utility.Ints.saturate;
import static org.dayflower.utility.Ints.toInt;

import java.lang.reflect.Field;
import java.util.Objects;

import org.dayflower.utility.FloatArrayOutputStream;
import org.dayflower.utility.ParameterArguments;

/**
 * A {@code Distribution2F} represents a 2-dimensional distribution and contains methods to sample it in a continuous or discrete way.
 * <p>
 * To use this class consider the following example:
 * <pre>
 * {@code
 * float[][] functions = ...;
 * 
 * Distribution2F distribution2F = new Distribution2F(functions);
 * 
 * Sample2F sample2F = new Sample2F(0.5F, 0.5F);
 * Sample2F sample2FContinuous = distribution2F.continuousRemap(sample2F);
 * Sample2F sample2FDiscrete = distribution2F.discreteRemap(sample2F);
 * 
 * float probabilityDensityFunctionContinuous0 = distribution2F.continuousProbabilityDensityFunction(sample2F);
 * float probabilityDensityFunctionContinuous1 = distribution2F.continuousProbabilityDensityFunction(sample2FContinuous, true);
 * float probabilityDensityFunctionDiscrete = distribution2F.discreteProbabilityDensityFunction(sample2F);
 * }
 * </pre>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Distribution2F {
	private final Distribution1F marginal;
	private final Distribution1F[] conditional;
	private final boolean isUV;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Distribution2F} instance.
	 * <p>
	 * If either {@code functions} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Distribution2F(functions, false);
	 * }
	 * </pre>
	 * 
	 * @param functions a {@code float[][]} with the values of the functions
	 * @throws NullPointerException thrown if, and only if, either {@code functions} or any of its elements are {@code null}
	 */
	public Distribution2F(final float[][] functions) {
		this(functions, false);
	}
	
	/**
	 * Constructs a new {@code Distribution2F} instance.
	 * <p>
	 * If either {@code functions} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code isUV} is {@code true}, the marginal represents the U-direction and the conditional represents the U- and V-directions (in that order). If, on the other hand, {@code isUV} is {@code false}, the marginal represents the V-direction and
	 * the conditional represents the V- and U-directions (in that order).
	 * 
	 * @param functions a {@code float[][]} with the values of the functions
	 * @param isUV {@code true} if, and only if, the marginal and conditional should be UV-based, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, either {@code functions} or any of its elements are {@code null}
	 */
	public Distribution2F(final float[][] functions, final boolean isUV) {
		this.conditional = new Distribution1F[functions.length];
		this.isUV = isUV;
		
		final float[] function = new float[functions.length];
		
		for(int i = 0; i < functions.length; i++) {
			final Distribution1F distribution = new Distribution1F(functions[i]);
			
			this.conditional[i] = distribution;
			
			function[i] = distribution.functionIntegral();
		}
		
		this.marginal = new Distribution1F(function);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Distribution1F} instance that represents the conditional at index {@code index}.
	 * <p>
	 * If {@code index} is less than {@code 0} or greater than {@code count() - 1}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param index the index
	 * @return the {@code Distribution1F} instance that represents the conditional at index {@code index}
	 * @throws IllegalArgumentException thrown if, and only if, {@code index} is less than {@code 0} or greater than {@code count() - 1}
	 */
	public Distribution1F getConditional(final int index) {
		return this.conditional[ParameterArguments.requireRange(index, 0, count() - 1, "index")];
	}
	
	/**
	 * Returns the {@link Distribution1F} instance that represents the marginal.
	 * 
	 * @return the {@code Distribution1F} instance that represents the marginal
	 */
	public Distribution1F getMarginal() {
		return this.marginal;
	}
	
	/**
	 * Performs a continuous remapping of {@code sample}.
	 * <p>
	 * Returns the remapped {@link Sample2F} instance.
	 * <p>
	 * If {@code sample} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sample the {@code Sample2F} instance
	 * @return the remapped {@code Sample2F} instance
	 * @throws NullPointerException thrown if, and only if, {@code sample} is {@code null}
	 */
	public Sample2F continuousRemap(final Sample2F sample) {
		final float m = this.isUV ? sample.getU() : sample.getV();
		final float c = this.isUV ? sample.getV() : sample.getU();
		
		final int indexM = this.marginal.index(m);
		final int indexC = this.conditional[indexM].index(c);
		
		final float mRemapped = this.marginal.continuousRemap(m, indexM);
		final float cRemapped = this.conditional[indexM].continuousRemap(c, indexC);
		
		return new Sample2F(this.isUV ? mRemapped : cRemapped, this.isUV ? cRemapped : mRemapped);
	}
	
	/**
	 * Performs a discrete remapping of {@code sample}.
	 * <p>
	 * Returns the remapped {@link Sample2F} instance.
	 * <p>
	 * If {@code sample} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sample the {@code Sample2F} instance
	 * @return the remapped {@code Sample2F} instance
	 * @throws NullPointerException thrown if, and only if, {@code sample} is {@code null}
	 */
	public Sample2F discreteRemap(final Sample2F sample) {
		final float m = this.isUV ? sample.getU() : sample.getV();
		final float c = this.isUV ? sample.getV() : sample.getU();
		
		final int indexM = this.marginal.index(m);
		final int indexC = this.conditional[indexM].index(c);
		
		final float mRemapped = this.marginal.discreteRemap(m, indexM);
		final float cRemapped = this.conditional[indexM].discreteRemap(c, indexC);
		
		return new Sample2F(this.isUV ? mRemapped : cRemapped, this.isUV ? cRemapped : mRemapped);
	}
	
	/**
	 * Returns the continuous probability density function (PDF) value for {@code sample}.
	 * <p>
	 * If {@code sample} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * distribution2F.continuousProbabilityDensityFunction(sample, false);
	 * }
	 * </pre>
	 * 
	 * @param sample the {@code Sample2F} instance
	 * @return the continuous probability density function (PDF) value for {@code sample}
	 * @throws NullPointerException thrown if, and only if, {@code sample} is {@code null}
	 */
	public float continuousProbabilityDensityFunction(final Sample2F sample) {
		return continuousProbabilityDensityFunction(sample, false);
	}
	
	/**
	 * Returns the continuous probability density function (PDF) value for {@code sample}.
	 * <p>
	 * If {@code sample} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sample the {@code Sample2F} instance
	 * @param isRemapped {@code true} if, and only if, {@code sample} is remapped, {@code false} otherwise
	 * @return the continuous probability density function (PDF) value for {@code sample}
	 * @throws NullPointerException thrown if, and only if, {@code sample} is {@code null}
	 */
	public float continuousProbabilityDensityFunction(final Sample2F sample, final boolean isRemapped) {
		final float m = this.isUV ? sample.getU() : sample.getV();
		final float c = this.isUV ? sample.getV() : sample.getU();
		
		if(isRemapped) {
			final int indexM = saturate(toInt(m * this.marginal.count()), 0, this.marginal.count() - 1);
			final int indexC = saturate(toInt(c * this.conditional[indexM].count()), 0, this.conditional[indexM].count() - 1);
			
			final float probabilityDensityFunctionValue = this.conditional[indexM].function(indexC) / this.marginal.functionIntegral();
			
			return probabilityDensityFunctionValue;
		}
		
		final int indexM = this.marginal.index(m);
		final int indexC = this.conditional[indexM].index(c);
		
		final float probabilityDensityFunctionValueM = this.marginal.continuousProbabilityDensityFunction(indexM);
		final float probabilityDensityFunctionValueC = this.conditional[indexM].continuousProbabilityDensityFunction(indexC);
		final float probabilityDensityFunctionValue = probabilityDensityFunctionValueM * probabilityDensityFunctionValueC;
		
		return probabilityDensityFunctionValue;
	}
	
	/**
	 * Returns the discrete probability density function (PDF) value for {@code sample}.
	 * <p>
	 * If {@code sample} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sample the {@code Sample2F} instance
	 * @return the continuous probability density function (PDF) value for {@code sample}
	 * @throws NullPointerException thrown if, and only if, {@code sample} is {@code null}
	 */
	public float discreteProbabilityDensityFunction(final Sample2F sample) {
		final float m = this.isUV ? sample.getU() : sample.getV();
		final float c = this.isUV ? sample.getV() : sample.getU();
		
		final int indexM = this.marginal.index(m);
		final int indexC = this.conditional[indexM].index(c);
		
		final float probabilityDensityFunctionValueM = this.marginal.discreteProbabilityDensityFunction(indexM);
		final float probabilityDensityFunctionValueC = this.conditional[indexM].discreteProbabilityDensityFunction(indexC);
		final float probabilityDensityFunctionValue = probabilityDensityFunctionValueM * probabilityDensityFunctionValueC;
		
		return probabilityDensityFunctionValue;
	}
	
	/**
	 * Returns a {@code float[]} representation of this {@code Distribution2F} instance.
	 * <p>
	 * The {@code float[]} will contain the following data:
	 * <ol>
	 * <li>The UV flag.</li>
	 * <li>The offset table length.</li>
	 * <li>The offset table.</li>
	 * <li>The marginal.</li>
	 * <li>The conditional.</li>
	 * </ol>
	 * 
	 * @return a {@code float[]} representation of this {@code Distribution2F} instance
	 */
	public float[] toArray() {
		final float[] marginal = this.marginal.toArray();
		final float[] offsetTable = new float[1 + this.conditional.length];
		final float[][] conditional = new float[this.conditional.length][];
		
		for(int i = 0; i < this.conditional.length; i++) {
			conditional[i] = this.conditional[i].toArray();
		}
		
		offsetTable[0] = offsetTable.length + 1 + 1;
		
		for(int i = 0; i < conditional.length; i++) {
			offsetTable[i + 1] = (int)(offsetTable[i]) + (i == 0 ? marginal.length : conditional[i - 1].length);
		}
		
		try(final FloatArrayOutputStream floatArrayOutputStream = new FloatArrayOutputStream()) {
			floatArrayOutputStream.write(this.isUV ? 1.0F : 0.0F);
			floatArrayOutputStream.write(offsetTable.length);
			floatArrayOutputStream.write(offsetTable);
			floatArrayOutputStream.write(marginal);
			
			for(final float[] conditionalEntry : conditional) {
				floatArrayOutputStream.write(conditionalEntry);
			}
			
			return floatArrayOutputStream.toFloatArray();
		}
	}
	
	/**
	 * Returns the conditional count.
	 * 
	 * @return the conditional count
	 */
	public int count() {
		return this.conditional.length;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Sample2F continuousRemap(final float[] array, final int offset, final Sample2F sample) {
		Objects.requireNonNull(array, "array == null");
		
		ParameterArguments.requireRange(offset, 0, array.length - 1, "offset");
		
		final boolean isUV = (int)(array[offset]) != 0;
		
		final float m = isUV ? sample.getU() : sample.getV();
		final float c = isUV ? sample.getV() : sample.getU();
		
		final int offsetM = offset + (int)(array[offset + 2]);
		final int indexM = Distribution1F.index(array, offsetM, m);
		final int offsetC = offset + (int)(array[offset + 3 + indexM]);
		final int indexC = Distribution1F.index(array, offsetC, c);
		
		final float mRemapped = Distribution1F.continuousRemap(array, offsetM, m, indexM);
		final float cRemapped = Distribution1F.continuousRemap(array, offsetC, c, indexC);
		
		return new Sample2F(isUV ? mRemapped : cRemapped, isUV ? cRemapped : mRemapped);
	}
	
//	TODO: Add Javadocs!
	public static Sample2F discreteRemap(final float[] array, final int offset, final Sample2F sample) {
		Objects.requireNonNull(array, "array == null");
		
		ParameterArguments.requireRange(offset, 0, array.length - 1, "offset");
		
		final boolean isUV = (int)(array[offset]) != 0;
		
		final float m = isUV ? sample.getU() : sample.getV();
		final float c = isUV ? sample.getV() : sample.getU();
		
		final int offsetM = offset + (int)(array[offset + 2]);
		final int indexM = Distribution1F.index(array, offsetM, m);
		final int offsetC = offset + (int)(array[offset + 3 + indexM]);
		final int indexC = Distribution1F.index(array, offsetC, c);
		
		final float mRemapped = Distribution1F.discreteRemap(array, offsetM, m, indexM);
		final float cRemapped = Distribution1F.discreteRemap(array, offsetC, c, indexC);
		
		return new Sample2F(isUV ? mRemapped : cRemapped, isUV ? cRemapped : mRemapped);
	}
	
//	TODO: Add Javadocs!
	public static float continuousProbabilityDensityFunction(final float[] array, final int offset, final Sample2F sample) {
		return continuousProbabilityDensityFunction(array, offset, sample, false);
	}
	
//	TODO: Add Javadocs!
	public static float continuousProbabilityDensityFunction(final float[] array, final int offset, final Sample2F sample, final boolean isRemapped) {
		Objects.requireNonNull(array, "array == null");
		
		ParameterArguments.requireRange(offset, 0, array.length - 1, "offset");
		
		final boolean isUV = (int)(array[offset]) != 0;
		
		final float m = isUV ? sample.getU() : sample.getV();
		final float c = isUV ? sample.getV() : sample.getU();
		
		if(isRemapped) {
			final int offsetM = offset + (int)(array[offset + 2]);
			final int countM = Distribution1F.count(array, offsetM);
			final int indexM = saturate((int)(m * countM), 0, countM - 1);
			final int offsetC = offset + (int)(array[offset + 3 + indexM]);
			final int countC = Distribution1F.count(array, offsetC);
			final int indexC = saturate((int)(c * countC), 0, countC - 1);
			
			final float probabilityDensityFunctionValue = Distribution1F.function(array, offsetC, indexC) / Distribution1F.functionIntegral(array, offsetM);
			
			return probabilityDensityFunctionValue;
		}
		
		final int offsetM = offset + (int)(array[offset + 2]);
		final int indexM = Distribution1F.index(array, offsetM, m);
		final int offsetC = offset + (int)(array[offset + 3 + indexM]);
		final int indexC = Distribution1F.index(array, offsetC, c);
		
		final float probabilityDensityFunctionValueM = Distribution1F.continuousProbabilityDensityFunction(array, offsetM, indexM);
		final float probabilityDensityFunctionValueC = Distribution1F.continuousProbabilityDensityFunction(array, offsetC, indexC);
		final float probabilityDensityFunctionValue = probabilityDensityFunctionValueM * probabilityDensityFunctionValueC;
		
		return probabilityDensityFunctionValue;
	}
	
//	TODO: Add Javadocs!
	public static float discreteProbabilityDensityFunction(final float[] array, final int offset, final Sample2F sample) {
		Objects.requireNonNull(array, "array == null");
		
		ParameterArguments.requireRange(offset, 0, array.length - 1, "offset");
		
		final boolean isUV = (int)(array[offset]) != 0;
		
		final float m = isUV ? sample.getU() : sample.getV();
		final float c = isUV ? sample.getV() : sample.getU();
		
		final int offsetM = offset + (int)(array[offset + 2]);
		final int indexM = Distribution1F.index(array, offsetM, m);
		final int offsetC = offset + (int)(array[offset + 3 + indexM]);
		final int indexC = Distribution1F.index(array, offsetC, c);
		
		final float probabilityDensityFunctionValueM = Distribution1F.discreteProbabilityDensityFunction(array, offsetM, indexM);
		final float probabilityDensityFunctionValueC = Distribution1F.discreteProbabilityDensityFunction(array, offsetC, indexC);
		final float probabilityDensityFunctionValue = probabilityDensityFunctionValueM * probabilityDensityFunctionValueC;
		
		return probabilityDensityFunctionValue;
	}
}