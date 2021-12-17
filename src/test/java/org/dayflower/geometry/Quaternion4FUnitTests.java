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
package org.dayflower.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.dayflower.mock.DataOutputMock;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public final class Quaternion4FUnitTests {
	public Quaternion4FUnitTests() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testAdd() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		final Quaternion4F b = new Quaternion4F(2.0F, 3.0F, 4.0F, 5.0F);
		final Quaternion4F c = Quaternion4F.add(a, b);
		
		assertEquals(3.0F, c.getComponent1());
		assertEquals(5.0F, c.getComponent2());
		assertEquals(7.0F, c.getComponent3());
		assertEquals(9.0F, c.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.add(a, null));
		assertThrows(NullPointerException.class, () -> Quaternion4F.add(null, b));
	}
	
	@Test
	public void testConjugate() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		final Quaternion4F b = Quaternion4F.conjugate(a);
		final Quaternion4F c = Quaternion4F.conjugate(b);
		
		assertEquals(-1.0F, b.getComponent1());
		assertEquals(-2.0F, b.getComponent2());
		assertEquals(-3.0F, b.getComponent3());
		assertEquals(+4.0F, b.getComponent4());
		
		assertEquals(+1.0F, c.getComponent1());
		assertEquals(+2.0F, c.getComponent2());
		assertEquals(+3.0F, c.getComponent3());
		assertEquals(+4.0F, c.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.conjugate(null));
	}
	
	@Test
	public void testConstructor() {
		final Quaternion4F quaternion = new Quaternion4F();
		
		assertEquals(0.0F, quaternion.getComponent1());
		assertEquals(0.0F, quaternion.getComponent2());
		assertEquals(0.0F, quaternion.getComponent3());
		assertEquals(1.0F, quaternion.getComponent4());
	}
	
	@Test
	public void testConstructorFloatFloatFloat() {
		final Quaternion4F quaternion = new Quaternion4F(2.0F, 3.0F, 4.0F);
		
		assertEquals(2.0F, quaternion.getComponent1());
		assertEquals(3.0F, quaternion.getComponent2());
		assertEquals(4.0F, quaternion.getComponent3());
		assertEquals(1.0F, quaternion.getComponent4());
	}
	
	@Test
	public void testConstructorFloatFloatFloatFloat() {
		final Quaternion4F quaternion = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		
		assertEquals(1.0F, quaternion.getComponent1());
		assertEquals(2.0F, quaternion.getComponent2());
		assertEquals(3.0F, quaternion.getComponent3());
		assertEquals(4.0F, quaternion.getComponent4());
	}
	
	@Test
	public void testConstructorVector3F() {
		final Quaternion4F quaternion = new Quaternion4F(new Vector3F(2.0F, 3.0F, 4.0F));
		
		assertEquals(2.0F, quaternion.getComponent1());
		assertEquals(3.0F, quaternion.getComponent2());
		assertEquals(4.0F, quaternion.getComponent3());
		assertEquals(1.0F, quaternion.getComponent4());
		
		assertThrows(NullPointerException.class, () -> new Quaternion4F(null));
	}
	
	@Test
	public void testDivide() {
		final Quaternion4F a = new Quaternion4F(2.0F, 4.0F, 8.0F, 16.0F);
		final Quaternion4F b = Quaternion4F.divide(a, 2.0F);
		final Quaternion4F c = Quaternion4F.divide(a, Float.NaN);
		
		assertEquals(1.0F, b.getComponent1());
		assertEquals(2.0F, b.getComponent2());
		assertEquals(4.0F, b.getComponent3());
		assertEquals(8.0F, b.getComponent4());
		
		assertEquals(Float.NaN, c.getComponent1());
		assertEquals(Float.NaN, c.getComponent2());
		assertEquals(Float.NaN, c.getComponent3());
		assertEquals(Float.NaN, c.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.divide(null, 2.0F));
	}
	
	@Test
	public void testDotProduct() {
		final Quaternion4F a = new Quaternion4F(+1.0F, +0.0F, +0.0F, +0.0F);
		final Quaternion4F b = new Quaternion4F(+1.0F, +0.0F, +0.0F, +0.0F);
		final Quaternion4F c = new Quaternion4F(+0.0F, -1.0F, +0.0F, +0.0F);
		final Quaternion4F d = new Quaternion4F(-1.0F, +0.0F, +0.0F, +0.0F);
		
		assertEquals(+1.0F, Quaternion4F.dotProduct(a, b));
		assertEquals(+0.0F, Quaternion4F.dotProduct(a, c));
		assertEquals(-1.0F, Quaternion4F.dotProduct(a, d));
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.dotProduct(a, null));
		assertThrows(NullPointerException.class, () -> Quaternion4F.dotProduct(null, b));
	}
	
	@Test
	public void testEquals() {
		final Quaternion4F a = new Quaternion4F(0.0F, 1.0F, 2.0F, 3.0F);
		final Quaternion4F b = new Quaternion4F(0.0F, 1.0F, 2.0F, 3.0F);
		final Quaternion4F c = new Quaternion4F(0.0F, 1.0F, 2.0F, 4.0F);
		final Quaternion4F d = new Quaternion4F(0.0F, 1.0F, 4.0F, 3.0F);
		final Quaternion4F e = new Quaternion4F(0.0F, 4.0F, 2.0F, 3.0F);
		final Quaternion4F f = new Quaternion4F(4.0F, 1.0F, 2.0F, 3.0F);
		final Quaternion4F g = null;
		
		assertEquals(a, a);
		assertEquals(a, b);
		assertEquals(b, a);
		assertNotEquals(a, c);
		assertNotEquals(c, a);
		assertNotEquals(a, d);
		assertNotEquals(d, a);
		assertNotEquals(a, e);
		assertNotEquals(e, a);
		assertNotEquals(a, f);
		assertNotEquals(f, a);
		assertNotEquals(a, g);
		assertNotEquals(g, a);
	}
	
	@Test
	public void testGetComponent1() {
		final Quaternion4F quaternion = new Quaternion4F(1.0F, 0.0F, 0.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getComponent1());
	}
	
	@Test
	public void testGetComponent2() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 1.0F, 0.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getComponent2());
	}
	
	@Test
	public void testGetComponent3() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 0.0F, 1.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getComponent3());
	}
	
	@Test
	public void testGetComponent4() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 0.0F, 0.0F, 1.0F);
		
		assertEquals(1.0F, quaternion.getComponent4());
	}
	
	@Test
	public void testGetW() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 0.0F, 0.0F, 1.0F);
		
		assertEquals(1.0F, quaternion.getW());
	}
	
	@Test
	public void testGetX() {
		final Quaternion4F quaternion = new Quaternion4F(1.0F, 0.0F, 0.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getX());
	}
	
	@Test
	public void testGetY() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 1.0F, 0.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getY());
	}
	
	@Test
	public void testGetZ() {
		final Quaternion4F quaternion = new Quaternion4F(0.0F, 0.0F, 1.0F, 0.0F);
		
		assertEquals(1.0F, quaternion.getZ());
	}
	
	@Test
	public void testHashCode() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		final Quaternion4F b = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		
		assertEquals(a.hashCode(), a.hashCode());
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	public void testLength() {
		final Quaternion4F a = new Quaternion4F(4.0F, 0.0F, 0.0F, 0.0F);
		final Quaternion4F b = new Quaternion4F(0.0F, 4.0F, 0.0F, 0.0F);
		final Quaternion4F c = new Quaternion4F(0.0F, 0.0F, 4.0F, 0.0F);
		final Quaternion4F d = new Quaternion4F(0.0F, 0.0F, 0.0F, 4.0F);
		
		assertEquals(4.0F, a.length());
		assertEquals(4.0F, b.length());
		assertEquals(4.0F, c.length());
		assertEquals(4.0F, d.length());
	}
	
	@Test
	public void testLengthSquared() {
		final Quaternion4F quaternion = new Quaternion4F(2.0F, 4.0F, 8.0F, 16.0F);
		
		assertEquals(340.0F, quaternion.lengthSquared());
	}
	
	@Test
	public void testMultiply() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		final Quaternion4F b = Quaternion4F.multiply(a, 2.0F);
		
		assertEquals(2.0F, b.getComponent1());
		assertEquals(4.0F, b.getComponent2());
		assertEquals(6.0F, b.getComponent3());
		assertEquals(8.0F, b.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.multiply(null, 2.0F));
	}
	
	@Test
	public void testNegate() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		final Quaternion4F b = Quaternion4F.negate(a);
		final Quaternion4F c = Quaternion4F.negate(b);
		
		assertEquals(-1.0F, b.getComponent1());
		assertEquals(-2.0F, b.getComponent2());
		assertEquals(-3.0F, b.getComponent3());
		assertEquals(-4.0F, b.getComponent4());
		
		assertEquals(+1.0F, c.getComponent1());
		assertEquals(+2.0F, c.getComponent2());
		assertEquals(+3.0F, c.getComponent3());
		assertEquals(+4.0F, c.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.negate(null));
	}
	
	@Test
	public void testRead() throws IOException {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final
		DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
		dataOutput.writeFloat(a.getComponent1());
		dataOutput.writeFloat(a.getComponent2());
		dataOutput.writeFloat(a.getComponent3());
		dataOutput.writeFloat(a.getComponent4());
		
		final byte[] bytes = byteArrayOutputStream.toByteArray();
		
		final Quaternion4F b = Quaternion4F.read(new DataInputStream(new ByteArrayInputStream(bytes)));
		
		assertEquals(a, b);
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.read(null));
		assertThrows(UncheckedIOException.class, () -> Quaternion4F.read(new DataInputStream(new ByteArrayInputStream(new byte[] {}))));
	}
	
	@Test
	public void testSubtract() {
		final Quaternion4F a = new Quaternion4F(3.0F, 5.0F, 7.0F, 9.0F);
		final Quaternion4F b = new Quaternion4F(2.0F, 3.0F, 4.0F, 5.0F);
		final Quaternion4F c = Quaternion4F.subtract(a, b);
		
		assertEquals(1.0F, c.getComponent1());
		assertEquals(2.0F, c.getComponent2());
		assertEquals(3.0F, c.getComponent3());
		assertEquals(4.0F, c.getComponent4());
		
		assertThrows(NullPointerException.class, () -> Quaternion4F.subtract(a, null));
		assertThrows(NullPointerException.class, () -> Quaternion4F.subtract(null, b));
	}
	
	@Test
	public void testToString() {
		final Quaternion4F quaternion = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		
		assertEquals("new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F)", quaternion.toString());
	}
	
	@Test
	public void testWrite() {
		final Quaternion4F a = new Quaternion4F(1.0F, 2.0F, 3.0F, 4.0F);
		
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		final DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
		
		a.write(dataOutput);
		
		final byte[] bytes = byteArrayOutputStream.toByteArray();
		
		final Quaternion4F b = Quaternion4F.read(new DataInputStream(new ByteArrayInputStream(bytes)));
		
		assertEquals(a, b);
		
		assertThrows(NullPointerException.class, () -> a.write(null));
		assertThrows(UncheckedIOException.class, () -> a.write(new DataOutputMock()));
	}
}