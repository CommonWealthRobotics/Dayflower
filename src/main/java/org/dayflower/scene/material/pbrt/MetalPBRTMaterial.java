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
package org.dayflower.scene.material.pbrt;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.image.Color3F;
import org.dayflower.node.NodeHierarchicalVisitor;
import org.dayflower.node.NodeTraversalException;
import org.dayflower.scene.BSSRDF;
import org.dayflower.scene.Intersection;
import org.dayflower.scene.TransportMode;
import org.dayflower.scene.bxdf.pbrt.PBRTBSDF;
import org.dayflower.scene.bxdf.pbrt.TorranceSparrowPBRTBRDF;
import org.dayflower.scene.fresnel.ConductorFresnel;
import org.dayflower.scene.fresnel.Fresnel;
import org.dayflower.scene.microfacet.MicrofacetDistribution;
import org.dayflower.scene.microfacet.TrowbridgeReitzMicrofacetDistribution;
import org.dayflower.scene.texture.ConstantTexture;
import org.dayflower.scene.texture.Texture;

/**
 * A {@code MetalPBRTMaterial} is an implementation of {@link PBRTMaterial} that represents metal.
 * <p>
 * This class is immutable and thread-safe as long as all {@link Texture} instances are.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class MetalPBRTMaterial implements PBRTMaterial {
	/**
	 * The name of this {@code MetalPBRTMaterial} class.
	 */
	public static final String NAME = "PBRT - Metal";
	
	/**
	 * The ID of this {@code MetalPBRTMaterial} class.
	 */
	public static final int ID = 103;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Texture textureEta;
	private final Texture textureK;
	private final Texture textureRoughnessU;
	private final Texture textureRoughnessV;
	private final boolean isRemappingRoughness;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code MetalPBRTMaterial} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new MetalPBRTMaterial(new ConstantTexture(Color3F.GOLD_ETA_MAXIMUM_TO_1), new ConstantTexture(Color3F.GOLD_K_MAXIMUM_TO_1));
	 * }
	 * </pre>
	 */
	public MetalPBRTMaterial() {
		this(new ConstantTexture(Color3F.GOLD_ETA_MAXIMUM_TO_1), new ConstantTexture(Color3F.GOLD_K_MAXIMUM_TO_1));
	}
	
	/**
	 * Constructs a new {@code MetalPBRTMaterial} instance.
	 * <p>
	 * If either {@code textureEta} or {@code textureK} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new MetalPBRTMaterial(textureEta, textureK, new ConstantTexture(new Color3F(0.01F)), new ConstantTexture(new Color3F(0.01F)), true);
	 * }
	 * </pre>
	 * 
	 * @param textureEta a {@link Texture} instance used for eta, also called index of refraction (IOR)
	 * @param textureK a {@code Texture} instance used for the absorption coefficient
	 * @throws NullPointerException thrown if, and only if, either {@code textureEta} or {@code textureK} are {@code null}
	 */
	public MetalPBRTMaterial(final Texture textureEta, final Texture textureK) {
		this(textureEta, textureK, new ConstantTexture(new Color3F(0.01F)), new ConstantTexture(new Color3F(0.01F)), true);
	}
	
	/**
	 * Constructs a new {@code MetalPBRTMaterial} instance.
	 * <p>
	 * If either {@code textureEta}, {@code textureK}, {@code textureRoughnessU} or {@code textureRoughnessV} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param textureEta a {@link Texture} instance used for eta, also called index of refraction (IOR)
	 * @param textureK a {@code Texture} instance used for the absorption coefficient
	 * @param textureRoughnessU a {@code Texture} instance used for the roughness along the U-axis
	 * @param textureRoughnessV a {@code Texture} instance used for the roughness along the V-axis
	 * @param isRemappingRoughness {@code true} if, and only if, the roughness values should be remapped, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, either {@code textureEta}, {@code textureK}, {@code textureRoughnessU} or {@code textureRoughnessV} are {@code null}
	 */
	public MetalPBRTMaterial(final Texture textureEta, final Texture textureK, final Texture textureRoughnessU, final Texture textureRoughnessV, final boolean isRemappingRoughness) {
		this.textureEta = Objects.requireNonNull(textureEta, "textureEta == null");
		this.textureK = Objects.requireNonNull(textureK, "textureK == null");
		this.textureRoughnessU = Objects.requireNonNull(textureRoughnessU, "textureRoughnessU == null");
		this.textureRoughnessV = Objects.requireNonNull(textureRoughnessV, "textureRoughnessV == null");
		this.isRemappingRoughness = isRemappingRoughness;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Computes the {@link BSSRDF} at {@code intersection}.
	 * <p>
	 * Returns an optional {@code BSSRDF} instance.
	 * <p>
	 * If either {@code intersection} or {@code transportMode} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param intersection the {@link Intersection} to compute the {@code BSSRDF} for
	 * @param transportMode the {@link TransportMode} to use
	 * @param isAllowingMultipleLobes {@code true} if, and only if, multiple lobes are allowed, {@code false} otherwise
	 * @return an optional {@code BSSRDF} instance
	 * @throws NullPointerException thrown if, and only if, either {@code intersection} or {@code transportMode} are {@code null}
	 */
	@Override
	public Optional<BSSRDF> computeBSSRDF(final Intersection intersection, final TransportMode transportMode, final boolean isAllowingMultipleLobes) {
		Objects.requireNonNull(intersection, "intersection == null");
		Objects.requireNonNull(transportMode, "transportMode == null");
		
		return Optional.empty();
	}
	
	/**
	 * Computes the {@link PBRTBSDF} at {@code intersection}.
	 * <p>
	 * Returns an optional {@code PBRTBSDF} instance.
	 * <p>
	 * If either {@code intersection} or {@code transportMode} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param intersection the {@link Intersection} to compute the {@code PBRTBSDF} for
	 * @param transportMode the {@link TransportMode} to use
	 * @param isAllowingMultipleLobes {@code true} if, and only if, multiple lobes are allowed, {@code false} otherwise
	 * @return an optional {@code PBRTBSDF} instance
	 * @throws NullPointerException thrown if, and only if, either {@code intersection} or {@code transportMode} are {@code null}
	 */
	@Override
	public Optional<PBRTBSDF> computeBSDF(final Intersection intersection, final TransportMode transportMode, final boolean isAllowingMultipleLobes) {
		Objects.requireNonNull(intersection, "intersection == null");
		Objects.requireNonNull(transportMode, "transportMode == null");
		
		final Color3F colorEtaI = Color3F.WHITE;
		final Color3F colorEtaT = this.textureEta.getColor(intersection);
		final Color3F colorK = this.textureK.getColor(intersection);
		final Color3F colorRoughnessU = this.textureRoughnessU.getColor(intersection);
		final Color3F colorRoughnessV = this.textureRoughnessV.getColor(intersection);
		
		final float roughnessU = this.isRemappingRoughness ? MicrofacetDistribution.convertRoughnessToAlpha(colorRoughnessU.average()) : colorRoughnessU.average();
		final float roughnessV = this.isRemappingRoughness ? MicrofacetDistribution.convertRoughnessToAlpha(colorRoughnessV.average()) : colorRoughnessV.average();
		
		final Fresnel fresnel = new ConductorFresnel(colorEtaI, colorEtaT, colorK);
		
		final MicrofacetDistribution microfacetDistribution = new TrowbridgeReitzMicrofacetDistribution(true, roughnessU, roughnessV);
		
		return Optional.of(new PBRTBSDF(intersection, Arrays.asList(new TorranceSparrowPBRTBRDF(Color3F.WHITE, fresnel, microfacetDistribution))));
	}
	
	/**
	 * Returns a {@code String} with the name of this {@code MetalPBRTMaterial} instance.
	 * 
	 * @return a {@code String} with the name of this {@code MetalPBRTMaterial} instance
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code MetalPBRTMaterial} instance.
	 * 
	 * @return a {@code String} representation of this {@code MetalPBRTMaterial} instance
	 */
	@Override
	public String toString() {
		return String.format("new MetalPBRTMaterial(%s, %s, %s, %s, %s)", this.textureEta, this.textureK, this.textureRoughnessU, this.textureRoughnessV, Boolean.toString(this.isRemappingRoughness));
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
				if(!this.textureEta.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
				
				if(!this.textureK.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
				
				if(!this.textureRoughnessU.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
				
				if(!this.textureRoughnessV.accept(nodeHierarchicalVisitor)) {
					return nodeHierarchicalVisitor.visitLeave(this);
				}
			}
			
			return nodeHierarchicalVisitor.visitLeave(this);
		} catch(final RuntimeException e) {
			throw new NodeTraversalException(e);
		}
	}
	
	/**
	 * Compares {@code object} to this {@code MetalPBRTMaterial} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code MetalPBRTMaterial}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code MetalPBRTMaterial} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code MetalPBRTMaterial}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof MetalPBRTMaterial)) {
			return false;
		} else if(!Objects.equals(this.textureEta, MetalPBRTMaterial.class.cast(object).textureEta)) {
			return false;
		} else if(!Objects.equals(this.textureK, MetalPBRTMaterial.class.cast(object).textureK)) {
			return false;
		} else if(!Objects.equals(this.textureRoughnessU, MetalPBRTMaterial.class.cast(object).textureRoughnessU)) {
			return false;
		} else if(!Objects.equals(this.textureRoughnessV, MetalPBRTMaterial.class.cast(object).textureRoughnessV)) {
			return false;
		} else if(this.isRemappingRoughness != MetalPBRTMaterial.class.cast(object).isRemappingRoughness) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a {@code float[]} representation of this {@code MetalPBRTMaterial} instance.
	 * 
	 * @return a {@code float[]} representation of this {@code MetalPBRTMaterial} instance
	 */
	@Override
	public float[] toArray() {
		return new float[0];//TODO: Implement!
	}
	
	/**
	 * Returns an {@code int} with the ID of this {@code MetalPBRTMaterial} instance.
	 * 
	 * @return an {@code int} with the ID of this {@code MetalPBRTMaterial} instance
	 */
	@Override
	public int getID() {
		return ID;
	}
	
	/**
	 * Returns a hash code for this {@code MetalPBRTMaterial} instance.
	 * 
	 * @return a hash code for this {@code MetalPBRTMaterial} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.textureEta, this.textureK, this.textureRoughnessU, this.textureRoughnessV, Boolean.valueOf(this.isRemappingRoughness));
	}
}