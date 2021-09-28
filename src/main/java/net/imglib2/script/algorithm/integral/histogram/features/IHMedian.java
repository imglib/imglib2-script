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
package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the image from which the {@link Histogram} is computed.
 * @see IHUnaryFeature
 */
public class IHMedian<T extends RealType<T>> implements IHUnaryFeature<T> {

	public IHMedian() {}

	@Override
	public void compute(final Histogram<T> histogram, final T output) {
		final long halfNPixels = histogram.nPixels / 2;
		long count = 0;
		for (int i=0; i<histogram.bins.length; ++i) {
			// Find the approximate median value
			count += histogram.bins[i];
			if (count > halfNPixels) {
				output.set(histogram.binValue(i));
				break;
			}
		}
	}
}
