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

import static org.dayflower.utility.Doubles.abs;
import static org.dayflower.utility.Doubles.acos;
import static org.dayflower.utility.Doubles.equal;
import static org.dayflower.utility.Doubles.isNaN;
import static org.dayflower.utility.Doubles.isZero;
import static org.dayflower.utility.Doubles.lerp;
import static org.dayflower.utility.Doubles.max;
import static org.dayflower.utility.Doubles.min;
import static org.dayflower.utility.Doubles.saturate;
import static org.dayflower.utility.Doubles.sin;
import static org.dayflower.utility.Doubles.sqrt;
import static org.dayflower.utility.Doubles.toDouble;
import static org.dayflower.utility.Ints.saturate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.geometry.AngleD;
import org.dayflower.geometry.BoundingVolume3D;
import org.dayflower.geometry.Matrix44D;
import org.dayflower.geometry.OrthonormalBasis33D;
import org.dayflower.geometry.Point2D;
import org.dayflower.geometry.Point3D;
import org.dayflower.geometry.Ray3D;
import org.dayflower.geometry.Shape3D;
import org.dayflower.geometry.SurfaceIntersection3D;
import org.dayflower.geometry.Vector2D;
import org.dayflower.geometry.Vector3D;
import org.dayflower.geometry.boundingvolume.AxisAlignedBoundingBox3D;
import org.dayflower.node.Node;
import org.dayflower.node.NodeHierarchicalVisitor;
import org.dayflower.node.NodeTraversalException;
import org.dayflower.utility.ParameterArguments;

/**
 * A {@code Curve3D} denotes a 3-dimensional curve that uses the data type {@code double}.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Curve3D implements Shape3D {
	/**
	 * The name of this {@code Curve3D} class.
	 */
	public static final String NAME = "Curve";
	
	/**
	 * The ID of this {@code Curve3D} class.
	 */
	public static final int ID = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Data data;
	private final double uMaximum;
	private final double uMinimum;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Curve3D} instance.
	 * <p>
	 * If {@code data} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param data a {@link Data} instance
	 * @param uMinimum the minimum U-value
	 * @param uMaximum the maximum U-value
	 * @throws NullPointerException thrown if, and only if, {@code data} is {@code null}
	 */
	public Curve3D(final Data data, final double uMinimum, final double uMaximum) {
		this.data = Objects.requireNonNull(data, "data == null");
		this.uMinimum = uMinimum;
		this.uMaximum = uMaximum;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link BoundingVolume3D} instance that contains this {@code Curve3D} instance.
	 * 
	 * @return a {@code BoundingVolume3D} instance that contains this {@code Curve3D} instance
	 */
	@Override
	public BoundingVolume3D getBoundingVolume() {
		final Data data = this.data;
		
		final double uMaximum = this.uMaximum;
		final double uMinimum = this.uMinimum;
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		final double widthC = lerp(widthA, widthB, uMinimum);
		final double widthD = lerp(widthA, widthB, uMaximum);
		final double widthE = max(widthC, widthD) * 0.5D;
		
		final Point3D pointA = data.getPointA();
		final Point3D pointB = data.getPointB();
		final Point3D pointC = data.getPointC();
		final Point3D pointD = data.getPointD();
		
		final Point3D pointE = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMinimum);
		final Point3D pointF = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMaximum);
		final Point3D pointG = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMaximum, uMaximum);
		final Point3D pointH = doBezierBlossom(pointA, pointB, pointC, pointD, uMaximum, uMaximum, uMaximum);
		
		final AxisAlignedBoundingBox3D axisAlignedBoundingBoxA = new AxisAlignedBoundingBox3D(pointE, pointF);
		final AxisAlignedBoundingBox3D axisAlignedBoundingBoxB = new AxisAlignedBoundingBox3D(pointG, pointH);
		final AxisAlignedBoundingBox3D axisAlignedBoundingBoxC = AxisAlignedBoundingBox3D.union(axisAlignedBoundingBoxA, axisAlignedBoundingBoxB);
		final AxisAlignedBoundingBox3D axisAlignedBoundingBoxD = AxisAlignedBoundingBox3D.expand(axisAlignedBoundingBoxC, widthE);
		
		return axisAlignedBoundingBoxD;
	}
	
	/**
	 * Performs an intersection test between {@code ray} and this {@code Curve3D} instance.
	 * <p>
	 * Returns an {@code Optional} with an optional {@link SurfaceIntersection3D} instance that contains information about the intersection, if it was found.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray the {@link Ray3D} to perform an intersection test against this {@code Curve3D} instance
	 * @param tMinimum the minimum parametric distance
	 * @param tMaximum the maximum parametric distance
	 * @return an {@code Optional} with an optional {@code SurfaceIntersection3D} instance that contains information about the intersection, if it was found
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public Optional<SurfaceIntersection3D> intersection(final Ray3D ray, final double tMinimum, final double tMaximum) {
		final Data data = this.data;
		
		final double uMaximum = this.uMaximum;
		final double uMinimum = this.uMinimum;
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		final double widthC = lerp(widthA, widthB, uMinimum);
		final double widthD = lerp(widthA, widthB, uMaximum);
		final double widthE = max(widthC, widthD) * 0.5D;
		
		final Point3D pointA = data.getPointA();
		final Point3D pointB = data.getPointB();
		final Point3D pointC = data.getPointC();
		final Point3D pointD = data.getPointD();
		
		final Point3D pointE = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMinimum);
		final Point3D pointF = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMaximum);
		final Point3D pointG = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMaximum, uMaximum);
		final Point3D pointH = doBezierBlossom(pointA, pointB, pointC, pointD, uMaximum, uMaximum, uMaximum);
		
		final Vector3D directionX = doCreateDirectionX(ray, pointE, pointH);
		
		final Matrix44D rayToObject = Matrix44D.lookAt(ray.getOrigin(), Point3D.add(ray.getOrigin(), ray.getDirection()), directionX);
		
		if(!rayToObject.isInvertible()) {
			return Optional.empty();
		}
		
		final Matrix44D objectToRay = Matrix44D.inverse(rayToObject);
		
		final Point3D pointI = Point3D.transform(objectToRay, pointE);
		final Point3D pointJ = Point3D.transform(objectToRay, pointF);
		final Point3D pointK = Point3D.transform(objectToRay, pointG);
		final Point3D pointL = Point3D.transform(objectToRay, pointH);
		
		final double xMaximum = 0.0D;
		final double yMaximum = 0.0D;
		final double zMaximum = ray.getDirection().length() * tMaximum;
		
		if(max(pointI.getX(), pointJ.getX(), pointK.getX(), pointL.getX()) + widthE < tMinimum || min(pointI.getX(), pointJ.getX(), pointK.getX(), pointL.getX()) - widthE > xMaximum) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		if(max(pointI.getY(), pointJ.getY(), pointK.getY(), pointL.getY()) + widthE < tMinimum || min(pointI.getY(), pointJ.getY(), pointK.getY(), pointL.getY()) - widthE > yMaximum) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		if(max(pointI.getZ(), pointJ.getZ(), pointK.getZ(), pointL.getZ()) + widthE < tMinimum || min(pointI.getZ(), pointJ.getZ(), pointK.getZ(), pointL.getZ()) - widthE > zMaximum) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		final double l01 = max(abs(pointI.getX() - 2.0D * pointJ.getX() + pointK.getX()), abs(pointI.getY() - 2.0D * pointJ.getY() + pointK.getY()), abs(pointI.getZ() - 2.0D * pointJ.getZ() + pointK.getZ()));
		final double l02 = max(abs(pointJ.getX() - 2.0D * pointK.getX() + pointL.getX()), abs(pointJ.getY() - 2.0D * pointK.getY() + pointL.getY()), abs(pointJ.getZ() - 2.0D * pointK.getZ() + pointL.getZ()));
		final double l03 = max(l01, l02);
		
		final int depth = saturate(doLog2(1.41421356237D * 6.0D * l03 / (8.0D * (max(widthA, widthB) * 0.05D))) / 2, 0, 10);
		
		return doIntersectionRecursive(ray, tMinimum, tMaximum, objectToRay, rayToObject, pointI, pointJ, pointK, pointL, uMinimum, uMaximum, depth);
	}
	
	/**
	 * Returns a {@code String} with the name of this {@code Curve3D} instance.
	 * 
	 * @return a {@code String} with the name of this {@code Curve3D} instance
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Curve3D} instance.
	 * 
	 * @return a {@code String} representation of this {@code Curve3D} instance
	 */
	@Override
	public String toString() {
		return String.format("new Curve3D(%s, %+.10f, %+.10f)", this.data, Double.valueOf(this.uMinimum), Double.valueOf(this.uMaximum));
	}
	
	/**
	 * Accepts a {@link NodeHierarchicalVisitor}.
	 * <p>
	 * Returns the result of {@code nodeHierarchicalVisitor.visitLeave(this)}.
	 * <p>
	 * If {@code nodeHierarchicalVisitor} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If a {@code RuntimeException} is thrown by the current {@code NodeHierarchicalVisitor}, a {@code NodeTraversalException} will be thrown with the {@code RuntimeException} wrapped.
	 * <p>
	 * This implementation will:
	 * <ul>
	 * <li>throw a {@code NullPointerException} if {@code nodeHierarchicalVisitor} is {@code null}.</li>
	 * <li>throw a {@code NodeTraversalException} if {@code nodeHierarchicalVisitor} throws a {@code RuntimeException}.</li>
	 * <li>traverse its child {@code Node} instances.</li>
	 * </ul>
	 * 
	 * @param nodeHierarchicalVisitor the {@code NodeHierarchicalVisitor} to accept
	 * @return the result of {@code nodeHierarchicalVisitor.visitLeave(this)}
	 * @throws NodeTraversalException thrown if, and only if, a {@code RuntimeException} is thrown by the current {@code NodeHierarchicalVisitor}
	 * @throws NullPointerException thrown if, and only if, {@code nodeHierarchicalVisitor} is {@code null}
	 */
	@Override
	public boolean accept(final NodeHierarchicalVisitor nodeHierarchicalVisitor) {
		Objects.requireNonNull(nodeHierarchicalVisitor, "nodeHierarchicalVisitor == null");
		
		try {
			if(nodeHierarchicalVisitor.visitEnter(this)) {
				if(!this.data.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
			}
			
			return nodeHierarchicalVisitor.visitLeave(this);
		} catch(final RuntimeException e) {
			throw new NodeTraversalException(e);
		}
	}
	
	/**
	 * Compares {@code object} to this {@code Curve3D} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Curve3D}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Curve3D} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Curve3D}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Curve3D)) {
			return false;
		} else if(!Objects.equals(this.data, Curve3D.class.cast(object).data)) {
			return false;
		} else if(!equal(this.uMaximum, Curve3D.class.cast(object).uMaximum)) {
			return false;
		} else if(!equal(this.uMinimum, Curve3D.class.cast(object).uMinimum)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the surface area of this {@code Curve3D} instance.
	 * 
	 * @return the surface area of this {@code Curve3D} instance
	 */
	@Override
	public double getSurfaceArea() {
		final Data data = this.data;
		
		final double uMaximum = this.uMaximum;
		final double uMinimum = this.uMinimum;
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		final double widthC = lerp(widthA, widthB, uMinimum);
		final double widthD = lerp(widthA, widthB, uMaximum);
		final double widthE = (widthC + widthD) * 0.5D;
		
		final Point3D pointA = data.getPointA();
		final Point3D pointB = data.getPointB();
		final Point3D pointC = data.getPointC();
		final Point3D pointD = data.getPointD();
		
		final Point3D pointE = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMinimum);
		final Point3D pointF = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMaximum);
		final Point3D pointG = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMaximum, uMaximum);
		final Point3D pointH = doBezierBlossom(pointA, pointB, pointC, pointD, uMaximum, uMaximum, uMaximum);
		
		final double approximateLength = Point3D.distance(pointE, pointF) + Point3D.distance(pointF, pointG) + Point3D.distance(pointG, pointH);
		
		return approximateLength * widthE;
	}
	
	/**
	 * Performs an intersection test between {@code ray} and this {@code Curve3D} instance.
	 * <p>
	 * Returns {@code t}, the parametric distance to the surface intersection point, or {@code Double.NaN} if no intersection exists.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray the {@link Ray3D} to perform an intersection test against this {@code Curve3D} instance
	 * @param tMinimum the minimum parametric distance
	 * @param tMaximum the maximum parametric distance
	 * @return {@code t}, the parametric distance to the surface intersection point, or {@code Double.NaN} if no intersection exists
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public double intersectionT(final Ray3D ray, final double tMinimum, final double tMaximum) {
		final Data data = this.data;
		
		final double uMaximum = this.uMaximum;
		final double uMinimum = this.uMinimum;
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		final double widthC = lerp(widthA, widthB, uMinimum);
		final double widthD = lerp(widthA, widthB, uMaximum);
		final double widthE = max(widthC, widthD) * 0.5D;
		
		final Point3D pointA = data.getPointA();
		final Point3D pointB = data.getPointB();
		final Point3D pointC = data.getPointC();
		final Point3D pointD = data.getPointD();
		
		final Point3D pointE = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMinimum);
		final Point3D pointF = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMinimum, uMaximum);
		final Point3D pointG = doBezierBlossom(pointA, pointB, pointC, pointD, uMinimum, uMaximum, uMaximum);
		final Point3D pointH = doBezierBlossom(pointA, pointB, pointC, pointD, uMaximum, uMaximum, uMaximum);
		
		final Vector3D directionX = doCreateDirectionX(ray, pointE, pointH);
		
		final Matrix44D rayToObject = Matrix44D.lookAt(ray.getOrigin(), Point3D.add(ray.getOrigin(), ray.getDirection()), directionX);
		
		if(!rayToObject.isInvertible()) {
			return Double.NaN;
		}
		
		final Matrix44D objectToRay = Matrix44D.inverse(rayToObject);
		
		final Point3D pointI = Point3D.transform(objectToRay, pointE);
		final Point3D pointJ = Point3D.transform(objectToRay, pointF);
		final Point3D pointK = Point3D.transform(objectToRay, pointG);
		final Point3D pointL = Point3D.transform(objectToRay, pointH);
		
		final double xMaximum = 0.0D;
		final double yMaximum = 0.0D;
		final double zMaximum = ray.getDirection().length() * tMaximum;
		
		if(max(pointI.getX(), pointJ.getX(), pointK.getX(), pointL.getX()) + widthE < tMinimum || min(pointI.getX(), pointJ.getX(), pointK.getX(), pointL.getX()) - widthE > xMaximum) {
			return Double.NaN;
		}
		
		if(max(pointI.getY(), pointJ.getY(), pointK.getY(), pointL.getY()) + widthE < tMinimum || min(pointI.getY(), pointJ.getY(), pointK.getY(), pointL.getY()) - widthE > yMaximum) {
			return Double.NaN;
		}
		
		if(max(pointI.getZ(), pointJ.getZ(), pointK.getZ(), pointL.getZ()) + widthE < tMinimum || min(pointI.getZ(), pointJ.getZ(), pointK.getZ(), pointL.getZ()) - widthE > zMaximum) {
			return Double.NaN;
		}
		
		final double l01 = max(abs(pointI.getX() - 2.0D * pointJ.getX() + pointK.getX()), abs(pointI.getY() - 2.0D * pointJ.getY() + pointK.getY()), abs(pointI.getZ() - 2.0D * pointJ.getZ() + pointK.getZ()));
		final double l02 = max(abs(pointJ.getX() - 2.0D * pointK.getX() + pointL.getX()), abs(pointJ.getY() - 2.0D * pointK.getY() + pointL.getY()), abs(pointJ.getZ() - 2.0D * pointK.getZ() + pointL.getZ()));
		final double l03 = max(l01, l02);
		
		final int depth = saturate(doLog2(1.41421356237D * 6.0D * l03 / (8.0D * (max(widthA, widthB) * 0.05D))) / 2, 0, 10);
		
		return doIntersectionTRecursive(ray, tMinimum, tMaximum, pointI, pointJ, pointK, pointL, uMinimum, uMaximum, depth);
	}
	
	/**
	 * Returns an {@code int} with the ID of this {@code Curve3D} instance.
	 * 
	 * @return an {@code int} with the ID of this {@code Curve3D} instance
	 */
	@Override
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns a hash code for this {@code Curve3D} instance.
	 * 
	 * @return a hash code for this {@code Curve3D} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.data, Double.valueOf(this.uMaximum), Double.valueOf(this.uMinimum));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code List} of {@code Curve3D} instances.
	 * <p>
	 * If either {@code pointA}, {@code pointB}, {@code pointC}, {@code pointD}, {@code type}, {@code normalA} or {@code normalB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param pointA a {@link Point3D} instance with the control point denoted by A
	 * @param pointB a {@code Point3D} instance with the control point denoted by B
	 * @param pointC a {@code Point3D} instance with the control point denoted by C
	 * @param pointD a {@code Point3D} instance with the control point denoted by D
	 * @param type a {@link Type} instance
	 * @param normalA a {@link Vector3D} instance with the normal denoted by A
	 * @param normalB a {@code Vector3D} instance with the normal denoted by B
	 * @param widthA the width denoted by A
	 * @param widthB the width denoted by B
	 * @param splitDepth the split depth
	 * @return a {@code List} of {@code Curve3D} instances
	 * @throws NullPointerException thrown if, and only if, either {@code pointA}, {@code pointB}, {@code pointC}, {@code pointD}, {@code type}, {@code normalA} or {@code normalB} are {@code null}
	 */
	public static List<Curve3D> createCurves(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final Type type, final Vector3D normalA, final Vector3D normalB, final double widthA, final double widthB, final int splitDepth) {
		Objects.requireNonNull(pointA, "pointA == null");
		Objects.requireNonNull(pointB, "pointB == null");
		Objects.requireNonNull(pointC, "pointC == null");
		Objects.requireNonNull(pointD, "pointD == null");
		Objects.requireNonNull(type, "type == null");
		Objects.requireNonNull(normalA, "normalA == null");
		Objects.requireNonNull(normalB, "normalB == null");
		
		final Data data = new Data(pointA, pointB, pointC, pointD, type, normalA, normalB, widthA, widthB);
		
		final List<Curve3D> curves = new ArrayList<>();
		
		final int segments = 1 << splitDepth;
		
		for(int segment = 0; segment < segments; segment++) {
			final double uMinimum = toDouble(segment + 0) / toDouble(segments);
			final double uMaximum = toDouble(segment + 1) / toDouble(segments);
			
			curves.add(new Curve3D(data, uMinimum, uMaximum));
		}
		
		return curves;
	}
	
	/**
	 * Returns a {@code List} of {@code Curve3D} instances.
	 * <p>
	 * If either {@code points}, at least one element in {@code points}, {@code normals}, at least one element in {@code normals} or {@code type} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code degree} is less than {@code 2} or greater than {@code 3}, {@code points} contains the wrong number of {@link Point3D} instances or {@code normals} contains the wrong number of {@link Vector3D} instances, an
	 * {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Below follows the constraints:
	 * <ul>
	 * <li>For any given {@code degree}, there must exist at least {@code (degree + 1)} {@code Point3D} instances in {@code points}.</li>
	 * <li>For a {@link Type} of {@link Type#RIBBON}, there must exist exactly {@code (points.size() - degree + 1)} {@code Vector3D} instances in {@code normals} and {@code 0} otherwise.</li>
	 * </ul>
	 * 
	 * @param points a {@code List} of {@code Point3D} instances that represents the control points
	 * @param normals a {@code List} of {@code Vector3D} instances that represents the normals
	 * @param type a {@code Type} instance
	 * @param widthA the width denoted by A
	 * @param widthB the width denoted by B
	 * @param degree the degree
	 * @param splitDepth the split depth
	 * @return a {@code List} of {@code Curve3D} instances
	 * @throws IllegalArgumentException thrown if, and only if, either {@code degree} is less than {@code 2} or greater than {@code 3}, {@code points} contains the wrong number of {@code Point3D} instances or {@code normals} contains the wrong number
	 *                                  of {@code Vector3D} instances
	 * @throws NullPointerException thrown if, and only if, either {@code points}, at least one element in {@code points}, {@code normals}, at least one element in {@code normals} or {@code type} are {@code null}
	 */
	public static List<Curve3D> createCurvesByBSpline(final List<Point3D> points, final List<Vector3D> normals, final Type type, final double widthA, final double widthB, final int degree, final int splitDepth) {
		ParameterArguments.requireNonNullList(points, "points");
		ParameterArguments.requireNonNullList(normals, "normals");
		
		Objects.requireNonNull(type, "type == null");
		
		if(degree != 2 && degree != 3) {
			throw new IllegalArgumentException(String.format("Parameter argument degree must be 2 or 3: degree == %d", Integer.valueOf(degree)));
		}
		
		if(points.size() < degree + 1) {
			throw new IllegalArgumentException(String.format("Parameter argument points must contain at least %d Point3F instances: points.size() == %d", Integer.valueOf(degree + 1), Integer.valueOf(points.size())));
		}
		
		final int segments = points.size() - degree;
		
		switch(type) {
			case CYLINDER:
				ParameterArguments.requireExact(normals.size(), 0, "normals.size()");
				
				break;
			case FLAT:
				ParameterArguments.requireExact(normals.size(), 0, "normals.size()");
				
				break;
			case RIBBON:
				ParameterArguments.requireExact(normals.size(), segments + 1, "normals.size()");
				
				break;
			default:
				break;
		}
		
		final List<Curve3D> curves = new ArrayList<>();
		
		for(int segment = 0; segment < segments; segment++) {
			final int offset = segment;
			
			if(degree == 2) {
				final Point3D point00 = points.get(offset + 0);
				final Point3D point01 = points.get(offset + 1);
				final Point3D point02 = points.get(offset + 2);
				
				final Point3D point10 = Point3D.lerp(point00, point01, 0.5D);
				final Point3D point11 = Point3D.lerp(point01, point02, 0.5D);
				
				final Point3D pointA = point10;
				final Point3D pointB = Point3D.lerp(point10, point01, 2.0D / 3.0D);
				final Point3D pointC = Point3D.lerp(point01, point11, 1.0D / 3.0D);
				final Point3D pointD = point11;
				
				final Vector3D normalA = segment + 0 < normals.size() ? normals.get(segment + 0) : new Vector3D();
				final Vector3D normalB = segment + 1 < normals.size() ? normals.get(segment + 1) : new Vector3D();
				
				final double widthC = lerp(widthA, widthB, toDouble(segment + 0) / toDouble(segments));
				final double widthD = lerp(widthA, widthB, toDouble(segment + 1) / toDouble(segments));
				
				curves.addAll(createCurves(pointA, pointB, pointC, pointD, type, normalA, normalB, widthC, widthD, splitDepth));
			} else {
				final Point3D point00 = points.get(offset + 0);
				final Point3D point01 = points.get(offset + 1);
				final Point3D point02 = points.get(offset + 2);
				final Point3D point03 = points.get(offset + 3);
				
				final Point3D point10 = Point3D.lerp(point00, point01, 2.0D / 3.0D);
				final Point3D point11 = Point3D.lerp(point01, point02, 1.0D / 3.0D);
				final Point3D point12 = Point3D.lerp(point01, point02, 2.0D / 3.0D);
				final Point3D point13 = Point3D.lerp(point02, point03, 1.0D / 3.0D);
				
				final Point3D point20 = Point3D.lerp(point10, point11, 0.5D);
				final Point3D point21 = Point3D.lerp(point12, point13, 0.5D);
				
				final Point3D pointA = point20;
				final Point3D pointB = point11;
				final Point3D pointC = point12;
				final Point3D pointD = point21;
				
				final Vector3D normalA = segment + 0 < normals.size() ? normals.get(segment + 0) : new Vector3D();
				final Vector3D normalB = segment + 1 < normals.size() ? normals.get(segment + 1) : new Vector3D();
				
				final double widthC = lerp(widthA, widthB, toDouble(segment + 0) / toDouble(segments));
				final double widthD = lerp(widthA, widthB, toDouble(segment + 1) / toDouble(segments));
				
				curves.addAll(createCurves(pointA, pointB, pointC, pointD, type, normalA, normalB, widthC, widthD, splitDepth));
			}
		}
		
		return curves;
	}
	
	/**
	 * Returns a {@code List} of {@code Curve3D} instances.
	 * <p>
	 * If either {@code points}, at least one element in {@code points}, {@code normals}, at least one element in {@code normals} or {@code type} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code degree} is less than {@code 2} or greater than {@code 3}, {@code points} contains the wrong number of {@link Point3D} instances or {@code normals} contains the wrong number of {@link Vector3D} instances, an
	 * {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Below follows the constraints:
	 * <ul>
	 * <li>For any given {@code degree}, there must exist at least {@code ((degree + 1) + n * degree)} {@code Point3D} instances in {@code points}.</li>
	 * <li>For a {@link Type} of {@link Type#RIBBON}, there must exist exactly {@code ((points.size() - 1) / degree + 1)} {@code Vector3D} instances in {@code normals} and {@code 0} otherwise.</li>
	 * </ul>
	 * 
	 * @param points a {@code List} of {@code Point3D} instances that represents the control points
	 * @param normals a {@code List} of {@code Vector3D} instances that represents the normals
	 * @param type a {@code Type} instance
	 * @param widthA the width denoted by A
	 * @param widthB the width denoted by B
	 * @param degree the degree
	 * @param splitDepth the split depth
	 * @return a {@code List} of {@code Curve3D} instances
	 * @throws IllegalArgumentException thrown if, and only if, either {@code degree} is less than {@code 2} or greater than {@code 3}, {@code points} contains the wrong number of {@code Point3D} instances or {@code normals} contains the wrong number
	 *                                  of {@code Vector3D} instances
	 * @throws NullPointerException thrown if, and only if, either {@code points}, at least one element in {@code points}, {@code normals}, at least one element in {@code normals} or {@code type} are {@code null}
	 */
	public static List<Curve3D> createCurvesByBezier(final List<Point3D> points, final List<Vector3D> normals, final Type type, final double widthA, final double widthB, final int degree, final int splitDepth) {
		ParameterArguments.requireNonNullList(points, "points");
		ParameterArguments.requireNonNullList(normals, "normals");
		
		Objects.requireNonNull(type, "type == null");
		
		if(degree != 2 && degree != 3) {
			throw new IllegalArgumentException(String.format("Parameter argument degree must be 2 or 3: degree == %d", Integer.valueOf(degree)));
		}
		
		if((points.size() - 1 - degree) % degree != 0) {
			throw new IllegalArgumentException(String.format("Parameter argument points must contain %d + n * %d Point3F instances: points.size() == %d", Integer.valueOf(degree + 1), Integer.valueOf(degree), Integer.valueOf(points.size())));
		}
		
		final int segments = (points.size() - 1) / degree;
		
		switch(type) {
			case CYLINDER:
				ParameterArguments.requireExact(normals.size(), 0, "normals.size()");
				
				break;
			case FLAT:
				ParameterArguments.requireExact(normals.size(), 0, "normals.size()");
				
				break;
			case RIBBON:
				ParameterArguments.requireExact(normals.size(), segments + 1, "normals.size()");
				
				break;
			default:
				break;
		}
		
		final List<Curve3D> curves = new ArrayList<>();
		
		for(int segment = 0; segment < segments; segment++) {
			final int offset = segment * degree;
			
			if(degree == 2) {
				final Point3D pointA = points.get(offset + 0);
				final Point3D pointB = Point3D.lerp(points.get(offset + 0), points.get(offset + 1), 2.0D / 3.0D);
				final Point3D pointC = Point3D.lerp(points.get(offset + 1), points.get(offset + 2), 1.0D / 3.0D);
				final Point3D pointD = points.get(offset + 2);
				
				final Vector3D normalA = segment + 0 < normals.size() ? normals.get(segment + 0) : new Vector3D();
				final Vector3D normalB = segment + 1 < normals.size() ? normals.get(segment + 1) : new Vector3D();
				
				final double widthC = lerp(widthA, widthB, toDouble(segment + 0) / toDouble(segments));
				final double widthD = lerp(widthA, widthB, toDouble(segment + 1) / toDouble(segments));
				
				curves.addAll(createCurves(pointA, pointB, pointC, pointD, type, normalA, normalB, widthC, widthD, splitDepth));
			} else {
				final Point3D pointA = points.get(offset + 0);
				final Point3D pointB = points.get(offset + 1);
				final Point3D pointC = points.get(offset + 2);
				final Point3D pointD = points.get(offset + 3);
				
				final Vector3D normalA = segment + 0 < normals.size() ? normals.get(segment + 0) : new Vector3D();
				final Vector3D normalB = segment + 1 < normals.size() ? normals.get(segment + 1) : new Vector3D();
				
				final double widthC = lerp(widthA, widthB, toDouble(segment + 0) / toDouble(segments));
				final double widthD = lerp(widthA, widthB, toDouble(segment + 1) / toDouble(segments));
				
				curves.addAll(createCurves(pointA, pointB, pointC, pointD, type, normalA, normalB, widthC, widthD, splitDepth));
			}
		}
		
		return curves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code Data} stores common data for one or more {@link Curve3D} instances.
	 * <p>
	 * This class is immutable and therefore thread-safe.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static final class Data implements Node {
		private final Point3D pointA;
		private final Point3D pointB;
		private final Point3D pointC;
		private final Point3D pointD;
		private final Type type;
		private final Vector3D normalA;
		private final Vector3D normalB;
		private final double normalAngle;
		private final double normalAngleSinReciprocal;
		private final double widthA;
		private final double widthB;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code Data} instance.
		 * <p>
		 * If either {@code pointA}, {@code pointB}, {@code pointC}, {@code pointD}, {@code type}, {@code normalA} or {@code normalB} are {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param pointA a {@link Point3D} instance with the control point denoted by A
		 * @param pointB a {@code Point3D} instance with the control point denoted by B
		 * @param pointC a {@code Point3D} instance with the control point denoted by C
		 * @param pointD a {@code Point3D} instance with the control point denoted by D
		 * @param type the {@link Type} instance associated with this {@code Data} instance
		 * @param normalA a {@link Vector3D} instance with the normal denoted by A
		 * @param normalB a {@code Vector3D} instance with the normal denoted by B
		 * @param widthA the width denoted by A
		 * @param widthB the width denoted by B
		 * @throws NullPointerException thrown if, and only if, either {@code pointA}, {@code pointB}, {@code pointC}, {@code pointD}, {@code type}, {@code normalA} or {@code normalB} are {@code null}
		 */
		public Data(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final Type type, final Vector3D normalA, final Vector3D normalB, final double widthA, final double widthB) {
			this.pointA = Point3D.getCached(Objects.requireNonNull(pointA, "pointA == null"));
			this.pointB = Point3D.getCached(Objects.requireNonNull(pointB, "pointB == null"));
			this.pointC = Point3D.getCached(Objects.requireNonNull(pointC, "pointC == null"));
			this.pointD = Point3D.getCached(Objects.requireNonNull(pointD, "pointD == null"));
			this.type = Objects.requireNonNull(type, "type == null");
			this.normalA = Vector3D.getCached(Vector3D.normalize(Objects.requireNonNull(normalA, "normalA == null")));
			this.normalB = Vector3D.getCached(Vector3D.normalize(Objects.requireNonNull(normalB, "normalB == null")));
			this.normalAngle = acos(saturate(Vector3D.dotProduct(normalA, normalB)));
			this.normalAngleSinReciprocal = 1.0D / sin(this.normalAngle);
			this.widthA = widthA;
			this.widthB = widthB;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns a {@link Point3D} instance with the control point denoted by A.
		 * 
		 * @return a {@code Point3D} instance with the control point denoted by A
		 */
		public Point3D getPointA() {
			return this.pointA;
		}
		
		/**
		 * Returns a {@link Point3D} instance with the control point denoted by B.
		 * 
		 * @return a {@code Point3D} instance with the control point denoted by B
		 */
		public Point3D getPointB() {
			return this.pointB;
		}
		
		/**
		 * Returns a {@link Point3D} instance with the control point denoted by C.
		 * 
		 * @return a {@code Point3D} instance with the control point denoted by C
		 */
		public Point3D getPointC() {
			return this.pointC;
		}
		
		/**
		 * Returns a {@link Point3D} instance with the control point denoted by D.
		 * 
		 * @return a {@code Point3D} instance with the control point denoted by D
		 */
		public Point3D getPointD() {
			return this.pointD;
		}
		
		/**
		 * Returns a {@code String} representation of this {@code Data} instance.
		 * 
		 * @return a {@code String} representation of this {@code Data} instance
		 */
		@Override
		public String toString() {
			return String.format("new Data(%s, %s, %s, %s, %s, %s, %s, %+.10f, %+.10f)", this.pointA, this.pointB, this.pointC, this.pointD, this.type, this.normalA, this.normalB, Double.valueOf(this.widthA), Double.valueOf(this.widthB));
		}
		
		/**
		 * Returns the {@link Type} instance associated with this {@code Data} instance.
		 * 
		 * @return the {@code Type} instance associated with this {@code Data} instance
		 */
		public Type getType() {
			return this.type;
		}
		
		/**
		 * Returns a {@link Vector3D} instance with the normal denoted by A.
		 * 
		 * @return a {@code Vector3D} instance with the normal denoted by A
		 */
		public Vector3D getNormalA() {
			return this.normalA;
		}
		
		/**
		 * Returns a {@link Vector3D} instance with the normal denoted by B.
		 * 
		 * @return a {@code Vector3D} instance with the normal denoted by B
		 */
		public Vector3D getNormalB() {
			return this.normalB;
		}
		
		/**
		 * Accepts a {@link NodeHierarchicalVisitor}.
		 * <p>
		 * Returns the result of {@code nodeHierarchicalVisitor.visitLeave(this)}.
		 * <p>
		 * If {@code nodeHierarchicalVisitor} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If a {@code RuntimeException} is thrown by the current {@code NodeHierarchicalVisitor}, a {@code NodeTraversalException} will be thrown with the {@code RuntimeException} wrapped.
		 * <p>
		 * This implementation will:
		 * <ul>
		 * <li>throw a {@code NullPointerException} if {@code nodeHierarchicalVisitor} is {@code null}.</li>
		 * <li>throw a {@code NodeTraversalException} if {@code nodeHierarchicalVisitor} throws a {@code RuntimeException}.</li>
		 * <li>traverse its child {@code Node} instances.</li>
		 * </ul>
		 * 
		 * @param nodeHierarchicalVisitor the {@code NodeHierarchicalVisitor} to accept
		 * @return the result of {@code nodeHierarchicalVisitor.visitLeave(this)}
		 * @throws NodeTraversalException thrown if, and only if, a {@code RuntimeException} is thrown by the current {@code NodeHierarchicalVisitor}
		 * @throws NullPointerException thrown if, and only if, {@code nodeHierarchicalVisitor} is {@code null}
		 */
		@Override
		public boolean accept(final NodeHierarchicalVisitor nodeHierarchicalVisitor) {
			Objects.requireNonNull(nodeHierarchicalVisitor, "nodeHierarchicalVisitor == null");
			
			try {
				if(nodeHierarchicalVisitor.visitEnter(this)) {
					if(!this.pointA.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
					
					if(!this.pointB.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
					
					if(!this.pointC.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
					
					if(!this.pointD.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
					
					if(!this.normalA.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
					
					if(!this.normalB.accept(nodeHierarchicalVisitor)) {
						return nodeHierarchicalVisitor.visitLeave(this);
					}
				}
				
				return nodeHierarchicalVisitor.visitLeave(this);
			} catch(final RuntimeException e) {
				throw new NodeTraversalException(e);
			}
		}
		
		/**
		 * Compares {@code object} to this {@code Data} instance for equality.
		 * <p>
		 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Data}, and their respective values are equal, {@code false} otherwise.
		 * 
		 * @param object the {@code Object} to compare to this {@code Data} instance for equality
		 * @return {@code true} if, and only if, {@code object} is an instance of {@code Data}, and their respective values are equal, {@code false} otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			} else if(!(object instanceof Data)) {
				return false;
			} else if(!Objects.equals(this.pointA, Data.class.cast(object).pointA)) {
				return false;
			} else if(!Objects.equals(this.pointB, Data.class.cast(object).pointB)) {
				return false;
			} else if(!Objects.equals(this.pointC, Data.class.cast(object).pointC)) {
				return false;
			} else if(!Objects.equals(this.pointD, Data.class.cast(object).pointD)) {
				return false;
			} else if(!Objects.equals(this.type, Data.class.cast(object).type)) {
				return false;
			} else if(!Objects.equals(this.normalA, Data.class.cast(object).normalA)) {
				return false;
			} else if(!Objects.equals(this.normalB, Data.class.cast(object).normalB)) {
				return false;
			} else if(!equal(this.normalAngle, Data.class.cast(object).normalAngle)) {
				return false;
			} else if(!equal(this.normalAngleSinReciprocal, Data.class.cast(object).normalAngleSinReciprocal)) {
				return false;
			} else if(!equal(this.widthA, Data.class.cast(object).widthA)) {
				return false;
			} else if(!equal(this.widthB, Data.class.cast(object).widthB)) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Returns the angle of the normal.
		 * 
		 * @return the angle of the normal
		 */
		public double getNormalAngle() {
			return this.normalAngle;
		}
		
		/**
		 * Returns the reciprocal (or inverse) sine of the angle of the normal.
		 * 
		 * @return the reciprocal (or inverse) sine of the angle of the normal
		 */
		public double getNormalAngleSinReciprocal() {
			return this.normalAngleSinReciprocal;
		}
		
		/**
		 * Returns the width denoted by A.
		 * 
		 * @return the width denoted by A
		 */
		public double getWidthA() {
			return this.widthA;
		}
		
		/**
		 * Returns the width denoted by B.
		 * 
		 * @return the width denoted by B
		 */
		public double getWidthB() {
			return this.widthB;
		}
		
		/**
		 * Returns a hash code for this {@code Data} instance.
		 * 
		 * @return a hash code for this {@code Data} instance
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.pointA, this.pointB, this.pointC, this.pointD, this.type, this.normalA, this.normalB, Double.valueOf(this.normalAngle), Double.valueOf(this.normalAngleSinReciprocal), Double.valueOf(this.widthA), Double.valueOf(this.widthB));
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code Type} contains type information about one or more {@link Curve3D} instances.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static enum Type {
		/**
		 * A {@code Type} instance used to represent a {@link Curve3D} instance that looks like a cylinder.
		 */
		CYLINDER,
		
		/**
		 * A {@code Type} instance used to represent a {@link Curve3D} instance that is flat.
		 */
		FLAT,
		
		/**
		 * A {@code Type} instance used to represent a {@link Curve3D} instance that looks like a ribbon.
		 */
		RIBBON;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private Type() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns a {@code String} representation of this {@code Type} instance.
		 * 
		 * @return a {@code String} representation of this {@code Type} instance
		 */
		@Override
		public String toString() {
			switch(this) {
				case CYLINDER:
					return "Type.CYLINDER";
				case FLAT:
					return "Type.FLAT";
				case RIBBON:
					return "Type.RIBBON";
				default:
					return "";
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Optional<SurfaceIntersection3D> doIntersectionRecursive(final Ray3D ray, final double tMinimum, final double tMaximum, final Matrix44D objectToRay, final Matrix44D rayToObject, final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final double uMinimum, final double uMaximum, final int depth) {
		final Data data = this.data;
		
		final double rayDirectionLength = ray.getDirection().length();
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		
		if(depth > 0) {
			final Point3D[] points = doBezierSubdivide(pointA, pointB, pointC, pointD);
			
			final double uA = uMinimum;
			final double uB = (uMinimum + uMaximum) / 2.0D;
			final double uC = uMaximum;
			
			final double widthC = max(lerp(widthA, widthB, uA), lerp(widthA, widthB, uB)) * 0.5D;
			final double widthD = max(lerp(widthA, widthB, uB), lerp(widthA, widthB, uC)) * 0.5D;
			
			final double xMaximum = 0.0D;
			final double yMaximum = 0.0D;
			final double zMaximum = rayDirectionLength * tMaximum;
			
			final boolean isInsideAX = max(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()) + widthC >= tMinimum && min(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()) - widthC <= xMaximum;
			final boolean isInsideAY = max(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()) + widthC >= tMinimum && min(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()) - widthC <= yMaximum;
			final boolean isInsideAZ = max(points[0].getZ(), points[1].getZ(), points[2].getZ(), points[3].getZ()) + widthC >= tMinimum && min(points[0].getZ(), points[1].getZ(), points[2].getZ(), points[3].getZ()) - widthC <= zMaximum;
			final boolean isInsideBX = max(points[3].getX(), points[4].getX(), points[5].getX(), points[6].getX()) + widthD >= tMinimum && min(points[3].getX(), points[4].getX(), points[5].getX(), points[6].getX()) - widthD <= xMaximum;
			final boolean isInsideBY = max(points[3].getY(), points[4].getY(), points[5].getY(), points[6].getY()) + widthD >= tMinimum && min(points[3].getY(), points[4].getY(), points[5].getY(), points[6].getY()) - widthD <= yMaximum;
			final boolean isInsideBZ = max(points[3].getZ(), points[4].getZ(), points[5].getZ(), points[6].getZ()) + widthD >= tMinimum && min(points[3].getZ(), points[4].getZ(), points[5].getZ(), points[6].getZ()) - widthD <= zMaximum;
			
			if(isInsideAX && isInsideAY && isInsideAZ) {
				final Optional<SurfaceIntersection3D> optionalSurfaceIntersection = doIntersectionRecursive(ray, tMinimum, tMaximum, objectToRay, rayToObject, points[0], points[1], points[2], points[3], uA, uB, depth - 1);
				
				if(optionalSurfaceIntersection.isPresent()) {
					return optionalSurfaceIntersection;
				}
			}
			
			if(isInsideBX && isInsideBY && isInsideBZ) {
				final Optional<SurfaceIntersection3D> optionalSurfaceIntersection = doIntersectionRecursive(ray, tMinimum, tMaximum, objectToRay, rayToObject, points[3], points[4], points[5], points[6], uB, uC, depth - 1);
				
				if(optionalSurfaceIntersection.isPresent()) {
					return optionalSurfaceIntersection;
				}
			}
			
			return SurfaceIntersection3D.EMPTY;
		}
		
		final double edgeA = (pointB.getY() - pointA.getY()) * -pointA.getY() + pointA.getX() * (pointA.getX() - pointB.getX());
		final double edgeB = (pointC.getY() - pointD.getY()) * -pointD.getY() + pointD.getX() * (pointD.getX() - pointC.getX());
		
		if(edgeA < 0.0D || edgeB < 0.0D) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		final Vector2D segmentDirection = new Vector2D(pointD.getX() - pointA.getX(), pointD.getY() - pointA.getY());
		
		final double denominator = segmentDirection.lengthSquared();
		
		if(isZero(denominator)) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		final double w = Vector2D.dotProduct(Vector2D.negate(new Vector2D(pointA.getX(), pointA.getY())), segmentDirection) / denominator;
		final double u = saturate(lerp(uMinimum, uMaximum, w), uMinimum, uMaximum);
		final double hitWidth = doComputeHitWidth(data, ray, u);
		
		final Point3D point = doBezierEvaluate(pointA, pointB, pointC, pointD, saturate(w));
		
		final Vector3D derivative = doBezierEvaluateDerivative(pointA, pointB, pointC, pointD, saturate(w));
		
		final double pointCurveDistanceSquared = point.getX() * point.getX() + point.getY() * point.getY();
		
		if(pointCurveDistanceSquared > hitWidth * hitWidth * 0.25D) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		final double zMaximum = rayDirectionLength * tMaximum;
		
		if(point.getZ() < tMinimum || point.getZ() > zMaximum) {
			return SurfaceIntersection3D.EMPTY;
		}
		
		final double pointCurveDistance = sqrt(pointCurveDistanceSquared);
		final double edgeFunction = derivative.getX() * -point.getY() + point.getX() * derivative.getY();
		final double v = edgeFunction > 0.0D ? 0.5D + pointCurveDistance / hitWidth : 0.5D - pointCurveDistance / hitWidth;
		final double t = point.getZ() / rayDirectionLength;
		
		final OrthonormalBasis33D orthonormalBasisG = doComputeOrthonormalBasisG(data, objectToRay, rayToObject, hitWidth, u, v);
		final OrthonormalBasis33D orthonormalBasisS = orthonormalBasisG;
		
		final Point2D textureCoordinates = new Point2D(u, v);
		
		final Point3D surfaceIntersectionPoint = Point3D.add(ray.getOrigin(), ray.getDirection(), t);
		
		final Vector3D surfaceIntersectionPointError = new Vector3D(2.0D * hitWidth);
		
		return Optional.of(new SurfaceIntersection3D(orthonormalBasisG, orthonormalBasisS, textureCoordinates, surfaceIntersectionPoint, ray, this, surfaceIntersectionPointError, t));
	}
	
	private double doIntersectionTRecursive(final Ray3D ray, final double tMinimum, final double tMaximum, final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final double uMinimum, final double uMaximum, final int depth) {
		final Data data = this.data;
		
		final double rayDirectionLength = ray.getDirection().length();
		
		final double widthA = data.getWidthA();
		final double widthB = data.getWidthB();
		
		if(depth > 0) {
			final Point3D[] points = doBezierSubdivide(pointA, pointB, pointC, pointD);
			
			final double uA = uMinimum;
			final double uB = (uMinimum + uMaximum) / 2.0D;
			final double uC = uMaximum;
			
			final double widthC = max(lerp(widthA, widthB, uA), lerp(widthA, widthB, uB)) * 0.5D;
			final double widthD = max(lerp(widthA, widthB, uB), lerp(widthA, widthB, uC)) * 0.5D;
			
			final double xMaximum = 0.0D;
			final double yMaximum = 0.0D;
			final double zMaximum = rayDirectionLength * tMaximum;
			
			final boolean isInsideAX = max(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()) + widthC >= tMinimum && min(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()) - widthC <= xMaximum;
			final boolean isInsideAY = max(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()) + widthC >= tMinimum && min(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()) - widthC <= yMaximum;
			final boolean isInsideAZ = max(points[0].getZ(), points[1].getZ(), points[2].getZ(), points[3].getZ()) + widthC >= tMinimum && min(points[0].getZ(), points[1].getZ(), points[2].getZ(), points[3].getZ()) - widthC <= zMaximum;
			final boolean isInsideBX = max(points[3].getX(), points[4].getX(), points[5].getX(), points[6].getX()) + widthD >= tMinimum && min(points[3].getX(), points[4].getX(), points[5].getX(), points[6].getX()) - widthD <= xMaximum;
			final boolean isInsideBY = max(points[3].getY(), points[4].getY(), points[5].getY(), points[6].getY()) + widthD >= tMinimum && min(points[3].getY(), points[4].getY(), points[5].getY(), points[6].getY()) - widthD <= yMaximum;
			final boolean isInsideBZ = max(points[3].getZ(), points[4].getZ(), points[5].getZ(), points[6].getZ()) + widthD >= tMinimum && min(points[3].getZ(), points[4].getZ(), points[5].getZ(), points[6].getZ()) - widthD <= zMaximum;
			
			if(isInsideAX && isInsideAY && isInsideAZ) {
				final double t = doIntersectionTRecursive(ray, tMinimum, tMaximum, points[0], points[1], points[2], points[3], uA, uB, depth - 1);
				
				if(!isNaN(t)) {
					return t;
				}
			}
			
			if(isInsideBX && isInsideBY && isInsideBZ) {
				final double t = doIntersectionTRecursive(ray, tMinimum, tMaximum, points[3], points[4], points[5], points[6], uB, uC, depth - 1);
				
				if(!isNaN(t)) {
					return t;
				}
			}
			
			return Double.NaN;
		}
		
		final double edgeA = (pointB.getY() - pointA.getY()) * -pointA.getY() + pointA.getX() * (pointA.getX() - pointB.getX());
		final double edgeB = (pointC.getY() - pointD.getY()) * -pointD.getY() + pointD.getX() * (pointD.getX() - pointC.getX());
		
		if(edgeA < 0.0D || edgeB < 0.0D) {
			return Double.NaN;
		}
		
		final Vector2D segmentDirection = new Vector2D(pointD.getX() - pointA.getX(), pointD.getY() - pointA.getY());
		
		final double denominator = segmentDirection.lengthSquared();
		
		if(isZero(denominator)) {
			return Double.NaN;
		}
		
		final double w = Vector2D.dotProduct(Vector2D.negate(new Vector2D(pointA.getX(), pointA.getY())), segmentDirection) / denominator;
		final double u = saturate(lerp(uMinimum, uMaximum, w), uMinimum, uMaximum);
		final double hitWidth = doComputeHitWidth(data, ray, u);
		
		final Point3D point = doBezierEvaluate(pointA, pointB, pointC, pointD, saturate(w));
		
		final double pointCurveDistanceSquared = point.getX() * point.getX() + point.getY() * point.getY();
		
		if(pointCurveDistanceSquared > hitWidth * hitWidth * 0.25D) {
			return Double.NaN;
		}
		
		final double zMaximum = rayDirectionLength * tMaximum;
		
		if(point.getZ() < tMinimum || point.getZ() > zMaximum) {
			return Double.NaN;
		}
		
		final double t = point.getZ() / rayDirectionLength;
		
		return t;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static OrthonormalBasis33D doComputeOrthonormalBasisG(final Data data, final Matrix44D objectToRay, final Matrix44D rayToObject, final double hitWidth, final double u, final double v) {
		switch(data.getType()) {
			case CYLINDER: {
				final Vector3D directionU = doBezierEvaluateDerivative(data.getPointA(), data.getPointB(), data.getPointC(), data.getPointD(), u);
				final Vector3D directionUPlane = Vector3D.transform(objectToRay, directionU);
				final Vector3D directionVPlane = Vector3D.transform(Matrix44D.rotate(AngleD.degrees(-lerp(-90.0D, 90.0D, v), -90.0D, 90.0D), directionUPlane), Vector3D.multiply(Vector3D.normalize(new Vector3D(-directionUPlane.getY(), directionUPlane.getX(), 0.0D)), hitWidth));
				final Vector3D directionV = Vector3D.transform(rayToObject, directionVPlane);
				final Vector3D directionW = Vector3D.normalize(Vector3D.crossProduct(directionU, directionV));
				
				return new OrthonormalBasis33D(directionW, directionV, directionU);
			}
			case FLAT: {
				final Vector3D directionU = doBezierEvaluateDerivative(data.getPointA(), data.getPointB(), data.getPointC(), data.getPointD(), u);
				final Vector3D directionUPlane = Vector3D.transform(objectToRay, directionU);
				final Vector3D directionVPlane = Vector3D.multiply(Vector3D.normalize(new Vector3D(-directionUPlane.getY(), directionUPlane.getX(), 0.0D)), hitWidth);
				final Vector3D directionV = Vector3D.transform(rayToObject, directionVPlane);
				final Vector3D directionW = Vector3D.normalize(Vector3D.crossProduct(directionU, directionV));
				
				return new OrthonormalBasis33D(directionW, directionV, directionU);
			}
			case RIBBON: {
				final double sinA = sin((1.0D - u) * data.getNormalAngle()) * data.getNormalAngleSinReciprocal();
				final double sinB = sin(u * data.getNormalAngle()) * data.getNormalAngleSinReciprocal();
				
				final Vector3D normal = Vector3D.add(Vector3D.multiply(data.getNormalA(), sinA), Vector3D.multiply(data.getNormalB(), sinB));
				
				final Vector3D directionU = doBezierEvaluateDerivative(data.getPointA(), data.getPointB(), data.getPointC(), data.getPointD(), u);
				final Vector3D directionV = Vector3D.multiply(Vector3D.normalize(Vector3D.crossProduct(normal, directionU)), hitWidth);
				final Vector3D directionW = Vector3D.normalize(Vector3D.crossProduct(directionU, directionV));
				
				return new OrthonormalBasis33D(directionW, directionV, directionU);
			}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}
	
	private static Point3D doBezierBlossom(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final double t1, final double t2, final double t3) {
		final Point3D pointAB = Point3D.lerp(pointA, pointB, t1);
		final Point3D pointBC = Point3D.lerp(pointB, pointC, t1);
		final Point3D pointCD = Point3D.lerp(pointC, pointD, t1);
		
		final Point3D pointABBC = Point3D.lerp(pointAB, pointBC, t2);
		final Point3D pointBCCD = Point3D.lerp(pointBC, pointCD, t2);
		
		return Point3D.lerp(pointABBC, pointBCCD, t3);
	}
	
	private static Point3D doBezierEvaluate(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final double t) {
		final Point3D pointAB = Point3D.lerp(pointA, pointB, t);
		final Point3D pointBC = Point3D.lerp(pointB, pointC, t);
		final Point3D pointCD = Point3D.lerp(pointC, pointD, t);
		
		final Point3D pointABBC = Point3D.lerp(pointAB, pointBC, t);
		final Point3D pointBCCD = Point3D.lerp(pointBC, pointCD, t);
		
		return Point3D.lerp(pointABBC, pointBCCD, t);
	}
	
	private static Point3D[] doBezierSubdivide(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD) {
		return new Point3D[] {
			pointA,
			Point3D.centroid(pointA, pointB),
			Point3D.centroid(pointA, pointB, pointB, pointC),
			Point3D.centroid(pointA, pointB, pointB, pointB, pointC, pointC, pointC, pointD),
			Point3D.centroid(pointB, pointC, pointC, pointD),
			Point3D.centroid(pointC, pointD),
			pointD
		};
	}
	
	private static Vector3D doBezierEvaluateDerivative(final Point3D pointA, final Point3D pointB, final Point3D pointC, final Point3D pointD, final double t) {
		final Point3D pointAB = Point3D.lerp(pointA, pointB, t);
		final Point3D pointBC = Point3D.lerp(pointB, pointC, t);
		final Point3D pointCD = Point3D.lerp(pointC, pointD, t);
		
		final Point3D pointABBC = Point3D.lerp(pointAB, pointBC, t);
		final Point3D pointBCCD = Point3D.lerp(pointBC, pointCD, t);
		
		final Vector3D direction = Vector3D.direction(pointABBC, pointBCCD);
		
		if(direction.lengthSquared() > 0.0D) {
			return Vector3D.multiply(direction, 3.0D);
		}
		
		return Vector3D.direction(pointA, pointD);
	}
	
	private static Vector3D doCreateDirectionX(final Ray3D ray, final Point3D eye, final Point3D lookAt) {
		final Vector3D directionR = ray.getDirection();
		final Vector3D directionX = Vector3D.crossProduct(directionR, Vector3D.direction(eye, lookAt));
		
		if(!isZero(directionX.lengthSquared())) {
			return directionX;
		} else if(abs(directionR.getX()) > abs(directionR.getY())) {
			return Vector3D.normalize(new Vector3D(-directionR.getZ(), 0.0D, directionR.getX()));
		} else {
			return Vector3D.normalize(new Vector3D(0.0D, directionR.getZ(), -directionR.getY()));
		}
	}
	
	private static double doComputeHitWidth(final Data data, final Ray3D ray, final double u) {
		if(data.getType() == Type.RIBBON) {
			final double sinA = sin((1.0D - u) * data.getNormalAngle()) * data.getNormalAngleSinReciprocal();
			final double sinB = sin(u * data.getNormalAngle()) * data.getNormalAngleSinReciprocal();
			
			final Vector3D normal = Vector3D.add(Vector3D.multiply(data.getNormalA(), sinA), Vector3D.multiply(data.getNormalB(), sinB));
			
			return lerp(data.getWidthA(), data.getWidthB(), u) * (abs(Vector3D.dotProduct(normal, ray.getDirection())) / ray.getDirection().length());
		}
		
		return lerp(data.getWidthA(), data.getWidthB(), u);
	}
	
	private static int doLog2(final double value) {
		if(value < 1.0D) {
			return 0;
		}
		
		final int bits = Float.floatToIntBits((float)(value));
		
		return (bits >> 23) - 127 + ((bits & (1 << 22)) != 0 ? 1 : 0);
	}
}