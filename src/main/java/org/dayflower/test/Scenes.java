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
package org.dayflower.test;

import java.io.File;

import org.dayflower.geometry.AngleF;
import org.dayflower.geometry.Matrix44F;
import org.dayflower.geometry.Plane3F;
import org.dayflower.geometry.Point3F;
import org.dayflower.geometry.RectangularCuboid3F;
import org.dayflower.geometry.ProceduralTerrain3F;
import org.dayflower.geometry.Sphere3F;
import org.dayflower.geometry.TriangleMesh3F;
import org.dayflower.geometry.Vector2F;
import org.dayflower.image.Color3F;
import org.dayflower.scene.Camera;
import org.dayflower.scene.Primitive;
import org.dayflower.scene.Scene;
import org.dayflower.scene.background.PerezBackground;
import org.dayflower.scene.bxdf.AshikhminShirleyBRDF;
import org.dayflower.scene.material.AshikhminShirleyMaterial;
import org.dayflower.scene.material.LambertianMaterial;
import org.dayflower.scene.texture.BullseyeTexture;
import org.dayflower.scene.texture.CheckerboardTexture;
import org.dayflower.scene.texture.ConstantTexture;
import org.dayflower.scene.texture.ImageTexture;

/**
 * A class that consists exclusively of static methods that returns {@link Scene} instances.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Scenes {
	private Scenes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Scene} instance.
	 * 
	 * @return a {@code Scene} instance
	 */
	public static Scene newDefaultScene() {
		final
		Scene scene = new Scene(new PerezBackground(), new Camera(new Point3F(0.0F, 2.0F, 0.0F)));
		scene.addPrimitive(new Primitive(new AshikhminShirleyMaterial(), new Sphere3F(10.0F), new BullseyeTexture(new ConstantTexture(new Color3F(1.0F, 0.1F, 0.1F)), new ConstantTexture(new Color3F(0.5F, 0.1F, 0.1F)), new Point3F(0.0F, 10.0F, 0.0F), 2.0F), new ConstantTexture(), new ConstantTexture(), Matrix44F.translate(0.0F, 2.0F, 20.0F)));
		scene.addPrimitive(new Primitive(new LambertianMaterial(), new Plane3F(), new CheckerboardTexture(new ConstantTexture(new Color3F(0.1F)), new ConstantTexture(new Color3F(1.0F)), AngleF.degrees(90.0F), new Vector2F(0.5F, 0.5F)), new ConstantTexture(), new ConstantTexture(), Matrix44F.identity()));
		scene.addPrimitive(new Primitive(new AshikhminShirleyMaterial(new AshikhminShirleyBRDF(0.02F)), new RectangularCuboid3F(new Point3F(-1.0F), new Point3F(1.0F)), new CheckerboardTexture(new ConstantTexture(new Color3F(0.2F, 0.4F, 0.2F)), new ConstantTexture(new Color3F(0.6F, 0.8F, 0.6F)), AngleF.degrees(90.0F), new Vector2F(1.5F, 1.5F)), new ConstantTexture(), new ConstantTexture(), Matrix44F.translate(-2.0F, 1.0F, 5.0F)));
		scene.addPrimitive(new Primitive(new AshikhminShirleyMaterial(new AshikhminShirleyBRDF(0.02F)), TriangleMesh3F.readWavefrontObject(new File("./resources/smoothMonkey2.obj"), true).get(0), new ConstantTexture(new Color3F(0.5F)), new ConstantTexture(), new ConstantTexture(), Matrix44F.multiply(Matrix44F.multiply(Matrix44F.translate(1.0F, 1.0F, 5.0F), Matrix44F.rotateY(AngleF.degrees(190.0F))), Matrix44F.scale(1.0F))));
		
		return scene;
	}
	
	/**
	 * Returns a {@link Scene} instance.
	 * 
	 * @return a {@code Scene} instance
	 */
	public static Scene newTerrainScene() {
		final
		Scene scene = new Scene(new PerezBackground(), new Camera(new Point3F(10.0F, 4.0F, 10.0F)));
		scene.addPrimitive(new Primitive(new LambertianMaterial(), ProceduralTerrain3F.sin(), new ConstantTexture(Color3F.GRAY), new ConstantTexture(), new ConstantTexture(), Matrix44F.scale(1.0F)));
		
		return scene;
	}
	
	/**
	 * Returns a {@link Scene} instance.
	 * 
	 * @return a {@code Scene} instance
	 */
	public static Scene newZealotScene() {
		final
		Scene scene = new Scene(new PerezBackground(), new Camera(new Point3F(0.0F, 2.0F, 0.0F)));
		scene.addPrimitive(new Primitive(new LambertianMaterial(), new Plane3F(), new CheckerboardTexture(new ConstantTexture(new Color3F(0.1F)), new ConstantTexture(new Color3F(1.0F)), AngleF.degrees(90.0F), new Vector2F(0.5F, 0.5F)), new ConstantTexture(), new ConstantTexture(), Matrix44F.identity()));
		scene.addPrimitive(new Primitive(new AshikhminShirleyMaterial(new AshikhminShirleyBRDF(0.02F)), TriangleMesh3F.readWavefrontObject(new File("./resources/Zealot.obj"), true).get(0), ImageTexture.load(new File("./resources/Zealot_albedo.png")), ImageTexture.load(new File("./resources/Zealot_emissive.png")), ImageTexture.load(new File("./resources/Zealot_normal.png")), Matrix44F.multiply(Matrix44F.multiply(Matrix44F.translate(0.0F, 0.0F, 5.0F), Matrix44F.rotateY(AngleF.degrees(180.0F))), Matrix44F.scale(0.05F))));
		
		return scene;
	}
}