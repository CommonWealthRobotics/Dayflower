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
package org.dayflower.simplex;

import static org.dayflower.simplex.Point.point3D;
import static org.dayflower.simplex.Point.point3DAdd;
import static org.dayflower.simplex.Point.point3DSet;
import static org.dayflower.simplex.Ray.ray3DGetDirection;
import static org.dayflower.simplex.Ray.ray3DGetOrigin;
import static org.dayflower.simplex.Ray.ray3DGetTMaximum;
import static org.dayflower.simplex.Ray.ray3DGetTMinimum;
import static org.dayflower.simplex.Vector.vector3D;
import static org.dayflower.simplex.Vector.vector3DCrossProduct;
import static org.dayflower.simplex.Vector.vector3DDirection;
import static org.dayflower.simplex.Vector.vector3DDirectionNormalized;
import static org.dayflower.simplex.Vector.vector3DDotProduct;
import static org.dayflower.simplex.Vector.vector3DLengthSquared;
import static org.dayflower.utility.Doubles.NaN;
import static org.dayflower.utility.Doubles.PI_MULTIPLIED_BY_2;
import static org.dayflower.utility.Doubles.atan2;
import static org.dayflower.utility.Doubles.getOrAdd;
import static org.dayflower.utility.Doubles.isNaN;
import static org.dayflower.utility.Doubles.isZero;
import static org.dayflower.utility.Doubles.solveQuadraticSystem;
import static org.dayflower.utility.Doubles.sqrt;
import static org.dayflower.utility.Doubles.toRadians;

import java.lang.reflect.Field;//TODO: Add Javadocs!

//TODO: Add Javadocs!
public final class Shape {
//	TODO: Add Javadocs!
	public static final int CONE_OFFSET_PHI_MAX = 0;
	
//	TODO: Add Javadocs!
	public static final int CONE_OFFSET_RADIUS = 1;
	
//	TODO: Add Javadocs!
	public static final int CONE_OFFSET_Z_MAX = 2;
	
//	TODO: Add Javadocs!
	public static final int CYLINDER_OFFSET_PHI_MAX = 0;
	
//	TODO: Add Javadocs!
	public static final int CYLINDER_OFFSET_RADIUS = 1;
	
//	TODO: Add Javadocs!
	public static final int CYLINDER_OFFSET_Z_MAX = 2;
	
//	TODO: Add Javadocs!
	public static final int CYLINDER_OFFSET_Z_MIN = 3;
	
//	TODO: Add Javadocs!
	public static final int DISK_OFFSET_PHI_MAX = 0;
	
//	TODO: Add Javadocs!
	public static final int DISK_OFFSET_RADIUS_INNER = 1;
	
//	TODO: Add Javadocs!
	public static final int DISK_OFFSET_RADIUS_OUTER = 2;
	
//	TODO: Add Javadocs!
	public static final int DISK_OFFSET_Z_MAX = 3;
	
//	TODO: Add Javadocs!
	public static final int PARABOLOID_OFFSET_PHI_MAX = 0;
	
//	TODO: Add Javadocs!
	public static final int PARABOLOID_OFFSET_RADIUS = 1;
	
//	TODO: Add Javadocs!
	public static final int PARABOLOID_OFFSET_Z_MAX = 2;
	
//	TODO: Add Javadocs!
	public static final int PARABOLOID_OFFSET_Z_MIN = 3;
	
//	TODO: Add Javadocs!
	public static final int PLANE_OFFSET_A = 0;
	
//	TODO: Add Javadocs!
	public static final int PLANE_OFFSET_B = 3;
	
//	TODO: Add Javadocs!
	public static final int PLANE_OFFSET_C = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Shape() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Cone3D //////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static double cone3DGetPhiMax(final double[] cone3D) {
		return cone3DGetPhiMax(cone3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cone3DGetPhiMax(final double[] cone3D, final int cone3DOffset) {
		return cone3D[cone3DOffset + CONE_OFFSET_PHI_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double cone3DGetRadius(final double[] cone3D) {
		return cone3DGetRadius(cone3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cone3DGetRadius(final double[] cone3D, final int cone3DOffset) {
		return cone3D[cone3DOffset + CONE_OFFSET_RADIUS];
	}
	
//	TODO: Add Javadocs!
	public static double cone3DGetZMax(final double[] cone3D) {
		return cone3DGetZMax(cone3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cone3DGetZMax(final double[] cone3D, final int cone3DOffset) {
		return cone3D[cone3DOffset + CONE_OFFSET_Z_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double cone3DIntersection(final double[] ray3D, final double[] cone3D) {
		return cone3DIntersection(ray3D, cone3D, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cone3DIntersection(final double[] ray3D, final double[] cone3D, final int ray3DOffset, final int cone3DOffset) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D, point3D(), ray3DOffset, 0);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D, vector3D(), ray3DOffset, 0);
		
		final double tMinimum = ray3DGetTMinimum(ray3D, ray3DOffset);
		final double tMaximum = ray3DGetTMaximum(ray3D, ray3DOffset);
		
		final double phiMax = cone3DGetPhiMax(cone3D, cone3DOffset);
		final double radius = cone3DGetRadius(cone3D, cone3DOffset);
		final double zMax = cone3DGetZMax(cone3D, cone3DOffset);
		
		final double k = (radius / zMax) * (radius / zMax);
		
		final double a = vector3DDirection[0] * vector3DDirection[0] + vector3DDirection[1] * vector3DDirection[1] - k * vector3DDirection[2] * vector3DDirection[2];
		final double b = 2.0D * (vector3DDirection[0] * point3DOrigin[0] + vector3DDirection[1] * point3DOrigin[1] - k * vector3DDirection[2] * (point3DOrigin[2] - zMax));
		final double c = point3DOrigin[0] * point3DOrigin[0] + point3DOrigin[1] * point3DOrigin[1] - k * (point3DOrigin[2] - zMax) * (point3DOrigin[2] - zMax);
		
		final double[] ts = solveQuadraticSystem(a, b, c);
		
		for(int i = 0; i < ts.length; i++) {
			final double t = ts[i];
			
			if(isNaN(t)) {
				return NaN;
			}
			
			if(t > tMinimum && t < tMaximum) {
				final double[] point3D = point3DAdd(point3DOrigin, vector3DDirection, t);
				
				final double phi = getOrAdd(atan2(point3D[1], point3D[0]), 0.0D, PI_MULTIPLIED_BY_2);
				
				if(point3D[2] >= 0.0D && point3D[2] <= zMax && phi <= phiMax) {
					return t;
				}
			}
		}
		
		return NaN;
	}
	
//	TODO: Add Javadocs!
	public static double[] cone3D() {
		return cone3D(toRadians(360.0D), 1.0D, 1.0D);
	}
	
//	TODO: Add Javadocs!
	public static double[] cone3D(final double phiMax, final double radius, final double zMax) {
		return new double[] {phiMax, radius, zMax};
	}
	
//	TODO: Add Javadocs!
	public static double[] cone3DSet(final double[] cone3DResult, final double phiMax, final double radius, final double zMax) {
		return cone3DSet(cone3DResult, phiMax, radius, zMax, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] cone3DSet(final double[] cone3DResult, final double phiMax, final double radius, final double zMax, final int cone3DResultOffset) {
		cone3DResult[cone3DResultOffset + CONE_OFFSET_PHI_MAX] = phiMax;
		cone3DResult[cone3DResultOffset + CONE_OFFSET_RADIUS] = radius;
		cone3DResult[cone3DResultOffset + CONE_OFFSET_Z_MAX] = zMax;
		
		return cone3DResult;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Cylinder3D //////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetPhiMax(final double[] cylinder3D) {
		return cylinder3DGetPhiMax(cylinder3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetPhiMax(final double[] cylinder3D, final int cylinder3DOffset) {
		return cylinder3D[cylinder3DOffset + CYLINDER_OFFSET_PHI_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetRadius(final double[] cylinder3D) {
		return cylinder3DGetRadius(cylinder3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetRadius(final double[] cylinder3D, final int cylinder3DOffset) {
		return cylinder3D[cylinder3DOffset + CYLINDER_OFFSET_RADIUS];
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetZMax(final double[] cylinder3D) {
		return cylinder3DGetZMax(cylinder3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetZMax(final double[] cylinder3D, final int cylinder3DOffset) {
		return cylinder3D[cylinder3DOffset + CYLINDER_OFFSET_Z_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetZMin(final double[] cylinder3D) {
		return cylinder3DGetZMin(cylinder3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DGetZMin(final double[] cylinder3D, final int cylinder3DOffset) {
		return cylinder3D[cylinder3DOffset + CYLINDER_OFFSET_Z_MIN];
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DIntersection(final double[] ray3D, final double[] cylinder3D) {
		return cylinder3DIntersection(ray3D, cylinder3D, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double cylinder3DIntersection(final double[] ray3D, final double[] cylinder3D, final int ray3DOffset, final int cylinder3DOffset) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D, point3D(), ray3DOffset, 0);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D, vector3D(), ray3DOffset, 0);
		
		final double tMinimum = ray3DGetTMinimum(ray3D, ray3DOffset);
		final double tMaximum = ray3DGetTMaximum(ray3D, ray3DOffset);
		
		final double phiMax = cylinder3DGetPhiMax(cylinder3D, cylinder3DOffset);
		final double radius = cylinder3DGetRadius(cylinder3D, cylinder3DOffset);
		final double zMax = cylinder3DGetZMax(cylinder3D, cylinder3DOffset);
		final double zMin = cylinder3DGetZMin(cylinder3D, cylinder3DOffset);
		
		final double a = vector3DDirection[0] * vector3DDirection[0] + vector3DDirection[1] * vector3DDirection[1];
		final double b = 2.0D * (vector3DDirection[0] * point3DOrigin[0] + vector3DDirection[1] * point3DOrigin[1]);
		final double c = point3DOrigin[0] * point3DOrigin[0] + point3DOrigin[1] * point3DOrigin[1] - radius * radius;
		
		final double[] ts = solveQuadraticSystem(a, b, c);
		
		for(int i = 0; i < ts.length; i++) {
			final double t = ts[i];
			
			if(isNaN(t)) {
				return NaN;
			}
			
			if(t > tMinimum && t < tMaximum) {
				final double[] point3D = point3DAdd(point3DOrigin, vector3DDirection, t);
				
				final double r = sqrt(point3D[0] * point3D[0] + point3D[1] * point3D[1]);
				final double phi = getOrAdd(atan2(point3D[1] * (radius / r), point3D[0] * (radius / r)), 0.0D, PI_MULTIPLIED_BY_2);
				
				if(point3D[2] >= zMin && point3D[2] <= zMax && phi <= phiMax) {
					return t;
				}
			}
		}
		
		return NaN;
	}
	
//	TODO: Add Javadocs!
	public static double[] cylinder3D() {
		return cylinder3D(toRadians(360.0D), 1.0D, 1.0D, -1.0D);
	}
	
//	TODO: Add Javadocs!
	public static double[] cylinder3D(final double phiMax, final double radius, final double zMax, final double zMin) {
		return new double[] {phiMax, radius, zMax, zMin};
	}
	
//	TODO: Add Javadocs!
	public static double[] cylinder3DSet(final double[] cylinder3DResult, final double phiMax, final double radius, final double zMax, final double zMin) {
		return cylinder3DSet(cylinder3DResult, phiMax, radius, zMax, zMin, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] cylinder3DSet(final double[] cylinder3DResult, final double phiMax, final double radius, final double zMax, final double zMin, final int cylinder3DResultOffset) {
		cylinder3DResult[cylinder3DResultOffset + CYLINDER_OFFSET_PHI_MAX] = phiMax;
		cylinder3DResult[cylinder3DResultOffset + CYLINDER_OFFSET_RADIUS] = radius;
		cylinder3DResult[cylinder3DResultOffset + CYLINDER_OFFSET_Z_MAX] = zMax;
		cylinder3DResult[cylinder3DResultOffset + CYLINDER_OFFSET_Z_MIN] = zMin;
		
		return cylinder3DResult;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Disk3D //////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static double disk3DGetPhiMax(final double[] disk3D) {
		return disk3DGetPhiMax(disk3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetPhiMax(final double[] disk3D, final int disk3DOffset) {
		return disk3D[disk3DOffset + DISK_OFFSET_PHI_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetRadiusInner(final double[] disk3D) {
		return disk3DGetRadiusInner(disk3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetRadiusInner(final double[] disk3D, final int disk3DOffset) {
		return disk3D[disk3DOffset + DISK_OFFSET_RADIUS_INNER];
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetRadiusOuter(final double[] disk3D) {
		return disk3DGetRadiusOuter(disk3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetRadiusOuter(final double[] disk3D, final int disk3DOffset) {
		return disk3D[disk3DOffset + DISK_OFFSET_RADIUS_OUTER];
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetZMax(final double[] disk3D) {
		return disk3DGetZMax(disk3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double disk3DGetZMax(final double[] disk3D, final int disk3DOffset) {
		return disk3D[disk3DOffset + DISK_OFFSET_Z_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double disk3DIntersection(final double[] ray3D, final double[] disk3D) {
		return disk3DIntersection(ray3D, disk3D, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double disk3DIntersection(final double[] ray3D, final double[] disk3D, final int ray3DOffset, final int disk3DOffset) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D, point3D(), ray3DOffset, 0);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D, vector3D(), ray3DOffset, 0);
		
		final double tMinimum = ray3DGetTMinimum(ray3D, ray3DOffset);
		final double tMaximum = ray3DGetTMaximum(ray3D, ray3DOffset);
		
		if(isZero(vector3DDirection[2])) {
			return NaN;
		}
		
		final double phiMax = disk3DGetPhiMax(disk3D, disk3DOffset);
		final double radiusInner = disk3DGetRadiusInner(disk3D, disk3DOffset);
		final double radiusOuter = disk3DGetRadiusOuter(disk3D, disk3DOffset);
		final double zMax = disk3DGetZMax(disk3D, disk3DOffset);
		
		final double t = (zMax - point3DOrigin[2]) / vector3DDirection[2];
		
		if(t <= tMinimum || t >= tMaximum) {
			return NaN;
		}
		
		final double[] point3D = point3DAdd(point3DOrigin, vector3DDirection, t);
		
		final double distanceSquared = point3D[0] * point3D[0] + point3D[1] * point3D[1];
		
		if(distanceSquared > radiusOuter * radiusOuter || distanceSquared < radiusInner * radiusInner) {
			return NaN;
		}
		
		final double phi = getOrAdd(atan2(point3D[1], point3D[0]), 0.0D, PI_MULTIPLIED_BY_2);
		
		if(phi > phiMax) {
			return NaN;
		}
		
		return t;
	}
	
//	TODO: Add Javadocs!
	public static double[] disk3D() {
		return disk3D(toRadians(360.0D), 0.0D, 1.0D, 0.0D);
	}
	
//	TODO: Add Javadocs!
	public static double[] disk3D(final double phiMax, final double radiusInner, final double radiusOuter, final double zMax) {
		return new double[] {phiMax, radiusInner, radiusOuter, zMax};
	}
	
//	TODO: Add Javadocs!
	public static double[] disk3DSet(final double[] disk3DResult, final double phiMax, final double radiusInner, final double radiusOuter, final double zMax) {
		return disk3DSet(disk3DResult, phiMax, radiusInner, radiusOuter, zMax, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] disk3DSet(final double[] disk3DResult, final double phiMax, final double radiusInner, final double radiusOuter, final double zMax, final int disk3DResultOffset) {
		disk3DResult[disk3DResultOffset + DISK_OFFSET_PHI_MAX] = phiMax;
		disk3DResult[disk3DResultOffset + DISK_OFFSET_RADIUS_INNER] = radiusInner;
		disk3DResult[disk3DResultOffset + DISK_OFFSET_RADIUS_OUTER] = radiusOuter;
		disk3DResult[disk3DResultOffset + DISK_OFFSET_Z_MAX] = zMax;
		
		return disk3DResult;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Paraboloid3D ////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetPhiMax(final double[] paraboloid3D) {
		return paraboloid3DGetPhiMax(paraboloid3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetPhiMax(final double[] paraboloid3D, final int paraboloid3DOffset) {
		return paraboloid3D[paraboloid3DOffset + PARABOLOID_OFFSET_PHI_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetRadius(final double[] paraboloid3D) {
		return paraboloid3DGetRadius(paraboloid3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetRadius(final double[] paraboloid3D, final int paraboloid3DOffset) {
		return paraboloid3D[paraboloid3DOffset + PARABOLOID_OFFSET_RADIUS];
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetZMax(final double[] paraboloid3D) {
		return paraboloid3DGetZMax(paraboloid3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetZMax(final double[] paraboloid3D, final int paraboloid3DOffset) {
		return paraboloid3D[paraboloid3DOffset + PARABOLOID_OFFSET_Z_MAX];
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetZMin(final double[] paraboloid3D) {
		return paraboloid3DGetZMin(paraboloid3D, 0);
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DGetZMin(final double[] paraboloid3D, final int paraboloid3DOffset) {
		return paraboloid3D[paraboloid3DOffset + PARABOLOID_OFFSET_Z_MIN];
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DIntersection(final double[] ray3D, final double[] paraboloid3D) {
		return paraboloid3DIntersection(ray3D, paraboloid3D, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double paraboloid3DIntersection(final double[] ray3D, final double[] paraboloid3D, final int ray3DOffset, final int paraboloid3DOffset) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D, point3D(), ray3DOffset, 0);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D, vector3D(), ray3DOffset, 0);
		
		final double tMinimum = ray3DGetTMinimum(ray3D, ray3DOffset);
		final double tMaximum = ray3DGetTMaximum(ray3D, ray3DOffset);
		
		final double phiMax = paraboloid3DGetPhiMax(paraboloid3D, paraboloid3DOffset);
		final double radius = paraboloid3DGetRadius(paraboloid3D, paraboloid3DOffset);
		final double zMax = paraboloid3DGetZMax(paraboloid3D, paraboloid3DOffset);
		final double zMin = paraboloid3DGetZMin(paraboloid3D, paraboloid3DOffset);
		
		final double k = zMax / (radius * radius);
		
		final double a = k * (vector3DDirection[0] * vector3DDirection[0] + vector3DDirection[1] * vector3DDirection[1]);
		final double b = 2.0D * k * (vector3DDirection[0] * point3DOrigin[0] + vector3DDirection[1] * point3DOrigin[1]) - vector3DDirection[2];
		final double c = k * (point3DOrigin[0] * point3DOrigin[0] + point3DOrigin[1] * point3DOrigin[1]) - point3DOrigin[2];
		
		final double[] ts = solveQuadraticSystem(a, b, c);
		
		for(int i = 0; i < ts.length; i++) {
			final double t = ts[i];
			
			if(isNaN(t)) {
				return NaN;
			}
			
			if(t > tMinimum && t < tMaximum) {
				final double[] point3D = point3DAdd(point3DOrigin, vector3DDirection, t);
				
				final double phi = getOrAdd(atan2(point3D[1], point3D[0]), 0.0D, PI_MULTIPLIED_BY_2);
				
				if(point3D[2] >= zMin && point3D[2] <= zMax && phi <= phiMax) {
					return t;
				}
			}
		}
		
		return NaN;
	}
	
//	TODO: Add Javadocs!
	public static double[] paraboloid3D() {
		return paraboloid3D(toRadians(360.0D), 1.0D, 1.0D, 0.0D);
	}
	
//	TODO: Add Javadocs!
	public static double[] paraboloid3D(final double phiMax, final double radius, final double zMax, final double zMin) {
		return new double[] {phiMax, radius, zMax, zMin};
	}
	
//	TODO: Add Javadocs!
	public static double[] paraboloid3DSet(final double[] paraboloid3DResult, final double phiMax, final double radius, final double zMax, final double zMin) {
		return paraboloid3DSet(paraboloid3DResult, phiMax, radius, zMax, zMin, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] paraboloid3DSet(final double[] paraboloid3DResult, final double phiMax, final double radius, final double zMax, final double zMin, final int paraboloid3DResultOffset) {
		paraboloid3DResult[paraboloid3DResultOffset + PARABOLOID_OFFSET_PHI_MAX] = phiMax;
		paraboloid3DResult[paraboloid3DResultOffset + PARABOLOID_OFFSET_RADIUS] = radius;
		paraboloid3DResult[paraboloid3DResultOffset + PARABOLOID_OFFSET_Z_MAX] = zMax;
		paraboloid3DResult[paraboloid3DResultOffset + PARABOLOID_OFFSET_Z_MIN] = zMin;
		
		return paraboloid3DResult;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Plane3D /////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static double plane3DIntersection(final double[] ray3D, final double[] plane3D) {
		return plane3DIntersection(ray3D, plane3D, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double plane3DIntersection(final double[] ray3D, final double[] plane3D, final int ray3DOffset, final int plane3DOffset) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D, point3D(), ray3DOffset, 0);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D, vector3D(), ray3DOffset, 0);
		
		final double tMinimum = ray3DGetTMinimum(ray3D, ray3DOffset);
		final double tMaximum = ray3DGetTMaximum(ray3D, ray3DOffset);
		
		final double[] point3DA = plane3DGetA(plane3D, point3D(), plane3DOffset, 0);
		final double[] point3DB = plane3DGetB(plane3D, point3D(), plane3DOffset, 0);
		final double[] point3DC = plane3DGetC(plane3D, point3D(), plane3DOffset, 0);
		
		final double[] vector3DAB = vector3DDirectionNormalized(point3DA, point3DB);
		final double[] vector3DAC = vector3DDirectionNormalized(point3DA, point3DC);
		
		final double[] vector3DSurfaceNormal = vector3DCrossProduct(vector3DAB, vector3DAC);
		
		final double surfaceNormalDotDirection = vector3DDotProduct(vector3DSurfaceNormal, vector3DDirection);
		
		if(isZero(surfaceNormalDotDirection)) {
			return NaN;
		}
		
		final double[] vector3DOriginToA = vector3DDirection(point3DOrigin, point3DA);
		
		final double t = vector3DDotProduct(vector3DOriginToA, vector3DSurfaceNormal) / surfaceNormalDotDirection;
		
		if(t > tMinimum && t < tMaximum) {
			return t;
		}
		
		return NaN;
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3D() {
		return plane3D(point3D(0.0D, 0.0D, 0.0D), point3D(0.0D, 0.0D, 1.0D), point3D(1.0D, 0.0D, 0.0D));
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3D(final double[] point3DA, final double[] point3DB, final double[] point3DC) {
		return plane3D(point3DA, point3DB, point3DC, 0, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3D(final double[] point3DA, final double[] point3DB, final double[] point3DC, final int point3DAOffset, final int point3DBOffset, final int point3DCOffset) {
		final double aX = point3DA[point3DAOffset + 0];
		final double aY = point3DA[point3DAOffset + 1];
		final double aZ = point3DA[point3DAOffset + 2];
		
		final double bX = point3DB[point3DBOffset + 0];
		final double bY = point3DB[point3DBOffset + 1];
		final double bZ = point3DB[point3DBOffset + 2];
		
		final double cX = point3DC[point3DCOffset + 0];
		final double cY = point3DC[point3DCOffset + 1];
		final double cZ = point3DC[point3DCOffset + 2];
		
		return new double[] {aX, aY, aZ, bX, bY, bZ, cX, cY, cZ};
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DComputeSurfaceNormal(final double[] plane3D) {
		return plane3DComputeSurfaceNormal(plane3D, vector3D());
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DComputeSurfaceNormal(final double[] plane3D, final double[] vector3DSurfaceNormalResult) {
		return plane3DComputeSurfaceNormal(plane3D, vector3DSurfaceNormalResult, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DComputeSurfaceNormal(final double[] plane3D, final double[] vector3DSurfaceNormalResult, final int plane3DOffset, final int vector3DSurfaceNormalResultOffset) {
		final double[] point3DA = plane3DGetA(plane3D, point3D(), plane3DOffset, 0);
		final double[] point3DB = plane3DGetB(plane3D, point3D(), plane3DOffset, 0);
		final double[] point3DC = plane3DGetC(plane3D, point3D(), plane3DOffset, 0);
		
		final double[] vector3DAB = vector3DDirectionNormalized(point3DA, point3DB);
		final double[] vector3DAC = vector3DDirectionNormalized(point3DA, point3DC);
		
		final double[] vector3DSurfaceNormal = vector3DCrossProduct(vector3DAB, vector3DAC, vector3DSurfaceNormalResult, 0, 0, vector3DSurfaceNormalResultOffset);
		
		return vector3DSurfaceNormal;
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetA(final double[] plane3D) {
		return plane3DGetA(plane3D, point3D());
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetA(final double[] plane3D, final double[] point3DAResult) {
		return plane3DGetA(plane3D, point3DAResult, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetA(final double[] plane3D, final double[] point3DAResult, final int plane3DOffset, final int point3DAResultOffset) {
		return point3DSet(point3DAResult, plane3D, point3DAResultOffset, plane3DOffset + PLANE_OFFSET_A);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetB(final double[] plane3D) {
		return plane3DGetB(plane3D, point3D());
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetB(final double[] plane3D, final double[] point3DBResult) {
		return plane3DGetB(plane3D, point3DBResult);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetB(final double[] plane3D, final double[] point3DBResult, final int plane3DOffset, final int point3DBResultOffset) {
		return point3DSet(point3DBResult, plane3D, point3DBResultOffset, plane3DOffset + PLANE_OFFSET_B);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetC(final double[] plane3D) {
		return plane3DGetC(plane3D, point3D());
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetC(final double[] plane3D, final double[] point3DCResult) {
		return plane3DGetC(plane3D, point3DCResult, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DGetC(final double[] plane3D, final double[] point3DCResult, final int plane3DOffset, final int point3DCResultOffset) {
		return point3DSet(point3DCResult, plane3D, point3DCResultOffset, plane3DOffset + PLANE_OFFSET_C);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DSet(final double[] plane3DResult, final double[] point3DA, final double[] point3DB, final double[] point3DC) {
		return plane3DSet(plane3DResult, point3DA, point3DB, point3DC, 0, 0, 0, 0);
	}
	
//	TODO: Add Javadocs!
	public static double[] plane3DSet(final double[] plane3DResult, final double[] point3DA, final double[] point3DB, final double[] point3DC, final int plane3DResultOffset, final int point3DAOffset, final int point3DBOffset, final int point3DCOffset) {
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_A + 0] = point3DA[point3DAOffset + 0];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_A + 1] = point3DA[point3DAOffset + 1];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_A + 2] = point3DA[point3DAOffset + 2];
		
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_B + 0] = point3DB[point3DBOffset + 0];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_B + 1] = point3DB[point3DBOffset + 1];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_B + 2] = point3DB[point3DBOffset + 2];
		
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_C + 0] = point3DC[point3DCOffset + 0];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_C + 1] = point3DC[point3DCOffset + 1];
		plane3DResult[plane3DResultOffset + PLANE_OFFSET_C + 2] = point3DC[point3DCOffset + 2];
		
		return plane3DResult;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
//	TODO: Refactor!
	public static double intersectionRaySphere(final double[] ray3D, final double[] sphereCenter, final double sphereRadius) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D);
		
		final double tMinimum = ray3DGetTMinimum(ray3D);
		final double tMaximum = ray3DGetTMaximum(ray3D);
		
		final double[] sphereCenterToRayOrigin = vector3DDirection(sphereCenter, point3DOrigin);
		
		final double a = vector3DLengthSquared(vector3DDirection);
		final double b = vector3DDotProduct(sphereCenterToRayOrigin, vector3DDirection) * 2.0D;
		final double c = vector3DLengthSquared(sphereCenterToRayOrigin) - sphereRadius * sphereRadius;
		
		final double[] ts = solveQuadraticSystem(a, b, c);
		
		final double t0 = ts[0];
		final double t1 = ts[1];
		
		if(!isNaN(t0) && t0 > tMinimum && t0 < tMaximum) {
			return t0;
		}
		
		if(!isNaN(t1) && t1 > tMinimum && t1 < tMaximum) {
			return t1;
		}
		
		return NaN;
	}
	
//	TODO: Add Javadocs!
//	TODO: Refactor!
	public static double intersectionRayTriangle(final double[] ray3D, final double[] triangleA, final double[] triangleB, final double[] triangleC) {
		final double[] point3DOrigin = ray3DGetOrigin(ray3D);
		
		final double[] vector3DDirection = ray3DGetDirection(ray3D);
		
		final double tMinimum = ray3DGetTMinimum(ray3D);
		final double tMaximum = ray3DGetTMaximum(ray3D);
		
		final double[] triangleAB = vector3DDirection(triangleA, triangleB);
		final double[] triangleCA = vector3DDirection(triangleC, triangleA);
		final double[] triangleSurfaceNormal = vector3DCrossProduct(triangleAB, triangleCA);
		
		final double determinant = vector3DDotProduct(vector3DDirection, triangleSurfaceNormal);
		
		final double[] rayOriginToTriangleA = vector3DDirection(point3DOrigin, triangleA);
		
		final double t = vector3DDotProduct(triangleSurfaceNormal, rayOriginToTriangleA) / determinant;
		
		if(t <= tMinimum || t >= tMaximum) {
			return NaN;
		}
		
		final double[] direction = vector3DCrossProduct(rayOriginToTriangleA, vector3DDirection);
		
		final double uScaled = vector3DDotProduct(direction, triangleCA);
		final double u = uScaled / determinant;
		
		if(u < 0.0D) {
			return NaN;
		}
		
		final double vScaled = vector3DDotProduct(direction, triangleAB);
		final double v = vScaled / determinant;
		
		if(v < 0.0D) {
			return NaN;
		}
		
		if((uScaled + vScaled) * determinant > determinant * determinant) {
			return NaN;
		}
		
		return t;
	}
}