/**
 * Copyright 2020 J&#246;rgen Lundgren
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

import static org.dayflower.util.Floats.PI;
import static org.dayflower.util.Floats.PI_DIVIDED_BY_2;
import static org.dayflower.util.Floats.PI_DIVIDED_BY_4;
import static org.dayflower.util.Floats.PI_MULTIPLIED_BY_2;
import static org.dayflower.util.Floats.cos;
import static org.dayflower.util.Floats.equal;
import static org.dayflower.util.Floats.max;
import static org.dayflower.util.Floats.pow;
import static org.dayflower.util.Floats.random;
import static org.dayflower.util.Floats.sin;
import static org.dayflower.util.Floats.sqrt;

/**
 * The class {@code SampleGeneratorF} contains methods for generating samples of type {@code float}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SampleGeneratorF {
	private SampleGeneratorF() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Samples a point on a concentric disk.
	 * <p>
	 * Returns a {@code Point2F} instance with the sampled point.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleConcentricDisk(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Point2F} instance with the sampled point
	 */
	public static Point2F sampleConcentricDisk() {
		return sampleConcentricDisk(random(), random());
	}
	
	/**
	 * Samples a point on a concentric disk.
	 * <p>
	 * Returns a {@code Point2F} instance with the sampled point.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleConcentricDisk(u, v, 1.0F);
	 * }
	 * </pre>
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Point2F} instance with the sampled point
	 */
	public static Point2F sampleConcentricDisk(final float u, final float v) {
		return sampleConcentricDisk(u, v, 1.0F);
	}
	
	/**
	 * Samples a point on a concentric disk.
	 * <p>
	 * Returns a {@code Point2F} instance with the sampled point.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param radius the radius of the disk
	 * @return a {@code Point2F} instance with the sampled point
	 */
	public static Point2F sampleConcentricDisk(final float u, final float v, final float radius) {
		if(equal(u, 0.0F) && equal(v, 0.0F)) {
			return new Point2F();
		}
		
		final float a = u * 2.0F - 1.0F;
		final float b = v * 2.0F - 1.0F;
		
		if(a * a > b * b) {
			final float phi = PI_DIVIDED_BY_4 * (b / a);
			final float r = radius * a;
			
			final float component1 = cos(phi) * r;
			final float component2 = sin(phi) * r;
			
			return new Point2F(component1, component2);
		}
		
		final float phi = PI_DIVIDED_BY_2 - (PI_DIVIDED_BY_4 * (a / b));
		final float r = radius * b;
		
		final float component1 = cos(phi) * r;
		final float component2 = sin(phi) * r;
		
		return new Point2F(component1, component2);
	}
	
	/**
	 * Samples a point on a disk with a uniform distribution.
	 * <p>
	 * Returns a {@code Point2F} instance with the sampled point.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleDiskUniformDistribution(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Point2F} instance with the sampled point
	 */
	public static Point2F sampleDiskUniformDistribution() {
		return sampleDiskUniformDistribution(random(), random());
	}
	
	/**
	 * Samples a point on a disk with a uniform distribution.
	 * <p>
	 * Returns a {@code Point2F} instance with the sampled point.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Point2F} instance with the sampled point
	 */
	public static Point2F sampleDiskUniformDistribution(final float u, final float v) {
		final float phi = PI_MULTIPLIED_BY_2 * v;
		final float r = sqrt(u);
		
		final float component1 = cos(phi) * r;
		final float component2 = sin(phi) * r;
		
		return new Point2F(component1, component2);
	}
	
	/**
	 * Samples a direction on a cone with a uniform distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param cosThetaMax the maximum cos theta value
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleConeUniformDistribution(final float u, final float v, final float cosThetaMax) {
		final float cosTheta = u * (cosThetaMax - 1.0F) + 1.0F;
		final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
		final float phi = PI_MULTIPLIED_BY_2 * v;
		
		final float component1 = cos(phi) * sinTheta;
		final float component2 = sin(phi) * sinTheta;
		final float component3 = cosTheta;
		
		return new Vector3F(component1, component2, component3);
	}
	
	/**
	 * Samples a direction on a hemisphere with a cosine distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleHemisphereCosineDistribution(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemisphereCosineDistribution() {
		return sampleHemisphereCosineDistribution(random(), random());
	}
	
	/**
	 * Samples a direction on a hemisphere with a cosine distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemisphereCosineDistribution(final float u, final float v) {
		final Point2F point = sampleConcentricDisk(u, v);
		
		final float component1 = point.getComponent1();
		final float component2 = point.getComponent2();
		final float component3 = sqrt(max(0.0F, 1.0F - component1 * component1 - component2 * component2));
		
		return new Vector3F(component1, component2, component3);
	}
	
	/**
	 * Samples a direction on a hemisphere with a power-cosine distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleHemispherePowerCosineDistribution(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemispherePowerCosineDistribution() {
		return sampleHemispherePowerCosineDistribution(random(), random());
	}
	
	/**
	 * Samples a direction on a hemisphere with a power-cosine distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleHemispherePowerCosineDistribution(u, v, 20.0F);
	 * }
	 * </pre>
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemispherePowerCosineDistribution(final float u, final float v) {
		return sampleHemispherePowerCosineDistribution(u, v, 20.0F);
	}
	
	/**
	 * Samples a direction on a hemisphere with a power-cosine distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param exponent the exponent to use
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemispherePowerCosineDistribution(final float u, final float v, final float exponent) {
		final float cosTheta = pow(1.0F - u, 1.0F / (exponent + 1.0F));
		final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
		final float phi = PI_MULTIPLIED_BY_2 * v;
		
		final float component1 = cos(phi) * sinTheta;
		final float component2 = sin(phi) * sinTheta;
		final float component3 = cosTheta;
		
		return new Vector3F(component1, component2, component3);
	}
	
	/**
	 * Samples a direction on a hemisphere with a uniform distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleHemisphereUniformDistribution(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemisphereUniformDistribution() {
		return sampleHemisphereUniformDistribution(random(), random());
	}
	
	/**
	 * Samples a direction on a hemisphere with a uniform distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleHemisphereUniformDistribution(final float u, final float v) {
		final float cosTheta = u;
		final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
		final float phi = PI_MULTIPLIED_BY_2 * v;
		
		final float component1 = cos(phi) * sinTheta;
		final float component2 = sin(phi) * sinTheta;
		final float component3 = cosTheta;
		
		return new Vector3F(component1, component2, component3);
	}
	
	/**
	 * Samples a direction on a sphere with a uniform distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * SampleGeneratorF.sampleSphereUniformDistribution(Floats.random(), Floats.random());
	 * }
	 * </pre>
	 * 
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleSphereUniformDistribution() {
		return sampleSphereUniformDistribution(random(), random());
	}
	
	/**
	 * Samples a direction on a sphere with a uniform distribution.
	 * <p>
	 * Returns a {@code Vector3F} instance with the sampled direction.
	 * 
	 * @param u a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @param v a random {@code float} with a uniform distribution between {@code 0.0F} and {@code 1.0F}
	 * @return a {@code Vector3F} instance with the sampled direction
	 */
	public static Vector3F sampleSphereUniformDistribution(final float u, final float v) {
		final float cosTheta = 1.0F - 2.0F * u;
		final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
		final float phi = PI_MULTIPLIED_BY_2 * v;
		
		final float component1 = cos(phi) * sinTheta;
		final float component2 = sin(phi) * sinTheta;
		final float component3 = cosTheta;
		
		return new Vector3F(component1, component2, component3);
	}
	
	/**
	 * Returns the probability density function (PDF) value for {@code cosThetaMax}.
	 * <p>
	 * This method is used together with {@link #sampleConeUniformDistribution(float, float, float)}.
	 * 
	 * @param cosThetaMax the maximum cos theta value
	 * @return the probability density function (PDF) value for {@code cosThetaMax}
	 */
	public static float coneUniformDistributionProbabilityDensityFunction(final float cosThetaMax) {
		return cosThetaMax >= 1.0F ? 0.0F : 1.0F / (2.0F * PI * (1.0F - cosThetaMax));
	}
	
	/**
	 * Computes the balance heuristic for multiple importance sampling (MIS).
	 * <p>
	 * Returns the result of the computation.
	 * 
	 * @param probabilityDensityFunctionValueA a probability density function (PDF) value
	 * @param probabilityDensityFunctionValueB a probability density function (PDF) value
	 * @param sampleCountA a sample count
	 * @param sampleCountB a sample count
	 * @return the result of the computation
	 */
	public static float multipleImportanceSamplingBalanceHeuristic(final float probabilityDensityFunctionValueA, final float probabilityDensityFunctionValueB, final int sampleCountA, final int sampleCountB) {
		return sampleCountA * probabilityDensityFunctionValueA / (sampleCountA * probabilityDensityFunctionValueA + sampleCountB * probabilityDensityFunctionValueB);
	}
	
	/**
	 * Computes the power heuristic for multiple importance sampling (MIS).
	 * <p>
	 * Returns the result of the computation.
	 * 
	 * @param probabilityDensityFunctionValueA a probability density function (PDF) value
	 * @param probabilityDensityFunctionValueB a probability density function (PDF) value
	 * @param sampleCountA a sample count
	 * @param sampleCountB a sample count
	 * @return the result of the computation
	 */
	public static float multipleImportanceSamplingPowerHeuristic(final float probabilityDensityFunctionValueA, final float probabilityDensityFunctionValueB, final int sampleCountA, final int sampleCountB) {
		final float weightA = sampleCountA * probabilityDensityFunctionValueA;
		final float weightB = sampleCountB * probabilityDensityFunctionValueB;
		
		return weightA * weightA / (weightA * weightA + weightB * weightB);
	}
}