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

package net.imglib2.script.edit;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * TODO
 *
 */
public class Duplicate<T extends NumericType<T>> extends ImgProxy<T>
{
	public Duplicate(final IterableInterval<T> img) {
		super(process(img));
	}
	
	private static final <R extends NumericType<R> & NativeType<R>> Img<R> copyAsArrayImg(final IterableInterval<R> img) {
		final Img< R > copy = new ArrayImgFactory<>( img.firstElement() ).create( img );
		if (img.iterationOrder().equals(copy.iterationOrder())) {
			final Cursor<R> c1 = img.cursor();
			final Cursor<R> c2 = copy.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().set(c1.get());
			}
		} else {
			final Cursor<R> c1 = img.cursor();
			final RandomAccess<R> c2 = copy.randomAccess();
			final long[] position = new long[img.numDimensions()];
			while (c1.hasNext()) {
				c1.fwd();
				c1.localize(position);
				c2.setPosition(position);
				c2.get().set(c1.get());
			}
		}
		return copy;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final <R extends NumericType<R>> Img<R> process(final IterableInterval<R> img) {
		if (img instanceof Img) {
			return ((Img<R>)img).copy();
		}

		if (img.firstElement() instanceof NativeType<?>) {
			return copyAsArrayImg((IterableInterval)img);
		}
		
		throw new IllegalArgumentException("Could not duplicate image of class " + img.getClass() + " with type " + img.firstElement().getClass());
	}
}
