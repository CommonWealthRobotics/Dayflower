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
package org.dayflower.renderer.gpu;

import static org.dayflower.util.Floats.PI_MULTIPLIED_BY_2;
import static org.dayflower.util.Floats.max;
import static org.dayflower.util.Floats.min;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.dayflower.image.Color3F;
import org.dayflower.image.Image;
import org.dayflower.renderer.Renderer;
import org.dayflower.renderer.RendererConfiguration;
import org.dayflower.renderer.RendererObserver;
import org.dayflower.scene.Camera;
import org.dayflower.scene.Scene;
import org.dayflower.util.Floats;
import org.dayflower.util.Longs;
import org.dayflower.util.Timer;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * An {@code AbstractGPURenderer} is an abstract implementation of {@link Renderer} that takes care of most aspects.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractGPURenderer extends Kernel implements Renderer {
	private static final float PRNG_NEXT_FLOAT_RECIPROCAL = 1.0F / (1 << 24);
	private static final long PRNG_ADDEND = 0xBL;
	private static final long PRNG_MASK = (1L << 48L) - 1L;
	private static final long PRNG_MULTIPLIER = 0x5DEECE66DL;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected float[] cameraArray;
	protected float[] matrix44FArray;
	protected float[] pixelArray;
	protected float[] radianceArray;
	protected float[] ray3FArray_$private$8;
	protected float[] sphere3FArray;
	protected int resolutionX;
	protected int resolutionY;
	protected int[] primitiveArray;
	protected long[] seedArray;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean isClearing;
	private final AtomicBoolean isRendering;
	private final AtomicReference<RendererConfiguration> rendererConfiguration;
	private final AtomicReference<RendererObserver> rendererObserver;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractGPURenderer} instance.
	 * <p>
	 * If either {@code rendererConfiguration} or {@code rendererObserver} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param rendererConfiguration the {@link RendererConfiguration} instance associated with this {@code AbstractGPURenderer} instance
	 * @param rendererObserver the {@link RendererObserver} instance associated with this {@code AbstractGPURenderer} instance
	 * @throws NullPointerException thrown if, and only if, either {@code rendererConfiguration} or {@code rendererObserver} are {@code null}
	 */
	protected AbstractGPURenderer(final RendererConfiguration rendererConfiguration, final RendererObserver rendererObserver) {
		this.cameraArray = new float[0];
		this.matrix44FArray = new float[0];
		this.pixelArray = new float[0];
		this.radianceArray = new float[0];
		this.ray3FArray_$private$8 = new float[8];
		this.sphere3FArray = new float[0];
		this.resolutionX = 0;
		this.resolutionY = 0;
		this.primitiveArray = new int[0];
		this.seedArray = new long[0];
		this.isClearing = new AtomicBoolean();
		this.isRendering = new AtomicBoolean();
		this.rendererConfiguration = new AtomicReference<>(Objects.requireNonNull(rendererConfiguration, "rendererConfiguration == null"));
		this.rendererObserver = new AtomicReference<>(Objects.requireNonNull(rendererObserver, "rendererObserver == null"));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link RendererConfiguration} instance associated with this {@code AbstractGPURenderer} instance.
	 * 
	 * @return the {@code RendererConfiguration} instance associated with this {@code AbstractGPURenderer} instance
	 */
	@Override
	public final RendererConfiguration getRendererConfiguration() {
		return this.rendererConfiguration.get();
	}
	
	/**
	 * Returns the {@link RendererObserver} instance associated with this {@code AbstractGPURenderer} instance.
	 * 
	 * @return the {@code RendererObserver} instance associated with this {@code AbstractGPURenderer} instance
	 */
	@Override
	public final RendererObserver getRendererObserver() {
		return this.rendererObserver.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code AbstractGPURenderer} instance is clearing the {@link Image} instance in the next {@link #render()} call, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code AbstractGPURenderer} instance is clearing the {@code Image} instance in the next {@code  render()} call, {@code false} otherwise
	 */
	@Override
	public final boolean isClearing() {
		return this.isClearing.get();
	}
	
	/**
	 * Renders the associated {@link Scene} instance to the associated {@link Image} instance and, optionally, updates the associated {@link Display} instance.
	 * <p>
	 * Returns {@code true} if, and only if, rendering was performed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, rendering was performed, {@code false} otherwise
	 */
	@Override
	public final boolean render() {
		this.isRendering.set(true);
		
		final RendererConfiguration rendererConfiguration = getRendererConfiguration();
		
		final RendererObserver rendererObserver = getRendererObserver();
		
		final Image image = rendererConfiguration.getImage();
		
		final Timer timer = rendererConfiguration.getTimer();
		
		final int renderPasses = rendererConfiguration.getRenderPasses();
		final int renderPassesPerDisplayUpdate = rendererConfiguration.getRenderPassesPerDisplayUpdate();
		final int resolutionX = image.getResolutionX();
		final int resolutionY = image.getResolutionY();
		
		final Range range = Range.create(resolutionX * resolutionY);
		
		for(int renderPass = 1; renderPass <= renderPasses; renderPass++) {
			if(this.isClearing.compareAndSet(true, false)) {
				rendererConfiguration.setRenderPass(0);
				rendererConfiguration.setRenderTime(0L);
				
				image.filmClear();
				image.filmRender();
				
				rendererObserver.onRenderDisplay(this, image);
				
				timer.restart();
			}
			
			rendererObserver.onRenderPassProgress(this, renderPass, renderPasses, 0.0D);
			
			final long currentTimeMillis = System.currentTimeMillis();
			
			execute(range);
			
			final long elapsedTimeMillis = System.currentTimeMillis() - currentTimeMillis;
			
			final float[] pixelArray = doGetPixelArray();
			final float[] radianceArray = doGetRadianceArray();
			
//			TODO: Fix the following bottleneck using the Kernel itself in a different mode! It taks around 200 milliseconds per render pass.
			for(int y = 0; y < resolutionY; y++) {
				for(int x = 0; x < resolutionX; x++) {
					final int index = y * resolutionX + x;
					final int indexPixelArray = index * 2;
					final int indexRadianceArray = index * 3;
					
					final float r = radianceArray[indexRadianceArray + 0];
					final float g = radianceArray[indexRadianceArray + 1];
					final float b = radianceArray[indexRadianceArray + 2];
					
					final float imageX = x;
					final float imageY = y;
					final float pixelX = pixelArray[indexPixelArray + 0];
					final float pixelY = pixelArray[indexPixelArray + 1];
					
					final Color3F colorRGB = new Color3F(r, g, b);
					final Color3F colorXYZ = Color3F.convertRGBToXYZUsingPBRT(colorRGB);
					
					if(!colorXYZ.hasInfinites() && !colorXYZ.hasNaNs()) {
						image.filmAddColorXYZ(imageX + pixelX, imageY + pixelY, colorXYZ);
					}
				}
			}
			
			rendererObserver.onRenderPassProgress(this, renderPass, renderPasses, 1.0D);
			
			if(renderPass == 1 || renderPass % renderPassesPerDisplayUpdate == 0 || renderPass == renderPasses) {
				image.filmRender();
				
				rendererObserver.onRenderDisplay(this, image);
			}
			
			rendererConfiguration.setRenderPass(rendererConfiguration.getRenderPass() + 1);
			rendererConfiguration.setRenderTime(elapsedTimeMillis);
			
			rendererObserver.onRenderPassComplete(this, renderPass, renderPasses, elapsedTimeMillis);
		}
		
		this.isRendering.set(false);
		
		return true;
	}
	
	/**
	 * Attempts to shutdown the rendering process of this {@code AbstractGPURenderer} instance.
	 * <p>
	 * Returns {@code true} if, and only if, this {@code AbstractGPURenderer} instance was rendering and is shutting down, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code AbstractGPURenderer} instance was rendering and is shutting down, {@code false} otherwise
	 */
	@Override
	public final boolean renderShutdown() {
		return this.isRendering.compareAndSet(true, false);
	}
	
	/**
	 * Call this method to clear the {@link Image} in the next {@link #render()} call.
	 */
	@Override
	public final void clear() {
		this.isClearing.set(true);
	}
	
	/**
	 * Disposes of any resources created by this {@code AbstractGPURenderer} instance.
	 */
	@Override
	public final synchronized void dispose() {
		super.dispose();
	}
	
	/**
	 * Sets the {@link RendererConfiguration} instance associated with this {@code AbstractGPURenderer} instance to {@code rendererConfiguration}.
	 * <p>
	 * If {@code rendererConfiguration} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param rendererConfiguration the {@code RendererConfiguration} instance associated with this {@code AbstractGPURenderer} instance
	 * @throws NullPointerException thrown if, and only if, {@code rendererConfiguration} is {@code null}
	 */
	@Override
	public final void setRendererConfiguration(final RendererConfiguration rendererConfiguration) {
		this.rendererConfiguration.set(Objects.requireNonNull(rendererConfiguration, "rendererConfiguration == null"));
	}
	
	/**
	 * Sets the {@link RendererObserver} instance associated with this {@code AbstractGPURenderer} instance to {@code rendererObserver}.
	 * <p>
	 * If {@code rendererObserver} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param rendererObserver the {@code RendererObserver} instance associated with this {@code AbstractGPURenderer} instance
	 * @throws NullPointerException thrown if, and only if, {@code rendererObserver} is {@code null}
	 */
	@Override
	public final void setRendererObserver(final RendererObserver rendererObserver) {
		this.rendererObserver.set(Objects.requireNonNull(rendererObserver, "rendererObserver == null"));
	}
	
	/**
	 * Sets up all necessary resources for this {@code AbstractGPURenderer} instance.
	 */
	@Override
	public final void setup() {
		setExplicit(true);
		
		doSetupPixelArray();
		doSetupRadianceArray();
		doSetupResolution();
		doSetupScene();
		doSetupSeedArray();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	protected final boolean cameraGenerateRay() {
//		Retrieve the image coordinates:
		final float imageX = getGlobalId() % this.resolutionX;
		final float imageY = getGlobalId() / this.resolutionX;
		
//		Retrieve the pixel coordinates:
		final float pixelX = random();
		final float pixelY = random();
		
//		Retrieve all values from the 'cameraArray' in the correct order:
		final float fieldOfViewX = tan(+this.cameraArray[Camera.ARRAY_OFFSET_FIELD_OF_VIEW_X] * 0.5F);
		final float fieldOfViewY = tan(-this.cameraArray[Camera.ARRAY_OFFSET_FIELD_OF_VIEW_Y] * 0.5F);
		final float lens = this.cameraArray[Camera.ARRAY_OFFSET_LENS];
		final float uX = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_U + 0];
		final float uY = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_U + 1];
		final float uZ = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_U + 2];
		final float vX = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_V + 0];
		final float vY = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_V + 1];
		final float vZ = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_V + 2];
		final float wX = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_W + 0];
		final float wY = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_W + 1];
		final float wZ = this.cameraArray[Camera.ARRAY_OFFSET_ORTHONORMAL_BASIS_W + 2];
		final float eyeX = this.cameraArray[Camera.ARRAY_OFFSET_EYE + 0];
		final float eyeY = this.cameraArray[Camera.ARRAY_OFFSET_EYE + 1];
		final float eyeZ = this.cameraArray[Camera.ARRAY_OFFSET_EYE + 2];
		final float apertureRadius = this.cameraArray[Camera.ARRAY_OFFSET_APERTURE_RADIUS];
		final float focalDistance = this.cameraArray[Camera.ARRAY_OFFSET_FOCAL_DISTANCE];
		final float resolutionX = this.cameraArray[Camera.ARRAY_OFFSET_RESOLUTION_X];
		final float resolutionY = this.cameraArray[Camera.ARRAY_OFFSET_RESOLUTION_Y];
		
//		Compute the camera coordinates:
		final float cameraX = 2.0F * ((imageX + pixelX) / (resolutionX - 1.0F)) - 1.0F;
		final float cameraY = 2.0F * ((imageY + pixelY) / (resolutionY - 1.0F)) - 1.0F;
		
//		Compute the 'wFactor' that is used for the fisheye lens:
		float wFactor = 1.0F;
		
		if(lens == 0.0F) {
			final float dotProduct = cameraX * cameraX + cameraY * cameraY;
			
			if(dotProduct > 1.0F) {
				return false;
			}
			
			wFactor = sqrt(1.0F - dotProduct);
		}
		
//		Sample the disk with a uniform distribution:
		final float u = random();
		final float v = random();
		final float r = sqrt(u);
		final float theta = PI_MULTIPLIED_BY_2 * v;
		final float diskX = r * cos(theta);
		final float diskY = r * sin(theta);
		
//		Compute the point on the plane one unit away from the eye:
		final float pointOnPlaneOneUnitAwayFromEyeX = (uX * fieldOfViewX * cameraX) + (vX * fieldOfViewY * cameraY) + (eyeX + wX * wFactor);
		final float pointOnPlaneOneUnitAwayFromEyeY = (uY * fieldOfViewX * cameraX) + (vY * fieldOfViewY * cameraY) + (eyeY + wY * wFactor);
		final float pointOnPlaneOneUnitAwayFromEyeZ = (uZ * fieldOfViewX * cameraX) + (vZ * fieldOfViewY * cameraY) + (eyeZ + wZ * wFactor);
		
//		Compute the point on the image plane:
		final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;
		final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;
		final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;
		
//		Compute the ray origin:
		final float originX = apertureRadius > 0.00001F ? eyeX + ((uX * diskX * apertureRadius) + (vX * diskY * apertureRadius)) : eyeX;
		final float originY = apertureRadius > 0.00001F ? eyeY + ((uY * diskX * apertureRadius) + (vY * diskY * apertureRadius)) : eyeY;
		final float originZ = apertureRadius > 0.00001F ? eyeZ + ((uZ * diskX * apertureRadius) + (vZ * diskY * apertureRadius)) : eyeZ;
		
//		Compute the ray direction:
		final float directionX = pointOnImagePlaneX - originX;
		final float directionY = pointOnImagePlaneY - originY;
		final float directionZ = pointOnImagePlaneZ - originZ;
		final float directionLengthReciprocal = rsqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
		final float directionNormalizedX = directionX * directionLengthReciprocal;
		final float directionNormalizedY = directionY * directionLengthReciprocal;
		final float directionNormalizedZ = directionZ * directionLengthReciprocal;
		
//		Compute offsets:
		final int pixelArrayOffset = getGlobalId() * 2;
		
//		Fill in the ray array:
		this.ray3FArray_$private$8[0] = originX;
		this.ray3FArray_$private$8[1] = originY;
		this.ray3FArray_$private$8[2] = originZ;
		this.ray3FArray_$private$8[3] = directionNormalizedX;
		this.ray3FArray_$private$8[4] = directionNormalizedY;
		this.ray3FArray_$private$8[5] = directionNormalizedZ;
		this.ray3FArray_$private$8[6] = 0.001F;
		this.ray3FArray_$private$8[7] = Float.MAX_VALUE;
		
//		Fill in the pixel array:
		this.pixelArray[pixelArrayOffset + 0] = pixelX;
		this.pixelArray[pixelArrayOffset + 1] = pixelY;
		
		return true;
	}
	
//	TODO: Add Javadocs!
	protected final boolean intersectsPlane3F() {
		return intersectionTPlane3F() > 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final boolean intersectsRectangularCuboid3F() {
		return intersectionTRectangularCuboid3F() > 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final boolean intersectsSphere3F() {
		return intersectionTSphere3F() > 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final boolean intersectsTorus3F() {
		return intersectionTTorus3F() > 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final boolean intersectsTriangle3F() {
		return intersectionTTriangle3F() > 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final float intersectionTPlane3F() {
//		Retrieve the ray variables that will be referred to by 'rayOrigin' and 'rayDirection' in the comments:
		final float rayOriginX = this.ray3FArray_$private$8[0];
		final float rayOriginY = this.ray3FArray_$private$8[1];
		final float rayOriginZ = this.ray3FArray_$private$8[2];
		final float rayDirectionX = this.ray3FArray_$private$8[3];
		final float rayDirectionY = this.ray3FArray_$private$8[4];
		final float rayDirectionZ = this.ray3FArray_$private$8[5];
		final float rayTMinimum = this.ray3FArray_$private$8[6];
		final float rayTMaximum = this.ray3FArray_$private$8[7];
		
//		Retrieve the plane variables that will be referred to by 'planeA' and 'planeSurfaceNormal' in the comments:
		final float planeAX = 0.0F;
		final float planeAY = 0.0F;
		final float planeAZ = 0.0F;
		final float planeSurfaceNormalX = 0.0F;
		final float planeSurfaceNormalY = 1.0F;
		final float planeSurfaceNormalZ = 0.0F;
		
//		Compute the determinant, which is the dot product between 'planeSurfaceNormal' and 'rayDirection':
		final float determinant = planeSurfaceNormalX * rayDirectionX + planeSurfaceNormalY * rayDirectionY + planeSurfaceNormalZ * rayDirectionZ;
		
//		Check if the determinant is close to 0.0 and, if that is the case, return a miss:
		if(determinant >= -0.0001F && determinant <= +0.0001F) {
			return 0.0F;
		}
		
//		Compute the direction from 'rayOrigin' to 'planeA', denoted by 'rayOriginToPlaneA' in the comments:
		final float rayOriginToPlaneAX = planeAX - rayOriginX;
		final float rayOriginToPlaneAY = planeAY - rayOriginY;
		final float rayOriginToPlaneAZ = planeAZ - rayOriginZ;
		
//		Compute the intersection as the dot product between 'rayOriginToPlaneA' and 'planeSurfaceNormal' followed by a division with the determinant:
		final float intersectionT = (rayOriginToPlaneAX * planeSurfaceNormalX + rayOriginToPlaneAY * planeSurfaceNormalY + rayOriginToPlaneAZ * planeSurfaceNormalZ) / determinant;
		
		if(intersectionT > rayTMinimum && intersectionT < rayTMaximum) {
			return intersectionT;
		}
		
		return 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final float intersectionTRectangularCuboid3F() {
//		Retrieve the ray variables:
		final float rayOriginX = this.ray3FArray_$private$8[0];
		final float rayOriginY = this.ray3FArray_$private$8[1];
		final float rayOriginZ = this.ray3FArray_$private$8[2];
		final float rayDirectionReciprocalX = 1.0F / this.ray3FArray_$private$8[3];
		final float rayDirectionReciprocalY = 1.0F / this.ray3FArray_$private$8[4];
		final float rayDirectionReciprocalZ = 1.0F / this.ray3FArray_$private$8[5];
		final float rayTMinimum = this.ray3FArray_$private$8[6];
		final float rayTMaximum = this.ray3FArray_$private$8[7];
		
//		Retrieve the rectangular cuboid variables:
		final float rectangularCuboidMaximumX = +0.5F;
		final float rectangularCuboidMaximumY = +0.5F;
		final float rectangularCuboidMaximumZ = +0.5F;
		final float rectangularCuboidMinimumX = -0.5F;
		final float rectangularCuboidMinimumY = -0.5F;
		final float rectangularCuboidMinimumZ = -0.5F;
		
//		Compute the intersection:
		final float intersectionTMinimumX = (rectangularCuboidMinimumX - rayOriginX) * rayDirectionReciprocalX;
		final float intersectionTMinimumY = (rectangularCuboidMinimumY - rayOriginY) * rayDirectionReciprocalY;
		final float intersectionTMinimumZ = (rectangularCuboidMinimumZ - rayOriginZ) * rayDirectionReciprocalZ;
		final float intersectionTMaximumX = (rectangularCuboidMaximumX - rayOriginX) * rayDirectionReciprocalX;
		final float intersectionTMaximumY = (rectangularCuboidMaximumY - rayOriginY) * rayDirectionReciprocalY;
		final float intersectionTMaximumZ = (rectangularCuboidMaximumZ - rayOriginZ) * rayDirectionReciprocalZ;
		final float intersectionTMinimum = max(min(intersectionTMinimumX, intersectionTMaximumX), min(intersectionTMinimumY, intersectionTMaximumY), min(intersectionTMinimumZ, intersectionTMaximumZ));
		final float intersectionTMaximum = min(max(intersectionTMinimumX, intersectionTMaximumX), max(intersectionTMinimumY, intersectionTMaximumY), max(intersectionTMinimumZ, intersectionTMaximumZ));
		
		if(intersectionTMinimum > intersectionTMaximum) {
			return 0.0F;
		}
		
		if(intersectionTMinimum > rayTMinimum && intersectionTMinimum < rayTMaximum) {
			return intersectionTMinimum;
		}
		
		if(intersectionTMaximum > rayTMinimum && intersectionTMaximum < rayTMaximum) {
			return intersectionTMaximum;
		}
		
		return 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final float intersectionTSphere3F() {
//		Retrieve the ray variables:
		final float rayOriginX = this.ray3FArray_$private$8[0];
		final float rayOriginY = this.ray3FArray_$private$8[1];
		final float rayOriginZ = this.ray3FArray_$private$8[2];
		final float rayDirectionX = this.ray3FArray_$private$8[3];
		final float rayDirectionY = this.ray3FArray_$private$8[4];
		final float rayDirectionZ = this.ray3FArray_$private$8[5];
		final float rayTMinimum = this.ray3FArray_$private$8[6];
		final float rayTMaximum = this.ray3FArray_$private$8[7];
		
//		Retrieve the sphere variables:
		final float sphereCenterX = 0.0F;
		final float sphereCenterY = 0.0F;
		final float sphereCenterZ = 5.0F;
		final float sphereRadius = 1.0F;
		final float sphereRadiusSquared = sphereRadius * sphereRadius;
		
//		Compute the direction from the sphere center to the ray origin:
		final float sphereCenterToRayOriginX = rayOriginX - sphereCenterX;
		final float sphereCenterToRayOriginY = rayOriginY - sphereCenterY;
		final float sphereCenterToRayOriginZ = rayOriginZ - sphereCenterZ;
		
//		Compute the variables for the quadratic system:
		final float a = rayDirectionX * rayDirectionX + rayDirectionY * rayDirectionY + rayDirectionZ * rayDirectionZ;
		final float b = 2.0F * (sphereCenterToRayOriginX * rayDirectionX + sphereCenterToRayOriginY * rayDirectionY + sphereCenterToRayOriginZ * rayDirectionZ);
		final float c = (sphereCenterToRayOriginX * sphereCenterToRayOriginX + sphereCenterToRayOriginY * sphereCenterToRayOriginY + sphereCenterToRayOriginZ * sphereCenterToRayOriginZ) - sphereRadiusSquared;
		
//		Compute the intersection by solving the quadratic system and checking the valid intersection interval:
		final float t = doSolveQuadraticSystem(a, b, c, rayTMinimum, rayTMaximum);
		
		return t;
	}
	
//	TODO: Add Javadocs!
	protected final float intersectionTTorus3F() {
//		Retrieve the ray variables:
		final float rayOriginX = this.ray3FArray_$private$8[0];
		final float rayOriginY = this.ray3FArray_$private$8[1];
		final float rayOriginZ = this.ray3FArray_$private$8[2];
		final float rayDirectionX = this.ray3FArray_$private$8[3];
		final float rayDirectionY = this.ray3FArray_$private$8[4];
		final float rayDirectionZ = this.ray3FArray_$private$8[5];
		final float rayTMinimum = this.ray3FArray_$private$8[6];
		final float rayTMaximum = this.ray3FArray_$private$8[7];
		
//		Retrieve the torus variables:
		final float torusRadiusInner = 0.25F;
		final float torusRadiusInnerSquared = torusRadiusInner * torusRadiusInner;
		final float torusRadiusOuter = 1.0F;
		final float torusRadiusOuterSquared = torusRadiusOuter * torusRadiusOuter;
		
//		Compute the variables used in the process of computing the variables for the quartic system:
		final float f0 = rayDirectionX * rayDirectionX + rayDirectionY * rayDirectionY + rayDirectionZ * rayDirectionZ;
		final float f1 = (rayOriginX * rayDirectionX + rayOriginY * rayDirectionY + rayOriginZ * rayDirectionZ) * 2.0F;
		final float f2 = torusRadiusInnerSquared;
		final float f3 = torusRadiusOuterSquared;
		final float f4 = (rayOriginX * rayOriginX + rayOriginY * rayOriginY + rayOriginZ * rayOriginZ) - f2 - f3;
		final float f5 = rayDirectionZ;
		final float f6 = rayOriginZ;
		
//		Compute the variables for the quartic system:
		final float a = f0 * f0;
		final float b = f0 * 2.0F * f1;
		final float c = f1 * f1 + 2.0F * f0 * f4 + 4.0F * f3 * f5 * f5;
		final float d = f1 * 2.0F * f4 + 8.0F * f3 * f6 * f5;
		final float e = f4 * f4 + 4.0F * f3 * f6 * f6 - 4.0F * f3 * f2;
		
//		Compute the intersection by solving the quartic system and checking the valid intersection interval:
		final float t = doSolveQuarticSystem(a, b, c, d, e, rayTMinimum, rayTMaximum);
		
		return t;
	}
	
//	TODO: Add Javadocs!
	protected final float intersectionTTriangle3F() {
//		Retrieve the ray variables that will be referred to by 'rayOrigin' and 'rayDirection' in the comments:
		final float rayOriginX = this.ray3FArray_$private$8[0];
		final float rayOriginY = this.ray3FArray_$private$8[1];
		final float rayOriginZ = this.ray3FArray_$private$8[2];
		final float rayDirectionX = this.ray3FArray_$private$8[3];
		final float rayDirectionY = this.ray3FArray_$private$8[4];
		final float rayDirectionZ = this.ray3FArray_$private$8[5];
		final float rayTMinimum = this.ray3FArray_$private$8[6];
		final float rayTMaximum = this.ray3FArray_$private$8[7];
		
//		Retrieve the triangle variables that will be referred to by 'triangleAPosition', 'triangleBPosition' and 'triangleCPosition' in the comments:
		final float triangleAPositionX = +0.0F;
		final float triangleAPositionY = +1.0F;
		final float triangleAPositionZ = +5.0F;
		final float triangleBPositionX = +1.0F;
		final float triangleBPositionY = -1.0F;
		final float triangleBPositionZ = +5.0F;
		final float triangleCPositionX = -1.0F;
		final float triangleCPositionY = -1.0F;
		final float triangleCPositionZ = +5.0F;
		
//		Compute the direction from 'triangleAPosition' to 'triangleBPosition', denoted by 'direction0' in the comments:
		final float direction0X = triangleBPositionX - triangleAPositionX;
		final float direction0Y = triangleBPositionY - triangleAPositionY;
		final float direction0Z = triangleBPositionZ - triangleAPositionZ;
		
//		Compute the direction from 'triangleAPosition' to 'triangleCPosition', denoted by 'direction1' in the comments:
		final float direction1X = triangleCPositionX - triangleAPositionX;
		final float direction1Y = triangleCPositionY - triangleAPositionY;
		final float direction1Z = triangleCPositionZ - triangleAPositionZ;
		
//		Compute the cross product between 'rayDirection' and 'direction1', denoted by 'direction2' in the comments:
		final float direction2X = rayDirectionY * direction1Z - rayDirectionZ * direction1Y;
		final float direction2Y = rayDirectionZ * direction1X - rayDirectionX * direction1Z;
		final float direction2Z = rayDirectionX * direction1Y - rayDirectionY * direction1X;
		
//		Compute the determinant, which is the dot product between 'direction0' and 'direction2':
		final float determinant = direction0X * direction2X + direction0Y * direction2Y + direction0Z * direction2Z;
		
//		Check if the determinant is close to 0.0 and, if that is the case, return a miss:
		if(determinant >= -0.0001F && determinant <= +0.0001F) {
			return 0.0F;
		}
		
//		Compute the direction from 'triangleAPosition' to 'rayOrigin', denoted by 'direction3' in the comments:
		final float direction3X = rayOriginX - triangleAPositionX;
		final float direction3Y = rayOriginY - triangleAPositionY;
		final float direction3Z = rayOriginZ - triangleAPositionZ;
		
//		Compute the reciprocal (or inverse) of the determinant, such that multiplications can be used instead of divisions:
		final float determinantReciprocal = 1.0F / determinant;
		
//		Compute the Barycentric U-coordinate as the dot product between 'direction3' and 'direction2' followed by a multiplication with the reciprocal (or inverse) determinant:
		final float barycentricU = (direction3X * direction2X + direction3Y * direction2Y + direction3Z * direction2Z) * determinantReciprocal;
		
//		Check that the Barycentric U-coordinate is in the interval [0.0, 1.0] and, if it is not, return a miss:
		if(barycentricU < 0.0F || barycentricU > 1.0F) {
			return 0.0F;
		}
		
//		Compute the cross product between 'direction3' and 'direction0', denoted by 'direction4' in the comments:
		final float direction4X = direction3Y * direction0Z - direction3Z * direction0Y;
		final float direction4Y = direction3Z * direction0X - direction3X * direction0Z;
		final float direction4Z = direction3X * direction0Y - direction3Y * direction0X;
		
//		Compute the Barycentric V-coordinate as the dot product between 'rayDirection' and 'direction4' followed by a multiplication with the reciprocal (or inverse) determinant:
		final float barycentricV = (rayDirectionX * direction4X + rayDirectionY * direction4Y + rayDirectionZ * direction4Z) * determinantReciprocal;
		
//		Check that the Barycentric V-coordinate is in the interval [0.0, 1.0] and, if it is not, return a miss:
		if(barycentricV < 0.0F || barycentricV > 1.0F) {
			return 0.0F;
		}
		
//		Check that the sum of the Barycentric U-coordinate and the Barycentric V-coordinate is in the interval [0.0, 1.0] and, if it is not, return a miss:
		if(barycentricU + barycentricV > 1.0F) {
			return 0.0F;
		}
		
//		Compute the intersection as the dot product between 'direction1' and 'direction4' followed by a multiplication with the reciprocal (or inverse) determinant:
		final float intersectionT = (direction1X * direction4X + direction1Y * direction4Y + direction1Z * direction4Z) * determinantReciprocal;
		
		if(intersectionT > rayTMinimum && intersectionT < rayTMaximum) {
			return intersectionT;
		}
		
		return 0.0F;
	}
	
//	TODO: Add Javadocs!
	protected final float max(final float a, final float b, final float c) {
		return max(max(a, b), c);
	}
	
//	TODO: Add Javadocs!
	protected final float min(final float a, final float b, final float c) {
		return min(min(a, b), c);
	}
	
	/**
	 * Returns a pseudorandom {@code float} value between {@code 0.0F} (inclusive) and {@code 1.0F} (exclusive).
	 * 
	 * @return a pseudorandom {@code float} value between {@code 0.0F} (inclusive) and {@code 1.0F} (exclusive)
	 */
	protected final float random() {
		return doNext(24) * PRNG_NEXT_FLOAT_RECIPROCAL;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Image doGetImage() {
		return getRendererConfiguration().getImage();
	}
	
	private Scene doGetScene() {
		return getRendererConfiguration().getScene();
	}
	
	private float doSolveCubicForQuarticSystem(final float p, final float q, final float r) {
		final float pSquared = p * p;
		final float q0 = (pSquared - 3.0F * q) / 9.0F;
		final float q0Cubed = q0 * q0 * q0;
		final float r0 = (p * (pSquared - 4.5F * q) + 13.5F * r) / 27.0F;
		final float r0Squared = r0 * r0;
		final float d = q0Cubed - r0Squared;
		final float e = p / 3.0F;
		
		if(d >= 0.0F) {
			final float d0 = r0 / sqrt(q0Cubed);
			final float theta = acos(d0) / 3.0F;
			final float q1 = -2.0F * sqrt(q0);
			final float q2 = q1 * cos(theta) - e;
			
			return q2;
		}
		
		final float q1 = pow(sqrt(r0Squared - q0Cubed) + abs(r0), 1.0F / 3.0F);
		final float q2 = r0 < 0.0F ? (q1 + q0 / q1) - e : -(q1 + q0 / q1) - e;
		
		return q2;
	}
	
	private float doSolveQuadraticSystem(final float a, final float b, final float c, final float minimum, final float maximum) {
		final float discriminantSquared = b * b - 4.0F * a * c;
		
		if(discriminantSquared == -0.0F || discriminantSquared == +0.0F) {
			final float q0 = -0.5F * b / a;
			final float q1 = q0 > minimum && q0 < maximum ? q0 : 0.0F;
			
			return q1;
		} else if(discriminantSquared > 0.0F) {
			final float discriminant = sqrt(discriminantSquared);
			
			final float q0 = -0.5F * (b > 0.0F ? b + discriminant : b - discriminant);
			final float q1 = q0 / a;
			final float q2 = c / q0;
			final float q3 = min(q1, q2);
			final float q4 = max(q1, q2);
			final float q5 = q3 > minimum && q3 < maximum ? q3 : q4 > minimum && q4 < maximum ? q4 : 0.0F;
			
			return q5;
		} else {
			return 0.0F;
		}
	}
	
	private float doSolveQuarticSystem(final float a, final float b, final float c, final float d, final float e, final float minimum, final float maximum) {
		final float aReciprocal = 1.0F / a;
		final float bA = b * aReciprocal;
		final float bASquared = bA * bA;
		final float cA = c * aReciprocal;
		final float dA = d * aReciprocal;
		final float eA = e * aReciprocal;
		final float p = -0.375F * bASquared + cA;
		final float q = 0.125F * bASquared * bA - 0.5F * bA * cA + dA;
		final float r = -0.01171875F * bASquared * bASquared + 0.0625F * bASquared * cA - 0.25F * bA * dA + eA;
		final float z = doSolveCubicForQuarticSystem(-0.5F * p, -r, 0.5F * r * p - 0.125F * q * q);
		
		float d1 = 2.0F * z - p;
		float d2 = 0.0F;
		
		if(d1 < 0.0F) {
			return 0.0F;
		} else if(d1 < 1.0e-10F) {
			d2 = z * z - r;
			
			if(d2 < 0.0F) {
				return 0.0F;
			}
			
			d2 = sqrt(d2);
		} else {
			d1 = sqrt(d1);
			d2 = 0.5F * q / d1;
		}
		
		final float q1 = d1 * d1;
		final float q2 = -0.25F * bA;
		final float pm = q1 - 4.0F * (z - d2);
		final float pp = q1 - 4.0F * (z + d2);
		
		if(pm >= 0.0F && pp >= 0.0F) {
			final float pmSqrt = sqrt(pm);
			final float ppSqrt = sqrt(pp);
			
			float result0 = -0.5F * (d1 + pmSqrt) + q2;
			float result1 = -0.5F * (d1 - pmSqrt) + q2;
			float result2 = +0.5F * (d1 + ppSqrt) + q2;
			float result3 = +0.5F * (d1 - ppSqrt) + q2;
			float result4 = 0.0F;
			
			if(result0 > result1) {
				result4 = result0;
				result0 = result1;
				result1 = result4;
			}
			
			if(result2 > result3) {
				result4 = result2;
				result2 = result3;
				result3 = result4;
			}
			
			if(result0 > result2) {
				result4 = result0;
				result0 = result2;
				result2 = result4;
			}
			
			if(result1 > result3) {
				result4 = result1;
				result1 = result3;
				result3 = result4;
			}
			
			if(result1 > result2) {
				result4 = result1;
				result1 = result2;
				result2 = result4;
			}
			
			if(result0 >= maximum || result3 <= minimum) {
				return 0.0F;
			} else if(result0 > minimum) {
				return result0;
			} else if(result1 > minimum) {
				return result1;
			} else if(result2 > minimum) {
				return result2;
			} else if(result3 > minimum) {
				return result3;
			} else {
				return 0.0F;
			}
		} else if(pm >= 0.0F) {
			final float pmSqrt = sqrt(pm);
			
			final float result0 = -0.5F * (d1 + pmSqrt) + q2;
			final float result1 = -0.5F * (d1 - pmSqrt) + q2;
			
			if(result0 >= maximum || result1 <= minimum) {
				return 0.0F;
			} else if(result0 > minimum) {
				return result0;
			} else if(result1 > minimum) {
				return result1;
			} else {
				return 0.0F;
			}
		} else if(pp >= 0.0F) {
			final float ppSqrt = sqrt(pp);
			
			final float result0 = +0.5F * (d1 - ppSqrt) + q2;
			final float result1 = +0.5F * (d1 + ppSqrt) + q2;
			
			if(result0 >= maximum || result1 <= minimum) {
				return 0.0F;
			} else if(result0 > minimum) {
				return result0;
			} else if(result1 > minimum) {
				return result1;
			} else {
				return 0.0F;
			}
		} else {
			return 0.0F;
		}
	}
	
	private float[] doGetAndReturn(final float[] array) {
		get(array);
		
		return array;
	}
	
	private float[] doGetPixelArray() {
		return doGetAndReturn(this.pixelArray);
	}
	
	private float[] doGetRadianceArray() {
		return doGetAndReturn(this.radianceArray);
	}
	
	private int doGetResolution() {
		return doGetImage().getResolution();
	}
	
	private int doNext(final int bits) {
		final int index = getGlobalId();
		
		final long oldSeed = this.seedArray[index];
		final long newSeed = (oldSeed * PRNG_MULTIPLIER + PRNG_ADDEND) & PRNG_MASK;
		
		this.seedArray[index] = newSeed;
		
		return (int)(newSeed >>> (48 - bits));
	}
	
	private void doSetupPixelArray() {
		put(this.pixelArray = Floats.array(doGetResolution() * 2, 0.0F));
	}
	
	private void doSetupRadianceArray() {
		put(this.radianceArray = Floats.array(doGetResolution() * 3, 0.0F));
	}
	
	private void doSetupResolution() {
		this.resolutionX = doGetImage().getResolutionX();
		this.resolutionY = doGetImage().getResolutionY();
	}
	
	private void doSetupScene() {
		final CompiledScene compiledScene = SceneCompiler.compile(doGetScene());
		
		put(this.cameraArray = compiledScene.getCameraArray());
		put(this.matrix44FArray = compiledScene.getMatrix44FArray());
		put(this.primitiveArray = compiledScene.getPrimitiveArray());
		put(this.sphere3FArray = compiledScene.getSphere3FArray());
	}
	
	private void doSetupSeedArray() {
		put(this.seedArray = Longs.array(doGetResolution(), () -> ThreadLocalRandom.current().nextLong()));
	}
}