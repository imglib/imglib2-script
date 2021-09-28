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


import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogramCursor;
import net.imglib2.script.algorithm.integral.histogram.features.IHMax;
import net.imglib2.script.algorithm.integral.histogram.features.IHMean;
import net.imglib2.script.algorithm.integral.histogram.features.IHMedian;
import net.imglib2.script.algorithm.integral.histogram.features.IHMin;
import net.imglib2.script.algorithm.integral.histogram.features.IHStdDev;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class HistogramFeatures<T extends RealType<T> & NativeType<T>, P extends IntegerType<P> & NativeType<P>> extends ImgProxy<T>
{
	static public final int NUM_FEATURES = 5;
	
	public HistogramFeatures(
			final Img<T> img,
			final Img<P> integralHistogram,
			final Histogram<T> histogram,
			final long[] radius) {
		super(create(img, integralHistogram, histogram, radius));
	}

	private static final <R extends RealType<R> & NativeType<R>, P extends IntegerType<P> & NativeType<P>>
	Img<R> create(
			final Img<R> img,
			final Img<P> integralHistogram,
			final Histogram<R> histogram,
			final long[] radius)
	{	
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		
		final Img< R > features = img.factory().create( dims );
		final RandomAccess<R> fr = features.randomAccess();
		
		// One histogram per pixel position, representing the histogram of the window centered at that pixel
		final IntegralHistogramCursor<P, R> h = new IntegralHistogramCursor<P, R>(integralHistogram, histogram, radius);
		
		final int lastDimension = fr.numDimensions() -1;
		
		final IHMin<R> ihMin = new IHMin<R>();
		final IHMax<R> ihMax = new IHMax<R>();
		final IHMean<R, DoubleType> ihMean = new IHMean<R, DoubleType>(new DoubleType());
		final IHMedian<R> ihMedian = new IHMedian<R>();
		final IHStdDev<R, DoubleType> ihStdDev = new IHStdDev<R, DoubleType>(new DoubleType());
		
		final R tmp = img.firstElement().createVariable();
		
		while (h.hasNext()) {
			h.fwd();
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features: gets put into the histogram, which is reused,
			// but the local pointer helps performance
			final Histogram<R> hist = h.get();

			// TODO above, the features should be composable, so that some features depend on others
			// like stdDev depends on the median (or the mean).
			// But in the end the results must return in order, with perhaps some results being a vector of multiple results.

			
			// Store
			fr.setPosition(0, lastDimension);
			ihMin.compute(hist, fr.get());
			fr.move(1, lastDimension);
			ihMax.compute(hist, fr.get());
			fr.move(1, lastDimension);
			ihMean.compute(hist, fr.get());
			fr.move(1, lastDimension);
			ihMedian.compute(hist, fr.get());
			tmp.set(fr.get()); // store the median
			fr.move(1, lastDimension);
			ihStdDev.compute(hist, tmp, fr.get());
		}

		return features;
	}
}
