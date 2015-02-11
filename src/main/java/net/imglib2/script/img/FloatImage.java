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

package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;

/** Create a new n-dimensional image based on an array of float. */
/**
 * TODO
 *
 */
public class FloatImage extends ArrayImg<FloatType, FloatArray>
{
	public FloatImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public FloatImage(final long[] dim) {
		this(dim, new float[AlgorithmUtil.size(dim)]);
	}

	public FloatImage(final long[] dim, final float[] pixels) {
		super(new FloatArray(pixels), dim, new Fraction());
		setLinkedType(new FloatType(this));
	}
}
