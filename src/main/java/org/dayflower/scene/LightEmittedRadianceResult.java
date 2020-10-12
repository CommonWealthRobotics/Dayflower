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
package org.dayflower.scene;

import static org.dayflower.util.Floats.equal;

import java.lang.reflect.Field;
import java.util.Objects;

import org.dayflower.geometry.Ray3F;
import org.dayflower.geometry.Vector3F;
import org.dayflower.image.Color3F;

//TODO: Add Javadocs!
public final class LightEmittedRadianceResult {
	private final Color3F result;
	private final Ray3F ray;
	private final Vector3F normal;
	private final float probabilityDensityFunctionValueDirection;
	private final float probabilityDensityFunctionValuePosition;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public LightEmittedRadianceResult(final Color3F result, final Ray3F ray, final Vector3F normal, final float probabilityDensityFunctionValueDirection, final float probabilityDensityFunctionValuePosition) {
		this.result = Objects.requireNonNull(result, "result == null");
		this.ray = Objects.requireNonNull(ray, "ray == null");
		this.normal = Objects.requireNonNull(normal, "normal == null");
		this.probabilityDensityFunctionValueDirection = probabilityDensityFunctionValueDirection;
		this.probabilityDensityFunctionValuePosition = probabilityDensityFunctionValuePosition;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Color3F getResult() {
		return this.result;
	}
	
//	TODO: Add Javadocs!
	public Ray3F getRay() {
		return this.ray;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code LightEmittedRadianceResult} instance.
	 * 
	 * @return a {@code String} representation of this {@code LightEmittedRadianceResult} instance
	 */
	@Override
	public String toString() {
		return String.format("new LightEmittedRadianceResult(%s, %s, %s, %+.10f, %+.10f)", this.result, this.ray, this.normal, Float.valueOf(this.probabilityDensityFunctionValueDirection), Float.valueOf(this.probabilityDensityFunctionValuePosition));
	}
	
//	TODO: Add Javadocs!
	public Vector3F getNormal() {
		return this.normal;
	}
	
	/**
	 * Compares {@code object} to this {@code LightEmittedRadianceResult} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code LightEmittedRadianceResult}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code LightEmittedRadianceResult} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code LightEmittedRadianceResult}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof LightEmittedRadianceResult)) {
			return false;
		} else if(!Objects.equals(this.result, LightEmittedRadianceResult.class.cast(object).result)) {
			return false;
		} else if(!Objects.equals(this.ray, LightEmittedRadianceResult.class.cast(object).ray)) {
			return false;
		} else if(!Objects.equals(this.normal, LightEmittedRadianceResult.class.cast(object).normal)) {
			return false;
		} else if(!equal(this.probabilityDensityFunctionValueDirection, LightEmittedRadianceResult.class.cast(object).probabilityDensityFunctionValueDirection)) {
			return false;
		} else if(!equal(this.probabilityDensityFunctionValuePosition, LightEmittedRadianceResult.class.cast(object).probabilityDensityFunctionValuePosition)) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs!
	public float getProbabilityDensityFunctionValueDirection() {
		return this.probabilityDensityFunctionValueDirection;
	}
	
//	TODO: Add Javadocs!
	public float getProbabilityDensityFunctionValuePosition() {
		return this.probabilityDensityFunctionValuePosition;
	}
	
	/**
	 * Returns a hash code for this {@code LightEmittedRadianceResult} instance.
	 * 
	 * @return a hash code for this {@code LightEmittedRadianceResult} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.result, this.ray, this.normal, Float.valueOf(this.probabilityDensityFunctionValueDirection), Float.valueOf(this.probabilityDensityFunctionValuePosition));
	}
}