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
package org.dayflower.utility;

/**
 * A class that consists exclusively of static methods that returns or performs various operations on {@code byte} values.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Bytes {
	private Bytes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code byte} representation of an {@code int} value.
	 * 
	 * @param value an {@code int} value
	 * @return a {@code byte} representation of an {@code int} value
	 */
	public static byte toByte(final int value) {
		return (byte)(value);
	}
}