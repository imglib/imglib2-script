/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
import net.imglib2.algorithm.integral.IntegralImg;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.script.algorithm.integral.FastIntegralImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

public class CompareIntegralImages
{
	static public final void main(String[] arg) {
		final String src = "/home/albert/lab/TEM/abd/microvolumes/Seg/180-220-int/180-220-int-00.tif";
		try {
			final Img< UnsignedByteType > img = new ImgOpener().openImgs( src, new ArrayImgFactory<>( new UnsignedByteType() ) ).get( 0 );
			
			// copy as short
			Img<UnsignedShortType> copyShort = ArrayImgs.unsignedShorts(Intervals.dimensionsAsLongArray(img));
			Cursor<UnsignedByteType> c1 = img.cursor();
			Cursor<UnsignedShortType> c2 = copyShort.cursor();
			while (c1.hasNext()) {
				c2.next().setInteger(c1.next().get());
			}
			
			// copy as float
			Img<FloatType> copyFloat =  ArrayImgs.floats(Intervals.dimensionsAsLongArray(img));
			c1.reset();
			Cursor<FloatType> c3 = copyFloat.cursor();
			while (c1.hasNext()) {
				c3.next().setReal(c1.next().get());
			}
			
			final int nIterations = 5;
			
			// Test as byte
			System.out.println("As byte/long: ");
			for (int i=0; i < nIterations; ++i) {
				test(img, new LongType());
			}
			
			System.out.println("As byte/int: ");
			for (int i=0; i < nIterations; ++i) {
				test(img, new IntType());
			}
			
			// Test as short
			System.out.println("As short/long: ");
			for (int i=0; i < nIterations; ++i) {
				test(copyShort, new LongType());
			}
			
			System.out.println("As short/int: ");
			for (int i=0; i < nIterations; ++i) {
				test(copyShort, new LongType());
			}
			
			// Test as float
			System.out.println("As float/double: ");
			for (int i=0; i < nIterations; ++i) {
				test(copyFloat, new DoubleType());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public final <T extends RealType<T>, I extends RealType<I> & NativeType<I>> void test(
			final Img<T> img,
			final I integralType)
	{
		try {
			
			final Converter<T, I> converter = new Converter<T, I>() {
				@Override
				public final void convert(final T input, final I output) {
					output.setReal(input.getRealDouble());
				}
			};
			
			long t0 = System.currentTimeMillis();
			
			final IntegralImg<T, I> oa = new IntegralImg<T, I>(img, integralType.createVariable(), converter);
			oa.process();
			final Img<I> ii1 = oa.getResult();
			
			long t1 = System.currentTimeMillis();
			
			System.out.println("IntegralImg: " + (t1 - t0) + " ms");
			
			long t2 = System.currentTimeMillis();
			
			final FastIntegralImg<T, I> fii = new FastIntegralImg<T, I>(img, integralType.createVariable());
			
			long t3 = System.currentTimeMillis();
			
			System.out.println("FastIntegralImg: " + (t3 - t2) + " ms");
			
			// Compare the values, must be identical
			final Cursor<I> c1 = ii1.cursor();
			final Cursor<I> c2 = fii.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				if (0 != c1.get().compareTo(c2.get())) {
					System.out.println("Different values at " + Util.printCoordinates(c1) + " :: " + c1.get() + ", " + c2.get());
					break;
				}
			}
			
			//ImgLib.wrap(ii1).show();
			//ImgLib.wrap(fii).show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
