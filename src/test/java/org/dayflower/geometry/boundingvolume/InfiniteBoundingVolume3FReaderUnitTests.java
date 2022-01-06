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
package org.dayflower.geometry.boundingvolume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public final class InfiniteBoundingVolume3FReaderUnitTests {
	public InfiniteBoundingVolume3FReaderUnitTests() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testIsSupported() {
		final InfiniteBoundingVolume3FReader infiniteBoundingVolume3FReader = new InfiniteBoundingVolume3FReader();
		
		assertTrue(infiniteBoundingVolume3FReader.isSupported(InfiniteBoundingVolume3F.ID));
		
		assertFalse(infiniteBoundingVolume3FReader.isSupported(0));
	}
	
	@Test
	public void testReadDataInput() {
		final InfiniteBoundingVolume3FReader infiniteBoundingVolume3FReader = new InfiniteBoundingVolume3FReader();
		
		final InfiniteBoundingVolume3F infiniteBoundingVolumeA = new InfiniteBoundingVolume3F();
		
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
		
		infiniteBoundingVolumeA.write(dataOutput);
		
		final byte[] bytes = byteArrayOutputStream.toByteArray();
		
		final InfiniteBoundingVolume3F infiniteBoundingVolumeB = infiniteBoundingVolume3FReader.read(new DataInputStream(new ByteArrayInputStream(bytes)));
		
		assertEquals(infiniteBoundingVolumeA, infiniteBoundingVolumeB);
		
		assertThrows(IllegalArgumentException.class, () -> infiniteBoundingVolume3FReader.read(new DataInputStream(new ByteArrayInputStream(new byte[] {0, 0, 0, 0}))));
		assertThrows(NullPointerException.class, () -> infiniteBoundingVolume3FReader.read(null));
		assertThrows(UncheckedIOException.class, () -> infiniteBoundingVolume3FReader.read(new DataInputStream(new ByteArrayInputStream(new byte[] {}))));
	}
	
	@Test
	public void testReadDataInputInt() {
		final InfiniteBoundingVolume3FReader infiniteBoundingVolume3FReader = new InfiniteBoundingVolume3FReader();
		
		final InfiniteBoundingVolume3F infiniteBoundingVolumeA = new InfiniteBoundingVolume3F();
		final InfiniteBoundingVolume3F infiniteBoundingVolumeB = infiniteBoundingVolume3FReader.read(new DataInputStream(new ByteArrayInputStream(new byte[] {})), InfiniteBoundingVolume3F.ID);
		
		assertEquals(infiniteBoundingVolumeA, infiniteBoundingVolumeB);
		
		assertThrows(IllegalArgumentException.class, () -> infiniteBoundingVolume3FReader.read(new DataInputStream(new ByteArrayInputStream(new byte[] {})), 0));
		assertThrows(NullPointerException.class, () -> infiniteBoundingVolume3FReader.read(null, InfiniteBoundingVolume3F.ID));
	}
}