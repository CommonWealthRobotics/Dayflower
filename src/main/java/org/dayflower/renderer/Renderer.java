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
package org.dayflower.renderer;

import org.dayflower.display.Display;
import org.dayflower.image.Image;
import org.dayflower.scene.Scene;

/**
 * A {@code Renderer} is a renderer that can render a {@link Scene} instance to an {@link Image} instance and display the result with a {@link Display} instance.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface Renderer {
	/**
	 * Renders {@code scene} to {@code image} and displays it using {@code display}.
	 * <p>
	 * If either {@code display}, {@code image} or {@code scene} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param display the {@link Display} instance to display with
	 * @param image the {@link Image} instance to render to
	 * @param scene the {@link Scene} instance to render
	 * @throws NullPointerException thrown if, and only if, either {@code display}, {@code image} or {@code scene} are {@code null}
	 */
	void render(final Display display, final Image image, final Scene scene);
	
	/**
	 * Renders {@code scene} to {@code image} and displays it using {@code display}.
	 * <p>
	 * If either {@code display}, {@code image}, {@code scene} or {@code rendererConfiguration} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param display the {@link Display} instance to display with
	 * @param image the {@link Image} instance to render to
	 * @param scene the {@link Scene} instance to render
	 * @param rendererConfiguration the {@link RendererConfiguration} instance to use
	 * @throws NullPointerException thrown if, and only if, either {@code display}, {@code image}, {@code scene} or {@code rendererConfiguration} are {@code null}
	 */
	void render(final Display display, final Image image, final Scene scene, final RendererConfiguration rendererConfiguration);
}