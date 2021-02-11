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
package org.dayflower.scene.modifier;

import static org.dayflower.utility.Floats.positiveModulo;
import static org.dayflower.utility.Ints.positiveModulo;
import static org.dayflower.utility.Ints.toInt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.dayflower.color.Color3F;
import org.dayflower.geometry.AngleF;
import org.dayflower.geometry.Point2F;
import org.dayflower.geometry.Vector2F;
import org.dayflower.geometry.Vector3F;
import org.dayflower.image.ImageF;
import org.dayflower.node.NodeHierarchicalVisitor;
import org.dayflower.node.NodeTraversalException;
import org.dayflower.scene.Intersection;
import org.dayflower.utility.BufferedImages;
import org.dayflower.utility.ParameterArguments;

/**
 * An {@code LDRImageNormalMapModifier} is a {@link Modifier} implementation that modifies the surface normal using a {@link Color3F} instance from a low-dynamic-range (LDR) image.
 * <p>
 * This class is immutable and therefore thread-safe.
 * <p>
 * This {@code LDRImageNormalMapModifier} class stores the image as an {@code int[]} with the colors in packed form and in the order ARGB. It is, however, possible to create an {@code LDRImageNormalMapModifier} instance from an {@link ImageF} instance.
 * This is useful if the requirement is to generate an image procedurally.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class LDRImageNormalMapModifier implements Modifier {
	private final AngleF angle;
	private final Vector2F scale;
	private final int resolution;
	private final int resolutionX;
	private final int resolutionY;
	private final int[] image;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code image} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new LDRImageNormalMapModifier(image, AngleF.degrees(0.0F));
	 * }
	 * </pre>
	 * 
	 * @param image an {@link ImageF} instance
	 * @throws NullPointerException thrown if, and only if, {@code image} is {@code null}
	 */
	public LDRImageNormalMapModifier(final ImageF image) {
		this(image, AngleF.degrees(0.0F));
	}
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code image} or {@code angle} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new LDRImageNormalMapModifier(image, angle, new Vector2F(1.0F, 1.0F));
	 * }
	 * </pre>
	 * 
	 * @param image an {@link ImageF} instance
	 * @param angle the {@link AngleF} instance to use
	 * @throws NullPointerException thrown if, and only if, either {@code image} or {@code angle} are {@code null}
	 */
	public LDRImageNormalMapModifier(final ImageF image, final AngleF angle) {
		this(image, angle, new Vector2F(1.0F, 1.0F));
	}
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code image}, {@code angle} or {@code scale} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new LDRImageNormalMapModifier(image.getResolutionX(), image.getResolutionY(), image.toIntArrayPackedForm(), angle, scale);
	 * }
	 * </pre>
	 * 
	 * @param image an {@link ImageF} instance
	 * @param angle the {@link AngleF} instance to use
	 * @param scale the {@link Vector2F} instance to use as the scale factor
	 * @throws NullPointerException thrown if, and only if, either {@code image}, {@code angle} or {@code scale} are {@code null}
	 */
	public LDRImageNormalMapModifier(final ImageF image, final AngleF angle, final Vector2F scale) {
		this(image.getResolutionX(), image.getResolutionY(), image.toIntArrayPackedForm(), angle, scale);
	}
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code image} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new LDRImageNormalMapModifier(resolutionX, resolutionY, image, AngleF.degrees(0.0F));
	 * }
	 * </pre>
	 * 
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 * @param image the image to clone and use
	 * @throws IllegalArgumentException thrown if, and only if, either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}
	 * @throws NullPointerException thrown if, and only if, {@code image} is {@code null}
	 */
	public LDRImageNormalMapModifier(final int resolutionX, final int resolutionY, final int[] image) {
		this(resolutionX, resolutionY, image, AngleF.degrees(0.0F));
	}
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code image} or {@code angle} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new LDRImageNormalMapModifier(resolutionX, resolutionY, image, angle, new Vector2F(1.0F, 1.0F));
	 * }
	 * </pre>
	 * 
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 * @param image the image to clone and use
	 * @param angle the {@link AngleF} instance to use
	 * @throws IllegalArgumentException thrown if, and only if, either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}
	 * @throws NullPointerException thrown if, and only if, either {@code image} or {@code angle} are {@code null}
	 */
	public LDRImageNormalMapModifier(final int resolutionX, final int resolutionY, final int[] image, final AngleF angle) {
		this(resolutionX, resolutionY, image, angle, new Vector2F(1.0F, 1.0F));
	}
	
	/**
	 * Constructs a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code image}, {@code angle} or {@code scale} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution of the X-axis
	 * @param resolutionY the resolution of the Y-axis
	 * @param image the image to clone and use
	 * @param angle the {@link AngleF} instance to use
	 * @param scale the {@link Vector2F} instance to use as the scale factor
	 * @throws IllegalArgumentException thrown if, and only if, either {@code resolutionX}, {@code resolutionY} or {@code resolutionX * resolutionY} are less than {@code 0}, or {@code resolutionX * resolutionY != image.length}
	 * @throws NullPointerException thrown if, and only if, either {@code image}, {@code angle} or {@code scale} are {@code null}
	 */
	public LDRImageNormalMapModifier(final int resolutionX, final int resolutionY, final int[] image, final AngleF angle, final Vector2F scale) {
		this.resolutionX = ParameterArguments.requireRange(resolutionX, 0, Integer.MAX_VALUE, "resolutionX");
		this.resolutionY = ParameterArguments.requireRange(resolutionY, 0, Integer.MAX_VALUE, "resolutionY");
		this.resolution = ParameterArguments.requireRange(resolutionX * resolutionY, 0, Integer.MAX_VALUE, "resolutionX * resolutionY");
		this.image = ParameterArguments.requireExactArrayLength(Objects.requireNonNull(image, "image == null"), resolutionX * resolutionY, "image").clone();
		this.angle = Objects.requireNonNull(angle, "angle == null");
		this.scale = Objects.requireNonNull(scale, "scale == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link AngleF} instance to use.
	 * 
	 * @return the {@code AngleF} instance to use
	 */
	public AngleF getAngle() {
		return this.angle;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code LDRImageNormalMapModifier} instance.
	 * 
	 * @return a {@code String} representation of this {@code LDRImageNormalMapModifier} instance
	 */
	@Override
	public String toString() {
		return String.format("new LDRImageNormalMapModifier(%d, %d, %s, %s, %s)", Integer.valueOf(this.resolutionX), Integer.valueOf(this.resolutionY), "new int[] {...}", this.angle, this.scale);
	}
	
	/**
	 * Returns the {@link Vector2F} instance to use as the scale factor.
	 * 
	 * @return the {@code Vector2F} instance to use as the scale factor
	 */
	public Vector2F getScale() {
		return this.scale;
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
				if(!this.scale.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
			}
			
			return nodeHierarchicalVisitor.visitLeave(this);
		} catch(final RuntimeException e) {
			throw new NodeTraversalException(e);
		}
	}
	
	/**
	 * Compares {@code object} to this {@code LDRImageNormalMapModifier} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code LDRImageNormalMapModifier}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code LDRImageNormalMapModifier} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code LDRImageNormalMapModifier}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof LDRImageNormalMapModifier)) {
			return false;
		} else if(!Objects.equals(this.angle, LDRImageNormalMapModifier.class.cast(object).angle)) {
			return false;
		} else if(!Objects.equals(this.scale, LDRImageNormalMapModifier.class.cast(object).scale)) {
			return false;
		} else if(this.resolution != LDRImageNormalMapModifier.class.cast(object).resolution) {
			return false;
		} else if(this.resolutionX != LDRImageNormalMapModifier.class.cast(object).resolutionX) {
			return false;
		} else if(this.resolutionY != LDRImageNormalMapModifier.class.cast(object).resolutionY) {
			return false;
		} else if(!Arrays.equals(this.image, LDRImageNormalMapModifier.class.cast(object).image)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the resolution.
	 * 
	 * @return the resolution
	 */
	public int getResolution() {
		return this.resolution;
	}
	
	/**
	 * Returns the resolution of the X-axis.
	 * 
	 * @return the resolution of the X-axis
	 */
	public int getResolutionX() {
		return this.resolutionX;
	}
	
	/**
	 * Returns the resolution of the Y-axis.
	 * 
	 * @return the resolution of the Y-axis
	 */
	public int getResolutionY() {
		return this.resolutionY;
	}
	
	/**
	 * Returns a hash code for this {@code LDRImageNormalMapModifier} instance.
	 * 
	 * @return a hash code for this {@code LDRImageNormalMapModifier} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.angle, this.scale, Integer.valueOf(this.resolution), Integer.valueOf(this.resolutionX), Integer.valueOf(this.resolutionY), Integer.valueOf(Arrays.hashCode(this.image)));
	}
	
	/**
	 * Returns an {@code int[]} containing the image with its colors in packed form using the order ARGB.
	 * <p>
	 * Modifying the returned {@code int[]} will not affect this {@code LDRImageNormalMapModifier} instance.
	 * 
	 * @return an {@code int[]} containing the image with its colors in packed form using the order ARGB
	 */
	public int[] getImage() {
		return this.image.clone();
	}
	
	/**
	 * Modifies the surface at {@code intersection}.
	 * <p>
	 * If {@code intersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param intersection an {@link Intersection} instance
	 * @throws NullPointerException thrown if, and only if, {@code intersection} is {@code null}
	 */
	@Override
	public void modify(final Intersection intersection) {
		Objects.requireNonNull(intersection, "intersection == null");
		
		final Color3F colorRGB = doGetColorRGB(intersection);
		
		final float r = colorRGB.getR();
		final float g = colorRGB.getG();
		final float b = colorRGB.getB();
		
		final float x = r * 2.0F - 1.0F;
		final float y = g * 2.0F - 1.0F;
		final float z = b * 2.0F - 1.0F;
		
		final Vector3F surfaceNormalSLocalSpace = new Vector3F(x, y, z);
		final Vector3F surfaceNormalSWorldSpace = Vector3F.normalize(Vector3F.transform(surfaceNormalSLocalSpace, intersection.getSurfaceIntersectionWorldSpace().getOrthonormalBasisS()));
		
		intersection.setSurfaceNormalS(surfaceNormalSWorldSpace);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code file}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * LDRImageNormalMapModifier.load(file, AngleF.degrees(0.0F));
	 * }
	 * </pre>
	 * 
	 * @param file a {@code File} that represents the file to load from
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final File file) {
		return load(file, AngleF.degrees(0.0F));
	}
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code file}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code file} or {@code angle} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * LDRImageNormalMapModifier.load(file, angle, new Vector2F(1.0F, 1.0F));
	 * }
	 * </pre>
	 * 
	 * @param file a {@code File} that represents the file to load from
	 * @param angle the {@link AngleF} instance to use
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, either {@code file} or {@code angle} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final File file, final AngleF angle) {
		return load(file, angle, new Vector2F(1.0F, 1.0F));
	}
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code file}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code file}, {@code angle} or {@code scale} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} that represents the file to load from
	 * @param angle the {@link AngleF} instance to use
	 * @param scale the {@link Vector2F} instance to use as the scale factor
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, either {@code file}, {@code angle} or {@code scale} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final File file, final AngleF angle, final Vector2F scale) {
		try {
			final BufferedImage bufferedImage = BufferedImages.getCompatibleBufferedImage(ImageIO.read(Objects.requireNonNull(file, "file == null")));
			
			final int resolutionX = bufferedImage.getWidth();
			final int resolutionY = bufferedImage.getHeight();
			
			final int[] image = DataBufferInt.class.cast(bufferedImage.getRaster().getDataBuffer()).getData();
			
			return new LDRImageNormalMapModifier(resolutionX, resolutionY, image, angle, scale);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code pathname}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code pathname} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * LDRImageNormalMapModifier.load(pathname, AngleF.degrees(0.0F));
	 * }
	 * </pre>
	 * 
	 * @param pathname a {@code String} that represents the pathname to the file to load from
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, {@code pathname} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final String pathname) {
		return load(pathname, AngleF.degrees(0.0F));
	}
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code pathname}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code pathname} or {@code angle} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * LDRImageNormalMapModifier.load(pathname, angle, new Vector2F(1.0F, 1.0F));
	 * }
	 * </pre>
	 * 
	 * @param pathname a {@code String} that represents the pathname to the file to load from
	 * @param angle the {@link AngleF} instance to use
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, either {@code pathname} or {@code angle} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final String pathname, final AngleF angle) {
		return load(pathname, angle, new Vector2F(1.0F, 1.0F));
	}
	
	/**
	 * Loads an {@code LDRImageNormalMapModifier} from the file represented by {@code pathname}.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If either {@code pathname}, {@code angle} or {@code scale} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param pathname a {@code String} that represents the pathname to the file to load from
	 * @param angle the {@link AngleF} instance to use
	 * @param scale the {@link Vector2F} instance to use as the scale factor
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, either {@code pathname}, {@code angle} or {@code scale} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static LDRImageNormalMapModifier load(final String pathname, final AngleF angle, final Vector2F scale) {
		return load(new File(Objects.requireNonNull(pathname, "pathname == null")), angle, scale);
	}
	
	/**
	 * Redoes gamma correction on {@code lDRImageNormalMapModifier} using sRGB.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code lDRImageNormalMapModifier} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param lDRImageNormalMapModifier an {@code LDRImageNormalMapModifier} instance
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, {@code lDRImageNormalMapModifier} is {@code null}
	 */
	public static LDRImageNormalMapModifier redoGammaCorrectionSRGB(final LDRImageNormalMapModifier lDRImageNormalMapModifier) {
		final int[] image = new int[lDRImageNormalMapModifier.image.length];
		
		for(int i = 0; i < lDRImageNormalMapModifier.image.length; i++) {
			final Color3F colorA = Color3F.unpack(lDRImageNormalMapModifier.image[i]);
			final Color3F colorB = Color3F.redoGammaCorrectionSRGB(colorA);
			
			image[i] = colorB.pack();
		}
		
		return new LDRImageNormalMapModifier(lDRImageNormalMapModifier.resolutionX, lDRImageNormalMapModifier.resolutionY, image, lDRImageNormalMapModifier.angle, lDRImageNormalMapModifier.scale);
	}
	
	/**
	 * Undoes gamma correction on {@code lDRImageNormalMapModifier} using sRGB.
	 * <p>
	 * Returns a new {@code LDRImageNormalMapModifier} instance.
	 * <p>
	 * If {@code lDRImageNormalMapModifier} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param lDRImageNormalMapModifier an {@code LDRImageNormalMapModifier} instance
	 * @return a new {@code LDRImageNormalMapModifier} instance
	 * @throws NullPointerException thrown if, and only if, {@code lDRImageNormalMapModifier} is {@code null}
	 */
	public static LDRImageNormalMapModifier undoGammaCorrectionSRGB(final LDRImageNormalMapModifier lDRImageNormalMapModifier) {
		final int[] image = new int[lDRImageNormalMapModifier.image.length];
		
		for(int i = 0; i < lDRImageNormalMapModifier.image.length; i++) {
			final Color3F colorA = Color3F.unpack(lDRImageNormalMapModifier.image[i]);
			final Color3F colorB = Color3F.undoGammaCorrectionSRGB(colorA);
			
			image[i] = colorB.pack();
		}
		
		return new LDRImageNormalMapModifier(lDRImageNormalMapModifier.resolutionX, lDRImageNormalMapModifier.resolutionY, image, lDRImageNormalMapModifier.angle, lDRImageNormalMapModifier.scale);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Color3F doGetColorRGB(final Intersection intersection) {
		final Point2F textureCoordinates = intersection.getTextureCoordinates();
		final Point2F textureCoordinatesRotated = Point2F.rotate(textureCoordinates, this.angle);
		final Point2F textureCoordinatesScaled = Point2F.scale(textureCoordinatesRotated, this.scale);
		final Point2F textureCoordinatesImage = Point2F.toImage(textureCoordinatesScaled, this.resolutionX, this.resolutionY);
		
		return doGetColorRGB(toInt(textureCoordinatesImage.getX()), toInt(textureCoordinatesImage.getY()));
	}
	
	/*
	private Color3F doGetColorRGB(final float x, final float y) {
		final int minimumX = toInt(floor(x));
		final int maximumX = toInt(ceil(x));
		
		final int minimumY = toInt(floor(y));
		final int maximumY = toInt(ceil(y));
		
		if(minimumX == maximumX && minimumY == maximumY) {
			return doGetColorRGB(minimumX, minimumY);
		}
		
		final Color3F color00 = doGetColorRGB(minimumX, minimumY);
		final Color3F color01 = doGetColorRGB(maximumX, minimumY);
		final Color3F color10 = doGetColorRGB(minimumX, maximumY);
		final Color3F color11 = doGetColorRGB(maximumX, maximumY);
		
		final float xFactor = x - minimumX;
		final float yFactor = y - minimumY;
		
		final Color3F color = Color3F.blend(Color3F.blend(color00, color01, xFactor), Color3F.blend(color10, color11, xFactor), yFactor);
		
		return color;
	}
	*/
	
	private Color3F doGetColorRGB(final int x, final int y) {
		return Color3F.unpack(this.image[positiveModulo(y, this.resolutionY) * this.resolutionX + positiveModulo(x, this.resolutionX)]);
	}
}