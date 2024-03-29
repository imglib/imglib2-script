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

package net.imglib2.script.color;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.color.fn.ColorFunction;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Given up to 4 channels--each represented by an {@link IFunction},
 *  this class composes them into an {@link ARGBType} {@link Img}.
 *  
 *  Channel order: A=4, R=3, G=2, B=1.
 *  
 *  Expects each channel in floats or doubles, in the range [0, 255].
 *  */
/**
 * TODO
 *
 */
public final class RGBA extends ColorFunction {

	private final IFunction red, green, blue, alpha;

	public RGBA(final IFunction red, final IFunction green, final IFunction blue, final IFunction alpha) {
		this.red = null == red ? empty : red;
		this.green = null == green ? empty : green;
		this.blue = null == blue ? empty : blue;
		this.alpha = null == alpha ? empty : alpha;
	}

	/** Interpret the @param img as an ARGB image. */
	public RGBA(final IterableRealInterval<? extends RealType<?>> img) {
		this(new Channel(img, 3), new Channel(img, 2), new Channel(img, 1), new Channel(img, 4));
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public RGBA(final Object red, final Object green, final Object blue, final Object alpha) throws Exception {
		this(wrap(red), wrap(green), wrap(blue), wrap(alpha));
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public RGBA(final Object red, final Object green, final Object blue) throws Exception {
		this(wrap(red), wrap(green), wrap(blue), empty);
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public RGBA(final Object red, final Object green) throws Exception {
		this(wrap(red), wrap(green), empty, empty);
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public RGBA(final Object red) throws Exception {
		this(wrap(red), empty, empty, empty);
	}

	/** Creates an RGBA with only the given channel filled, from 1 to 4,
	 *  where RGBA is really ARGB and thus A=4, R=3, G=2, B=1.
	 *  
	 *  @throws Exception If the channel < 1 or > 4. */
	public RGBA(final IFunction fn, final int channel) throws IllegalArgumentException {
		this(3 == channel ? fn : empty, 2 == channel ? fn : empty,
			 1 == channel ? fn : empty, 4 == channel ? fn : empty);
		if (channel < 1 || channel > 4) throw new IllegalArgumentException("RGB: channel must be >= 1 and <= 4");
	}

	/** Creates an RGBA with only the given channel filled, from 1 to 4,
	 *  where RGBA is really ARGB and thus A=4, R=3, G=2, B=1.
	 *  
	 *  @param ob can be an instance of {@link Image}, {@link Number}, {@link IFunction}, or null.
	 *  
	 *  @throws Exception If the channel < 1 or > 4. */
	public RGBA(final Object ob, final int channel) throws Exception, IllegalArgumentException {
		this(wrap(ob), channel);
		if (channel < 1 || channel > 4) throw new IllegalArgumentException("RGB: channel must be >= 1 and <= 4");
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new RGBA(red.duplicate(), green.duplicate(), blue.duplicate(), alpha.duplicate());
	}

	/** Returns each ARGB value packed in an {@code int} that is casted to {@code double}.
	 *  The integers are cropped to their lower byte. */
	@Override
	public final double eval() {
		return ((((int)alpha.eval()) & 0xff) << 24) | ((((int)red.eval()) & 0xff) << 16)
			    | ((((int)green.eval()) & 0xff) << 8) | (((int)blue.eval()) & 0xff);
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		alpha.findCursors(cursors);
		red.findCursors(cursors);
		green.findCursors(cursors);
		blue.findCursors(cursors);
	}

	@Override
	public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
		alpha.findImgs(iris);
		red.findImgs(iris);
		green.findImgs(iris);
		blue.findImgs(iris);
	}
}
