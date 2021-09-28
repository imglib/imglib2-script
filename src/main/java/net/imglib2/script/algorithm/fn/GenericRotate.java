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

package net.imglib2.script.algorithm.fn;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class GenericRotate<R extends RealType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	
	static public enum Mode {R90, R180, R270}

	public GenericRotate(final Img<R> img, final Mode mode) {
		super(process(img, mode));
	}

	static private final <R extends RealType<R>> IntervalView<R> process(final Img<R> img, final Mode mode) {
		IntervalView<R> iv;
		if (Mode.R90 == mode) {
			iv = Views.rotate(img, 0, 1);
		} else if (Mode.R270 == mode) {
			iv = Views.rotate(img, 1, 0);
		} else if (Mode.R180 == mode) {
			iv = Views.rotate(Views.rotate(img, 0, 1), 0, 1);
		} else {
			throw new IllegalArgumentException("Invalid Mode: " + mode);
		}
		return Views.zeroMin(iv);
	}
}
