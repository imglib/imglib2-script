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
package net.imglib2.script.algorithm.integral;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class IntegralCursor<T extends NumericType<T>> extends Point implements RandomAccess<Pair<T, long[]>>, Cursor<Pair<T, long[]>>
{
	protected final Img<T> integralImg;
	/** RandomAccess over the integralHistogram, with a mirroring strategy for out of bounds. */
	private final RandomAccess<T> ra;
	/** Radius in each dimension. */
	protected final long[] radius;
	/** Index of last sample that can be retrieved. */
	private final long lastIndex;
	/** All the corner points from which the histogram is computed.
	 * Derived from window and specified as relative positive and negative offsets for each dimension. */
	protected final Point[] offsets;
	/** Correlated with offsets, holds the sign to add or subtract the histogram at that Point. */
	protected final int[] signs;
	/** The dimensions over which this Cursor/RandomAccess is defined, which are one less than
	 * in the integralHistogram, and each dimension one less as well. */
	protected final long[] dimensions;
	/** The type used at every call to {@link #get()}. */
	protected final T sum;
	/** The type used for flipping the sign. */
	protected final T tmp;
	
	protected final Pair<T, long[]> packet;
	protected final long[] cellMinPositions, cellMaxPositions;
	
	public IntegralCursor(
			final Img<T> integralImg,
			final long[] radius)
	{
		super(integralImg.numDimensions());
		this.integralImg = integralImg;
		this.radius = radius;
		this.sum = integralImg.firstElement().createVariable();
		this.tmp = this.sum.createVariable();
		this.packet = new ValuePair<T, long[]>(sum, new long[1]);
		this.cellMinPositions = new long[integralImg.numDimensions()];
		this.cellMaxPositions = new long[integralImg.numDimensions()];

		
		// Establish the dimensions where this Cursor/RandomAccess is defined:
		this.dimensions = new long[integralImg.numDimensions()];
		integralImg.dimensions(this.dimensions);
		
		// Compute the size of the underlying, original image from which the integralHistogram was computed:
		this.lastIndex = integralImg.size() - 1;
				
		// Set starting index at -1
		reset();
	
		// Instead, I have to send the coords back to the nearest existing within the domain.
		this.ra = this.integralImg.randomAccess();
				
		// N-dimensional corner coordinates, relative to any one pixel location
		this.offsets = new Point[(int)Math.pow(2, numDimensions())];
		for (int i=0; i<offsets.length; ++i) {
			offsets[i] = new Point(numDimensions());
		}
		int d = 0;
		while (d < numDimensions()) {
			final int flip = (int)Math.pow(2, d);
			int sign = -1;
			for (int i=0; i<offsets.length;) {
				long delta = radius[d];
				// increasing is inclusive, but decreasing is exclusive. This way, a radius of zero also works.
				offsets[i].setPosition(sign * delta + (-1 == sign ? -1 : 0), d);
				++i; // done before flipping sign, so coords will be (almost) in order
				if (0 == i % flip) sign *= -1;
			}
			++d;
		}

		// Compute the sign of each corner
		this.signs = new int[offsets.length];
		for (int o=0; o<offsets.length; ++o) {
			// Count the number of negative signs
			signs[o] = 0;
			for (d=0; d<numDimensions(); ++d) {
				signs[o] += offsets[o].getLongPosition(d) < 0 ? 1 : 0;
			}
			// Set the proper sign
			signs[o] = signs[o] % 2 != 0 ? -1 : 1;
		}
	}

	private final long inside(final long pos, final int d) {
		return Math.min(dimensions[d] -1, Math.max(0, pos));
	}
	
	@Override
	public Pair<T, long[]> get() {
		// Reset sum
		sum.setZero();
		// Reset positions, to compute cell dimensions
		for (int d=0; d<numDimensions(); ++d) {
			cellMinPositions[d] = position[d];
			cellMaxPositions[d] = position[d];
		}
		// Compute sum of cell
		for (int o=0; o<offsets.length; ++o) {
			for (int d=0; d<n; ++d) {
				// position[d] is here
				// + 1 to move over the leading zeros in integralHistogram
				// + offset to go to the right corner
				final long pos = inside(position[d] + 1 + offsets[o].getLongPosition(d), d);
				cellMinPositions[d] = Math.min(pos, cellMinPositions[d]);
				cellMaxPositions[d] = Math.max(pos, cellMaxPositions[d]);
				ra.setPosition(pos, d);
			}
			if (1 == signs[o]) {
				sum.add(ra.get());
			} else {
				sum.sub(ra.get());
			}
		}
		// Compute cell count
		long count = 1;
		for (int d=0; d<numDimensions(); ++d) {
			count *= cellMaxPositions[d] - cellMinPositions[d] + 1;
		}
		packet.getB()[0] = count;
		return packet;
	}

	@Override
	public IntegralCursor<T> copy() {
		return new IntegralCursor<T>(integralImg, radius);
	}

	@Override
	public void jumpFwd(long steps) {
		IntervalIndexer.indexToPosition(IntervalIndexer.positionToIndex(position, dimensions) + steps, dimensions, position);
	}

	@Override
	public void fwd() {
		jumpFwd(1);
	}

	@Override
	public void reset() {
		position[0] = -1;
		for (int d=1; d<n; ++d) position[d] = 0;
	}

	@Override
	public boolean hasNext() {
		return IntervalIndexer.positionToIndex(position, dimensions) < lastIndex;
	}

	@Override
	public Pair<T, long[]> next() {
		fwd();
		return get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IntegralCursor<T> copyCursor() {
		return new IntegralCursor<T>(integralImg, radius);
	}

	@Override
	public IntegralCursor<T> copyRandomAccess() {
		return new IntegralCursor<T>(integralImg, radius);
	}
}
