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
package org.dayflower.image;

import static org.dayflower.util.Floats.abs;
import static org.dayflower.util.Floats.max;

import java.lang.reflect.Field;

//TODO: Add Javadocs!
public final class TriangleFilter extends Filter {
//	TODO: Add Javadocs!
	public TriangleFilter() {
		this(2.0F, 2.0F);
	}
	
//	TODO: Add Javadocs!
	public TriangleFilter(final float resolutionX, final float resolutionY) {
		super(resolutionX, resolutionY);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	@Override
	public float evaluate(final float x, final float y) {
		return max(0.0F, getResolutionX() - abs(x)) * max(0.0F, getResolutionY() - abs(y));
	}
}