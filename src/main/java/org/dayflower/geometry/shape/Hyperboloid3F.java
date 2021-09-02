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
package org.dayflower.geometry.shape;

import static org.dayflower.utility.Floats.cos;
import static org.dayflower.utility.Floats.equal;
import static org.dayflower.utility.Floats.isInfinite;
import static org.dayflower.utility.Floats.isNaN;
import static org.dayflower.utility.Floats.isZero;
import static org.dayflower.utility.Floats.max;
import static org.dayflower.utility.Floats.min;
import static org.dayflower.utility.Floats.sin;
import static org.dayflower.utility.Floats.solveQuadraticSystem;
import static org.dayflower.utility.Floats.sqrt;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.geometry.AngleF;
import org.dayflower.geometry.BoundingVolume3F;
import org.dayflower.geometry.OrthonormalBasis33F;
import org.dayflower.geometry.Point2F;
import org.dayflower.geometry.Point3F;
import org.dayflower.geometry.Ray3F;
import org.dayflower.geometry.Shape3F;
import org.dayflower.geometry.SurfaceIntersection3F;
import org.dayflower.geometry.Vector3F;
import org.dayflower.geometry.boundingvolume.AxisAlignedBoundingBox3F;

/**
 * A {@code Hyperboloid3F} is an implementation of {@link Shape3F} that represents a hyperboloid.
 * <p>
 * This class is immutable and therefore thread-safe.
 * <p>
 * This {@code Shape3F} implementation is supported on the GPU.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Hyperboloid3F implements Shape3F {
	/**
	 * The name of this {@code Hyperboloid3F} class.
	 */
	public static final String NAME = "Hyperboloid";
	
	/**
	 * The ID of this {@code Hyperboloid3F} class.
	 */
	public static final int ID = 7;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AngleF phiMax;
	private final Point3F a;
	private final Point3F b;
	private final float aH;
	private final float cH;
	private final float rMax;
	private final float zMax;
	private final float zMin;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Hyperboloid3F} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Hyperboloid3F(AngleF.degrees(360.0F));
	 * }
	 * </pre>
	 */
	public Hyperboloid3F() {
		this(AngleF.degrees(360.0F));
	}
	
	/**
	 * Constructs a new {@code Hyperboloid3F} instance.
	 * <p>
	 * If {@code phiMax} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Hyperboloid3F(phiMax, new Point3F(0.0001F, 0.0001F, 0.0F));
	 * }
	 * </pre>
	 * 
	 * @param phiMax the maximum phi
	 * @throws NullPointerException thrown if, and only if, {@code phiMax} is {@code null}
	 */
	public Hyperboloid3F(final AngleF phiMax) {
		this(phiMax, new Point3F(0.0001F, 0.0001F, 0.0F));
	}
	
	/**
	 * Constructs a new {@code Hyperboloid3F} instance.
	 * <p>
	 * If either {@code phiMax} or {@code a} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Hyperboloid3F(phiMax, a, new Point3F(1.0F, 1.0F, 1.0F));
	 * }
	 * </pre>
	 * 
	 * @param phiMax the maximum phi
	 * @param a the {@link Point3F} instance denoted by {@code A}
	 * @throws NullPointerException thrown if, and only if, either {@code phiMax} or {@code a} are {@code null}
	 */
	public Hyperboloid3F(final AngleF phiMax, final Point3F a) {
		this(phiMax, a, new Point3F(1.0F, 1.0F, 1.0F));
	}
	
	/**
	 * Constructs a new {@code Hyperboloid3F} instance.
	 * <p>
	 * If either {@code phiMax}, {@code a} or {@code b} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param phiMax the maximum phi
	 * @param a the {@link Point3F} instance denoted by {@code A}
	 * @param b the {@code Point3F} instance denoted by {@code B}
	 * @throws NullPointerException thrown if, and only if, either {@code phiMax}, {@code a} or {@code b} are {@code null}
	 */
	public Hyperboloid3F(final AngleF phiMax, final Point3F a, final Point3F b) {
		Objects.requireNonNull(phiMax, "phiMax == null");
		Objects.requireNonNull(a, "a == null");
		Objects.requireNonNull(b, "b == null");
		
		Point3F pointA = isZero(a.getZ()) ? a : isZero(b.getZ()) ? b : a;
		Point3F pointB = isZero(a.getZ()) ? b : isZero(b.getZ()) ? a : b;
		Point3F pointC = pointA;
		
		float aH = Float.POSITIVE_INFINITY;
		float cH = Float.POSITIVE_INFINITY;
		
		for(int i = 0; i < 10 && (isInfinite(aH) || isNaN(aH)); i++) {
			pointC = Point3F.add(pointC, Vector3F.multiply(Vector3F.direction(pointA, pointB), 2.0F));
			
			final float c = pointC.getX() * pointC.getX() + pointC.getY() * pointC.getY();
			final float d = pointB.getX() * pointB.getX() + pointB.getY() * pointB.getY();
			
			aH = (1.0F / c - (pointC.getZ() * pointC.getZ()) / (c * pointB.getZ() * pointB.getZ())) / (1.0F - (d * pointC.getZ() * pointC.getZ()) / (c * pointB.getZ() * pointB.getZ()));
			cH = (aH * d - 1.0F) / (pointB.getZ() * pointB.getZ());
		}
		
		if(isInfinite(aH) || isNaN(aH)) {
			throw new IllegalArgumentException();
		}
		
		this.phiMax = phiMax;
		this.a = pointA;
		this.b = pointB;
		this.aH = aH;
		this.cH = cH;
		this.rMax = max(sqrt(a.getX() * a.getX() + a.getY() * a.getY()), sqrt(b.getX() * b.getX() + b.getY() * b.getY()));
		this.zMax = max(a.getZ(), b.getZ());
		this.zMin = min(a.getZ(), b.getZ());
	}
	
	/**
	 * Constructs a new {@code Hyperboloid3F} instance.
	 * <p>
	 * If either {@code phiMax}, {@code a} or {@code b} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param phiMax the maximum phi
	 * @param a the {@link Point3F} instance denoted by {@code A}
	 * @param b the {@link Point3F} instance denoted by {@code B}
	 * @param aH a {@code float} value
	 * @param cH a {@code float} value
	 * @param rMax the maximum radius
	 * @param zMax the maximum Z value
	 * @param zMin the minimum Z value
	 * @throws NullPointerException thrown if, and only if, either {@code phiMax}, {@code a} or {@code b} are {@code null}
	 */
	public Hyperboloid3F(final AngleF phiMax, final Point3F a, final Point3F b, final float aH, final float cH, final float rMax, final float zMax, final float zMin) {
		this.phiMax = Objects.requireNonNull(phiMax, "phiMax == null");
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.aH = aH;
		this.cH = cH;
		this.rMax = rMax;
		this.zMax = zMax;
		this.zMin = zMin;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the maximum phi of this {@code Hyperboloid3F} instance.
	 * 
	 * @return the maximum phi of this {@code Hyperboloid3F} instance
	 */
	public AngleF getPhiMax() {
		return this.phiMax;
	}
	
	/**
	 * Returns a {@link BoundingVolume3F} instance that contains this {@code Hyperboloid3F} instance.
	 * 
	 * @return a {@code BoundingVolume3F} instance that contains this {@code Hyperboloid3F} instance
	 */
	@Override
	public BoundingVolume3F getBoundingVolume() {
		return new AxisAlignedBoundingBox3F(new Point3F(-this.rMax, -this.rMax, this.zMin), new Point3F(this.rMax, this.rMax, this.zMax));
	}
	
	/**
	 * Performs an intersection test between {@code ray} and this {@code Hyperboloid3F} instance.
	 * <p>
	 * Returns an {@code Optional} with an optional {@link SurfaceIntersection3F} instance that contains information about the intersection, if it was found.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray the {@link Ray3F} to perform an intersection test against this {@code Hyperboloid3F} instance
	 * @param tMinimum the minimum parametric distance
	 * @param tMaximum the maximum parametric distance
	 * @return an {@code Optional} with an optional {@code SurfaceIntersection3F} instance that contains information about the intersection, if it was found
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public Optional<SurfaceIntersection3F> intersection(final Ray3F ray, final float tMinimum, final float tMaximum) {
		final float t = intersectionT(ray, tMinimum, tMaximum);
		
		if(isNaN(t)) {
			return SurfaceIntersection3F.EMPTY;
		}
		
		return Optional.of(doCreateSurfaceIntersection(ray, t));
	}
	
	/**
	 * Returns the {@link Point3F} instance denoted by {@code A}.
	 * 
	 * @return the {@code Point3F} instance denoted by {@code A}
	 */
	public Point3F getA() {
		return this.a;
	}
	
	/**
	 * Returns the {@link Point3F} instance denoted by {@code B}.
	 * 
	 * @return the {@code Point3F} instance denoted by {@code B}
	 */
	public Point3F getB() {
		return this.b;
	}
	
	/**
	 * Returns a {@code String} with the name of this {@code Hyperboloid3F} instance.
	 * 
	 * @return a {@code String} with the name of this {@code Hyperboloid3F} instance
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Hyperboloid3F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Hyperboloid3F} instance
	 */
	@Override
	public String toString() {
		return String.format("new Paraboloid3F(%s, %s, %s)", this.phiMax, this.a, this.b);
	}
	
	/**
	 * Compares {@code object} to this {@code Hyperboloid3F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Hyperboloid3F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Hyperboloid3F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Hyperboloid3F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Hyperboloid3F)) {
			return false;
		} else if(!Objects.equals(this.phiMax, Hyperboloid3F.class.cast(object).phiMax)) {
			return false;
		} else if(!Objects.equals(this.a, Hyperboloid3F.class.cast(object).a)) {
			return false;
		} else if(!Objects.equals(this.b, Hyperboloid3F.class.cast(object).b)) {
			return false;
		} else if(!equal(this.aH, Hyperboloid3F.class.cast(object).aH)) {
			return false;
		} else if(!equal(this.cH, Hyperboloid3F.class.cast(object).cH)) {
			return false;
		} else if(!equal(this.rMax, Hyperboloid3F.class.cast(object).rMax)) {
			return false;
		} else if(!equal(this.zMax, Hyperboloid3F.class.cast(object).zMax)) {
			return false;
		} else if(!equal(this.zMin, Hyperboloid3F.class.cast(object).zMin)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the value of the variable {@code AH}.
	 * 
	 * @return the value of the variable {@code AH}
	 */
	public float getAH() {
		return this.aH;
	}
	
	/**
	 * Returns the value of the variable {@code CH}.
	 * 
	 * @return the value of the variable {@code CH}
	 */
	public float getCH() {
		return this.cH;
	}
	
	/**
	 * Returns the maximum radius.
	 * 
	 * @return the maximum radius
	 */
	public float getRMax() {
		return this.rMax;
	}
	
	/**
	 * Returns the surface area of this {@code Hyperboloid3F} instance.
	 * 
	 * @return the surface area of this {@code Hyperboloid3F} instance
	 */
	@Override
	public float getSurfaceArea() {
		final float aX11 = this.a.getX();
		final float aX21 = aX11 * aX11;
		final float aX31 = aX21 * aX11;
		final float aX41 = aX31 * aX11;
		final float aX42 = aX41 * 2.0F;
		final float aY11 = this.a.getY();
		final float aY21 = aY11 * aY11;
		final float aY25 = aY21 * 5.0F;
		final float aZ11 = this.a.getZ();
		final float aZ21 = aZ11 * aZ11;
		
		final float bX11 = this.b.getX();
		final float bX21 = bX11 * bX11;
		final float bX31 = bX21 * bX11;
		final float bX41 = bX31 * bX11;
		final float bX42 = bX41 * 2.0F;
		final float bY11 = this.b.getY();
		final float bY21 = bY11 * bY11;
		final float bY24 = bY21 * 4.0F;
		final float bY25 = bY21 * 5.0F;
		final float bZ11 = this.b.getZ();
		final float bZ21 = bZ11 * bZ11;
		
		final float cX11 = aX11 * bX11;
		final float cX12 = cX11 * 2.0F;
		final float cY11 = aY11 * bY11;
		final float cY12 = cY11 * 2.0F;
		final float cY15 = cY11 * 5.0F;
		final float cZ11 = aZ11 * bZ11;
		final float cZ12 = cZ11 * 2.0F;
		
		final float dY11 = (aY11 - bY11) * (aY11 - bY11);
		final float dZ11 = (aZ11 - bZ11) * (aZ11 - bZ11);
		final float dZ12 = dZ11 * 2.0F;
		
		final float a = aX42;
		final float b = aX31 * bX11 * 2.0F;
		final float c = bX42;
		final float d = (aY21 + cY11 + bY21) * (dY11 + dZ11) * 2.0F;
		final float e = bX21 * (aY25 + cY12 - bY24 + dZ12);
		final float f = aX21 * ((aY21 * -4.0F) + cY12 + bY25 + dZ12);
		final float g = cX12 * (bX21 - aY21 + cY15 - bY21 - aZ21 + cZ12 - bZ21);
		final float h = a - b + c + d + e + f - g;
		
		final float phiMax = this.phiMax.getRadians();
		
		final float surfaceArea = phiMax / 6.0F * h;
		
		return surfaceArea;
	}
	
	/**
	 * Returns the maximum Z value.
	 * 
	 * @return the maximum Z value
	 */
	public float getZMax() {
		return this.zMax;
	}
	
	/**
	 * Returns the minimum Z value.
	 * 
	 * @return the minimum Z value
	 */
	public float getZMin() {
		return this.zMin;
	}
	
	/**
	 * Performs an intersection test between {@code ray} and this {@code Hyperboloid3F} instance.
	 * <p>
	 * Returns {@code t}, the parametric distance to the surface intersection point, or {@code Float.NaN} if no intersection exists.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray the {@link Ray3F} to perform an intersection test against this {@code Hyperboloid3F} instance
	 * @param tMinimum the minimum parametric distance
	 * @param tMaximum the maximum parametric distance
	 * @return {@code t}, the parametric distance to the surface intersection point, or {@code Float.NaN} if no intersection exists
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public float intersectionT(final Ray3F ray, final float tMinimum, final float tMaximum) {
		final Point3F origin = ray.getOrigin();
		
		final Vector3F direction = ray.getDirection();
		
		final float a = this.aH * direction.getX() * direction.getX() + this.aH * direction.getY() * direction.getY() - this.cH * direction.getZ() * direction.getZ();
		final float b = 2.0F * (this.aH * direction.getX() * origin.getX() + this.aH * direction.getY() * origin.getY() - this.cH * direction.getZ() * origin.getZ());
		final float c = this.aH * origin.getX() * origin.getX() + this.aH * origin.getY() * origin.getY() - this.cH * origin.getZ() * origin.getZ() - 1.0F;
		
		final float[] ts = solveQuadraticSystem(a, b, c);
		
		for(int i = 0; i < ts.length; i++) {
			final float t = ts[i];
			
			if(isNaN(t)) {
				return Float.NaN;
			}
			
			if(t > tMinimum && t < tMaximum) {
				final Point3F surfaceIntersectionPoint = doCreateSurfaceIntersectionPoint(ray, t);
				
				if(surfaceIntersectionPoint.getZ() >= this.zMin && surfaceIntersectionPoint.getZ() <= this.zMax && doComputePhi(surfaceIntersectionPoint) <= this.phiMax.getRadians()) {
					return t;
				}
			}
		}
		
		return Float.NaN;
	}
	
	/**
	 * Returns an {@code int} with the ID of this {@code Hyperboloid3F} instance.
	 * 
	 * @return an {@code int} with the ID of this {@code Hyperboloid3F} instance
	 */
	@Override
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns a hash code for this {@code Hyperboloid3F} instance.
	 * 
	 * @return a hash code for this {@code Hyperboloid3F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.phiMax, this.a, this.b, Float.valueOf(this.aH), Float.valueOf(this.cH), Float.valueOf(this.rMax), Float.valueOf(this.zMax), Float.valueOf(this.zMin));
	}
	
	/**
	 * Writes this {@code Hyperboloid3F} instance to {@code dataOutput}.
	 * <p>
	 * If {@code dataOutput} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataOutput the {@code DataOutput} instance to write to
	 * @throws NullPointerException thrown if, and only if, {@code dataOutput} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	@Override
	public void write(final DataOutput dataOutput) {
		try {
			dataOutput.writeInt(ID);
			
			this.phiMax.write(dataOutput);
			this.a.write(dataOutput);
			this.b.write(dataOutput);
			
			dataOutput.writeFloat(this.aH);
			dataOutput.writeFloat(this.cH);
			dataOutput.writeFloat(this.rMax);
			dataOutput.writeFloat(this.zMax);
			dataOutput.writeFloat(this.zMin);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private OrthonormalBasis33F doCreateOrthonormalBasisG(final Point3F surfaceIntersectionPoint) {
		final float phi = doComputePhi(surfaceIntersectionPoint);
		final float phiCos = cos(phi);
		final float phiSin = sin(phi);
		
		final float uX = -this.phiMax.getRadians() * surfaceIntersectionPoint.getY();
		final float uY = +this.phiMax.getRadians() * surfaceIntersectionPoint.getX();
		final float uZ = +0.0F;
		
		final float vX = (this.b.getX() - this.a.getX()) * phiCos - (this.b.getY() - this.a.getY()) * phiSin;
		final float vY = (this.b.getX() - this.a.getX()) * phiSin + (this.b.getY() - this.a.getY()) * phiCos;
		final float vZ = this.b.getZ() - this.a.getZ();
		
		final Vector3F u = Vector3F.normalize(new Vector3F(uX, uY, uZ));
		final Vector3F v = Vector3F.normalize(new Vector3F(vX, vY, vZ));
		final Vector3F w = Vector3F.crossProduct(u, v);
		
		return new OrthonormalBasis33F(w, v, u);
	}
	
	private Point2F doCreateTextureCoordinates(final Point3F surfaceIntersectionPoint) {
		final float v = (surfaceIntersectionPoint.getZ() - this.a.getZ()) / (this.b.getZ() - this.a.getZ());
		final float u = doComputePhi(surfaceIntersectionPoint, v) / this.phiMax.getRadians();
		
		return new Point2F(u, v);
	}
	
	private SurfaceIntersection3F doCreateSurfaceIntersection(final Ray3F ray, final float t) {
		final Point3F surfaceIntersectionPoint = doCreateSurfaceIntersectionPoint(ray, t);
		
		final OrthonormalBasis33F orthonormalBasisG = doCreateOrthonormalBasisG(surfaceIntersectionPoint);
		final OrthonormalBasis33F orthonormalBasisS = orthonormalBasisG;
		
		final Point2F textureCoordinates = doCreateTextureCoordinates(surfaceIntersectionPoint);
		
		final Vector3F surfaceIntersectionPointError = new Vector3F();
		
		return new SurfaceIntersection3F(orthonormalBasisG, orthonormalBasisS, textureCoordinates, surfaceIntersectionPoint, ray, this, surfaceIntersectionPointError, t);
	}
	
	private float doComputePhi(final Point3F surfaceIntersectionPoint) {
		return doComputePhi(surfaceIntersectionPoint, (surfaceIntersectionPoint.getZ() - this.a.getZ()) / (this.b.getZ() - this.a.getZ()));
	}
	
	private float doComputePhi(final Point3F surfaceIntersectionPoint, final float v) {
		final Point3F a = Point3F.lerp(this.a, this.b, v);
		final Point3F b = new Point3F(surfaceIntersectionPoint.getX() * a.getX() + surfaceIntersectionPoint.getY() * a.getY(), surfaceIntersectionPoint.getY() * a.getX() - surfaceIntersectionPoint.getX() * a.getY(), 0.0F);
		
		return b.sphericalPhi();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Point3F doCreateSurfaceIntersectionPoint(final Ray3F ray, final float t) {
		return Point3F.add(ray.getOrigin(), ray.getDirection(), t);
	}
}