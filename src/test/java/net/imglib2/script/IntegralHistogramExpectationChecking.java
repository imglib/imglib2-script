/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package net.imglib2.script;

import ij.ImageJ;
import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogram;
import net.imglib2.script.algorithm.integral.histogram.LinearHistogram;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.util.Intervals;

import org.junit.Test;

public class IntegralHistogramExpectationChecking {
	
	static public final <R extends IntegerType<R> & NativeType<R>> void main(String[] arg) {
		IntegralHistogramExpectationChecking I = new IntegralHistogramExpectationChecking();
		//I.testIntegralHistogram2dB();
		//I.testIntegralHistogram1d();
		I.<R>testIntegralHistogram2d();
		//I.testUnsignedAnyBitImg();
	}

	@Test
	public <R extends IntegerType<R> & NativeType<R>> void testIntegralHistogram1d() {
		// Create a 1d image with values 0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7
		Img<UnsignedByteType> img =
				new UnsignedByteType().createSuitableNativeImg(
						new ArrayImgFactory<UnsignedByteType>(), new long[]{16});
		Cursor<UnsignedByteType> c = img.cursor();
		int i = 0;
		int next = 0;
		while (c.hasNext()) {
			c.fwd();
			c.get().set(next);
			++i;
			if (0 == i % 2) ++next;
		}
		//
		LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(16, 1, new UnsignedByteType(0), new UnsignedByteType(7));
		Img<R> ih = IntegralHistogram.create(img, lh);
		
		new ImageJ();
		try {
			ImgLib.wrap((Img)ih, "histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
		
		RandomAccess<R> ra = ih.randomAccess();
		long[] position = new long[2];
		StringBuilder sb = new StringBuilder();
		for (int k=0; k<ih.dimension(0); ++k) {
			position[0] = k;
			sb.append(k + " :: {");
			for (int h=0; h<ih.dimension(1); ++h) {
				position[1] = h;
				ra.setPosition(position);
				System.out.println(ra.get().getRealDouble());
				sb.append(h).append(':').append((long)ra.get().getRealDouble()).append("; ");
			}
			sb.append("}\n");
		}
		System.out.println(sb.toString());
	}
	
	@Test
	public <R extends IntegerType<R> & NativeType<R>> void testIntegralHistogram2d() {
		// Create a 2d image with values 0-9 in every dimension, so bottom right is 81.
		Img<UnsignedByteType> img =
				new UnsignedByteType().createSuitableNativeImg(
						new ArrayImgFactory<UnsignedByteType>(),
						new long[]{10, 10});
		long[] p = new long[2];
		Cursor<UnsignedByteType> c = img.cursor();
		while (c.hasNext()) {
			c.fwd();
			c.localize(p);
			c.get().setInteger(p[0] * p[1]);
		}
		// Create integral histogram with 10 bins
		LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(10, 2, new UnsignedByteType(0), new UnsignedByteType(81));
		Img<R> ih = IntegralHistogram.create(img, lh);
		new ImageJ();
		try {
			ImgLib.wrap((Img)ih, "histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
	
	public <R extends IntegerType<R> & NativeType<R>> void testIntegralHistogram2dB() {
		try {
			Img<UnsignedByteType> img = (Img<UnsignedByteType>) new ImgOpener().openImgs("/home/albert/Desktop/t2/bridge-crop.tif").get(0);
			// Integral histogram with 10 bins
			LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(10, 2, new UnsignedByteType(0), new UnsignedByteType(255));
			Img<R> ih = IntegralHistogram.create(img, lh);
			new ImageJ();
			try {
				ImgLib.wrap((Img)ih, "histogram").show();
			} catch (ImgLibException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUnsignedAnyBitImg() {
		try {
			Img<UnsignedByteType> img1 = (Img<UnsignedByteType>) new ImgOpener().openImgs("/home/albert/Desktop/t2/bridge-crop.tif").get(0);
			
			Img<UnsignedVariableBitLengthType> img2 = new UnsignedVariableBitLengthType(10).createSuitableNativeImg(new ArrayImgFactory<UnsignedVariableBitLengthType>(), Intervals.dimensionsAsLongArray(img1));

			Cursor<UnsignedByteType> c1 = img1.cursor();
			Cursor<UnsignedVariableBitLengthType> c2 = img2.cursor();
			
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().set(c1.get().getIntegerLong());
			}
			
			new ImageJ();
			ImgLib.wrap(img2, "copy").show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
