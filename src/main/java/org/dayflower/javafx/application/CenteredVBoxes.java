/**
 * Copyright 2014 - 2022 J&#246;rgen Lundgren
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
package org.dayflower.javafx.application;

import java.util.Arrays;
import java.util.Objects;

import org.dayflower.geometry.AngleF;
import org.dayflower.geometry.Matrix44F;
import org.dayflower.geometry.Point3F;
import org.dayflower.geometry.Quaternion4F;
import org.dayflower.geometry.Shape3F;
import org.dayflower.geometry.shape.Cone3F;
import org.dayflower.geometry.shape.Cylinder3F;
import org.dayflower.geometry.shape.Disk3F;
import org.dayflower.geometry.shape.Hyperboloid3F;
import org.dayflower.geometry.shape.Paraboloid3F;
import org.dayflower.geometry.shape.Plane3F;
import org.dayflower.geometry.shape.Rectangle3F;
import org.dayflower.geometry.shape.RectangularCuboid3F;
import org.dayflower.geometry.shape.Sphere3F;
import org.dayflower.geometry.shape.Torus3F;
import org.dayflower.geometry.shape.Triangle3F;
import org.dayflower.javafx.scene.layout.CenteredVBox;
import org.dayflower.renderer.CombinedProgressiveImageOrderRenderer;
import org.dayflower.renderer.ProgressiveImageOrderRenderer;
import org.dayflower.renderer.Renderer;
import org.dayflower.renderer.RenderingAlgorithm;
import org.dayflower.renderer.gpu.GPURenderer;
import org.dayflower.scene.AbstractCameraObserver;
import org.dayflower.scene.Camera;
import org.dayflower.scene.Lens;
import org.dayflower.scene.Material;
import org.dayflower.scene.Primitive;
import org.dayflower.scene.Scene;
import org.dayflower.scene.Transform;
import org.dayflower.scene.material.BullseyeMaterial;
import org.dayflower.scene.material.CheckerboardMaterial;
import org.dayflower.scene.material.ClearCoatMaterial;
import org.dayflower.scene.material.DisneyMaterial;
import org.dayflower.scene.material.GlassMaterial;
import org.dayflower.scene.material.GlossyMaterial;
import org.dayflower.scene.material.HairMaterial;
import org.dayflower.scene.material.MatteMaterial;
import org.dayflower.scene.material.MetalMaterial;
import org.dayflower.scene.material.MirrorMaterial;
import org.dayflower.scene.material.PlasticMaterial;
import org.dayflower.scene.material.PolkaDotMaterial;
import org.dayflower.scene.material.SubstrateMaterial;
import org.dayflower.scene.material.UberMaterial;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

final class CenteredVBoxes {
	private CenteredVBoxes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static CenteredVBox createCenteredVBoxForCamera(final Renderer renderer) {
		final Camera camera = renderer.getScene().getCamera();
		
		final CenteredVBox centeredVBox = new CenteredVBox();
		
		final ComboBox<Lens> comboBoxLens = centeredVBox.addComboBox(Arrays.asList(Lens.FISHEYE, Lens.THIN), camera.getLens());
		
		centeredVBox.addButton("Update Lens", actionEvent -> camera.setLens(comboBoxLens.getValue()), false);
		centeredVBox.addLabel("Aperture Radius", 12.0D);
		
		final Slider sliderApertureRadius = centeredVBox.addSlider(0.0D, 1.0D, camera.getApertureRadius(), 0.1D, 0.5D, true, true, false, (observableValue, oldValue, newValue) -> doHandleCameraApertureRadiusChange(camera, newValue.floatValue()));
		
		centeredVBox.addLabel("Focal Distance", 12.0D);
		
		final Slider sliderFocalDistance = centeredVBox.addSlider(0.0D, 100.0D, camera.getFocalDistance(), 1.0D, 20.0D, true, true, false, (observableValue, oldValue, newValue) -> doHandleCameraFocalDistanceChange(camera, newValue.floatValue()));
		
		centeredVBox.addLabel("Pitch", 12.0D);
		
		final Slider sliderPitch = centeredVBox.addSlider(-90.0D, 90.0D, camera.getPitch().getDegrees(), 10.0D, 20.0D, true, true, false, (observableValue, oldValue, newValue) -> doHandleCameraPitchChange(camera, newValue.floatValue()));
		
		centeredVBox.addLabel("Yaw", 12.0D);
		
		final Slider sliderYaw = centeredVBox.addSlider(0.0D, 360.0D, camera.getPitch().getDegrees(), 20.0D, 40.0D, true, true, false, (observableValue, oldValue, newValue) -> doHandleCameraYawChange(camera, newValue.floatValue()));
		
		camera.addCameraObserver(new CameraObserverImpl(sliderApertureRadius, sliderFocalDistance, sliderPitch, sliderYaw));
		
		return centeredVBox;
	}
	
	public static CenteredVBox createCenteredVBoxForRenderer(final Renderer renderer) {
		final CenteredVBox centeredVBox = new CenteredVBox();
		
		if(renderer instanceof CombinedProgressiveImageOrderRenderer) {
			final CombinedProgressiveImageOrderRenderer combinedProgressiveImageOrderRenderer = CombinedProgressiveImageOrderRenderer.class.cast(renderer);
			
			final ComboBox<RenderingAlgorithm> comboBox = centeredVBox.addComboBox(Arrays.asList(RenderingAlgorithm.AMBIENT_OCCLUSION, RenderingAlgorithm.DEPTH_CAMERA, RenderingAlgorithm.PATH_TRACING, RenderingAlgorithm.RAY_CASTING, RenderingAlgorithm.RAY_TRACING), combinedProgressiveImageOrderRenderer.getRenderingAlgorithm());
			
			centeredVBox.addButton("Update Renderer", actionEvent -> {
				final RenderingAlgorithm renderingAlgorithm = comboBox.getValue();
				
				if(renderingAlgorithm != null) {
					combinedProgressiveImageOrderRenderer.setRenderingAlgorithm(renderingAlgorithm);
					combinedProgressiveImageOrderRenderer.renderShutdown();
					combinedProgressiveImageOrderRenderer.clear();
				}
			}, false);
		}
		
		return centeredVBox;
	}
	
	public static CenteredVBox createCenteredVBoxForScene(final Renderer renderer) {
		final CenteredVBox centeredVBox = new CenteredVBox();
		
		final ComboBox<String> comboBoxMaterial = centeredVBox.addComboBox(Arrays.asList(BullseyeMaterial.NAME, CheckerboardMaterial.NAME, ClearCoatMaterial.NAME, DisneyMaterial.NAME, GlassMaterial.NAME, GlossyMaterial.NAME, HairMaterial.NAME, MatteMaterial.NAME, MetalMaterial.NAME, MirrorMaterial.NAME, PlasticMaterial.NAME, PolkaDotMaterial.NAME, SubstrateMaterial.NAME, UberMaterial.NAME), MatteMaterial.NAME);
		final ComboBox<String> comboBoxShape = centeredVBox.addComboBox(Arrays.asList(Cone3F.NAME, Cylinder3F.NAME, Disk3F.NAME, Hyperboloid3F.NAME, Paraboloid3F.NAME, Plane3F.NAME, Rectangle3F.NAME, RectangularCuboid3F.NAME, Sphere3F.NAME, Torus3F.NAME, Triangle3F.NAME), Plane3F.NAME);
		
		centeredVBox.addButton("Add Primitive", actionEvent -> {
			final Material material = doCreateMaterial(comboBoxMaterial);
			
			final Shape3F shape = doCreateShape(comboBoxShape);
			
			if(material != null && shape != null) {
				final
				Scene scene = renderer.getScene();
				scene.addPrimitive(new Primitive(material, shape, new Transform(doGetPointByShape(renderer, shape), doGetQuaternionByShape(shape))));
			}
		}, renderer instanceof GPURenderer);
		centeredVBox.addSeparator();
		centeredVBox.addButton("Build Acceleration Structure", actionEvent -> {
			renderer.getScene().buildAccelerationStructure();
			renderer.renderShutdown();
			
			if(renderer instanceof ProgressiveImageOrderRenderer) {
				ProgressiveImageOrderRenderer.class.cast(renderer).clear();
			}
		}, renderer instanceof GPURenderer);
		centeredVBox.addButton("Clear Acceleration Structure", actionEvent -> {
			renderer.getScene().clearAccelerationStructure();
			renderer.renderShutdown();
			
			if(renderer instanceof ProgressiveImageOrderRenderer) {
				ProgressiveImageOrderRenderer.class.cast(renderer).clear();
			}
		}, renderer instanceof GPURenderer);
		
		return centeredVBox;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Material doCreateMaterial(final ComboBox<String> comboBoxMaterial) {
		final String selectedItem = comboBoxMaterial.getSelectionModel().getSelectedItem();
		
		if(selectedItem != null) {
			switch(selectedItem) {
				case BullseyeMaterial.NAME:
					return new BullseyeMaterial();
				case CheckerboardMaterial.NAME:
					return new CheckerboardMaterial();
				case ClearCoatMaterial.NAME:
					return new ClearCoatMaterial();
				case DisneyMaterial.NAME:
					return new DisneyMaterial();
				case GlassMaterial.NAME:
					return new GlassMaterial();
				case GlossyMaterial.NAME:
					return new GlossyMaterial();
				case HairMaterial.NAME:
					return new HairMaterial();
				case MatteMaterial.NAME:
					return new MatteMaterial();
				case MetalMaterial.NAME:
					return new MetalMaterial();
				case MirrorMaterial.NAME:
					return new MirrorMaterial();
				case PlasticMaterial.NAME:
					return new PlasticMaterial();
				case PolkaDotMaterial.NAME:
					return new PolkaDotMaterial();
				case SubstrateMaterial.NAME:
					return new SubstrateMaterial();
				case UberMaterial.NAME:
					return new UberMaterial();
				default:
					return null;
			}
		}
		
		return null;
	}
	
	private static Point3F doGetPointByShape(final Renderer renderer, final Shape3F shape) {
		if(shape instanceof Cone3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Cylinder3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Disk3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Hyperboloid3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Paraboloid3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Plane3F) {
			return renderer.getScene().getCamera().getPointBelowEye(1.0F);
		} else if(shape instanceof Rectangle3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof RectangularCuboid3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Sphere3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Torus3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else if(shape instanceof Triangle3F) {
			return renderer.getScene().getCamera().getPointInfrontOfEye(7.5F);
		} else {
			return new Point3F();
		}
	}
	
	private static Quaternion4F doGetQuaternionByShape(final Shape3F shape) {
		return shape instanceof Plane3F ? Quaternion4F.from(Matrix44F.rotateX(AngleF.degrees(90.0F))) : new Quaternion4F();
	}
	
	private static Shape3F doCreateShape(final ComboBox<String> comboBoxShape) {
		final String selectedItem = comboBoxShape.getSelectionModel().getSelectedItem();
		
		if(selectedItem != null) {
			switch(selectedItem) {
				case Cone3F.NAME:
					return new Cone3F();
				case Cylinder3F.NAME:
					return new Cylinder3F();
				case Disk3F.NAME:
					return new Disk3F();
				case Hyperboloid3F.NAME:
					return new Hyperboloid3F();
				case Paraboloid3F.NAME:
					return new Paraboloid3F();
				case Plane3F.NAME:
					return new Plane3F();
				case Rectangle3F.NAME:
					return new Rectangle3F();
				case RectangularCuboid3F.NAME:
					return new RectangularCuboid3F();
				case Sphere3F.NAME:
					return new Sphere3F();
				case Torus3F.NAME:
					return new Torus3F();
				case Triangle3F.NAME:
					return new Triangle3F();
				default:
					return null;
			}
		}
		
		return null;
	}
	
	private static void doHandleCameraApertureRadiusChange(final Camera camera, final float value) {
		camera.setApertureRadius(value);
	}
	
	private static void doHandleCameraFocalDistanceChange(final Camera camera, final float value) {
		camera.setFocalDistance(value);
	}
	
	private static void doHandleCameraPitchChange(final Camera camera, final float value) {
		camera.setPitch(AngleF.degrees(value, -90.0F, 90.0F));
		camera.setOrthonormalBasis();
	}
	
	private static void doHandleCameraYawChange(final Camera camera, final float value) {
		camera.setYaw(AngleF.degrees(value));
		camera.setOrthonormalBasis();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class CameraObserverImpl extends AbstractCameraObserver {
		private final Slider sliderApertureRadius;
		private final Slider sliderFocalDistance;
		private final Slider sliderPitch;
		private final Slider sliderYaw;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public CameraObserverImpl(final Slider sliderApertureRadius, final Slider sliderFocalDistance, final Slider sliderPitch, final Slider sliderYaw) {
			this.sliderApertureRadius = Objects.requireNonNull(sliderApertureRadius, "sliderApertureRadius == null");
			this.sliderFocalDistance = Objects.requireNonNull(sliderFocalDistance, "sliderFocalDistance == null");
			this.sliderPitch = Objects.requireNonNull(sliderPitch, "sliderPitch == null");
			this.sliderYaw = Objects.requireNonNull(sliderYaw, "sliderYaw == null");
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		public void onChangeApertureRadius(final Camera camera, final float oldApertureRadius, final float newApertureRadius) {
			Objects.requireNonNull(camera, "camera == null");
			
			doRunInFXApplicationThread(() -> this.sliderApertureRadius.setValue(newApertureRadius));
		}
		
		@Override
		public void onChangeFocalDistance(final Camera camera, final float oldFocalDistance, final float newFocalDistance) {
			Objects.requireNonNull(camera, "camera == null");
			
			doRunInFXApplicationThread(() -> this.sliderFocalDistance.setValue(newFocalDistance));
		}
		
		@Override
		public void onChangePitch(final Camera camera, final AngleF oldPitch, final AngleF newPitch) {
			Objects.requireNonNull(camera, "camera == null");
			Objects.requireNonNull(oldPitch, "oldPitch == null");
			Objects.requireNonNull(newPitch, "newPitch == null");
			
			doRunInFXApplicationThread(() -> this.sliderPitch.setValue(newPitch.getDegrees()));
		}
		
		@Override
		public void onChangeYaw(final Camera camera, final AngleF oldYaw, final AngleF newYaw) {
			Objects.requireNonNull(camera, "camera == null");
			Objects.requireNonNull(oldYaw, "oldYaw == null");
			Objects.requireNonNull(newYaw, "newYaw == null");
			
			doRunInFXApplicationThread(() -> this.sliderYaw.setValue(newYaw.getDegrees()));
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private static void doRunInFXApplicationThread(final Runnable runnable) {
			if(Platform.isFxApplicationThread()) {
				runnable.run();
			} else {
				Platform.runLater(runnable);
			}
		}
	}
}