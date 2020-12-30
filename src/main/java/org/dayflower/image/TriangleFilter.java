/**
 * Copyright 2020 - 2021 J&#246;rgen Lundgren
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
package org.dayflower.image;

import static org.dayflower.util.Floats.abs;
import static org.dayflower.util.Floats.equal;
import static org.dayflower.util.Floats.max;

import java.util.Objects;

/**
 * A {@code TriangleFilter} is an implementation of {@link Filter} that represents a Triangle filter.
 * <p>
 * This class is immutable and therefore also suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class TriangleFilter extends Filter {
	/**
	 * Constructs a new {@code TriangleFilter} given {@code 2.0F} and {@code 2.0F}.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new TriangleFilter(2.0F, 2.0F);
	 * }
	 * </pre>
	 */
	public TriangleFilter() {
		this(2.0F, 2.0F);
	}
	
	/**
	 * Constructs a new {@code TriangleFilter} given {@code resolutionX} and {@code resolutionY}.
	 * 
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 */
	public TriangleFilter(final float resolutionX, final float resolutionY) {
		super(resolutionX, resolutionY);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code TriangleFilter} instance.
	 * 
	 * @return a {@code String} representation of this {@code TriangleFilter} instance
	 */
	@Override
	public String toString() {
		return String.format("new TriangleFilter(%+.10f, %+.10f)", Float.valueOf(getResolutionX()), Float.valueOf(getResolutionY()));
	}
	
	/**
	 * Compares {@code object} to this {@code TriangleFilter} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code TriangleFilter}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code TriangleFilter} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code TriangleFilter}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof TriangleFilter)) {
			return false;
		} else if(!equal(getResolutionX(), TriangleFilter.class.cast(object).getResolutionX())) {
			return false;
		} else if(!equal(getResolutionXReciprocal(), TriangleFilter.class.cast(object).getResolutionXReciprocal())) {
			return false;
		} else if(!equal(getResolutionY(), TriangleFilter.class.cast(object).getResolutionY())) {
			return false;
		} else if(!equal(getResolutionYReciprocal(), TriangleFilter.class.cast(object).getResolutionYReciprocal())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Evaluates this {@code TriangleFilter} instance given {@code x} and {@code y}.
	 * <p>
	 * Returns the evaluated value.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @return the evaluated value
	 */
	@Override
	public float evaluate(final float x, final float y) {
		return max(0.0F, getResolutionX() - abs(x)) * max(0.0F, getResolutionY() - abs(y));
	}
	
	/**
	 * Returns a hash code for this {@code TriangleFilter} instance.
	 * 
	 * @return a hash code for this {@code TriangleFilter} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(getResolutionX()), Float.valueOf(getResolutionXReciprocal()), Float.valueOf(getResolutionY()), Float.valueOf(getResolutionYReciprocal()));
	}
}