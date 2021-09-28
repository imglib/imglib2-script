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

package net.imglib2.script;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.script.math.Add;
import net.imglib2.script.view.RectangleROI;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class Rois {

	static public final <T extends NumericType<T> & NativeType<T>> void main(final String[] args) {
		
		// Generate some data
		final FloatProcessor b1 = new FloatProcessor(512, 512);
		b1.setValue(127);
		b1.setRoi(new Roi(100, 100, 200, 200));
		b1.fill();
		
		final FloatProcessor b2 = new FloatProcessor(512, 512);
		b2.setValue(128);
		b2.setRoi(new Roi(10, 30, 200, 200));
		b2.fill();
		
		final Img<T> img1 = ImageJFunctions.wrap(new ImagePlus("1", b1));
		final Img<T> img2 = ImageJFunctions.wrap(new ImagePlus("2", b2));
		
		
		// Add two ROIs of both images
		final RectangleROI<T> r1 = new RectangleROI<T>(img1, 50, 50, 200, 200);
		final RectangleROI<T> r2 = new RectangleROI<T>(img2, 50, 50, 200, 200);
		try {
			final Img<FloatType> result = new Add(r1, r2).asImage(1);
			
			new ImageJ();
			
			ImageJFunctions.show(r1, "r1");
			ImageJFunctions.show(r2, "r2");
			ImageJFunctions.show(img1, "img1");
			ImageJFunctions.show(img2, "img2");
			ImageJFunctions.show(result, "added rois");

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
