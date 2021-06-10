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
package org.dayflower.scene.compiler;

import static org.dayflower.utility.Ints.pack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dayflower.node.NodeFilter;
import org.dayflower.scene.Material;
import org.dayflower.scene.Scene;
import org.dayflower.scene.material.ClearCoatMaterial;
import org.dayflower.scene.material.DisneyMaterial;
import org.dayflower.scene.material.GlassMaterial;
import org.dayflower.scene.material.GlossyMaterial;
import org.dayflower.scene.material.MatteMaterial;
import org.dayflower.scene.material.MetalMaterial;
import org.dayflower.scene.material.MirrorMaterial;
import org.dayflower.scene.material.PlasticMaterial;
import org.dayflower.scene.material.SubstrateMaterial;
import org.dayflower.scene.texture.Texture;
import org.dayflower.utility.Ints;

final class MaterialCache {
	private final List<ClearCoatMaterial> distinctClearCoatMaterials;
	private final List<DisneyMaterial> distinctDisneyMaterials;
	private final List<GlassMaterial> distinctGlassMaterials;
	private final List<GlossyMaterial> distinctGlossyMaterials;
	private final List<Material> distinctMaterials;
	private final List<MatteMaterial> distinctMatteMaterials;
	private final List<MetalMaterial> distinctMetalMaterials;
	private final List<MirrorMaterial> distinctMirrorMaterials;
	private final List<PlasticMaterial> distinctPlasticMaterials;
	private final List<SubstrateMaterial> distinctSubstrateMaterials;
	private final Map<ClearCoatMaterial, Integer> distinctToOffsetsClearCoatMaterials;
	private final Map<DisneyMaterial, Integer> distinctToOffsetsDisneyMaterials;
	private final Map<GlassMaterial, Integer> distinctToOffsetsGlassMaterials;
	private final Map<GlossyMaterial, Integer> distinctToOffsetsGlossyMaterials;
	private final Map<MatteMaterial, Integer> distinctToOffsetsMatteMaterials;
	private final Map<MetalMaterial, Integer> distinctToOffsetsMetalMaterials;
	private final Map<MirrorMaterial, Integer> distinctToOffsetsMirrorMaterials;
	private final Map<PlasticMaterial, Integer> distinctToOffsetsPlasticMaterials;
	private final Map<SubstrateMaterial, Integer> distinctToOffsetsSubstrateMaterials;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public MaterialCache() {
		this.distinctClearCoatMaterials = new ArrayList<>();
		this.distinctDisneyMaterials = new ArrayList<>();
		this.distinctGlassMaterials = new ArrayList<>();
		this.distinctGlossyMaterials = new ArrayList<>();
		this.distinctMaterials = new ArrayList<>();
		this.distinctMatteMaterials = new ArrayList<>();
		this.distinctMetalMaterials = new ArrayList<>();
		this.distinctMirrorMaterials = new ArrayList<>();
		this.distinctPlasticMaterials = new ArrayList<>();
		this.distinctSubstrateMaterials = new ArrayList<>();
		this.distinctToOffsetsClearCoatMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsDisneyMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsGlassMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsGlossyMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsMatteMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsMetalMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsMirrorMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsPlasticMaterials = new LinkedHashMap<>();
		this.distinctToOffsetsSubstrateMaterials = new LinkedHashMap<>();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean contains(final Material material) {
		return this.distinctMaterials.contains(Objects.requireNonNull(material, "material == null"));
	}
	
	public int findOffsetFor(final Material material) {
		Objects.requireNonNull(material, "material == null");
		
		if(material instanceof ClearCoatMaterial) {
			return this.distinctToOffsetsClearCoatMaterials.get(material).intValue();
		} else if(material instanceof DisneyMaterial) {
			return this.distinctToOffsetsDisneyMaterials.get(material).intValue();
		} else if(material instanceof GlassMaterial) {
			return this.distinctToOffsetsGlassMaterials.get(material).intValue();
		} else if(material instanceof GlossyMaterial) {
			return this.distinctToOffsetsGlossyMaterials.get(material).intValue();
		} else if(material instanceof MatteMaterial) {
			return this.distinctToOffsetsMatteMaterials.get(material).intValue();
		} else if(material instanceof MetalMaterial) {
			return this.distinctToOffsetsMetalMaterials.get(material).intValue();
		} else if(material instanceof MirrorMaterial) {
			return this.distinctToOffsetsMirrorMaterials.get(material).intValue();
		} else if(material instanceof PlasticMaterial) {
			return this.distinctToOffsetsPlasticMaterials.get(material).intValue();
		} else if(material instanceof SubstrateMaterial) {
			return this.distinctToOffsetsSubstrateMaterials.get(material).intValue();
		} else {
			return 0;
		}
	}
	
	public int[] toMaterialClearCoatMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialClearCoatMaterialArray = Ints.toArray(this.distinctClearCoatMaterials, clearCoatMaterial -> clearCoatMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctClearCoatMaterials.size(); i++) {
			final ClearCoatMaterial clearCoatMaterial = this.distinctClearCoatMaterials.get(i);
			
			final Texture textureEmission = clearCoatMaterial.getTextureEmission();
			final Texture textureKD = clearCoatMaterial.getTextureKD();
			final Texture textureKS = clearCoatMaterial.getTextureKS();
			
			final int materialClearCoatMaterialArrayTextureEmission = i * ClearCoatMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialClearCoatMaterialArrayTextureKDAndTextureKS = i * ClearCoatMaterial.ARRAY_LENGTH + ClearCoatMaterial.ARRAY_OFFSET_TEXTURE_K_D_AND_TEXTURE_K_S;
			
			materialClearCoatMaterialArray[materialClearCoatMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialClearCoatMaterialArray[materialClearCoatMaterialArrayTextureKDAndTextureKS] = pack(textureKD.getID(), textureCache.findOffsetFor(textureKD), textureKS.getID(), textureCache.findOffsetFor(textureKS));
		}
		
		return materialClearCoatMaterialArray;
	}
	
	public int[] toMaterialDisneyMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialDisneyMaterialArray = Ints.toArray(this.distinctDisneyMaterials, disneyMaterial -> disneyMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctDisneyMaterials.size(); i++) {
			final DisneyMaterial disneyMaterial = this.distinctDisneyMaterials.get(i);
			
			final Texture textureEmission = disneyMaterial.getTextureEmission();
			final Texture textureAnisotropic = disneyMaterial.getTextureAnisotropic();
			final Texture textureClearCoat = disneyMaterial.getTextureClearCoat();
			final Texture textureClearCoatGloss = disneyMaterial.getTextureClearCoatGloss();
			final Texture textureColor = disneyMaterial.getTextureColor();
			final Texture textureDiffuseTransmission = disneyMaterial.getTextureDiffuseTransmission();
			final Texture textureEta = disneyMaterial.getTextureEta();
			final Texture textureFlatness = disneyMaterial.getTextureFlatness();
			final Texture textureMetallic = disneyMaterial.getTextureMetallic();
			final Texture textureRoughness = disneyMaterial.getTextureRoughness();
			final Texture textureScatterDistance = disneyMaterial.getTextureScatterDistance();
			final Texture textureSheen = disneyMaterial.getTextureSheen();
			final Texture textureSheenTint = disneyMaterial.getTextureSheenTint();
			final Texture textureSpecularTint = disneyMaterial.getTextureSpecularTint();
			final Texture textureSpecularTransmission = disneyMaterial.getTextureSpecularTransmission();
			
			final boolean isThin = disneyMaterial.isThin();
			
			final int materialDisneyMaterialArrayTextureEmissionAndTextureAnisotropic = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_EMISSION_AND_TEXTURE_ANISOTROPIC;
			final int materialDisneyMaterialArrayTextureClearCoatAndTextureClearCoatGloss = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_CLEAR_COAT_AND_TEXTURE_CLEAR_COAT_GLOSS;
			final int materialDisneyMaterialArrayTextureColorAndTextureDiffuseTransmission = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_COLOR_AND_TEXTURE_DIFFUSE_TRANSMISSION;
			final int materialDisneyMaterialArrayTextureEtaAndTextureFlatness = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_ETA_AND_TEXTURE_FLATNESS;
			final int materialDisneyMaterialArrayTextureMetallicAndTextureRoughness = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_METALLIC_AND_TEXTURE_ROUGHNESS;
			final int materialDisneyMaterialArrayTextureScatterDistanceAndTextureSheen = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_SCATTER_DISTANCE_AND_TEXTURE_SHEEN;
			final int materialDisneyMaterialArrayTextureSheenTintAndTextureSpecularTint = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_SHEEN_TINT_AND_TEXTURE_SPECULAR_TINT;
			final int materialDisneyMaterialArrayTextureSpecularTransmissionAndIsThin = i * DisneyMaterial.ARRAY_LENGTH + DisneyMaterial.ARRAY_OFFSET_TEXTURE_SPECULAR_TRANSMISSION_AND_IS_THIN;
			
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureEmissionAndTextureAnisotropic] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), textureAnisotropic.getID(), textureCache.findOffsetFor(textureAnisotropic));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureClearCoatAndTextureClearCoatGloss] = pack(textureClearCoat.getID(), textureCache.findOffsetFor(textureClearCoat), textureClearCoatGloss.getID(), textureCache.findOffsetFor(textureClearCoatGloss));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureColorAndTextureDiffuseTransmission] = pack(textureColor.getID(), textureCache.findOffsetFor(textureColor), textureDiffuseTransmission.getID(), textureCache.findOffsetFor(textureDiffuseTransmission));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureEtaAndTextureFlatness] = pack(textureEta.getID(), textureCache.findOffsetFor(textureEta), textureFlatness.getID(), textureCache.findOffsetFor(textureFlatness));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureMetallicAndTextureRoughness] = pack(textureMetallic.getID(), textureCache.findOffsetFor(textureMetallic), textureRoughness.getID(), textureCache.findOffsetFor(textureRoughness));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureScatterDistanceAndTextureSheen] = pack(textureScatterDistance.getID(), textureCache.findOffsetFor(textureScatterDistance), textureSheen.getID(), textureCache.findOffsetFor(textureSheen));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureSheenTintAndTextureSpecularTint] = pack(textureSheenTint.getID(), textureCache.findOffsetFor(textureSheenTint), textureSpecularTint.getID(), textureCache.findOffsetFor(textureSpecularTint));
			materialDisneyMaterialArray[materialDisneyMaterialArrayTextureSpecularTransmissionAndIsThin] = pack(textureSpecularTransmission.getID(), textureCache.findOffsetFor(textureSpecularTransmission), isThin ? 1 : 0, 0);
		}
		
		return materialDisneyMaterialArray;
	}
	
	public int[] toMaterialGlassMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialGlassMaterialArray = Ints.toArray(this.distinctGlassMaterials, glassMaterial -> glassMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctGlassMaterials.size(); i++) {
			final GlassMaterial glassMaterial = this.distinctGlassMaterials.get(i);
			
			final Texture textureEmission = glassMaterial.getTextureEmission();
			final Texture textureEta = glassMaterial.getTextureEta();
			final Texture textureKR = glassMaterial.getTextureKR();
			final Texture textureKT = glassMaterial.getTextureKT();
			final Texture textureRoughnessU = glassMaterial.getTextureRoughnessU();
			final Texture textureRoughnessV = glassMaterial.getTextureRoughnessV();
			
			final int materialGlassMaterialArrayTextureEmissionAndTextureEta = i * GlassMaterial.ARRAY_LENGTH + GlassMaterial.ARRAY_OFFSET_TEXTURE_EMISSION_AND_TEXTURE_ETA;
			final int materialGlassMaterialArrayTextureKRAndTextureKT = i * GlassMaterial.ARRAY_LENGTH + GlassMaterial.ARRAY_OFFSET_TEXTURE_K_R_AND_TEXTURE_K_T;
			final int materialGlassMaterialArrayTextureRoughnessUAndTextureRoughnessV = i * GlassMaterial.ARRAY_LENGTH + GlassMaterial.ARRAY_OFFSET_TEXTURE_ROUGHNESS_U_AND_TEXTURE_ROUGHNESS_V;
			
			materialGlassMaterialArray[materialGlassMaterialArrayTextureEmissionAndTextureEta] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), textureEta.getID(), textureCache.findOffsetFor(textureEta));
			materialGlassMaterialArray[materialGlassMaterialArrayTextureKRAndTextureKT] = pack(textureKR.getID(), textureCache.findOffsetFor(textureKR), textureKT.getID(), textureCache.findOffsetFor(textureKT));
			materialGlassMaterialArray[materialGlassMaterialArrayTextureRoughnessUAndTextureRoughnessV] = pack(textureRoughnessU.getID(), textureCache.findOffsetFor(textureRoughnessU), textureRoughnessV.getID(), textureCache.findOffsetFor(textureRoughnessV));
		}
		
		return materialGlassMaterialArray;
	}
	
	public int[] toMaterialGlossyMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialGlossyMaterialArray = Ints.toArray(this.distinctGlossyMaterials, glossyMaterial -> glossyMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctGlossyMaterials.size(); i++) {
			final GlossyMaterial glossyMaterial = this.distinctGlossyMaterials.get(i);
			
			final Texture textureEmission = glossyMaterial.getTextureEmission();
			final Texture textureKR = glossyMaterial.getTextureKR();
			final Texture textureRoughness = glossyMaterial.getTextureRoughness();
			
			final int materialGlossyMaterialArrayTextureEmission = i * GlossyMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialGlossyMaterialArrayTextureKRAndTextureRoughness = i * GlossyMaterial.ARRAY_LENGTH + GlossyMaterial.ARRAY_OFFSET_TEXTURE_K_R_AND_TEXTURE_ROUGHNESS;
			
			materialGlossyMaterialArray[materialGlossyMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialGlossyMaterialArray[materialGlossyMaterialArrayTextureKRAndTextureRoughness] = pack(textureKR.getID(), textureCache.findOffsetFor(textureKR), textureRoughness.getID(), textureCache.findOffsetFor(textureRoughness));
		}
		
		return materialGlossyMaterialArray;
	}
	
	public int[] toMaterialMatteMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialMatteMaterialArray = Ints.toArray(this.distinctMatteMaterials, matteMaterial -> matteMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctMatteMaterials.size(); i++) {
			final MatteMaterial matteMaterial = this.distinctMatteMaterials.get(i);
			
			final Texture textureEmission = matteMaterial.getTextureEmission();
			final Texture textureAngle = matteMaterial.getTextureAngle();
			final Texture textureKD = matteMaterial.getTextureKD();
			
			final int materialMatteMaterialArrayTextureEmission = i * MatteMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialMatteMaterialArrayTextureAngleAndTextureKD = i * MatteMaterial.ARRAY_LENGTH + MatteMaterial.ARRAY_OFFSET_TEXTURE_ANGLE_AND_TEXTURE_K_D;
			
			materialMatteMaterialArray[materialMatteMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialMatteMaterialArray[materialMatteMaterialArrayTextureAngleAndTextureKD] = pack(textureAngle.getID(), textureCache.findOffsetFor(textureAngle), textureKD.getID(), textureCache.findOffsetFor(textureKD));
		}
		
		return materialMatteMaterialArray;
	}
	
	public int[] toMaterialMetalMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialMetalMaterialArray = Ints.toArray(this.distinctMetalMaterials, metalMaterial -> metalMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctMetalMaterials.size(); i++) {
			final MetalMaterial metalMaterial = this.distinctMetalMaterials.get(i);
			
			final Texture textureEmission = metalMaterial.getTextureEmission();
			final Texture textureEta = metalMaterial.getTextureEta();
			final Texture textureK = metalMaterial.getTextureK();
			final Texture textureRoughnessU = metalMaterial.getTextureRoughnessU();
			final Texture textureRoughnessV = metalMaterial.getTextureRoughnessV();
			
			final int materialMetalMaterialArrayTextureEmission = i * MetalMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialMetalMaterialArrayTextureEtaAndTextureK = i * MetalMaterial.ARRAY_LENGTH + MetalMaterial.ARRAY_OFFSET_TEXTURE_ETA_AND_TEXTURE_K;
			final int materialMetalMaterialArrayTextureRoughnessUAndTextureRoughnessV = i * MetalMaterial.ARRAY_LENGTH + MetalMaterial.ARRAY_OFFSET_TEXTURE_ROUGHNESS_U_AND_TEXTURE_ROUGHNESS_V;
			
			materialMetalMaterialArray[materialMetalMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialMetalMaterialArray[materialMetalMaterialArrayTextureEtaAndTextureK] = pack(textureEta.getID(), textureCache.findOffsetFor(textureEta), textureK.getID(), textureCache.findOffsetFor(textureK));
			materialMetalMaterialArray[materialMetalMaterialArrayTextureRoughnessUAndTextureRoughnessV] = pack(textureRoughnessU.getID(), textureCache.findOffsetFor(textureRoughnessU), textureRoughnessV.getID(), textureCache.findOffsetFor(textureRoughnessV));
		}
		
		return materialMetalMaterialArray;
	}
	
	public int[] toMaterialMirrorMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialMirrorMaterialArray = Ints.toArray(this.distinctMirrorMaterials, mirrorMaterial -> mirrorMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctMirrorMaterials.size(); i++) {
			final MirrorMaterial mirrorMaterial = this.distinctMirrorMaterials.get(i);
			
			final Texture textureEmission = mirrorMaterial.getTextureEmission();
			final Texture textureKR = mirrorMaterial.getTextureKR();
			
			final int materialMirrorMaterialArrayTextureEmissionAndTextureKR = i * MirrorMaterial.ARRAY_LENGTH + MirrorMaterial.ARRAY_OFFSET_TEXTURE_EMISSION_AND_TEXTURE_K_R;
			
			materialMirrorMaterialArray[materialMirrorMaterialArrayTextureEmissionAndTextureKR] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), textureKR.getID(), textureCache.findOffsetFor(textureKR));
		}
		
		return materialMirrorMaterialArray;
	}
	
	public int[] toMaterialPlasticMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialPlasticMaterialArray = Ints.toArray(this.distinctPlasticMaterials, plasticMaterial -> plasticMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctPlasticMaterials.size(); i++) {
			final PlasticMaterial plasticMaterial = this.distinctPlasticMaterials.get(i);
			
			final Texture textureEmission = plasticMaterial.getTextureEmission();
			final Texture textureKD = plasticMaterial.getTextureKD();
			final Texture textureKS = plasticMaterial.getTextureKS();
			final Texture textureRoughness = plasticMaterial.getTextureRoughness();
			
			final int materialPlasticMaterialArrayTextureEmission = i * PlasticMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialPlasticMaterialArrayTextureKDAndTextureKS = i * PlasticMaterial.ARRAY_LENGTH + PlasticMaterial.ARRAY_OFFSET_TEXTURE_K_D_AND_TEXTURE_K_S;
			final int materialPlasticMaterialArrayTextureRoughness = i * PlasticMaterial.ARRAY_LENGTH + PlasticMaterial.ARRAY_OFFSET_TEXTURE_ROUGHNESS;
			
			materialPlasticMaterialArray[materialPlasticMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialPlasticMaterialArray[materialPlasticMaterialArrayTextureKDAndTextureKS] = pack(textureKD.getID(), textureCache.findOffsetFor(textureKD), textureKS.getID(), textureCache.findOffsetFor(textureKS));
			materialPlasticMaterialArray[materialPlasticMaterialArrayTextureRoughness] = pack(textureRoughness.getID(), textureCache.findOffsetFor(textureRoughness), 0, 0);
		}
		
		return materialPlasticMaterialArray;
	}
	
	public int[] toMaterialSubstrateMaterialArray(final TextureCache textureCache) {
		Objects.requireNonNull(textureCache, "textureCache == null");
		
		final int[] materialSubstrateMaterialArray = Ints.toArray(this.distinctSubstrateMaterials, substrateMaterial -> substrateMaterial.toArray(), 1);
		
		for(int i = 0; i < this.distinctSubstrateMaterials.size(); i++) {
			final SubstrateMaterial substrateMaterial = this.distinctSubstrateMaterials.get(i);
			
			final Texture textureEmission = substrateMaterial.getTextureEmission();
			final Texture textureKD = substrateMaterial.getTextureKD();
			final Texture textureKS = substrateMaterial.getTextureKS();
			final Texture textureRoughnessU = substrateMaterial.getTextureRoughnessU();
			final Texture textureRoughnessV = substrateMaterial.getTextureRoughnessV();
			
			final int materialSubstrateMaterialArrayTextureEmission = i * SubstrateMaterial.ARRAY_LENGTH + Material.ARRAY_OFFSET_TEXTURE_EMISSION;
			final int materialSubstrateMaterialArrayTextureKDAndTextureKS = i * SubstrateMaterial.ARRAY_LENGTH + SubstrateMaterial.ARRAY_OFFSET_TEXTURE_K_D_AND_TEXTURE_K_S;
			final int materialSubstrateMaterialArrayTextureRoughnessUAndTextureRoughnessV = i * SubstrateMaterial.ARRAY_LENGTH + SubstrateMaterial.ARRAY_OFFSET_TEXTURE_ROUGHNESS_U_AND_TEXTURE_ROUGHNESS_V;
			
			materialSubstrateMaterialArray[materialSubstrateMaterialArrayTextureEmission] = pack(textureEmission.getID(), textureCache.findOffsetFor(textureEmission), 0, 0);
			materialSubstrateMaterialArray[materialSubstrateMaterialArrayTextureKDAndTextureKS] = pack(textureKD.getID(), textureCache.findOffsetFor(textureKD), textureKS.getID(), textureCache.findOffsetFor(textureKS));
			materialSubstrateMaterialArray[materialSubstrateMaterialArrayTextureRoughnessUAndTextureRoughnessV] = pack(textureRoughnessU.getID(), textureCache.findOffsetFor(textureRoughnessU), textureRoughnessV.getID(), textureCache.findOffsetFor(textureRoughnessV));
		}
		
		return materialSubstrateMaterialArray;
	}
	
	public void clear() {
		this.distinctClearCoatMaterials.clear();
		this.distinctDisneyMaterials.clear();
		this.distinctGlassMaterials.clear();
		this.distinctGlossyMaterials.clear();
		this.distinctMaterials.clear();
		this.distinctMatteMaterials.clear();
		this.distinctMetalMaterials.clear();
		this.distinctMirrorMaterials.clear();
		this.distinctPlasticMaterials.clear();
		this.distinctSubstrateMaterials.clear();
		this.distinctToOffsetsClearCoatMaterials.clear();
		this.distinctToOffsetsDisneyMaterials.clear();
		this.distinctToOffsetsGlassMaterials.clear();
		this.distinctToOffsetsGlossyMaterials.clear();
		this.distinctToOffsetsMatteMaterials.clear();
		this.distinctToOffsetsMetalMaterials.clear();
		this.distinctToOffsetsMirrorMaterials.clear();
		this.distinctToOffsetsPlasticMaterials.clear();
		this.distinctToOffsetsSubstrateMaterials.clear();
	}
	
	public void setup(final Scene scene) {
		Objects.requireNonNull(scene, "scene == null");
		
//		Add all distinct ClearCoatMaterial instances:
		this.distinctClearCoatMaterials.clear();
		this.distinctClearCoatMaterials.addAll(NodeFilter.filterAllDistinct(scene, ClearCoatMaterial.class));
		
//		Add all distinct DisneyMaterial instances:
		this.distinctDisneyMaterials.clear();
		this.distinctDisneyMaterials.addAll(NodeFilter.filterAllDistinct(scene, DisneyMaterial.class));
		
//		Add all distinct GlassMaterial instances:
		this.distinctGlassMaterials.clear();
		this.distinctGlassMaterials.addAll(NodeFilter.filterAllDistinct(scene, GlassMaterial.class));
		
//		Add all distinct GlossyMaterial instances:
		this.distinctGlossyMaterials.clear();
		this.distinctGlossyMaterials.addAll(NodeFilter.filterAllDistinct(scene, GlossyMaterial.class));
		
//		Add all distinct MatteMaterial instances:
		this.distinctMatteMaterials.clear();
		this.distinctMatteMaterials.addAll(NodeFilter.filterAllDistinct(scene, MatteMaterial.class));
		
//		Add all distinct MetalMaterial instances:
		this.distinctMetalMaterials.clear();
		this.distinctMetalMaterials.addAll(NodeFilter.filterAllDistinct(scene, MetalMaterial.class));
		
//		Add all distinct MirrorMaterial instances:
		this.distinctMirrorMaterials.clear();
		this.distinctMirrorMaterials.addAll(NodeFilter.filterAllDistinct(scene, MirrorMaterial.class));
		
//		Add all distinct PlasticMaterial instances:
		this.distinctPlasticMaterials.clear();
		this.distinctPlasticMaterials.addAll(NodeFilter.filterAllDistinct(scene, PlasticMaterial.class));
		
//		Add all distinct SubstrateMaterial instances:
		this.distinctSubstrateMaterials.clear();
		this.distinctSubstrateMaterials.addAll(NodeFilter.filterAllDistinct(scene, SubstrateMaterial.class));
		
//		Add all distinct Material instances:
		this.distinctMaterials.clear();
		this.distinctMaterials.addAll(this.distinctClearCoatMaterials);
		this.distinctMaterials.addAll(this.distinctDisneyMaterials);
		this.distinctMaterials.addAll(this.distinctGlassMaterials);
		this.distinctMaterials.addAll(this.distinctGlossyMaterials);
		this.distinctMaterials.addAll(this.distinctMatteMaterials);
		this.distinctMaterials.addAll(this.distinctMetalMaterials);
		this.distinctMaterials.addAll(this.distinctMirrorMaterials);
		this.distinctMaterials.addAll(this.distinctPlasticMaterials);
		this.distinctMaterials.addAll(this.distinctSubstrateMaterials);
		
//		Create offset mappings for all distinct ClearCoatMaterial instances:
		this.distinctToOffsetsClearCoatMaterials.clear();
		this.distinctToOffsetsClearCoatMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctClearCoatMaterials, ClearCoatMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct DisneyMaterial instances:
		this.distinctToOffsetsDisneyMaterials.clear();
		this.distinctToOffsetsDisneyMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctDisneyMaterials, DisneyMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct GlassMaterial instances:
		this.distinctToOffsetsGlassMaterials.clear();
		this.distinctToOffsetsGlassMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctGlassMaterials, GlassMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct GlossyMaterial instances:
		this.distinctToOffsetsGlossyMaterials.clear();
		this.distinctToOffsetsGlossyMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctGlossyMaterials, GlossyMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct MatteMaterial instances:
		this.distinctToOffsetsMatteMaterials.clear();
		this.distinctToOffsetsMatteMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctMatteMaterials, MatteMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct MetalMaterial instances:
		this.distinctToOffsetsMetalMaterials.clear();
		this.distinctToOffsetsMetalMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctMetalMaterials, MetalMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct MirrorMaterial instances:
		this.distinctToOffsetsMirrorMaterials.clear();
		this.distinctToOffsetsMirrorMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctMirrorMaterials, MirrorMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct PlasticMaterial instances:
		this.distinctToOffsetsPlasticMaterials.clear();
		this.distinctToOffsetsPlasticMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctPlasticMaterials, PlasticMaterial.ARRAY_LENGTH));
		
//		Create offset mappings for all distinct SubstrateMaterial instances:
		this.distinctToOffsetsSubstrateMaterials.clear();
		this.distinctToOffsetsSubstrateMaterials.putAll(NodeFilter.mapDistinctToOffsets(this.distinctSubstrateMaterials, SubstrateMaterial.ARRAY_LENGTH));
	}
}