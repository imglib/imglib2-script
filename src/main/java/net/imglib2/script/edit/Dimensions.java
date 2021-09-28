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

import java.util.ArrayList;

import net.imglib2.RealInterval;
import net.imglib2.img.Img;

/**
 * Extract the dimensions of an {@link Img}.
 *
 */
@SuppressWarnings("serial")
public class Dimensions extends ArrayList<Long> {
	
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)(img.realMax(i) - img.realMin(i)) + 1);
		}
	}
	
	/**
	 * Extract the dimensions of {@param img} multiplied by the {@param factor}.
	 * @param img The interval to extract the dimensions of.
	 * @param factor The factor to multiply the dimensions.
	 */
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img, final Number factor) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)((img.realMax(i) - img.realMin(i) + 1) * factor.doubleValue()));
		}
	}
}
