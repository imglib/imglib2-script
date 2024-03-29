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

package net.imglib2.script.algorithm;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.script.algorithm.fn.AbstractAffine3D;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.NumericType;

/**
 * Performs a mathematically correct transformation of an image.
 * This means that an image of 2000x2000 scaled by a factor of 2
 * will result in an image of 3999x3999 pixels.
 * 
 * If the above is not what you expect, then use {@link Resample} instead.
 * 
 * 
 * Expects a matrix of 12 elements
 * 
 * For 2D image it will do:
 *
 * AffineModel2D aff = new AffineModel2D();
 * aff.set(m[0], m[4], m[1], m[5], m[3], m[7]);
 * 
 * For 3D image it will do:
 *  
 * AffineModel3D aff = new AffineModel3D();
 * aff.set(m[0], m[1], m[2], m[3],
 *         m[4], m[5], m[6], m[7],
 *         m[8], m[9], m[10], m[11]);
 *          
 * For RGBA images, each channel will be transformed independently. Hence,
 * the operation will take 4 times as long and require 4 times the memory of a single operation.
 *          
 * The constructors accept either an {@link Img} or an {@link IFunction} from which an {@link Img} is generated.
 *  
 * See the underlying transformation model classes: {@link mpicbg.models.AffineModel2D}, {@link mpicbg.models.AffineModel3D}.
 */
public class Affine3D<T extends NumericType<T>> extends AbstractAffine3D<T>
{	
	/**
	 * TODO
	 * 
	 * @param img The {@link Img} to transform.
	 * @param matrix The values of the transformation matrix, ordered as explained above.
	 * @param mode Either LINEAR or NEAREST_NEIGHBOR.
	 */
	public Affine3D(final Img<T> img, final float[] matrix, final Mode mode) throws Exception {
		this(img, matrix, mode, new OutOfBoundsConstantValueFactory<T,Img<T>>(img.firstElement().createVariable())); // default value is zero
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn, final float[] matrix, final Mode mode, final Number outside) throws Exception {
		super(AlgorithmUtil.wrap(fn), matrix, mode, outside);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn, final float[] matrix, final Mode mode, final OutOfBoundsFactory<T,Img<T>> oobf) throws Exception {
		super(AlgorithmUtil.wrap(fn), matrix, mode, oobf);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final Number outside) throws Exception {
		super(AlgorithmUtil.wrap(fn), new float[]{scaleX, shearX, 0, translateX,
				  						 shearY, scaleY, 0, translateY,
				  			   			 0, 0, 1, 0}, mode, outside);
	}

	@SuppressWarnings("unchecked")
	public Affine3D(final Object fn,
			final float scaleX, final float shearX,
			final float shearY, final float scaleY,
			final float translateX, final float translateY,
			final Mode mode, final OutOfBoundsFactory<T,Img<T>> oobf) throws Exception {
		super(AlgorithmUtil.wrap(fn), new float[]{scaleX, shearX, 0, translateX,
				  						 shearY, scaleY, 0, translateY,
				  			   			 0, 0, 1, 0}, mode, oobf);
	}
}
