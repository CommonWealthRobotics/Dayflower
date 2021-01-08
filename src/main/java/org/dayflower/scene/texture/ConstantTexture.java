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
package org.dayflower.scene.texture;

import java.util.Objects;

import org.dayflower.image.Color3F;
import org.dayflower.scene.Intersection;

/**
 * A {@code ConstantTexture} is a {@link Texture} implementation that returns a constant {@link Color3F} instance.
 * <p>
 * This class is immutable and therefore thread-safe.
 * <p>
 * This {@code Texture} implementation is supported on the GPU.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ConstantTexture implements Texture {
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.BLACK)}.
	 */
	public static final ConstantTexture BLACK = new ConstantTexture(Color3F.BLACK);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_0_10)}.
	 */
	public static final ConstantTexture GRAY_0_10 = new ConstantTexture(Color3F.GRAY_0_10);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_0_25)}.
	 */
	public static final ConstantTexture GRAY_0_25 = new ConstantTexture(Color3F.GRAY_0_25);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_0_30)}.
	 */
	public static final ConstantTexture GRAY_0_30 = new ConstantTexture(Color3F.GRAY_0_30);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_0_50)}.
	 */
	public static final ConstantTexture GRAY_0_50 = new ConstantTexture(Color3F.GRAY_0_50);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_1_50)}.
	 */
	public static final ConstantTexture GRAY_1_50 = new ConstantTexture(Color3F.GRAY_1_50);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_1_55)}.
	 */
	public static final ConstantTexture GRAY_1_55 = new ConstantTexture(Color3F.GRAY_1_55);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.GRAY_2_00)}.
	 */
	public static final ConstantTexture GRAY_2_00 = new ConstantTexture(Color3F.GRAY_2_00);
	
	/**
	 * A {@code ConstantTexture} of {@code new ConstantTexture(Color3F.WHITE)}.
	 */
	public static final ConstantTexture WHITE = new ConstantTexture(Color3F.WHITE);
	
	/**
	 * The length of the {@code float[]}.
	 */
	public static final int ARRAY_LENGTH = 4;
	
	/**
	 * The offset for the {@link Color3F} instance representing the color in the {@code float[]}.
	 */
	public static final int ARRAY_OFFSET_COLOR = 0;
	
	/**
	 * The ID of this {@code ConstantTexture} class.
	 */
	public static final int ID = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color3F color;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ConstantTexture} instance with {@code Color3F.GREEN} as its constant {@link Color3F} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new ConstantTexture(Color3F.BLACK);
	 * }
	 * </pre>
	 */
	public ConstantTexture() {
		this(Color3F.BLACK);
	}
	
	/**
	 * Constructs a new {@code ConstantTexture} instance with {@code color} as its constant {@link Color3F} instance.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color3F} instance to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public ConstantTexture(final Color3F color) {
		this.color = Objects.requireNonNull(color, "color == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Color3F} instance to use.
	 * 
	 * @return the {@code Color3F} instance to use
	 */
	public Color3F getColor() {
		return this.color;
	}
	
	/**
	 * Returns a {@link Color3F} instance representing the color of the surface at {@code intersection}.
	 * <p>
	 * If {@code intersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param intersection an {@link Intersection} instance
	 * @return a {@code Color3F} instance representing the color of the surface at {@code intersection}
	 * @throws NullPointerException thrown if, and only if, {@code intersection} is {@code null}
	 */
	@Override
	public Color3F getColor(final Intersection intersection) {
		Objects.requireNonNull(intersection, "intersection == null");
		
		return this.color;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code ConstantTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code ConstantTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new ConstantTexture(%s)", this.color);
	}
	
	/**
	 * Compares {@code object} to this {@code ConstantTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code ConstantTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code ConstantTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code ConstantTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof ConstantTexture)) {
			return false;
		} else if(!Objects.equals(this.color, ConstantTexture.class.cast(object).color)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a {@code float} representing the value of the surface at {@code intersection}.
	 * <p>
	 * If {@code intersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param intersection an {@link Intersection} instance
	 * @return a {@code float} representing the value of the surface at {@code intersection}
	 * @throws NullPointerException thrown if, and only if, {@code intersection} is {@code null}
	 */
	@Override
	public float getFloat(final Intersection intersection) {
		return getColor(intersection).average();
	}
	
	/**
	 * Returns a {@code float[]} representation of this {@code ConstantTexture} instance.
	 * 
	 * @return a {@code float[]} representation of this {@code ConstantTexture} instance
	 */
	public float[] toArray() {
		final float[] array = new float[ARRAY_LENGTH];
		
//		Because the ConstantTexture occupy 4/8 positions in a block, it should be aligned.
		array[ARRAY_OFFSET_COLOR + 0] = this.color.getR();
		array[ARRAY_OFFSET_COLOR + 1] = this.color.getG();
		array[ARRAY_OFFSET_COLOR + 2] = this.color.getB();
		array[3] = 0.0F;
		
		return array;
	}
	
	/**
	 * Returns an {@code int} with the ID of this {@code ConstantTexture} instance.
	 * 
	 * @return an {@code int} with the ID of this {@code ConstantTexture} instance
	 */
	@Override
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns a hash code for this {@code ConstantTexture} instance.
	 * 
	 * @return a hash code for this {@code ConstantTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.color);
	}
}