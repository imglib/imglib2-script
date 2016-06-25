/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.script.algorithm.integral.histogram;

import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The {@link RealType} of the data of the image from which the histogram is computed.
 */
public final class LinearHistogram<T extends RealType<T>> extends Histogram<T>
{
	
	// For clarity and perhaps for performance
	private final double K, dmin, dmax, drange;
	
	/** @see Histogram */
	public LinearHistogram(
			final int nBins,
			final int numDimensions,
			final T min,
			final T max)
	{
		super(nBins, numDimensions, min, max);
		// Compute values of each bin
		this.K = nBins -1;
		int i = -1;
		for (final T bin : binValues) {
			bin.set(range);
			bin.mul(++i / this.K);
			bin.add(min);
		}
		dmin = min.getRealDouble();
		dmax = max.getRealDouble();
		drange = range.getRealDouble();
	}

	@Override
	public final long computeBin(final T value) {
		return (long)(((Math.min(dmax, Math.max(dmin, value.getRealDouble())) - dmin) / drange) * K + 0.5);
	}

	@Override
	public Histogram<T> clone() {
		return new LinearHistogram<T>(nBins(), maxPositions.length, min, max);
	}
}
