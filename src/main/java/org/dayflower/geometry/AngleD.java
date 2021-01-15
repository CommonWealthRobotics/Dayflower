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

import static org.dayflower.util.Doubles.PI_MULTIPLIED_BY_2;
import static org.dayflower.util.Doubles.asin;
import static org.dayflower.util.Doubles.atan;
import static org.dayflower.util.Doubles.atan2;
import static org.dayflower.util.Doubles.equal;
import static org.dayflower.util.Doubles.max;
import static org.dayflower.util.Doubles.min;
import static org.dayflower.util.Doubles.tan;
import static org.dayflower.util.Doubles.toDegrees;
import static org.dayflower.util.Doubles.toRadians;
import static org.dayflower.util.Doubles.wrapAround;

import java.util.Objects;

/**
 * An {@code AngleD} encapsulates angles in forms such as degrees and radians using the data type {@code double}.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class AngleD {
	private static final double DEGREES_MAXIMUM = 360.0D;
	private static final double DEGREES_MAXIMUM_PITCH = 90.0D;
	private static final double DEGREES_MINIMUM = 0.0D;
	private static final double DEGREES_MINIMUM_PITCH = -90.0D;
	private static final double RADIANS_MAXIMUM = PI_MULTIPLIED_BY_2;
	private static final double RADIANS_MINIMUM = 0.0D;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final double degrees;
	private final double degreesMaximum;
	private final double degreesMinimum;
	private final double radians;
	private final double radiansMaximum;
	private final double radiansMinimum;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AngleD(final double degrees, final double degreesMinimum, final double degreesMaximum, final double radians, final double radiansMinimum, final double radiansMaximum) {
		this.degrees = degrees;
		this.degreesMinimum = degreesMinimum;
		this.degreesMaximum = degreesMaximum;
		this.radians = radians;
		this.radiansMinimum = radiansMinimum;
		this.radiansMaximum = radiansMaximum;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code AngleD} instance.
	 * 
	 * @return a {@code String} representation of this {@code AngleD} instance
	 */
	@Override
	public String toString() {
		return String.format("AngleD.degrees(%+.10f, %+.10f, %+.10f)", Double.valueOf(this.degrees), Double.valueOf(this.degreesMinimum), Double.valueOf(this.degreesMaximum));
	}
	
	/**
	 * Compares {@code object} to this {@code AngleD} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code AngleD}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code AngleD} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code AngleD}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof AngleD)) {
			return false;
		} else if(!equal(this.degrees, AngleD.class.cast(object).degrees)) {
			return false;
		} else if(!equal(this.degreesMaximum, AngleD.class.cast(object).degreesMaximum)) {
			return false;
		} else if(!equal(this.degreesMinimum, AngleD.class.cast(object).degreesMinimum)) {
			return false;
		} else if(!equal(this.radians, AngleD.class.cast(object).radians)) {
			return false;
		} else if(!equal(this.radiansMaximum, AngleD.class.cast(object).radiansMaximum)) {
			return false;
		} else if(!equal(this.radiansMinimum, AngleD.class.cast(object).radiansMinimum)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the angle in degrees.
	 * 
	 * @return the angle in degrees
	 */
	public double getDegrees() {
		return this.degrees;
	}
	
	/**
	 * Returns the maximum angle in degrees.
	 * 
	 * @return the maximum angle in degrees
	 */
	public double getDegreesMaximum() {
		return this.degreesMaximum;
	}
	
	/**
	 * Returns the minimum angle in degrees.
	 * 
	 * @return the minimum angle in degrees
	 */
	public double getDegreesMinimum() {
		return this.degreesMinimum;
	}
	
	/**
	 * Returns the angle in radians.
	 * 
	 * @return the angle in radians
	 */
	public double getRadians() {
		return this.radians;
	}
	
	/**
	 * Returns the maximum angle in radians.
	 * 
	 * @return the maximum angle in radians
	 */
	public double getRadiansMaximum() {
		return this.radiansMaximum;
	}
	
	/**
	 * Returns the minimum angle in radians.
	 * 
	 * @return the minimum angle in radians
	 */
	public double getRadiansMinimum() {
		return this.radiansMinimum;
	}
	
	/**
	 * Returns a hash code for this {@code AngleD} instance.
	 * 
	 * @return a hash code for this {@code AngleD} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Double.valueOf(this.degrees), Double.valueOf(this.degreesMaximum), Double.valueOf(this.degreesMinimum), Double.valueOf(this.radians), Double.valueOf(this.radiansMaximum), Double.valueOf(this.radiansMinimum));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds {@code angleRHS} to {@code angleLHS}.
	 * <p>
	 * Returns a new {@code AngleD} instance with the result of the addition.
	 * <p>
	 * If either {@code angleLHS} or {@code angleRHS} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param angleLHS an {@code AngleD} instance
	 * @param angleRHS an {@code AngleD} instance
	 * @return a new {@code AngleD} instance with the result of the addition
	 * @throws NullPointerException thrown if, and only if, either {@code angleLHS} or {@code angleRHS} are {@code null}
	 */
	public static AngleD add(final AngleD angleLHS, final AngleD angleRHS) {
		final double degreesMinimum = min(angleLHS.degreesMinimum, angleRHS.degreesMinimum);
		final double degreesMaximum = max(angleLHS.degreesMaximum, angleRHS.degreesMaximum);
		final double degrees = wrapAround(angleLHS.degrees + angleRHS.degrees, degreesMinimum, degreesMaximum);
		
		return degrees(degrees, degreesMinimum, degreesMaximum);
	}
	
	/**
	 * Returns a new {@code AngleD} instance based on an angle in degrees.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * AngleD.degrees(degrees, 0.0D, 360.0D)
	 * }
	 * </pre>
	 * 
	 * @param degrees the angle in degrees
	 * @return a new {@code AngleD} instance based on an angle in degrees
	 */
	public static AngleD degrees(final double degrees) {
		return degrees(degrees, DEGREES_MINIMUM, DEGREES_MAXIMUM);
	}
	
	/**
	 * Returns a new {@code AngleD} instance based on an angle in degrees and an interval of valid degrees.
	 * 
	 * @param degrees the angle in degrees
	 * @param degreesIntervalEndA the degrees that represents one of the ends of the interval of valid degrees
	 * @param degreesIntervalEndB the degrees that represents one of the ends of the interval of valid degrees
	 * @return a new {@code AngleD} instance based on an angle in degrees and an interval of valid degrees
	 */
	public static AngleD degrees(final double degrees, final double degreesIntervalEndA, final double degreesIntervalEndB) {
		final double newDegreesMinimum = min(degreesIntervalEndA, degreesIntervalEndB);
		final double newDegreesMaximum = max(degreesIntervalEndA, degreesIntervalEndB);
		final double newDegrees = wrapAround(degrees, newDegreesMinimum, newDegreesMaximum);
		
		final double newRadians = toRadians(newDegrees);
		final double newRadiansMinimum = toRadians(newDegreesMinimum);
		final double newRadiansMaximum = toRadians(newDegreesMaximum);
		
		return new AngleD(newDegrees, newDegreesMinimum, newDegreesMaximum, newRadians, newRadiansMinimum, newRadiansMaximum);
	}
	
	/**
	 * Returns a field of view (FOV) {@code AngleD} based on {@code focalDistance} and {@code resolution}.
	 * <p>
	 * This method allows you to use {@code resolution} in either X- or Y-direction. So, either width or height.
	 * 
	 * @param focalDistance the focal distance (also known as focal length}
	 * @param resolution the resolution in X- or Y-direction (width or height)
	 * @return a field of view (FOV) {@code AngleD} based on {@code focalDistance} and {@code resolution}
	 */
	public static AngleD fieldOfView(final double focalDistance, final double resolution) {
		return radians(2.0D * atan(resolution * 0.5D / focalDistance));
	}
	
	/**
	 * Returns a horizontal field of view (FOV) {@code AngleD} based on {@code fieldOfViewY}, {@code resolutionX} and {@code resolutionY}.
	 * <p>
	 * If {@code fieldOfViewY} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param fieldOfViewY the vertical field of view
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 * @return a horizontal field of view (FOV) {@code AngleD} based on {@code fieldOfViewY}, {@code resolutionX} and {@code resolutionY}
	 * @throws NullPointerException thrown if, and only if, {@code fieldOfViewY} is {@code null}
	 */
	public static AngleD fieldOfViewX(final AngleD fieldOfViewY, final double resolutionX, final double resolutionY) {
		return radians(2.0D * atan(tan(fieldOfViewY.radians * 0.5D) * (resolutionX / resolutionY)));
	}
	
	/**
	 * Returns a vertical field of view (FOV) {@code AngleD} based on {@code fieldOfViewX}, {@code resolutionX} and {@code resolutionY}.
	 * <p>
	 * If {@code fieldOfViewX} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param fieldOfViewX the horizontal field of view
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 * @return a vertical field of view (FOV) {@code AngleD} based on {@code fieldOfViewX}, {@code resolutionX} and {@code resolutionY}
	 * @throws NullPointerException thrown if, and only if, {@code fieldOfViewX} is {@code null}
	 */
	public static AngleD fieldOfViewY(final AngleD fieldOfViewX, final double resolutionX, final double resolutionY) {
		return radians(2.0D * atan(tan(fieldOfViewX.radians * 0.5D) * (resolutionY / resolutionX)));
	}
	
	/**
	 * Returns a new {@code AngleD} instance that represents half of {@code angle}.
	 * <p>
	 * If {@code angle} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param angle an {@code AngleD} instance
	 * @return a new {@code AngleD} instance that represents half of {@code angle}
	 * @throws NullPointerException thrown if, and only if, {@code angle} is {@code null}
	 */
	public static AngleD half(final AngleD angle) {
		final double degreesMinimum = angle.degreesMinimum;
		final double degreesMaximum = angle.degreesMaximum;
		final double degrees = wrapAround(angle.degrees * 0.5D, degreesMinimum, degreesMaximum);
		
		return degrees(degrees, degreesMinimum, degreesMaximum);
	}
	
	/**
	 * Returns a new pitch {@code AngleD} instance based on {@code eye} and {@code lookAt}.
	 * <p>
	 * If either {@code eye} or {@code lookAt} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye the {@link Point3D} on which the "eye" is positioned
	 * @param lookAt the {@code Point3D} to which the "eye" is looking
	 * @return a new pitch {@code AngleD} instance based on {@code eye} and {@code lookAt}
	 * @throws NullPointerException thrown if, and only if, either {@code eye} or {@code lookAt} are {@code null}
	 */
	public static AngleD pitch(final Point3D eye, final Point3D lookAt) {
		return pitch(Vector3D.directionNormalized(eye, lookAt));
	}
	
	/**
	 * Returns a new pitch {@code AngleD} instance based on {@code direction}.
	 * <p>
	 * If {@code direction} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param direction a normalized direction {@link Vector3D}
	 * @return a new pitch {@code AngleD} instance based on {@code direction}
	 * @throws NullPointerException thrown if, and only if, {@code direction} is {@code null}
	 */
	public static AngleD pitch(final Vector3D direction) {
		return degrees(toDegrees(asin(direction.getY())), DEGREES_MINIMUM_PITCH, DEGREES_MAXIMUM_PITCH);
	}
	
	/**
	 * Returns a new {@code AngleD} instance based on an angle in radians.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * AngleD.radians(radians, 0.0D, PI * 2.0D)
	 * }
	 * </pre>
	 * 
	 * @param radians the angle in radians
	 * @return a new {@code AngleD} instance based on an angle in radians
	 */
	public static AngleD radians(final double radians) {
		return radians(radians, RADIANS_MINIMUM, RADIANS_MAXIMUM);
	}
	
	/**
	 * Returns a new {@code AngleD} instance based on an angle in radians and an interval of valid radians.
	 * 
	 * @param radians the angle in radians
	 * @param radiansIntervalEndA the radians that represents one of the ends of the interval of valid radians
	 * @param radiansIntervalEndB the radians that represents one of the ends of the interval of valid radians
	 * @return a new {@code AngleD} instance based on an angle in radians and an interval of valid radians
	 */
	public static AngleD radians(final double radians, final double radiansIntervalEndA, final double radiansIntervalEndB) {
		final double newRadiansMinimum = min(radiansIntervalEndA, radiansIntervalEndB);
		final double newRadiansMaximum = max(radiansIntervalEndA, radiansIntervalEndB);
		final double newRadians = wrapAround(radians, newRadiansMinimum, newRadiansMaximum);
		
		final double newDegrees = toDegrees(newRadians);
		final double newDegreesMinimum = toDegrees(newRadiansMinimum);
		final double newDegreesMaximum = toDegrees(newRadiansMaximum);
		
		return new AngleD(newDegrees, newDegreesMinimum, newDegreesMaximum, newRadians, newRadiansMinimum, newRadiansMaximum);
	}
	
	/**
	 * Subtracts {@code angleRHS} from {@code angleLHS}.
	 * <p>
	 * Returns a new {@code AngleD} instance with the result of the subtraction.
	 * <p>
	 * If either {@code angleLHS} or {@code angleRHS} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param angleLHS an {@code AngleD} instance
	 * @param angleRHS an {@code AngleD} instance
	 * @return a new {@code AngleD} instance with the result of the subtraction
	 * @throws NullPointerException thrown if, and only if, either {@code angleLHS} or {@code angleRHS} are {@code null}
	 */
	public static AngleD subtract(final AngleD angleLHS, final AngleD angleRHS) {
		final double degreesMinimum = min(angleLHS.degreesMinimum, angleRHS.degreesMinimum);
		final double degreesMaximum = max(angleLHS.degreesMaximum, angleRHS.degreesMaximum);
		final double degrees = wrapAround(angleLHS.degrees - angleRHS.degrees, degreesMinimum, degreesMaximum);
		
		return degrees(degrees, degreesMinimum, degreesMaximum);
	}
	
	/**
	 * Returns a new yaw {@code AngleD} instance based on {@code eye} and {@code lookAt}.
	 * <p>
	 * If either {@code eye} or {@code lookAt} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye the {@link Point3D} on which the "eye" is positioned
	 * @param lookAt the {@code Point3D} to which the "eye" is looking
	 * @return a new yaw {@code AngleD} instance based on {@code eye} and {@code lookAt}
	 * @throws NullPointerException thrown if, and only if, either {@code eye} or {@code lookAt} are {@code null}
	 */
	public static AngleD yaw(final Point3D eye, final Point3D lookAt) {
		return yaw(Vector3D.directionNormalized(eye, lookAt));
	}
	
	/**
	 * Returns a new yaw {@code AngleD} instance based on {@code direction}.
	 * <p>
	 * If {@code direction} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param direction a normalized direction {@link Vector3D}
	 * @return a new yaw {@code AngleD} instance based on {@code direction}
	 * @throws NullPointerException thrown if, and only if, {@code direction} is {@code null}
	 */
	public static AngleD yaw(final Vector3D direction) {
		return degrees(toDegrees(atan2(direction.getX(), direction.getZ())));
	}
}