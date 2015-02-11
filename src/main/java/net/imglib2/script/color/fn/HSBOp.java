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

import java.awt.Color;

import net.imglib2.IterableRealInterval;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the HSB saturation of an RGB pixel. */
/**
 * TODO
 *
 */
public abstract class HSBOp extends RGBAOp {

	private final float[] hsb = new float[3];

	public HSBOp(final IterableRealInterval<? extends ARGBType> img) {
		super(img);
	}

	abstract protected int getIndex();

	@Override
	public final double eval() {
		c.fwd();
		final int v = c.get().get();
		Color.RGBtoHSB((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff, hsb);
		return hsb[getIndex()];
	}
}
