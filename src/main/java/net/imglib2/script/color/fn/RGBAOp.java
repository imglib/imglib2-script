/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the red pixel value. */
/**
 * TODO
 *
 */
public abstract class RGBAOp implements IFunction {

	protected final IterableRealInterval<? extends ARGBType> img;
	protected final RealCursor<? extends ARGBType> c;

	public RGBAOp(final IterableRealInterval<? extends ARGBType> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(IterableRealInterval.class).newInstance(img);
	}
	
	@Override
	public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
		iris.add(img);
	}
}
