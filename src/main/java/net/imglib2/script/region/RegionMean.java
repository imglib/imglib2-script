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

package net.imglib2.script.region;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.region.fn.ARegionFn;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class RegionMean<T extends RealType<T>> extends ARegionFn<T>
{
	private final double size;
	
	public RegionMean(Img<T> img, RandomAccess<T> ra, long span) {
		super(img, ra, span);
		this.size = img.size();
	}

	@Override
	protected final double fn0(final double a) {
		return a / size;
	}
	
	@Override
	protected final double fnE(final double r) {
		return r;
	}

	@Override
	protected final double fnR(final double r, final double a) {
		return r + a / size;
	}
}
