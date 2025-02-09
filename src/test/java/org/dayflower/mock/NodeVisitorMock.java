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
package org.dayflower.mock;

import org.dayflower.node.Node;
import org.dayflower.node.NodeVisitor;

public final class NodeVisitorMock implements NodeVisitor {
	private final boolean isThrowingRuntimeException;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public NodeVisitorMock() {
		this(false);
	}
	
	public NodeVisitorMock(final boolean isThrowingRuntimeException) {
		this.isThrowingRuntimeException = isThrowingRuntimeException;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(final Node node) {
		if(this.isThrowingRuntimeException) {
			throw new RuntimeException();
		}
	}
}