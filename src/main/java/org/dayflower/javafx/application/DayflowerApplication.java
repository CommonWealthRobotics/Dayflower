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

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.dayflower.javafx.scene.control.NodeSelectionTabPane;
import org.dayflower.javafx.scene.control.PathMenuBar;
import org.dayflower.renderer.CombinedProgressiveImageOrderRenderer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This {@code DayflowerApplication} class is the main entry point for the Dayflower GUI.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class DayflowerApplication extends Application {
	private static final String PATH_ELEMENT_FILE = "File";
	private static final String PATH_ELEMENT_RENDERER = "Renderer";
	private static final String PATH_FILE = "File";
	private static final String PATH_RENDERER = "Renderer";
	private static final String TEXT_EXIT = "Exit";
	private static final String TEXT_FILE = "File";
	private static final String TEXT_G_P_U = "GPU";
	private static final String TEXT_NEW = "New";
	private static final String TEXT_OPEN = "Open";
	private static final String TEXT_RENDERER = "Renderer";
	private static final String TEXT_SAVE = "Save";
	private static final String TEXT_SAVE_AS = "Save As...";
	private static final String TITLE = "Dayflower";
	private static final double MINIMUM_RESOLUTION_X = 400.0D;
	private static final double MINIMUM_RESOLUTION_Y = 400.0D;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean isUsingGPU;
	private final AtomicReference<Stage> stage;
	private final BorderPane borderPane;
	private final ExecutorService executorService;
	private final NodeSelectionTabPane<RendererTabPane, CombinedProgressiveImageOrderRenderer> nodeSelectionTabPane;
	private final PathMenuBar pathMenuBar;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code DayflowerApplication} instance.
	 */
	public DayflowerApplication() {
		this.isUsingGPU = new AtomicBoolean();
		this.stage = new AtomicReference<>();
		this.borderPane = new BorderPane();
		this.executorService = Executors.newFixedThreadPool(4);
		this.nodeSelectionTabPane = new NodeSelectionTabPane<>(RendererTabPane.class, rendererPane -> rendererPane.getCombinedProgressiveImageOrderRenderer(), renderer -> new RendererTabPane(renderer, this.executorService), (a, b) -> a.equals(b), renderer -> renderer.getScene().getName());
		this.pathMenuBar = new PathMenuBar();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts this {@code DayflowerApplication} instance.
	 * <p>
	 * If {@code stage} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param stage the main {@code Stage}
	 * @throws NullPointerException thrown if, and only if, {@code stage} is {@code null}
	 */
	@Override
	public void start(final Stage stage) {
		doSetStage(stage);
		doConfigureBorderPane();
		doConfigureNodeSelectionTabPane();
		doConfigurePathMenuBar();
		doConfigureAndShowStage();
		doCreateAndStartAnimationTimer();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts this {@code DayflowerApplication} instance.
	 * 
	 * @param args the parameter arguments supplied
	 */
	public static void main(final String[] args) {
		launch(args);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Stage doGetStage() {
		return this.stage.get();
	}
	
	private void doConfigureAndShowStage() {
		final
		Stage stage = doGetStage();
		stage.setMinHeight(MINIMUM_RESOLUTION_Y);
		stage.setMinWidth(MINIMUM_RESOLUTION_X);
		stage.setResizable(true);
		stage.setScene(new javafx.scene.Scene(this.borderPane));
		stage.setTitle(TITLE);
		stage.show();
	}
	
	private void doConfigureBorderPane() {
		this.borderPane.setCenter(this.nodeSelectionTabPane);
		this.borderPane.setTop(this.pathMenuBar);
	}
	
	private void doConfigureNodeSelectionTabPane() {
		this.nodeSelectionTabPane.getSelectionModel().selectedItemProperty().addListener(this::doHandleTabChangeSelectionTabPane);
	}
	
	private void doConfigurePathMenuBar() {
		this.pathMenuBar.setPathElementText(PATH_ELEMENT_FILE, TEXT_FILE);
		this.pathMenuBar.setPathElementText(PATH_ELEMENT_RENDERER, TEXT_RENDERER);
		this.pathMenuBar.addMenuItem(PATH_FILE, TEXT_NEW, new FileNewEventHandler(this.isUsingGPU, this.executorService, this.nodeSelectionTabPane), new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), true);
		this.pathMenuBar.addMenuItem(PATH_FILE, TEXT_OPEN, new FileOpenEventHandler(this.isUsingGPU, this.executorService, this.nodeSelectionTabPane, doGetStage()), new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), true);
		this.pathMenuBar.addSeparatorMenuItem(PATH_FILE);
		this.pathMenuBar.addMenuItem(PATH_FILE, TEXT_SAVE, new FileSaveEventHandler(this.nodeSelectionTabPane, doGetStage()), new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), false);
		this.pathMenuBar.addMenuItem(PATH_FILE, TEXT_SAVE_AS, new FileSaveAsEventHandler(this.nodeSelectionTabPane, doGetStage()), null, false);
		this.pathMenuBar.addSeparatorMenuItem(PATH_FILE);
		this.pathMenuBar.addMenuItem(PATH_FILE, TEXT_EXIT, new FileExitEventHandler(this.executorService, this.nodeSelectionTabPane), null, true);
		this.pathMenuBar.addCheckMenuItem(PATH_RENDERER, TEXT_G_P_U, new RendererGPUEventHandler(this.isUsingGPU), true, false);
	}
	
	private void doCreateAndStartAnimationTimer() {
		final
		AnimationTimer animationTimer = new AnimationTimerImpl(this.nodeSelectionTabPane);
		animationTimer.start();
	}
	
	@SuppressWarnings("unused")
	private void doHandleTabChangeSelectionTabPane(final ObservableValue<? extends Tab> observableValue, final Tab oldTab, final Tab newTab) {
		if(newTab != null) {
			this.pathMenuBar.getMenuItem(PATH_FILE, TEXT_SAVE).setDisable(false);
			this.pathMenuBar.getMenuItem(PATH_FILE, TEXT_SAVE_AS).setDisable(false);
		} else {
			this.pathMenuBar.getMenuItem(PATH_FILE, TEXT_SAVE).setDisable(true);
			this.pathMenuBar.getMenuItem(PATH_FILE, TEXT_SAVE_AS).setDisable(true);
		}
		
		Platform.runLater(() -> {
			final Rectangle2D rectangle = Screen.getPrimary().getVisualBounds();
			
			final
			Stage stage = doGetStage();
			stage.sizeToScene();
			stage.setX((rectangle.getWidth()  - stage.getWidth())  / 2.0D);
			stage.setY((rectangle.getHeight() - stage.getHeight()) / 2.0D);
		});
	}
	
	private void doSetStage(final Stage stage) {
		this.stage.set(Objects.requireNonNull(stage, "stage == null"));
	}
}