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

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.RandomAccess;
import net.imglib2.converter.Converter;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.script.algorithm.integral.FastIntegralImg;
import net.imglib2.script.algorithm.integral.IntegralCursor;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;

public class ExampleIntegralImageFeatures
{

	private class IdentityConverter<S extends IntegerType<S>, T extends IntegerType<T>> implements Converter<S, T> {
		@Override
		public void convert(S input, T output) {
			output.setInteger(input.getIntegerLong());
		}
	}
	
	private class SquaringConverter<S extends IntegerType<S>, T extends IntegerType<T>> implements Converter<S, T> {

		@Override
		public void convert(S input, T output) {
			output.setInteger((long)Math.pow(input.getIntegerLong(), 2));
		}
	}
	
	public ExampleIntegralImageFeatures() throws ImgIOException {
		// Open an image
		String src = "/home/albert/lab/TEM/abd/microvolumes/Seg/180-220-int/180-220-int-00.tif"; // 2d
		final Img<UnsignedByteType> img = (Img<UnsignedByteType>) new ImgOpener().openImgs(src).get(0);
		
		// Integral image
		final Img<LongType> integralImg = new FastIntegralImg<UnsignedByteType, LongType>(img, new LongType(), new IdentityConverter<UnsignedByteType, LongType>());
		// Integral image of squares
		final Img<LongType> integralImgSq = new FastIntegralImg<UnsignedByteType, LongType>(img, new LongType(), new SquaringConverter<UnsignedByteType, LongType>());
		
		// Size of the window
		final long[] radius = new long[integralImg.numDimensions()];
		for (int d=0; d<radius.length; ++d) radius[d] = 5;
		
		// Cursors
		final IntegralCursor<LongType> c1 = new IntegralCursor<LongType>(integralImg, radius);
		final IntegralCursor<LongType> c2 = new IntegralCursor<LongType>(integralImgSq, radius);
		
		// View features
		final long NUM_FEATURES = 4;
		final long[] dims = new long[integralImg.numDimensions() + 1];
		for (int d = 0; d<dims.length -1; ++d) dims[d] = integralImg.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		final Img< FloatType > featureStack = ArrayImgs.floats( dims );
		final RandomAccess<FloatType> cf = featureStack.randomAccess();
		
		while (c1.hasNext()) {
			final Pair<LongType, long[]> sum = c1.next();
			final Pair<LongType, long[]> sumSq = c2.next();
			cf.setPosition(c1);
			// 1. The sum
			cf.setPosition(0, dims.length -1);
			cf.get().set(sum.getA().getRealFloat());
			// 2. The sum of squares
			cf.fwd(dims.length -1);
			cf.get().set(sumSq.getA().getRealFloat());
			// 3. The mean
			cf.fwd(dims.length -1);
			cf.get().set(sum.getA().getRealFloat() / sum.getB()[0]);
			// 4. The stdDev
			cf.fwd(dims.length -1);
			cf.get().set((float)((sumSq.getA().getRealFloat() - (Math.pow(sum.getA().getRealFloat(), 2) / sum.getB()[0])) / sum.getB()[0]));
		}
		
		try {
			new ImageJ();
			ImgLib.wrap(featureStack).show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
	
	static public final void main(String[] args) {
		try {
			new ExampleIntegralImageFeatures();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
