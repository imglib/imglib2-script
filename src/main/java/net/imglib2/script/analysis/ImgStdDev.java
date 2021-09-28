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

package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.NumericReduceOperation;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Compute the standard deviation of the image.
 * 
 *
 * @author Albert Cardona
 */
public final class ImgStdDev extends NumericReduceOperation
{
	private static final long serialVersionUID = 1L;
	private final double mean;

	public ImgStdDev(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}
	
	public ImgStdDev(final IFunction fn, final Number mean) throws Exception {
		this(Compute.inFloats(fn), mean);
	}
	
	public ImgStdDev(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		this(img, new ImgMean(img));
	}

	public ImgStdDev(final IterableRealInterval<? extends RealType<?>> img, final Number mean) throws Exception {
		super(img);
		this.mean = mean.doubleValue();
		invoke();
	}

	@Override
	public final double reduce(final double r, final double v) {
		return r + Math.pow(v - mean, 2);
	}
	
	@SuppressWarnings("boxing")
	@Override
	public final Double initial() {
		return 0d;
	}
	
	@Override
	public final double end(final double r) {
		return r / (imgSize -1);
	}

	public final double mean() {
		return mean;
	}
}
