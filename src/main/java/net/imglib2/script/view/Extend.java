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

package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class Extend<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleInterval<T> img, final Number value) {
		super(Views.interval(Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())), img));
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img) {
		this(img, 0);
	}

	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy, final Number value) {
		this(proxy.getRandomAccessibleInterval(), value);
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy) {
		this(proxy.getRandomAccessibleInterval(), 0);
	}
}
