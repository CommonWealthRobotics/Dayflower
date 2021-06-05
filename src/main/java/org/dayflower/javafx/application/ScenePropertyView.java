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
package org.dayflower.javafx.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.dayflower.color.Color3F;
import org.dayflower.color.Color4F;
import org.dayflower.filter.BoxFilter2F;
import org.dayflower.geometry.Point2I;
import org.dayflower.geometry.Point3F;
import org.dayflower.geometry.Quaternion4F;
import org.dayflower.geometry.Shape3F;
import org.dayflower.geometry.Vector3F;
import org.dayflower.geometry.shape.Rectangle2I;
import org.dayflower.geometry.shape.Sphere3F;
import org.dayflower.image.PixelImageF;
import org.dayflower.javafx.scene.control.ObjectTreeView;
import org.dayflower.javafx.scene.image.WritableImageCache;
import org.dayflower.renderer.CombinedProgressiveImageOrderRenderer;
import org.dayflower.renderer.RenderingAlgorithm;
import org.dayflower.renderer.cpu.CPURenderer;
import org.dayflower.renderer.observer.NoOpRendererObserver;
import org.dayflower.scene.AreaLight;
import org.dayflower.scene.Camera;
import org.dayflower.scene.Material;
import org.dayflower.scene.Primitive;
import org.dayflower.scene.Scene;
import org.dayflower.scene.Transform;
import org.dayflower.scene.light.DiffuseAreaLight;
import org.dayflower.scene.material.MatteMaterial;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

final class ScenePropertyView extends VBox {
	private static final WritableImageCache<Material> WRITABLE_IMAGE_CACHE_MATERIAL = new WritableImageCache<>(ScenePropertyView::doCreateWritableImageMaterial);
	private static final WritableImageCache<Shape3F> WRITABLE_IMAGE_CACHE_SHAPE = new WritableImageCache<>(ScenePropertyView::doCreateWritableImageShape);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final ObjectTreeView<String, Object> objectTreeView;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ScenePropertyView(final Scene scene) {
		Objects.requireNonNull(scene, "scene == null");
		
		this.objectTreeView = doCreateObjectTreeView(scene);
		
		for(final Primitive primitive : scene.getPrimitives()) {
			this.objectTreeView.add(primitive);
		}
		
		setBorder(new Border(new BorderStroke(Color.rgb(181, 181, 181), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.0D, 0.0D, 0.0D, 1.0D))));
		setFillWidth(true);
		setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		setSpacing(20.0D);
		
		getChildren().add(this.objectTreeView);
		
		VBox.setVgrow(this.objectTreeView, Priority.ALWAYS);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ObjectTreeView<String, Object> getObjectTreeView() {
		return this.objectTreeView;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Function<Object, ContextMenu> doCreateMapperUToContextMenu(final Scene scene) {
		return object -> {
			if(object instanceof Primitive) {
				final
				MenuItem menuItem = new MenuItem();
				menuItem.setOnAction(actionEvent -> scene.removePrimitive(Primitive.class.cast(object)));
				menuItem.setText("Delete");
				
				final
				ContextMenu contextMenu = new ContextMenu();
				contextMenu.getItems().add(menuItem);
				
				return contextMenu;
			}
			
			return null;
		};
	}
	
	private static Function<Object, List<Object>> doCreateMapperUToListU() {
		return object -> {
			final List<Object> list = new ArrayList<>();
			
			if(object instanceof Primitive) {
				final Primitive primitive = Primitive.class.cast(object);
				
				list.add(primitive.getMaterial());
				list.add(primitive.getShape());
//				list.add(primitive.getTransform());
			} else if(object instanceof Transform) {
				final Transform transform = Transform.class.cast(object);
				
				list.add(transform.getPosition());
				list.add(transform.getRotation());
				list.add(transform.getScale());
			}
			
			return list;
		};
	}
	
	private static Function<Object, Node> doCreateMapperUToGraphic() {
		return object -> {
			if(object instanceof Material) {
				return new ImageView(WRITABLE_IMAGE_CACHE_MATERIAL.get(Material.class.cast(object)));
			} else if(object instanceof Point3F) {
				return null;
			} else if(object instanceof Primitive) {
				return null;
			} else if(object instanceof Quaternion4F) {
				return null;
			} else if(object instanceof Scene) {
				return null;
			} else if(object instanceof Shape3F) {
				return new ImageView(WRITABLE_IMAGE_CACHE_SHAPE.get(Shape3F.class.cast(object)));
			} else if(object instanceof Transform) {
				return null;
			} else if(object instanceof Vector3F) {
				return null;
			} else {
				return null;
			}
		};
	}
	
	private static Function<Object, String> doCreateMapperUToT() {
		return object -> {
			if(object instanceof Material) {
				return Material.class.cast(object).getName();
			} else if(object instanceof Point3F) {
				final Point3F position = Point3F.class.cast(object);
				
				return String.format("[%+.10f, %+.10f, %+.10f]", Float.valueOf(position.getX()), Float.valueOf(position.getY()), Float.valueOf(position.getZ()));
			} else if(object instanceof Primitive) {
				return "Primitive";
			} else if(object instanceof Quaternion4F) {
				final Quaternion4F rotation = Quaternion4F.class.cast(object);
				
				return String.format("[%+.10f, %+.10f, %+.10f, %+.10f]", Float.valueOf(rotation.getX()), Float.valueOf(rotation.getY()), Float.valueOf(rotation.getZ()), Float.valueOf(rotation.getW()));
			} else if(object instanceof Scene) {
				return Scene.class.cast(object).getName();
			} else if(object instanceof Shape3F) {
				return Shape3F.class.cast(object).getName();
			} else if(object instanceof Transform) {
				return "Transform";
			} else if(object instanceof Vector3F) {
				final Vector3F scale = Vector3F.class.cast(object);
				
				return String.format("[%+.10f, %+.10f, %+.10f]", Float.valueOf(scale.getX()), Float.valueOf(scale.getY()), Float.valueOf(scale.getZ()));
			} else {
				return "";
			}
		};
	}
	
	private static ObjectTreeView<String, Object> doCreateObjectTreeView(final Scene scene) {
		return new ObjectTreeView<>(doCreateMapperUToContextMenu(scene), doCreateMapperUToListU(), doCreateMapperUToGraphic(), doCreateMapperUToT(), scene);
	}
	
	private static Scene doCreateMaterialPreviewScene(final Material material) {
		Objects.requireNonNull(material, "material == null");
		
		final
		Camera camera = new Camera();
		camera.setResolution(32.0F, 32.0F);
		camera.setFieldOfViewY();
		camera.setOrthonormalBasis();
		
		final Material material0 = material;
		final Material material1 = new MatteMaterial();
		
		final Shape3F shape0 = new Sphere3F();
		final Shape3F shape1 = new Sphere3F(2.0F);
		
		final Transform transform0 = new Transform(camera.getPointInfrontOfEye(4.0F));
		final Transform transform1 = new Transform(camera.getPointBehindEye(4.0F));
		
		final AreaLight areaLight1 = new DiffuseAreaLight(transform1, 1, new Color3F(12.0F), shape1, true);
		
		final Primitive primitive0 = new Primitive(material0, shape0, transform0);
		final Primitive primitive1 = new Primitive(material1, shape1, transform1, areaLight1);
		
		final
		Scene scene = new Scene();
		scene.addLight(areaLight1);
		scene.addPrimitive(primitive0);
		scene.addPrimitive(primitive1);
		scene.setCamera(camera);
		scene.setName("Preview");
		
		return scene;
	}
	
	private static WritableImage doCreateWritableImageMaterial(final Material material) {
		final
		CombinedProgressiveImageOrderRenderer combinedProgressiveImageOrderRenderer = new CPURenderer(new NoOpRendererObserver());
		combinedProgressiveImageOrderRenderer.setImage(new PixelImageF(32, 32, Color4F.BLACK, new BoxFilter2F()));
		combinedProgressiveImageOrderRenderer.setPreviewMode(true);
		combinedProgressiveImageOrderRenderer.setRenderingAlgorithm(RenderingAlgorithm.PATH_TRACING);
		combinedProgressiveImageOrderRenderer.setRenderPasses(10);
		combinedProgressiveImageOrderRenderer.setScene(doCreateMaterialPreviewScene(material));
		combinedProgressiveImageOrderRenderer.render();
		
		final
		PixelImageF pixelImageF = PixelImageF.class.cast(combinedProgressiveImageOrderRenderer.getImage());
		pixelImageF.drawRectangle(new Rectangle2I(new Point2I(0, 0), new Point2I(pixelImageF.getResolutionX() - 1, pixelImageF.getResolutionY() - 1)), new Color4F(181, 181, 181));
		
		return pixelImageF.toWritableImage();
	}
	
	@SuppressWarnings("unused")
	private static WritableImage doCreateWritableImageShape(final Shape3F shape) {
		final
		PixelImageF pixelImageF = new PixelImageF(32, 32, Color4F.WHITE);
		pixelImageF.drawRectangle(new Rectangle2I(new Point2I(0, 0), new Point2I(pixelImageF.getResolutionX() - 1, pixelImageF.getResolutionY() - 1)), new Color4F(181, 181, 181));
		
		return pixelImageF.toWritableImage();
	}
}